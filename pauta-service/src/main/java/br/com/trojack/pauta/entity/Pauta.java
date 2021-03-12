package br.com.trojack.pauta.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pauta {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    private ZonedDateTime dataEncerramentoVotacao;
}
