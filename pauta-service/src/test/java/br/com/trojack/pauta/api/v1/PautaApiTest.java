package br.com.trojack.pauta.api.v1;

import br.com.trojack.pauta.api.v1.request.*;
import br.com.trojack.pauta.api.v1.response.PautaResponse;
import br.com.trojack.pauta.api.v1.response.PautaResponseMock;
import br.com.trojack.pauta.api.v1.response.ResultadoPautaResponse;
import br.com.trojack.pauta.api.v1.response.ResultadoPautaResponseMock;
import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.dto.PautaDtoMock;
import br.com.trojack.pauta.dto.ResultadoPautaDto;
import br.com.trojack.pauta.dto.ResultadoPautaDtoMock;
import br.com.trojack.pauta.exception.*;
import br.com.trojack.pauta.service.PautaService;
import br.com.trojack.pauta.util.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Arrays;
import java.util.List;

import static br.com.trojack.pauta.util.JsonConvertionUtils.asJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class PautaApiTest {

    private static final String PATH = "/pauta";
    private static final String ABRIR_VOTACAO_PAUTA_PATH = PATH + "/abrir-votacao/";
    private static final String VOTAR_PAUTA_PATH = PATH + "/votar/";
    private static final String RESULTADO_PAUTA_PATH = PATH + "/resultado/";

    @InjectMocks
    private PautaApi pautaApi;

    @Mock
    private PautaService pautaService;

    @Mock
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pautaApi)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    public void quandoObterPautasEntaoRetornarListaDePautasStatusOk() throws Exception {
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoMock();
        PautaResponse pautaResponse = PautaResponseMock.criarPautaResponseMock();

        when(pautaService.obterPautas()).thenReturn(Arrays.asList(pautaDto));
        when(objectMapper.convertValue(any(List.class), eq(PautaResponse[].class))).thenReturn(new PautaResponse[]{pautaResponse});

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].titulo", Is.is(pautaResponse.getTitulo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].descricao", Is.is(pautaResponse.getDescricao())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataEncerramentoVotacao", Is.is(pautaResponse.getDataEncerramentoVotacao())));
    }

    @Test
    public void quandoReceberUmaPautaValidaEntaoRetornarPautaSalvaStatusCreated() throws Exception {
        PautaRequest pautaRequest = PautaRequestMock.criarPautaRequestMock();
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoMock();
        PautaResponse pautaResponse = PautaResponseMock.criarPautaResponseMock();

        when(objectMapper.convertValue(any(PautaRequest.class), eq(PautaDto.class))).thenReturn(pautaDto);
        when(pautaService.inserirPauta(any())).thenReturn(pautaDto);
        when(objectMapper.convertValue(any(PautaDto.class), eq(PautaResponse.class))).thenReturn(pautaResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .content(asJsonString(pautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo", Is.is(pautaResponse.getTitulo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao", Is.is(pautaResponse.getDescricao())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataEncerramentoVotacao", Is.is(pautaResponse.getDataEncerramentoVotacao())));
    }

    @Test
    public void quandoReceberUmaPautaNulaEntaoRetornarStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .content(asJsonString(null))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void quandoReceberUmaPautaInvalidaEntaoRetornarCamposComErrosStatusBadRequest() throws Exception {
        PautaRequest pautaRequest = PautaRequestMock.criarPautaRequestMock();
        pautaRequest.setTitulo(null);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .content(asJsonString(pautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo", Is.is("O título é obrigatório")));
    }

    @Test
    public void quandoAbrirVotacaoPautaNaoExistenteEntaoRetornarStatusNotFound() throws Exception {
        AbrirVotacaoPautaRequest abrirVotacaoPautaRequest = new AbrirVotacaoPautaRequest(10);

        doThrow(new PautaInexistenteException()).when(pautaService).abrirVotacaoPauta(anyString(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.put(ABRIR_VOTACAO_PAUTA_PATH + "1")
                .content(asJsonString(abrirVotacaoPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void quandoAbrirVotacaoPautaJaVotadaEntaoRetornarStatusExpectationFailed() throws Exception {
        AbrirVotacaoPautaRequest abrirVotacaoPautaRequest = new AbrirVotacaoPautaRequest(10);

        doThrow(new PautaJaVotadaException()).when(pautaService).abrirVotacaoPauta(anyString(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.put(ABRIR_VOTACAO_PAUTA_PATH + "1")
                .content(asJsonString(abrirVotacaoPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isExpectationFailed());
    }

    @Test
    public void quandoAbrirVotacaoPautaEntaoRetornarStatusOk() throws Exception {
        AbrirVotacaoPautaRequest abrirVotacaoPautaRequest = new AbrirVotacaoPautaRequest(10);

        doNothing().when(pautaService).abrirVotacaoPauta(anyString(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.put(ABRIR_VOTACAO_PAUTA_PATH + "1")
                .content(asJsonString(abrirVotacaoPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void quandoVotarPautaInexistenteEntaoRetornarStatusNotFound() throws Exception {
        VotarPautaRequest votarPautaRequest = VotarPautaRequestMock.criarVotarPautaRequest();

        doThrow(new PautaInexistenteException()).when(pautaService).votarPauta(anyString(), anyString(), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.post(VOTAR_PAUTA_PATH + "1")
                .content(asJsonString(votarPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void quandoVotarPautaFechadaEntaoRetornarStatusExpectationFailed() throws Exception {
        VotarPautaRequest votarPautaRequest = VotarPautaRequestMock.criarVotarPautaRequest();

        doThrow(new PautaVotacaoFechadaException()).when(pautaService).votarPauta(anyString(), anyString(), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.post(VOTAR_PAUTA_PATH + "1")
                .content(asJsonString(votarPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isExpectationFailed());
    }

    @Test
    public void quandoVotarPautaComCpfInvalidoEntaoRetornarStatusExpectationFailed() throws Exception {
        VotarPautaRequest votarPautaRequest = VotarPautaRequestMock.criarVotarPautaRequest();

        doThrow(new CpfInvalidoException(HttpStatus.NOT_FOUND, "")).when(pautaService).votarPauta(anyString(), anyString(), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.post(VOTAR_PAUTA_PATH + "1")
                .content(asJsonString(votarPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void quandoVotarPautaComCpfInaptoEntaoRetornarStatusExpectationFailed() throws Exception {
        VotarPautaRequest votarPautaRequest = VotarPautaRequestMock.criarVotarPautaRequest();

        doThrow(new CpfInaptoAVotarException()).when(pautaService).votarPauta(anyString(), anyString(), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.post(VOTAR_PAUTA_PATH + "1")
                .content(asJsonString(votarPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void quandoVotarPautaEVotoJaEstiverComputadoEntaoRetornarStatusConflict() throws Exception {
        VotarPautaRequest votarPautaRequest = VotarPautaRequestMock.criarVotarPautaRequest();

        doThrow(new VotoJaComputadoException()).when(pautaService).votarPauta(anyString(), anyString(), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.post(VOTAR_PAUTA_PATH + "1")
                .content(asJsonString(votarPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void quandoVotarPautaAbertaEntaoRetornarStatusOk() throws Exception {
        VotarPautaRequest votarPautaRequest = VotarPautaRequestMock.criarVotarPautaRequest();

        doNothing().when(pautaService).votarPauta(anyString(), anyString(), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.post(VOTAR_PAUTA_PATH + "1")
                .content(asJsonString(votarPautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void quandoObterResultadoDePautaInexistenteEntaoRetornarStatusNotFound() throws Exception {

        doThrow(new PautaInexistenteException()).when(pautaService).obterResultadoPauta(anyString());

        mockMvc.perform(MockMvcRequestBuilders.get(RESULTADO_PAUTA_PATH + "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void quandoObterResultadoDePautaFechadaEntaoRetornarStatusExpectationFailed() throws Exception {

        doThrow(new PautaVotacaoFechadaException()).when(pautaService).obterResultadoPauta(anyString());

        mockMvc.perform(MockMvcRequestBuilders.get(RESULTADO_PAUTA_PATH + "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isExpectationFailed());
    }

    @Test
    public void quandoObterResultadoDePautaEntaoRetornarStatusOk() throws Exception {

        ResultadoPautaResponse resultadoPautaResponse = ResultadoPautaResponseMock.criarResultadoPautaResponse();
        ResultadoPautaDto resultadoPautaDto = ResultadoPautaDtoMock.criarResultadoPautaDto();

        when(pautaService.obterResultadoPauta(any())).thenReturn(resultadoPautaDto);
        when(objectMapper.convertValue(any(ResultadoPautaDto.class), eq(ResultadoPautaResponse.class))).thenReturn(resultadoPautaResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(RESULTADO_PAUTA_PATH + "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pauta.titulo", Is.is(resultadoPautaResponse.getPauta().getTitulo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pauta.descricao", Is.is(resultadoPautaResponse.getPauta().getDescricao())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantidadeVotosNao", Is.is(resultadoPautaResponse.getQuantidadeVotosNao())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantidadeVotosSim", Is.is(resultadoPautaResponse.getQuantidadeVotosSim())));
    }
}