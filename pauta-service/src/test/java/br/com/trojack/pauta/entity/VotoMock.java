package br.com.trojack.pauta.entity;

import java.util.UUID;

public class VotoMock {

    public static Voto criarVotoSim(){
        return Voto.builder()
                .cpf("0123456789")
                .idPauta(UUID.randomUUID().toString())
                .escolhaVoto(true)
                .build();
    }

    public static Voto criarVotoNao(){
        return Voto.builder()
                .cpf("0123456789")
                .idPauta(UUID.randomUUID().toString())
                .escolhaVoto(false)
                .build();
    }
}
