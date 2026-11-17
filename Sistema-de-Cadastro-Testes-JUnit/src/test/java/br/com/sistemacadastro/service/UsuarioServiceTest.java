package br.com.sistemacadastro.service;

import br.com.sistemacadastro.exception.AutenticacaoException;
import br.com.sistemacadastro.exception.RegistroNaoEncontradoException;
import br.com.sistemacadastro.exception.ValidacaoException;
import br.com.sistemacadastro.model.PerfilUsuario;
import br.com.sistemacadastro.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Testes unitários (JUnit 5) de UsuarioService com o UsuarioDAOFake (memória). */
class UsuarioServiceTest {

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(new UsuarioDAOFake());
    }

    private Usuario novoUsuario(String login) {
        return new Usuario("Maria Silva", "maria@email.com", PerfilUsuario.OPERADOR, login, "123");
    }

    @Test
    @DisplayName("Cadastro válido deve gerar id")
    void cadastroValidoGeraId() {
        assertTrue(service.cadastrar(novoUsuario("maria")).getId() > 0);
    }

    @Test
    @DisplayName("Cadastro com nome vazio deve lançar ValidacaoException")
    void cadastroComNomeVazioDeveFalhar() {
        Usuario usuario = novoUsuario("maria");
        usuario.setNome("");
        assertThrows(ValidacaoException.class, () -> service.cadastrar(usuario));
    }

    @Test
    @DisplayName("Cadastro sem perfil deve lançar ValidacaoException")
    void cadastroSemPerfilDeveFalhar() {
        Usuario usuario = novoUsuario("maria");
        usuario.setPerfil(null);
        assertThrows(ValidacaoException.class, () -> service.cadastrar(usuario));
    }

    @Test
    @DisplayName("Login duplicado não é permitido")
    void loginDuplicadoDeveFalhar() {
        service.cadastrar(novoUsuario("duplicado"));
        assertThrows(ValidacaoException.class, () -> service.cadastrar(novoUsuario("duplicado")));
    }

    @Test
    @DisplayName("Autenticação com credenciais corretas retorna o usuário")
    void autenticacaoCorretaRetornaUsuario() {
        service.cadastrar(novoUsuario("maria"));
        Usuario autenticado = service.autenticar("maria", "123");
        assertEquals("maria", autenticado.getLogin());
        assertEquals(PerfilUsuario.OPERADOR, autenticado.getPerfil());
    }

    @Test
    @DisplayName("Autenticação com senha incorreta deve lançar AutenticacaoException")
    void autenticacaoComSenhaErradaDeveFalhar() {
        service.cadastrar(novoUsuario("maria"));
        assertThrows(AutenticacaoException.class, () -> service.autenticar("maria", "errada"));
    }

    @Test
    @DisplayName("Autenticação com campos vazios deve lançar ValidacaoException")
    void autenticacaoComCamposVaziosDeveFalhar() {
        assertThrows(ValidacaoException.class, () -> service.autenticar("", ""));
        assertThrows(ValidacaoException.class, () -> service.autenticar(null, "123"));
    }

    @Test
    @DisplayName("Atualização de usuário inexistente deve lançar RegistroNaoEncontradoException")
    void atualizacaoDeInexistenteDeveFalhar() {
        Usuario fantasma = novoUsuario("fantasma");
        fantasma.setId(999);
        assertThrows(RegistroNaoEncontradoException.class, () -> service.atualizar(fantasma));
    }

    @Test
    @DisplayName("Atualização mantendo o próprio login é permitida")
    void atualizacaoMantendoLoginEPermitida() {
        Usuario salvo = service.cadastrar(novoUsuario("maria"));
        salvo.setNome("Maria Souza");
        assertDoesNotThrow(() -> service.atualizar(salvo));
        assertEquals("Maria Souza", service.buscarPorIdOuFalhar(salvo.getId()).getNome());
    }

    @Test
    @DisplayName("Exclusão remove o usuário da listagem")
    void exclusaoRemoveDaListagem() {
        Usuario salvo = service.cadastrar(novoUsuario("maria"));
        service.excluir(salvo.getId());
        assertTrue(service.listarTodos().isEmpty());
    }
}
