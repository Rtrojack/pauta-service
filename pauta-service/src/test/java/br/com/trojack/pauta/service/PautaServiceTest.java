package br.com.trojack.pauta.service;

import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.dto.PautaDtoMock;
import br.com.trojack.pauta.entity.Pauta;
import br.com.trojack.pauta.entity.PautaMock;
import br.com.trojack.pauta.entity.Voto;
import br.com.trojack.pauta.entity.VotoMock;
import br.com.trojack.pauta.exception.PautaJaVotadaException;
import br.com.trojack.pauta.exception.PautaNaoExistenteException;
import br.com.trojack.pauta.exception.PautaVotacaoFechadaException;
import br.com.trojack.pauta.exception.VotoJaComputadoException;
import br.com.trojack.pauta.repository.PautaRepository;
import br.com.trojack.pauta.repository.VotoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PautaServiceTest {
    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {

    }

    @Test
    public void quandoInserirPautaEntaoRetornarNovaPauta() {
        Pauta pauta = PautaMock.criarPautaMock();
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoMock();

        when(objectMapper.convertValue(any(PautaDto.class), eq(Pauta.class))).thenReturn(pauta);
        when(pautaRepository.save(any())).thenReturn(pauta);
        when(objectMapper.convertValue(any(Pauta.class), eq(PautaDto.class))).thenReturn(pautaDto);

        PautaDto pautaCriada = pautaService.inserirPauta(pautaDto);

        verify(pautaRepository, times(1)).save(any());

        assertEquals(pautaDto, pautaCriada);
    }

    @Test
    public void quandoObterPautasEntaoRetornarListDePautas() {
        Pauta pauta = PautaMock.criarPautaMock();
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoMock();

        when(pautaRepository.findAll()).thenReturn(Arrays.asList(pauta));
        when(objectMapper.convertValue(any(List.class), eq(PautaDto[].class))).thenReturn(new PautaDto[]{pautaDto});

        List<PautaDto> pautasRetornadas = pautaService.obterPautas();

        verify(pautaRepository, times(1)).findAll();

        assertEquals(Arrays.asList(pautaDto), pautasRetornadas);
    }

    @Test(expected = PautaNaoExistenteException.class)
    public void quandoAbrirVotacaoDePautaNaoExistenteEntaoSubirExcecao() {
        when(pautaRepository.findById(any())).thenReturn(Optional.empty());

        pautaService.abrirVotacaoPauta("", 0);
    }

    @Test(expected = PautaJaVotadaException.class)
    public void quandoAbrirVotacaoDePautaJaVotadaEntaoSubirExcecao() {
        Pauta pauta = PautaMock.criarPautaVotadaMock();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));

        pautaService.abrirVotacaoPauta(pauta.getId(), 0);
    }

    @Test
    public void quandoAbrirVotacaoDePautaInformandoNumeroEntaoSalvarPauta() {
        Pauta pauta = PautaMock.criarPautaMock();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));

        pautaService.abrirVotacaoPauta(pauta.getId(), 10);

        verify(pautaRepository, times(1)).save(any());
    }

    @Test
    public void quandoAbrirVotacaoDePautaSemInformarNumeroEntaoSalvarPauta() {
        Pauta pauta = PautaMock.criarPautaMock();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));

        pautaService.abrirVotacaoPauta(pauta.getId(), null);

        verify(pautaRepository, times(1)).save(any());
    }

    @Test(expected = PautaNaoExistenteException.class)
    public void quandoVotarPautaInexistenteEntaoSubirExcecao() {
        when(pautaRepository.findById(any())).thenReturn(Optional.empty());

        pautaService.votarPauta("", "", true);
    }

    @Test(expected = PautaVotacaoFechadaException.class)
    public void quandoVotarPautaNaoIniciadaEntaoSubirExcecao() {
        Pauta pauta = PautaMock.criarPautaMock();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));

        pautaService.votarPauta("", "", true);
    }

    @Test(expected = PautaVotacaoFechadaException.class)
    public void quandoVotarPautaJaEncerradaEntaoSubirExcecao() {
        Pauta pauta = PautaMock.criarPautaVotadaMock();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));

        pautaService.votarPauta("", "", true);
    }

    @Test
    public void quandoVotarPautaEmVotacaoEntaoSalvarVoto() {
        Pauta pauta = PautaMock.criarPautaEmVotacaoMock();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));
        when(votoRepository.findByIdPautaAndCpf(any(), anyString())).thenReturn(Optional.empty());

        pautaService.votarPauta(pauta.getId(), "0123456789", true);

        verify(pautaRepository, times(1)).findById(any());
        verify(votoRepository, times(1)).findByIdPautaAndCpf(any(), anyString());
        verify(votoRepository, times(1)).save(any());
    }

    @Test(expected = VotoJaComputadoException.class)
    public void quandoCreditarVotoJaCreditadoEntaoSubirExcecao() {
        Pauta pauta = PautaMock.criarPautaEmVotacaoMock();
        Voto voto = VotoMock.criarVoto();

        when(pautaRepository.findById(any())).thenReturn(Optional.of(pauta));
        when(votoRepository.findByIdPautaAndCpf(any(), anyString())).thenReturn(Optional.of(voto));

        pautaService.votarPauta(pauta.getId(), "0123456789", true);
    }
}
