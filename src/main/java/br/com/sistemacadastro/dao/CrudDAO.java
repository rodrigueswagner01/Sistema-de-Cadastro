package br.com.sistemacadastro.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de persistência (CRUD).
 *
 * Aplicação do Dependency Inversion Principle (DIP): as classes de
 * serviço (regra de negócio) dependem desta abstração, não das
 * implementações JDBC concretas. Isso também torna as classes de
 * serviço testáveis com um "fake" ou "mock" de DAO, sem precisar de um
 * banco de dados real durante os testes.
 *
 * @param <T>  tipo da entidade
 * @param <ID> tipo da chave primária
 */
public interface CrudDAO<T, ID> {

    T salvar(T entidade);

    void atualizar(T entidade);

    void excluir(ID id);

    List<T> listarTodos();

    Optional<T> buscarPorId(ID id);
}
