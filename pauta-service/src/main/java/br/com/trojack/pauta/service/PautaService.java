package br.com.trojack.pauta.service;

import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.exception.PautaJaVotadaException;
import br.com.trojack.pauta.exception.PautaNaoExistenteException;
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
        Optional<Pauta> optionalPauta = pautaRepository.findById(id);

        if (optionalPauta.isEmpty()) {
            log.warn("Tentativa de abertura de votação de pauta não existente. Id: {}", id);
            throw new PautaNaoExistenteException("Pauta informada não existe.");
        }

        Pauta pauta = optionalPauta.get();

        if (pauta.getDataEncerramentoVotacao() != null) {
            log.warn("Tentativa de abertura de votação de pauta já votada ou em votação. Id: {} - {} - Encerramento em: {}", id, pauta.getTitulo(), DateTimeUtils.format(pauta.getDataEncerramentoVotacao()));
            throw new PautaJaVotadaException("Pauta informada não existe.");
        }

        pauta.setDataEncerramentoVotacao(minutosVotacao == null || minutosVotacao == 0
                ? ZonedDateTime.now().plusMinutes(TEMPO_VOTACAO_PADRAO)
                : ZonedDateTime.now().plusMinutes(minutosVotacao));

        pautaRepository.save(pauta);

        log.info("Pauta {} - {} aberta para votação. Encerramento em: {}", pauta.getId(), pauta.getTitulo(), DateTimeUtils.format(pauta.getDataEncerramentoVotacao()));
    }
}
