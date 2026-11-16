package br.com.sistemacadastro.teste;

import br.com.sistemacadastro.dao.UsuarioDAO;
import br.com.sistemacadastro.model.Usuario;

import java.util.*;

/**
 * Implementação em memória de UsuarioDAO, usada apenas para testes.
 *
 * Só é possível existir graças ao Dependency Inversion Principle:
 * UsuarioService depende da interface UsuarioDAO, não da implementação
 * JDBC — então aqui trocamos a implementação sem tocar em uma linha do
 * serviço, testando as regras de negócio isoladamente e sem exigir
 * conexão com MySQL.
 */
public class UsuarioDAOFake implements UsuarioDAO {

    private final Map<Integer, Usuario> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public Usuario salvar(Usuario usuario) {
        usuario.setId(proximoId++);
        dados.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public void atualizar(Usuario usuario) {
        dados.put(usuario.getId(), usuario);
    }

    @Override
    public void excluir(Integer id) {
        dados.remove(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return dados.values().stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    @Override
    public Optional<Usuario> autenticar(String login, String senha) {
        return dados.values().stream()
            .filter(u -> u.getLogin().equals(login) && u.getSenha().equals(senha))
            .findFirst();
    }
}
