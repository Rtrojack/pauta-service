package br.com.trojack.pauta.api.v1.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErroSimplesResponse {
    private String mensagem;
}
