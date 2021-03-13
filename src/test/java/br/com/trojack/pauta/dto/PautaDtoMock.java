package br.com.trojack.pauta.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class PautaDtoMock {

    public static PautaDto criarPautaDtoMock() {
        return PautaDto.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(null)
                .build();
    }

    public static PautaDto criarPautaDtoVotadaMock() {
        return PautaDto.builder()
                .id(UUID.randomUUID().toString())
                .titulo("Pauta Mock")
                .descricao("Aceita a pauta mock?")
                .dataEncerramentoVotacao(ZonedDateTime.now().minusDays(1))
                .build();
    }
}
