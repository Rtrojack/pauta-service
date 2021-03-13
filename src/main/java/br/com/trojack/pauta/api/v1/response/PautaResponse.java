package br.com.trojack.pauta.api.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PautaResponse {

    private String id;

    private String titulo;

    private String descricao;

    private ZonedDateTime dataEncerramentoVotacao;
}