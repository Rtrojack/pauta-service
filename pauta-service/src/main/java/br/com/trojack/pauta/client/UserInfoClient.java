package br.com.trojack.pauta.client;

import br.com.trojack.pauta.client.response.CpfStatusResponse;
import br.com.trojack.pauta.exception.CpfInvalidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInfoClient {
    @Value("${user-info.baseUrl}")
    private String baseUrl;

    @Value("${user-info.users-cpf}")
    private String usersCpfEndpoint;

    public CpfStatusResponse obterValidadeCpf(String cpf) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl.concat(usersCpfEndpoint).replace("{cpf}", cpf);

        log.info("GET Url: {} - obtendo status do Cpf {}", url, cpf);
        try {
            CpfStatusResponse cpfStatus = restTemplate.getForObject(url, CpfStatusResponse.class);

            log.info("Status do CPF {} obtido com sucesso: {}", cpf, cpfStatus);

            return cpfStatus;

        } catch (HttpClientErrorException e) {
            log.error("Cpf inválido. Resposta do serviço {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CpfInvalidoException(e.getStatusCode(), e.getStatusText());
        } catch (HttpServerErrorException e) {
            log.error("Falha ao chamar serviço de validação de cpf. Resposta {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
}
