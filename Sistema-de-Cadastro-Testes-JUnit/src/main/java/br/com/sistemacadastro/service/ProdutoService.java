package br.com.sistemacadastro.service;

import br.com.sistemacadastro.dao.ProdutoDAO;
import br.com.sistemacadastro.exception.RegistroNaoEncontradoException;
import br.com.sistemacadastro.exception.ValidacaoException;
import br.com.sistemacadastro.model.NivelEstoque;
import br.com.sistemacadastro.model.Produto;
import br.com.sistemacadastro.model.StatusProduto;

import java.util.List;

/**
 * Regras de negócio relacionadas a Produto.
 * Ver justificativa detalhada em UsuarioService — o mesmo princípio
 * (SRP: separar regra de negócio de interface e de persistência) é
 * aplicado aqui.
 */
public class ProdutoService {

    /** Até esta quantidade (inclusive) o estoque é considerado CRÍTICO. */
    public static final int LIMITE_CRITICO = 3;
    /** Até esta quantidade (inclusive) o estoque é considerado BAIXO. */
    public static final int LIMITE_BAIXO = 10;

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

    // ------------------------------------------------------------------
    // Regras de cálculo (Etapa 7) — métodos próprios, sem acesso a banco,
    // criados para serem testados de forma isolada (SRP).
    // ------------------------------------------------------------------

    /**
     * Classifica o estoque: 0 = ESGOTADO; 1 a 3 = CRITICO; 4 a 10 = BAIXO;
     * 11 ou mais = NORMAL. Quantidade negativa é inválida.
     */
    public NivelEstoque calcularNivelEstoque(int quantidade) {
        if (quantidade < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
        if (quantidade == 0) {
            return NivelEstoque.ESGOTADO;
        }
        if (quantidade <= LIMITE_CRITICO) {
            return NivelEstoque.CRITICO;
        }
        if (quantidade <= LIMITE_BAIXO) {
            return NivelEstoque.BAIXO;
        }
        return NivelEstoque.NORMAL;
    }

    /**
     * Calcula a quantidade restante depois de uma venda. A quantidade
     * vendida deve ser maior que zero e não pode superar o estoque atual.
     */
    public int calcularQuantidadeAposVenda(int quantidadeAtual, int quantidadeVendida) {
        if (quantidadeAtual < 0) {
            throw new ValidacaoException("A quantidade em estoque não pode ser negativa.");
        }
        if (quantidadeVendida <= 0) {
            throw new ValidacaoException("A quantidade vendida deve ser maior que zero.");
        }
        if (quantidadeVendida > quantidadeAtual) {
            throw new ValidacaoException("Estoque insuficiente para essa venda.");
        }
        return quantidadeAtual - quantidadeVendida;
    }

    /** Define o status coerente com a quantidade: zero = VENDIDO; acima de zero = DISPONIVEL. */
    public StatusProduto definirStatusPorQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
        return quantidade == 0 ? StatusProduto.VENDIDO : StatusProduto.DISPONIVEL;
    }

    /**
     * Registra a venda de uma quantidade de um produto cadastrado:
     * recalcula a quantidade, ajusta o status e persiste a alteração.
     */
    public Produto registrarVenda(int id, int quantidadeVendida) {
        Produto produto = buscarPorIdOuFalhar(id);
        int restante = calcularQuantidadeAposVenda(produto.getQuantidade(), quantidadeVendida);
        produto.setQuantidade(restante);
        produto.setStatus(definirStatusPorQuantidade(restante));
        produtoDAO.atualizar(produto);
        return produto;
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
