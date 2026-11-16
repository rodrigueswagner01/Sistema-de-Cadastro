package br.com.sistemacadastro.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementação concreta de ConnectionFactory para MySQL.
 *
 * Refatoração em relação à antiga classe util.Conexao:
 * a conexão deixou de ser uma classe estática (util.Conexao) e passou a
 * ser uma implementação da interface ConnectionFactory (DIP). URL, usuário
 * e senha têm valores padrão do ambiente de desenvolvimento, mas podem ser
 * sobrescritos pelas variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD.
 */
public class MySqlConnectionFactory implements ConnectionFactory {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/SistemadeCadastro";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "sua_senha";

    private final String url;
    private final String usuario;
    private final String senha;

    public MySqlConnectionFactory() {
        this.url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        this.usuario = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        this.senha = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
    }

    public MySqlConnectionFactory(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
