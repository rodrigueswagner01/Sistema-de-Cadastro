package br.com.sistemacadastro.dao;

import br.com.sistemacadastro.model.Produto;
import br.com.sistemacadastro.model.StatusProduto;
import br.com.sistemacadastro.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JDBC de ProdutoDAO (MySQL).
 * Mesma responsabilidade única aplicada em UsuarioDAOJdbc: apenas acesso
 * a dados, nenhuma validação de negócio.
 */
public class ProdutoDAOJdbc implements ProdutoDAO {

    private final ConnectionFactory connectionFactory;

    public ProdutoDAOJdbc(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Produto salvar(Produto produto) {
        String sql = "INSERT INTO Produtos (nome, descricao, quantidade, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preencherParametros(stmt, produto);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    produto.setId(keys.getInt(1));
                }
            }
            return produto;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE Produtos SET nome=?, descricao=?, quantidade=?, status=? WHERE id=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherParametros(stmt, produto);
            stmt.setInt(5, produto.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(Integer id) {
        String sql = "DELETE FROM Produtos WHERE id=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produtos";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public Optional<Produto> buscarPorId(Integer id) {
        String sql = "SELECT * FROM Produtos WHERE id=?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Produto> buscarPorNome(String nomeBusca) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produtos WHERE nome LIKE ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nomeBusca + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por nome: " + e.getMessage(), e);
        }

        return lista;
    }

    private void preencherParametros(PreparedStatement stmt, Produto produto) throws SQLException {
        stmt.setString(1, produto.getNome());
        stmt.setString(2, produto.getDescricao());
        stmt.setInt(3, produto.getQuantidade());
        stmt.setString(4, produto.getStatus().getDescricao());
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("descricao"),
            rs.getInt("quantidade"),
            StatusProduto.fromDescricao(rs.getString("status"))
        );
    }
}
