package br.com.trojack.pauta.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class PautaMock {
    public static Pauta criarPautaMock() {
        return Pauta.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(null)
                .build();
    }

    public static Pauta criarPautaVotadaMock() {
        return Pauta.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(ZonedDateTime.now().minusDays(1))
                .build();
    }
}
