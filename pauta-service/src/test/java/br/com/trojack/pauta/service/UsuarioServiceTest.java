package br.com.trojack.pauta.service;

import br.com.trojack.pauta.client.UserInfoClient;
import br.com.trojack.pauta.enumeration.CpfStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UserInfoClient userInfoClient;

    @Test
    public void quandoCpfForAptoAVotarEntaoRetornarVerdadeiro() {
        when(userInfoClient.obterValidadeCpf(any())).thenReturn(CpfStatus.ABLE_TO_VOTE);

        Boolean resultado = usuarioService.cpfAptoParaVotar("");

        assertTrue(resultado);
    }

    @Test
    public void quandoCpfForInaptoAVotarEntaoRetornarFalso() {
        when(userInfoClient.obterValidadeCpf(any())).thenReturn(CpfStatus.UNABLE_TO_VOTE);

        Boolean resultado = usuarioService.cpfAptoParaVotar("");

        assertFalse(resultado);
    }
}
