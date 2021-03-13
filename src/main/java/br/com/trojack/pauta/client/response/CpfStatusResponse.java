package br.com.trojack.pauta.client.response;

import br.com.trojack.pauta.enumeration.CpfStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpfStatusResponse {

    private CpfStatus status;
}
