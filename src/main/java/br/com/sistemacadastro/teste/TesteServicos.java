package br.com.sistemacadastro.teste;

import br.com.sistemacadastro.exception.AutenticacaoException;
import br.com.sistemacadastro.exception.RegistroNaoEncontradoException;
import br.com.sistemacadastro.exception.ValidacaoException;
import br.com.sistemacadastro.model.Produto;
import br.com.sistemacadastro.model.PerfilUsuario;
import br.com.sistemacadastro.model.StatusProduto;
import br.com.sistemacadastro.model.Usuario;
import br.com.sistemacadastro.service.AutorizacaoService;
import br.com.sistemacadastro.service.ProdutoService;
import br.com.sistemacadastro.service.UsuarioService;

import java.util.List;

/**
 * Classe de testes manuais (sem framework de testes) executada via
 * método main(), conforme solicitado no roteiro da Etapa 6:
 * "Procure implementar testes no método main() do projeto para
 * certificar-se de que tudo está funcionando."
 *
 * Usa os DAOs fake (em memória) para validar toda a camada de serviço
 * sem depender de um banco MySQL configurado, o que também comprova, na
 * prática, o benefício do Dependency Inversion Principle aplicado no
 * projeto: a mesma UsuarioService/ProdutoService funciona tanto com
 * o DAO JDBC real quanto com esta implementação de teste.
 */
public class TesteServicos {

    private static int testesExecutados = 0;
    private static int testesComFalha = 0;

    public static void main(String[] args) {
        System.exit(executar() ? 0 : 1);
    }

    /**
     * Executa todos os testes automáticos de regras de negócio (sem banco
     * de dados, usando DAOs falsos em memória) e retorna true se todos passaram.
     */
    public static boolean executar() {
        testesExecutados = 0;
        testesComFalha = 0;
        testarCadastroDeUsuarioValido();
        testarCadastroDeUsuarioComCampoObrigatorioVazio();
        testarLoginDuplicadoNaoPermitido();
        testarAutenticacaoComSucesso();
        testarAutenticacaoComCredenciaisInvalidas();
        testarAtualizacaoDeUsuarioInexistente();

        testarCadastroDeProdutoValido();
        testarCadastroDeProdutoComQuantidadeNegativa();
        testarBuscaDeProdutoPorNome();
        testarExclusaoDeProdutoInexistente();

        testarRegrasDeAutorizacaoPorPerfil();
        testarConversaoDePerfilEStatus();
        testarAtualizacaoDeProdutoComQuantidadeNegativa();
        testarAtualizacaoDeProdutoExistente();

        System.out.println();
        System.out.println("======================================");
        System.out.println("Testes executados: " + testesExecutados);
        System.out.println("Testes com falha : " + testesComFalha);
        System.out.println(testesComFalha == 0 ? "RESULTADO: TODOS OS TESTES PASSARAM" : "RESULTADO: HÁ TESTES FALHANDO");
        System.out.println("======================================");
        return testesComFalha == 0;
    }

    // ---------- Testes de UsuarioService ----------

    private static void testarCadastroDeUsuarioValido() {
        iniciar("Cadastro de usuário válido");
        UsuarioService service = new UsuarioService(new UsuarioDAOFake());

        Usuario usuario = new Usuario("Maria Silva", "maria@email.com", PerfilUsuario.OPERADOR, "maria", "123");
        Usuario salvo = service.cadastrar(usuario);

        checar(salvo.getId() > 0, "Usuário deveria receber um id gerado.");
        checar(service.listarTodos().size() == 1, "Deveria existir 1 usuário cadastrado.");
    }

    private static void testarCadastroDeUsuarioComCampoObrigatorioVazio() {
        iniciar("Cadastro de usuário com nome vazio deve falhar");
        UsuarioService service = new UsuarioService(new UsuarioDAOFake());

        try {
            service.cadastrar(new Usuario("", "x@email.com", PerfilUsuario.OPERADOR, "x", "123"));
            falhar("Deveria ter lançado ValidacaoException para nome vazio.");
        } catch (ValidacaoException e) {
            checar(true, "");
        }
    }

