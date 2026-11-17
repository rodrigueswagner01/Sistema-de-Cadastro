package br.com.sistemacadastro.model;

/**
 * Nível de estoque de um produto, calculado a partir da quantidade
 * (ver ProdutoService.calcularNivelEstoque).
 */
public enum NivelEstoque {
    ESGOTADO("Esgotado"),
    CRITICO("Crítico"),
    BAIXO("Baixo"),
    NORMAL("Normal");

    private final String descricao;

    NivelEstoque(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
