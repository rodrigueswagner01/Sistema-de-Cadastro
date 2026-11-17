package br.com.sistemacadastro.model;

import java.text.Normalizer;

/**
 * Situação de um produto.
 *
 * Refatoração: o status era uma String livre ("Disponível" na tela e
 * "Disponivel" em dados do banco). O enum elimina a divergência.
 */
public enum StatusProduto {
    DISPONIVEL("Disponível"),
    VENDIDO("Vendido");

    private final String descricao;

    StatusProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Converte texto para o enum, ignorando acentos e caixa. */
    public static StatusProduto fromDescricao(String descricao) {
        if (descricao != null) {
            String alvo = normalizar(descricao);
            for (StatusProduto s : values()) {
                if (normalizar(s.descricao).equals(alvo)) {
                    return s;
                }
            }
        }
        throw new IllegalArgumentException("Status inválido: " + descricao);
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase();
    }

    @Override
    public String toString() {
        return descricao;
    }
}
