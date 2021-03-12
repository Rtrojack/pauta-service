package br.com.trojack.pauta.service;

import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.repository.PautaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PautaService {
    private final PautaRepository pautaRepository;
    private final ObjectMapper objectMapper;

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
}
