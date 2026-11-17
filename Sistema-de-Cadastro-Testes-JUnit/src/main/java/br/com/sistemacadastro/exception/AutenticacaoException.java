package br.com.sistemacadastro.exception;

/**
 * Exceção lançada quando login/senha informados não correspondem a
 * nenhum usuário cadastrado.
 */
public class AutenticacaoException extends RuntimeException {

    public AutenticacaoException(String message) {
        super(message);
    }
}

