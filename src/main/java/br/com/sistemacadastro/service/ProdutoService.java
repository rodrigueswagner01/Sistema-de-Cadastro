package br.com.sistemacadastro.service;

import br.com.sistemacadastro.dao.ProdutoDAO;
import br.com.sistemacadastro.exception.RegistroNaoEncontradoException;
import br.com.sistemacadastro.exception.ValidacaoException;
import br.com.sistemacadastro.model.Produto;

import java.util.List;

/**
 * Regras de negócio relacionadas a Produto.
 * Ver justificativa detalhada em UsuarioService — o mesmo princípio
 * (SRP: separar regra de negócio de interface e de persistência) é
 * aplicado aqui.
 */
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public Produto cadastrar(Produto produto) {
        validar(produto);
        return produtoDAO.salvar(produto);
    }

    public void atualizar(Produto produto) {
        validar(produto);
        buscarPorIdOuFalhar(produto.getId());
        produtoDAO.atualizar(produto);
    }

    public void excluir(int id) {
        buscarPorIdOuFalhar(id);
        produtoDAO.excluir(id);
    }

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public List<Produto> buscarPorNome(String nomeBusca) {
        return produtoDAO.buscarPorNome(nomeBusca == null ? "" : nomeBusca.trim());
    }

    public Produto buscarPorIdOuFalhar(int id) {
        return produtoDAO.buscarPorId(id)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Produto não encontrado (id=" + id + ")."));
    }

    private void validar(Produto produto) {
        if (produto == null) {
            throw new ValidacaoException("Produto não pode ser nulo.");
        }
        if (isVazio(produto.getNome())) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        if (isVazio(produto.getDescricao())) {
            throw new ValidacaoException("A descrição é obrigatória.");
        }
        if (produto.getStatus() == null) {
            throw new ValidacaoException("O status é obrigatório.");
        }
        // No código desktop original, o valor digitado em txtQuantidade era
        // convertido com Integer.parseInt() diretamente no actionPerformed,
        // sem qualquer tratamento — um NumberFormatException derrubava a
        // interface com uma tela de erro genérica. Além disso, nenhuma
        // regra impedia quantidades negativas.
        if (produto.getQuantidade() < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
