package br.com.trojack.pauta.api.v1.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbrirVotacaoPautaRequest {
    private Integer minutosVotacao;
}
