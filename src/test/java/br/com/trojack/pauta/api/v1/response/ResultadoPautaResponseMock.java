package br.com.trojack.pauta.api.v1.response;

public class ResultadoPautaResponseMock {
    public static ResultadoPautaResponse criarResultadoPautaResponse(){
        return ResultadoPautaResponse.builder()
                .pauta(PautaResponseMock.criarPautaResponseVotadaMock())
                .quantidadeVotosNao(1)
                .quantidadeVotosSim(2)
                .build();
    }
}
