package br.com.trojack.pauta.api.v1.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VotarPautaRequest {

    @ApiModelProperty(value = "CPF do associado.")
    @NotBlank(message = "Necessário informar o cpf do associado.")
    private String cpf;

    @ApiModelProperty(value = "Voto do associado. true = SIM / false = NÃO")
    @NotNull(message = "Necessário informar a escolha do voto.")
    private Boolean escolhaVoto;
}
