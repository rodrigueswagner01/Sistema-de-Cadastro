package br.com.sistemacadastro.service;

import br.com.sistemacadastro.model.PerfilUsuario;

/**
 * Regras de autorização por perfil de usuário.
 *
 * No projeto desktop original, essas regras estavam "cravadas" dentro
 * do método configurarPermissoes() de view.MenuPrincipalFrame, com uma
 * cadeia de if/else comparando Strings. Isso violava o Open/Closed
 * Principle: qualquer novo perfil ou nova tela exigia editar
 * diretamente a classe de interface gráfica. Extraindo a regra para cá,
 * a política de acesso passa a ser reutilizável tanto pela tela Swing
 * quanto por uma futura camada web (ex.: um filtro de autorização em
 * uma API REST), sem duplicar lógica.
 */
public class AutorizacaoService {

    public boolean podeGerenciarUsuarios(PerfilUsuario perfil) {
        return perfil == PerfilUsuario.ADMINISTRADOR;
    }

    public boolean podeGerenciarProdutos(PerfilUsuario perfil) {
        return perfil == PerfilUsuario.ADMINISTRADOR || perfil == PerfilUsuario.TECNICO;
    }

    public boolean podeConsultarProdutos(PerfilUsuario perfil) {
        // Todos os perfis cadastrados podem consultar.
        return perfil != null;
    }
}
