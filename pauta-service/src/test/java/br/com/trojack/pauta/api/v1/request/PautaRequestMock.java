package br.com.trojack.pauta.api.v1.request;

public class PautaRequestMock {

    public static PautaRequest criarPautaRequestMock(){
        return PautaRequest.builder()
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .build();
    }
}
