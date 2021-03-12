package br.com.trojack.pauta.exception;

public class PautaNaoExistenteException extends RuntimeException {
    public PautaNaoExistenteException(String message) {
        super(message);
    }
}
