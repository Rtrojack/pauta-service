package br.com.trojack.pauta.api.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultadoPautaResponse {

    private PautaResponse pauta;

    private Integer quantidadeVotosSim;

    private Integer quantidadeVotosNao;
}
