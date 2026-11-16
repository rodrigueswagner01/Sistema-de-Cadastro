package br.com.sistemacadastro.exception;

/**
 * Exceção lançada quando uma operação referencia um registro (usuário ou
 * produto) que não existe na base de dados. Substitui os antigos
 * caminhos silenciosos ("if (linha >= 0) ... else JOptionPane") por um
 * sinal explícito de erro que qualquer camada superior pode tratar.
 */
public class RegistroNaoEncontradoException extends RuntimeException {

    public RegistroNaoEncontradoException(String message) {
        super(message);
    }
}
