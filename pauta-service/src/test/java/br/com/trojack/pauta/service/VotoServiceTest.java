package br.com.trojack.pauta.service;

import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.dto.PautaDtoMock;
import br.com.trojack.pauta.dto.ResultadoPautaDto;
import br.com.trojack.pauta.dto.ResultadoPautaDtoMock;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.entity.PautaMock;
import br.com.trojack.pauta.entity.Voto;
import br.com.trojack.pauta.entity.VotoMock;
import br.com.trojack.pauta.exception.VotoJaComputadoException;
import br.com.trojack.pauta.repository.VotoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VotoServiceTest {
    @InjectMocks
    private VotoService votoService;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void quandoVotarPautaEmVotacaoEntaoSalvarVoto() {
        Pauta pauta = PautaMock.criarPautaEmVotacaoMock();

        when(votoRepository.findByIdPautaAndCpf(any(), anyString())).thenReturn(Optional.empty());

        votoService.creditarVoto(pauta, "0123456789", true);

        verify(votoRepository, times(1)).findByIdPautaAndCpf(any(), anyString());
        verify(votoRepository, times(1)).save(any());
    }

    @Test(expected = VotoJaComputadoException.class)
    public void quandoCreditarVotoJaCreditadoEntaoSubirExcecao() {
        Pauta pauta = PautaMock.criarPautaEmVotacaoMock();
        Voto voto = VotoMock.criarVotoSim();

        when(votoRepository.findByIdPautaAndCpf(any(), anyString())).thenReturn(Optional.of(voto));

        votoService.creditarVoto(pauta, "0123456789", true);
    }

    @Test
    public void quandoContabilizarResultadoEntaoRetornarResultado() {
        Pauta pauta = PautaMock.criarPautaVotadaMock();
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoVotadaMock();
        ResultadoPautaDto resultadoPautaDto = ResultadoPautaDtoMock.criarResultadoPautaDto();

        when(votoRepository.findByIdPauta(anyString())).thenReturn(Arrays.asList(VotoMock.criarVotoSim(), VotoMock.criarVotoNao()));
        when(objectMapper.convertValue(any(Pauta.class), eq(PautaDto.class))).thenReturn(pautaDto);

        ResultadoPautaDto resultadoRetornado = votoService.contabilizarVotosPauta(pauta);

        assertEquals(resultadoPautaDto.getQuantidadeVotosNao(), resultadoRetornado.getQuantidadeVotosNao());
        assertEquals(resultadoPautaDto.getQuantidadeVotosSim(), resultadoRetornado.getQuantidadeVotosSim());
    }

    @Test
    public void quandoContabilizarResultadoSemVotosEntaoRetornarResultado() {
        Pauta pauta = PautaMock.criarPautaVotadaMock();
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoVotadaMock();
        ResultadoPautaDto resultadoPautaDto = ResultadoPautaDtoMock.criarResultadoPautaDtoSemVotos();

        when(votoRepository.findByIdPauta(anyString())).thenReturn(null);
        when(objectMapper.convertValue(any(Pauta.class), eq(PautaDto.class))).thenReturn(pautaDto);

        ResultadoPautaDto resultadoRetornado = votoService.contabilizarVotosPauta(pauta);

        assertEquals(resultadoPautaDto.getQuantidadeVotosNao(), resultadoRetornado.getQuantidadeVotosNao());
        assertEquals(resultadoPautaDto.getQuantidadeVotosSim(), resultadoRetornado.getQuantidadeVotosSim());
    }
}
