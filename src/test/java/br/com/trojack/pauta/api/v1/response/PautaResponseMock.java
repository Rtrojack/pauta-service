package br.com.trojack.pauta.api.v1.response;

import java.time.ZonedDateTime;
import java.util.UUID;

public class PautaResponseMock {

    public static PautaResponse criarPautaResponseMock() {
        return PautaResponse.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(null)
                .build();
    }

    public static PautaResponse criarPautaResponseVotadaMock() {
        return PautaResponse.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(ZonedDateTime.now().minusDays(1))
                .build();
    }
}
