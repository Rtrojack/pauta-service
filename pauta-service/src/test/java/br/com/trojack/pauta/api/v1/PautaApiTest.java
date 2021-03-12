package br.com.trojack.pauta.api.v1;

import br.com.trojack.pauta.api.v1.request.PautaRequest;
import br.com.trojack.pauta.api.v1.request.PautaRequestMock;
import br.com.trojack.pauta.api.v1.response.PautaResponse;
import br.com.trojack.pauta.api.v1.response.PautaResponseMock;
import br.com.trojack.pauta.dto.PautaDto;
import br.com.trojack.pauta.dto.PautaDtoMock;
import br.com.trojack.pauta.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PautaApiTest {

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

        mockMvc.perform(MockMvcRequestBuilders.get("/pauta/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].titulo", Is.is(pautaResponse.getTitulo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].descricao", Is.is(pautaResponse.getDescricao())));
    }

    @Test
    public void quandoReceberUmaPautaValidaEntaoRetornarPautaSalvaStatusCreated() throws Exception {
        PautaRequest pautaRequest = PautaRequestMock.criarPautaRequestMock();
        PautaDto pautaDto = PautaDtoMock.criarPautaDtoMock();
        PautaResponse pautaResponse = PautaResponseMock.criarPautaResponseMock();

        when(objectMapper.convertValue(any(PautaRequest.class), eq(PautaDto.class))).thenReturn(pautaDto);
        when(pautaService.inserirPauta(any())).thenReturn(pautaDto);
        when(objectMapper.convertValue(any(PautaDto.class), eq(PautaResponse.class))).thenReturn(pautaResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/pauta/")
                .content(asJsonString(pautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo", Is.is(pautaResponse.getTitulo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao", Is.is(pautaResponse.getDescricao())));
    }

    @Test
    public void quandoReceberUmaPautaNulaEntaoRetornarStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/pauta/")
                .content(asJsonString(null))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void quandoReceberUmaPautaInvalidaEntaoRetornarCamposComErrosStatusBadRequest() throws Exception {
        PautaRequest pautaRequest = PautaRequestMock.criarPautaRequestMock();
        pautaRequest.setTitulo(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/pauta/")
                .content(asJsonString(pautaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo", Is.is("O título é obrigatório")));
    }
}