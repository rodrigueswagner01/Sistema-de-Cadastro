package br.com.sistemacadastro.dao;

import br.com.sistemacadastro.model.Produto;
import java.util.List;

/**
 * Contrato de persistência específico de Produto.
 */
public interface ProdutoDAO extends CrudDAO<Produto, Integer> {

    List<Produto> buscarPorNome(String nomeBusca);
}
