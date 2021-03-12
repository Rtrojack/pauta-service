package br.com.trojack.pauta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PautaDto {

    private String id;

    private String titulo;

    private String descricao;

    private ZonedDateTime dataEncerramentoVotacao;
}