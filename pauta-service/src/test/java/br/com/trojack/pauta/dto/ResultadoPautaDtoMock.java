package br.com.trojack.pauta.dto;

public class ResultadoPautaDtoMock {
    public static ResultadoPautaDto criarResultadoPautaDto(){
        return ResultadoPautaDto.builder()
                .pauta(PautaDtoMock.criarPautaDtoVotadaMock())
                .quantidadeVotosNao(1)
                .quantidadeVotosSim(1)
                .build();
    }

    public static ResultadoPautaDto criarResultadoPautaDtoSemVotos(){
        return ResultadoPautaDto.builder()
                .pauta(PautaDtoMock.criarPautaDtoVotadaMock())
                .quantidadeVotosNao(0)
                .quantidadeVotosSim(0)
                .build();
    }
}
