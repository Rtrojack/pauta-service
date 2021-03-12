package br.com.trojack.pauta.api.v1.request;

public class VotarPautaRequestMock {

    public static VotarPautaRequest criarVotarPautaRequest() {
        return VotarPautaRequest.builder()
                .cpf("0123456789")
                .escolhaVoto(false)
                .build();
    }
}
