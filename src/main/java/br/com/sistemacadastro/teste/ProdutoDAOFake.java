package br.com.sistemacadastro.teste;

import br.com.sistemacadastro.dao.ProdutoDAO;
import br.com.sistemacadastro.model.Produto;

import java.util.*;

/**
 * Implementação em memória de ProdutoDAO, usada apenas para testes.
 */
public class ProdutoDAOFake implements ProdutoDAO {

    private final Map<Integer, Produto> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public Produto salvar(Produto produto) {
        produto.setId(proximoId++);
        dados.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public void atualizar(Produto produto) {
        dados.put(produto.getId(), produto);
    }

    @Override
    public void excluir(Integer id) {
        dados.remove(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public Optional<Produto> buscarPorId(Integer id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Produto> buscarPorNome(String nomeBusca) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto e : dados.values()) {
            if (e.getNome().toLowerCase().contains(nomeBusca.toLowerCase())) {
                resultado.add(e);
            }
        }
        return resultado;
    }
}
