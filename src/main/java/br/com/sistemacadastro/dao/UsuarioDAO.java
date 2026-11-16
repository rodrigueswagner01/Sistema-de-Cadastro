package br.com.sistemacadastro.dao;

import br.com.sistemacadastro.model.Usuario;
import java.util.Optional;

/**
 * Contrato de persistência específico de Usuario, estendendo as
 * operações genéricas de CrudDAO com consultas próprias do domínio
 * (busca por login, autenticação).
 */
public interface UsuarioDAO extends CrudDAO<Usuario, Integer> {

    Optional<Usuario> buscarPorLogin(String login);

    Optional<Usuario> autenticar(String login, String senha);
}
