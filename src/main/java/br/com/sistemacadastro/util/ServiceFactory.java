package br.com.sistemacadastro.util;

import br.com.sistemacadastro.dao.ProdutoDAO;
import br.com.sistemacadastro.dao.ProdutoDAOJdbc;
import br.com.sistemacadastro.dao.UsuarioDAO;
import br.com.sistemacadastro.dao.UsuarioDAOJdbc;
import br.com.sistemacadastro.service.AutorizacaoService;
import br.com.sistemacadastro.service.ProdutoService;
import br.com.sistemacadastro.service.UsuarioService;

/**
 * Ponto único de composição das dependências da aplicação
 * (uma "poor man's" injeção de dependência, sem framework).
 *
 * Isola em um só lugar o "new UsuarioDAO()" / "new ProdutoDAO()" que,
 * no projeto original, era repetido em cada JFrame. Assim, as telas Swing
 * (e, no futuro, os controllers web) apenas pedem o serviço já pronto,
 * sem conhecer os detalhes de montagem (qual implementação de DAO usar,
 * qual ConnectionFactory etc.).
 */
public final class ServiceFactory {

    private static final ConnectionFactory CONNECTION_FACTORY = new MySqlConnectionFactory();
    private static final UsuarioDAO USUARIO_DAO = new UsuarioDAOJdbc(CONNECTION_FACTORY);
    private static final ProdutoDAO PRODUTO_DAO = new ProdutoDAOJdbc(CONNECTION_FACTORY);

    private static final UsuarioService USUARIO_SERVICE = new UsuarioService(USUARIO_DAO);
    private static final ProdutoService PRODUTO_SERVICE = new ProdutoService(PRODUTO_DAO);
    private static final AutorizacaoService AUTORIZACAO_SERVICE = new AutorizacaoService();

    private ServiceFactory() {
    }

    public static UsuarioService usuarioService() {
        return USUARIO_SERVICE;
    }

    public static ProdutoService produtoService() {
        return PRODUTO_SERVICE;
    }

    public static AutorizacaoService autorizacaoService() {
        return AUTORIZACAO_SERVICE;
    }
}
