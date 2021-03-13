package br.com.trojack.pauta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class CpfInvalidoException extends HttpClientErrorException {
    public CpfInvalidoException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }
}
