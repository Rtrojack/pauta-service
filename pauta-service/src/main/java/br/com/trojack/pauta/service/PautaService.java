package br.com.trojack.pauta.service;

import br.com.trojack.pauta.client.UserInfoClient;
import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.dto.ResultadoPautaDto;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.enumeration.CpfStatus;
import br.com.trojack.pauta.exception.CpfInaptoAVotarException;
import br.com.trojack.pauta.exception.PautaInexistenteException;
import br.com.trojack.pauta.exception.PautaJaVotadaException;
import br.com.trojack.pauta.exception.PautaVotacaoFechadaException;
import br.com.trojack.pauta.repository.PautaRepository;
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
    private final VotoService votoService;
    private final UsuarioService usuarioService;
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
        } catch (PautaInexistenteException e) {
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
        } catch (PautaInexistenteException e) {
            log.warn("Tentativa de voto em pauta não existente. Id: {} - Cpf: {}", id, cpf);
            throw e;
        }

        if (pauta.getDataEncerramentoVotacao() == null || pauta.getDataEncerramentoVotacao().isBefore(ZonedDateTime.now())) {
            log.warn("Tentativa de voto em pauta com votação fechada. Id: {} - Cpf: {}", id, cpf);
            throw new PautaVotacaoFechadaException();
        }

        if (!usuarioService.cpfAptoParaVotar(cpf)) {
            throw new CpfInaptoAVotarException();
        }

        votoService.creditarVoto(pauta, cpf, escolhaVoto);
    }

    public ResultadoPautaDto obterResultadoPauta(String id) {
        Pauta pauta;

        try {
            pauta = obterPauta(id);
        } catch (PautaInexistenteException e) {
            log.warn("Tentativa de obter resultados de pauta inexistente. Id: {}", id);
            throw e;
        }

        if (pauta.getDataEncerramentoVotacao() == null) {
            log.warn("Tentativa de obter resultados de pauta ainda não votada. Id: {} - {}", pauta.getId(), pauta.getTitulo());
            throw new PautaVotacaoFechadaException();
        }

        return votoService.contabilizarVotosPauta(pauta);
    }

    private Pauta obterPauta(String id) {
        Optional<Pauta> optionalPauta = pautaRepository.findById(id);

        if (optionalPauta.isEmpty()) {
            throw new PautaInexistenteException();
        }

        return optionalPauta.get();
    }

    private Pauta obterPautaEmCache(String id) {
        //Todo: obter pauta do cache

        return obterPauta(id);
    }

}
