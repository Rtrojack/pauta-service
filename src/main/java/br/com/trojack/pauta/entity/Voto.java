package br.com.trojack.pauta.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Voto {

    @Id
    private String id;

    @Indexed
    private String idPauta;

    @Indexed
    private String cpf;

    private Boolean escolhaVoto;
}

