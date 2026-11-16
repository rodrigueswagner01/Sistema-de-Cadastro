package br.com.sistemacadastro.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstração para obtenção de conexões com o banco de dados (DIP).
 *
 * As classes DAO dependem desta interface, e não de uma implementação
 * concreta ligada ao MySQL. Isso permite, por exemplo, trocar o banco de
 * dados ou usar um pool de conexões (HikariCP) numa futura versão web sem
 * alterar uma linha sequer das classes DAO.
 */
public interface ConnectionFactory {

    Connection getConnection() throws SQLException;
}
