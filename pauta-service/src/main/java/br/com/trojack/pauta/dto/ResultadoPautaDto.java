package br.com.trojack.pauta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultadoPautaDto {

    private PautaDto pauta;

    private Integer quantidadeVotosSim;

    private Integer quantidadeVotosNao;
}
