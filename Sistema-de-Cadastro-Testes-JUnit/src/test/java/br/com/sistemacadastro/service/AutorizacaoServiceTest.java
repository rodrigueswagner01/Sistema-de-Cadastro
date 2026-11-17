package br.com.sistemacadastro.service;

import br.com.sistemacadastro.model.PerfilUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Testes unitários (JUnit 5) das permissões por perfil (AutorizacaoService). */
class AutorizacaoServiceTest {

    private final AutorizacaoService service = new AutorizacaoService();

    @Test
    @DisplayName("Administrador gerencia usuários e produtos")
    void administradorTemAcessoTotal() {
        assertTrue(service.podeGerenciarUsuarios(PerfilUsuario.ADMINISTRADOR));
        assertTrue(service.podeGerenciarProdutos(PerfilUsuario.ADMINISTRADOR));
    }

    @Test
    @DisplayName("Técnico gerencia produtos, mas não usuários")
    void tecnicoGerenciaApenasProdutos() {
        assertTrue(service.podeGerenciarProdutos(PerfilUsuario.TECNICO));
        assertFalse(service.podeGerenciarUsuarios(PerfilUsuario.TECNICO));
    }

    @Test
    @DisplayName("Operador não gerencia usuários nem produtos")
    void operadorSoConsulta() {
        assertFalse(service.podeGerenciarUsuarios(PerfilUsuario.OPERADOR));
        assertFalse(service.podeGerenciarProdutos(PerfilUsuario.OPERADOR));
    }

    @Test
    @DisplayName("Todos os perfis podem consultar produtos")
    void todosConsultam() {
        for (PerfilUsuario perfil : PerfilUsuario.values()) {
            assertTrue(service.podeConsultarProdutos(perfil), "Perfil sem acesso à consulta: " + perfil);
        }
    }

    @Test
    @DisplayName("Perfil nulo não tem nenhuma permissão")
    void perfilNuloNaoTemPermissao() {
        assertFalse(service.podeGerenciarUsuarios(null));
        assertFalse(service.podeGerenciarProdutos(null));
        assertFalse(service.podeConsultarProdutos(null));
    }
}