    private static void testarLoginDuplicadoNaoPermitido() {
        iniciar("Login duplicado não deve ser permitido");
        UsuarioService service = new UsuarioService(new UsuarioDAOFake());

        service.cadastrar(new Usuario("Usuário 1", "u1@email.com", PerfilUsuario.OPERADOR, "duplicado", "123"));
        try {
            service.cadastrar(new Usuario("Usuário 2", "u2@email.com", PerfilUsuario.OPERADOR, "duplicado", "456"));
            falhar("Deveria ter lançado ValidacaoException para login duplicado.");
        } catch (ValidacaoException e) {
            checar(true, "");
        }
    }

    private static void testarAutenticacaoComSucesso() {
        iniciar("Autenticação com credenciais corretas");
        UsuarioService service = new UsuarioService(new UsuarioDAOFake());
        service.cadastrar(new Usuario("Admin", "admin@email.com", PerfilUsuario.ADMINISTRADOR, "admin", "123"));

        Usuario autenticado = service.autenticar("admin", "123");
        checar(autenticado.getPerfil() == PerfilUsuario.ADMINISTRADOR, "Perfil autenticado deveria ser ADMINISTRADOR.");
    }

    private static void testarAutenticacaoComCredenciaisInvalidas() {
        iniciar("Autenticação com credenciais inválidas deve falhar");
        UsuarioService service = new UsuarioService(new UsuarioDAOFake());
        service.cadastrar(new Usuario("Admin", "admin@email.com", PerfilUsuario.ADMINISTRADOR, "admin", "123"));

        try {
            service.autenticar("admin", "senha-errada");
            falhar("Deveria ter lançado AutenticacaoException.");
        } catch (AutenticacaoException e) {
            checar(true, "");
        }
    }

    private static void testarAtualizacaoDeUsuarioInexistente() {
        iniciar("Atualização de usuário inexistente deve falhar");
        UsuarioService service = new UsuarioService(new UsuarioDAOFake());

        Usuario usuario = new Usuario("Fantasma", "fantasma@email.com", PerfilUsuario.OPERADOR, "fantasma", "123");
        usuario.setId(999);

        try {
            service.atualizar(usuario);
            falhar("Deveria ter lançado RegistroNaoEncontradoException.");
        } catch (RegistroNaoEncontradoException e) {
            checar(true, "");
        }
    }

    // ---------- Testes de ProdutoService ----------

    private static void testarCadastroDeProdutoValido() {
        iniciar("Cadastro de produto válido");
        ProdutoService service = new ProdutoService(new ProdutoDAOFake());

        Produto produto = new Produto("Celular Nokia", "Aparelho celular", 5, StatusProduto.DISPONIVEL);
        Produto salvo = service.cadastrar(produto);

        checar(salvo.getId() > 0, "Produto deveria receber um id gerado.");
    }

    private static void testarCadastroDeProdutoComQuantidadeNegativa() {
        iniciar("Cadastro de produto com quantidade negativa deve falhar");
        ProdutoService service = new ProdutoService(new ProdutoDAOFake());

        try {
            service.cadastrar(new Produto("Balança", "Balança de cozinha", -1, StatusProduto.DISPONIVEL));
            falhar("Deveria ter lançado ValidacaoException para quantidade negativa.");
        } catch (ValidacaoException e) {
            checar(true, "");
        }
    }

    private static void testarBuscaDeProdutoPorNome() {
        iniciar("Busca de produto por nome (parcial)");
        ProdutoService service = new ProdutoService(new ProdutoDAOFake());

        service.cadastrar(new Produto("Cafeteira Philco", "Cafeteira elétrica", 2, StatusProduto.DISPONIVEL));
        service.cadastrar(new Produto("Celular Nokia", "Aparelho celular", 5, StatusProduto.VENDIDO));

        List<Produto> resultado = service.buscarPorNome("celu");
        checar(resultado.size() == 1, "Busca por 'celu' deveria retornar 1 resultado.");
    }

    private static void testarExclusaoDeProdutoInexistente() {
        iniciar("Exclusão de produto inexistente deve falhar");
        ProdutoService service = new ProdutoService(new ProdutoDAOFake());

        try {
            service.excluir(999);
            falhar("Deveria ter lançado RegistroNaoEncontradoException.");
        } catch (RegistroNaoEncontradoException e) {
            checar(true, "");
        }
    }

