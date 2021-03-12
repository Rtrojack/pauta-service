package br.com.trojack.pauta.api.v1.response;

import java.util.UUID;

public class PautaResponseMock {

    public static PautaResponse criarPautaResponseMock(){
        return PautaResponse.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .build();
    }
}
