package br.com.trojack.pauta.api.v1.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbrirVotacaoPautaRequest {

    @ApiModelProperty(value = "Quantos minutos a votação ficará aberta. Padrão 1 minuto.")
    private Integer minutosVotacao;
}
