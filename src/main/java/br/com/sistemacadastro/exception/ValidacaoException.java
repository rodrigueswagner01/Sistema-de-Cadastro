package br.com.sistemacadastro.exception;

/**
 * Exceção lançada quando uma regra de validação de negócio é violada
 * (ex.: campo obrigatório vazio, quantidade negativa, login duplicado).
 *
 * No código desktop original, essas validações disparavam diretamente
 * um javax.swing.JOptionPane dentro do método actionPerformed do botão,
 * misturando regra de negócio com apresentação. Ao lançar uma exceção de
 * domínio, a camada de serviço permanece livre de qualquer dependência
 * de Swing e pode ser reaproveitada por uma view desktop, uma futura API
 * web (traduzindo esta exceção para HTTP 400, por exemplo) ou por testes
 * automatizados.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String message) {
        super(message);
    }
}
