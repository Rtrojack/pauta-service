package br.com.trojack.pauta.service;

import br.com.trojack.pauta.client.UserInfoClient;
import br.com.trojack.pauta.client.response.CpfStatusResponse;
import br.com.trojack.pauta.enumeration.CpfStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UsuarioService {

    private final UserInfoClient userInfoClient;

    public Boolean cpfAptoParaVotar(String cpf) {
        //Todo: obter status do cpf em cache

        CpfStatusResponse cpfStatusResponse = userInfoClient.obterValidadeCpf(cpf);

        //Todo: salvar status cpf em cache

        return cpfStatusResponse.getStatus() == CpfStatus.ABLE_TO_VOTE;
    }
}
