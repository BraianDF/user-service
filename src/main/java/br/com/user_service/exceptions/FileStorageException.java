package br.com.user_service.exceptions;

public class FileStorageException extends RuntimeException {

    public FileStorageException(String mensagem) {
        super(mensagem);
    }

    public FileStorageException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
