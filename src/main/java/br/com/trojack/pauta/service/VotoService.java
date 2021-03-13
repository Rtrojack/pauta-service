package br.com.trojack.pauta.service;

import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.dto.ResultadoPautaDto;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.entity.Voto;
import br.com.trojack.pauta.exception.VotoJaComputadoException;
import br.com.trojack.pauta.repository.VotoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class VotoService {

    private final VotoRepository votoRepository;
    private final ObjectMapper objectMapper;

    public void creditarVoto(Pauta pauta, String cpf, Boolean escolhaVoto) {
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

    public ResultadoPautaDto contabilizarVotosPauta(Pauta pauta) {
        List<Voto> votos = votoRepository.findByIdPauta(pauta.getId());

        if (votos == null || votos.isEmpty()) {
            return ResultadoPautaDto.builder()
                    .pauta(objectMapper.convertValue(pauta, PautaDto.class))
                    .quantidadeVotosNao(0)
                    .quantidadeVotosSim(0)
                    .build();
        }

        long quantidadeVotosSim = votos.stream().filter(Voto::getEscolhaVoto).count();
        long quantidadeVotosNao = votos.stream().filter(v -> !v.getEscolhaVoto()).count();

        return ResultadoPautaDto.builder()
                .pauta(objectMapper.convertValue(pauta, PautaDto.class))
                .quantidadeVotosSim((int) quantidadeVotosSim)
                .quantidadeVotosNao((int) quantidadeVotosNao)
                .build();
    }
}