    // ---------- Testes de AutorizacaoService ----------

    private static void testarRegrasDeAutorizacaoPorPerfil() {
        iniciar("Regras de autorização por perfil");
        AutorizacaoService autorizacao = new AutorizacaoService();

        checar(autorizacao.podeGerenciarUsuarios(PerfilUsuario.ADMINISTRADOR), "Administrador deveria gerenciar usuários.");
        checar(!autorizacao.podeGerenciarUsuarios(PerfilUsuario.OPERADOR), "Operador não deveria gerenciar usuários.");
        checar(autorizacao.podeGerenciarProdutos(PerfilUsuario.TECNICO), "Técnico deveria gerenciar produtos.");
        checar(!autorizacao.podeGerenciarProdutos(PerfilUsuario.OPERADOR), "Operador não deveria gerenciar produtos.");
        checar(autorizacao.podeConsultarProdutos(PerfilUsuario.OPERADOR), "Operador deveria poder consultar produtos.");
    }

    // ---------- Testes dos enums e de atualização de produto ----------

    private static void testarConversaoDePerfilEStatus() {
        iniciar("Conversão de perfil e status aceita variações legadas");
        checar(PerfilUsuario.fromDescricao("Técnico") == PerfilUsuario.TECNICO, "'Técnico' deveria virar TECNICO.");
        checar(PerfilUsuario.fromDescricao("Tecnico") == PerfilUsuario.TECNICO, "'Tecnico' (sem acento) deveria virar TECNICO.");
        checar(PerfilUsuario.fromDescricao("Técnico de Laboratório") == PerfilUsuario.TECNICO, "Valor legado deveria virar TECNICO.");
        checar(StatusProduto.fromDescricao("Disponivel") == StatusProduto.DISPONIVEL, "'Disponivel' (sem acento) deveria virar DISPONIVEL.");
        checar(StatusProduto.fromDescricao("vendido") == StatusProduto.VENDIDO, "'vendido' deveria virar VENDIDO.");
        try {
            PerfilUsuario.fromDescricao("Gerente");
            falhar("Perfil inexistente deveria lançar IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            checar(true, "");
        }
    }

    private static void testarAtualizacaoDeProdutoComQuantidadeNegativa() {
        iniciar("Atualização de produto com quantidade negativa deve falhar");
        ProdutoService service = new ProdutoService(new ProdutoDAOFake());
        Produto salvo = service.cadastrar(new Produto("Máquina de serra", "Makita", 2, StatusProduto.DISPONIVEL));

        salvo.setQuantidade(-5);
        try {
            service.atualizar(salvo);
            falhar("Deveria ter lançado ValidacaoException.");
        } catch (ValidacaoException e) {
            checar(true, "");
        }
    }

    private static void testarAtualizacaoDeProdutoExistente() {
        iniciar("Atualização de produto existente altera os dados");
        ProdutoService service = new ProdutoService(new ProdutoDAOFake());
        Produto salvo = service.cadastrar(new Produto("Cafeteira", "Philco", 3, StatusProduto.DISPONIVEL));

        salvo.setQuantidade(1);
        salvo.setStatus(StatusProduto.VENDIDO);
        service.atualizar(salvo);

        Produto lido = service.buscarPorIdOuFalhar(salvo.getId());
        checar(lido.getQuantidade() == 1 && lido.getStatus() == StatusProduto.VENDIDO, "Dados deveriam ter sido atualizados.");
    }

    // ---------- Infraestrutura mínima de teste ----------

    private static void iniciar(String nomeDoTeste) {
        testesExecutados++;
        System.out.println("[TESTE] " + nomeDoTeste);
    }

    private static void checar(boolean condicao, String mensagemSeFalhar) {
        if (condicao) {
            System.out.println("   OK");
        } else {
            testesComFalha++;
            System.out.println("   FALHOU: " + mensagemSeFalhar);
        }
    }

    private static void falhar(String mensagem) {
        testesComFalha++;
        System.out.println("   FALHOU: " + mensagem);
    }
}
