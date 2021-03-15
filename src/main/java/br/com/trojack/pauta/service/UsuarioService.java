package br.com.trojack.pauta.service;

import br.com.trojack.pauta.client.UserInfoClient;
import br.com.trojack.pauta.client.response.CpfStatusResponse;
import br.com.trojack.pauta.enumeration.CpfStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

@Service
@AllArgsConstructor
@Slf4j
public class UsuarioService {

    private final UserInfoClient userInfoClient;

    @Cacheable("usuario")
    public Boolean cpfAptoParaVotar(String cpf) {
        CpfStatusResponse cpfStatusResponse = userInfoClient.obterValidadeCpf(cpf);

        return cpfStatusResponse.getStatus() == CpfStatus.ABLE_TO_VOTE;
    }
}
