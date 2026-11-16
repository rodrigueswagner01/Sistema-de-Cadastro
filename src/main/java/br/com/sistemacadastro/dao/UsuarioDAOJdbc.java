package br.com.sistemacadastro.dao;

import br.com.sistemacadastro.model.PerfilUsuario;
import br.com.sistemacadastro.model.Usuario;
import br.com.sistemacadastro.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JDBC de UsuarioDAO (MySQL).
 *
 * Responsabilidade única (SRP): apenas traduzir chamadas de persistência
 * em comandos SQL. Nenhuma validação de negócio (campos obrigatórios,
 * duplicidade de login etc.) é feita aqui — isso é papel de
 * UsuarioService. Essa separação corresponde exatamente ao code smell
 * "Divergent Change" identificado na classe original dao.UsuarioDAO, que
 * antes coexistia com regras de negócio na camada de view.
 */
public class UsuarioDAOJdbc implements UsuarioDAO {

    private final ConnectionFactory connectionFactory;

    public UsuarioDAOJdbc(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, perfil, login, senha) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preencherParametros(stmt, usuario);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
            }
            return usuario;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome=?, email=?, perfil=?, login=?, senha=? WHERE id=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherParametros(stmt, usuario);
            stmt.setInt(6, usuario.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(Integer id) {
        String sql = "DELETE FROM usuarios WHERE id=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        String sql = "SELECT * FROM usuarios WHERE id=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE login=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por login: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Usuario> autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login=? AND senha=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    private void preencherParametros(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNome());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getPerfil().getDescricao());
        stmt.setString(4, usuario.getLogin());
        stmt.setString(5, usuario.getSenha());
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            PerfilUsuario.fromDescricao(rs.getString("perfil")),
            rs.getString("login"),
            rs.getString("senha")
        );
    }
}
