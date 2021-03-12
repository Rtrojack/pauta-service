package br.com.trojack.pauta.entity;

import java.util.UUID;

public class PautaMock {
    public static Pauta criarPautaMock(){
        return Pauta.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(null)
                .build();
    }
}
