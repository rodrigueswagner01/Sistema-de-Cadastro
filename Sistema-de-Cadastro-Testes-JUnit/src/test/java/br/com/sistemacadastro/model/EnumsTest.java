package br.com.sistemacadastro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/** Testes unitários (JUnit 5) da conversão de texto para os enums PerfilUsuario e StatusProduto. */
class EnumsTest {

    @ParameterizedTest(name = "\"{0}\" -> TECNICO")
    @CsvSource({"Técnico", "Tecnico", "TÉCNICO", "  técnico  ", "Técnico de Laboratório"})
    @DisplayName("Variações de 'Técnico' (inclusive o valor legado) viram TECNICO")
    void variacoesDeTecnico(String texto) {
        assertEquals(PerfilUsuario.TECNICO, PerfilUsuario.fromDescricao(texto));
    }

    @Test
    @DisplayName("Perfis Administrador e Operador são reconhecidos")
    void outrosPerfis() {
        assertEquals(PerfilUsuario.ADMINISTRADOR, PerfilUsuario.fromDescricao("administrador"));
        assertEquals(PerfilUsuario.OPERADOR, PerfilUsuario.fromDescricao("Operador"));
    }

    @Test
    @DisplayName("Perfil inexistente ou nulo lança IllegalArgumentException")
    void perfilInvalido() {
        assertThrows(IllegalArgumentException.class, () -> PerfilUsuario.fromDescricao("Gerente"));
        assertThrows(IllegalArgumentException.class, () -> PerfilUsuario.fromDescricao(null));
    }

    @ParameterizedTest(name = "\"{0}\" -> DISPONIVEL")
    @CsvSource({"Disponível", "Disponivel", "DISPONÍVEL"})
    @DisplayName("Variações de 'Disponível' viram DISPONIVEL")
    void variacoesDeDisponivel(String texto) {
        assertEquals(StatusProduto.DISPONIVEL, StatusProduto.fromDescricao(texto));
    }

    @Test
    @DisplayName("Status 'vendido' é reconhecido e status inexistente lança erro")
    void statusVendidoEInvalido() {
        assertEquals(StatusProduto.VENDIDO, StatusProduto.fromDescricao("vendido"));
        assertThrows(IllegalArgumentException.class, () -> StatusProduto.fromDescricao("Reservado"));
    }

    @Test
    @DisplayName("toString devolve o texto exibido nas telas")
    void toStringDevolveDescricao() {
        assertEquals("Técnico", PerfilUsuario.TECNICO.toString());
        assertEquals("Disponível", StatusProduto.DISPONIVEL.toString());
        assertEquals("Crítico", NivelEstoque.CRITICO.toString());
    }
}
