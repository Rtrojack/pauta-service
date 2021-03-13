package br.com.trojack.pauta.api.v1.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PautaRequest {

    @ApiModelProperty(value = "Título da pauta.")
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @ApiModelProperty(value = "Descrição da pauta.")
    @NotBlank(message = "A Descrição é obrigatória")
    private String descricao;
}
