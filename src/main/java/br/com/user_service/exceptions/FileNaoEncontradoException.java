package br.com.user_service.exceptions;

public class FileNaoEncontradoException extends RuntimeException {

    public FileNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public FileNaoEncontradoException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
