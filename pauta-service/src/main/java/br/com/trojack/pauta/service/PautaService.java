package br.com.trojack.pauta.service;

import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.entity.Voto;
import br.com.trojack.pauta.exception.PautaJaVotadaException;
import br.com.trojack.pauta.exception.PautaNaoExistenteException;
import br.com.trojack.pauta.exception.PautaVotacaoFechadaException;
import br.com.trojack.pauta.exception.VotoJaComputadoException;
import br.com.trojack.pauta.repository.PautaRepository;
import br.com.trojack.pauta.repository.VotoRepository;
import br.com.trojack.pauta.util.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PautaService {
    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;
    private final ObjectMapper objectMapper;

    private static final Integer TEMPO_VOTACAO_PADRAO = 1;

    public List<PautaDto> obterPautas() {
        log.info("Obtendo todas pautas...");

        List<Pauta> pautas = pautaRepository.findAll();

        log.info("Quantidade de pautas obtidas: {}", pautas.size());

        return Arrays.asList(objectMapper.convertValue(pautas, PautaDto[].class));
    }

    public PautaDto inserirPauta(PautaDto pautaDto) {

        log.info("Inserindo nova pauta: {}", pautaDto);
        Pauta pauta = pautaRepository.save(objectMapper.convertValue(pautaDto, Pauta.class));

        log.info("Nova pauta inserida com sucesso. {} - {}", pauta.getId(), pauta.getTitulo());
        return objectMapper.convertValue(pauta, PautaDto.class);
    }

    public void abrirVotacaoPauta(String id, Integer minutosVotacao) {
        Pauta pauta;

        try {
            pauta = obterPauta(id);
        } catch (PautaNaoExistenteException e) {
            log.warn("Tentativa de abertura de votação de pauta não existente. Id: {}", id);
            throw e;
        }

        if (pauta.getDataEncerramentoVotacao() != null) {
            log.warn("Tentativa de abertura de votação de pauta já votada ou em votação. Id: {} - {} - Encerramento em: {}", id, pauta.getTitulo(), DateTimeUtils.format(pauta.getDataEncerramentoVotacao()));
            throw new PautaJaVotadaException();
        }

        pauta.setDataEncerramentoVotacao(minutosVotacao == null || minutosVotacao == 0
                ? ZonedDateTime.now().plusMinutes(TEMPO_VOTACAO_PADRAO)
                : ZonedDateTime.now().plusMinutes(minutosVotacao));

        pautaRepository.save(pauta);

        log.info("Pauta {} - {} aberta para votação. Encerramento em: {}", pauta.getId(), pauta.getTitulo(), DateTimeUtils.format(pauta.getDataEncerramentoVotacao()));
    }

    public void votarPauta(String id, String cpf, Boolean escolhaVoto) {
        Pauta pauta;
        try {
            pauta = obterPautaEmCache(id);
        } catch (PautaNaoExistenteException e) {
            log.warn("Tentativa de voto em pauta não existente. Id: {} - Cpf: {}", id, cpf);
            throw e;
        }

        if (pauta.getDataEncerramentoVotacao() == null || pauta.getDataEncerramentoVotacao().isBefore(ZonedDateTime.now())) {
            log.warn("Tentativa de voto em pauta com votação fechada. Id: {} - Cpf: {}", id, cpf);
            throw new PautaVotacaoFechadaException();
        }

        //Todo validar cpf

        creditarVoto(pauta, cpf, escolhaVoto);
    }

    private void creditarVoto(Pauta pauta, String cpf, Boolean escolhaVoto) {
        if (votoRepository.findByIdPautaAndCpf(pauta.getId(), cpf).isPresent()) {
            log.warn("Voto já creditado na pauta {} - {} para o Cpf: {}", pauta.getId(), pauta.getTitulo(), cpf);
            throw new VotoJaComputadoException();
        }

        Voto voto = Voto.builder()
                .cpf(cpf)
                .idPauta(pauta.getId())
                .escolhaVoto(escolhaVoto)
                .build();

        votoRepository.save(voto);

        log.info("Voto creditado com sucesso para o Cpf {} na pauta {} - {}. Voto: {}", cpf, pauta.getId(), pauta.getTitulo(), escolhaVoto);
    }

    private Pauta obterPauta(String id) {
        Optional<Pauta> optionalPauta = pautaRepository.findById(id);

        if (optionalPauta.isEmpty()) {
            throw new PautaNaoExistenteException();
        }

        return optionalPauta.get();
    }

    private Pauta obterPautaEmCache(String id) {
        //Todo: obter pauta do cache

        return obterPauta(id);
    }
}
