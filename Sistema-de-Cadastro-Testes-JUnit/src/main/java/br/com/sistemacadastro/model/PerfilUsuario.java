package br.com.sistemacadastro.model;

import java.text.Normalizer;

/**
 * Perfis de acesso do sistema.
 *
 * Refatoração: no código desktop original o perfil era uma String solta
 * ("Administrador", "Técnico", "Técnico de Laboratório", "Operador"),
 * comparada com equals() dentro das telas (magic strings). Havia até
 * valores divergentes entre o banco ("Tecnico") e a tela ("Técnico").
 * O enum centraliza os valores válidos e a conversão de/para texto.
 */
public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    TECNICO("Técnico"),
    OPERADOR("Operador");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte o texto (tela ou banco) para o enum, ignorando acentos e
     * caixa. Aceita também o valor legado "Técnico de Laboratório".
     */
    public static PerfilUsuario fromDescricao(String descricao) {
        if (descricao != null) {
            String alvo = normalizar(descricao);
            if (alvo.equals("tecnico de laboratorio")) {
                return TECNICO;
            }
            for (PerfilUsuario p : values()) {
                if (normalizar(p.descricao).equals(alvo)) {
                    return p;
                }
            }
        }
        throw new IllegalArgumentException("Perfil inválido: " + descricao);
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
