package br.com.sistemacadastro.service;

import br.com.sistemacadastro.dao.UsuarioDAO;
import br.com.sistemacadastro.exception.AutenticacaoException;
import br.com.sistemacadastro.exception.RegistroNaoEncontradoException;
import br.com.sistemacadastro.exception.ValidacaoException;
import br.com.sistemacadastro.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio relacionadas a Usuario.
 *
 * Esta classe concentra todas as validações que, no projeto desktop
 * original, estavam misturadas ao código de interface (ex.: o método
 * btnAdicionarActionPerformed em view.UsuarioFrame validava campos
 * vazios diretamente antes de chamar o DAO). Isolar essas regras aqui
 * segue o Single Responsibility Principle: a classe muda apenas quando
 * uma regra de negócio muda, nunca por causa de um ajuste visual na
 * tela. É também o que permite que esta mesma classe seja reaproveitada
 * por um futuro controller REST, sem reescrever nenhuma validação.
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario cadastrar(Usuario usuario) {
        validarCamposObrigatorios(usuario);
        validarLoginDisponivel(usuario.getLogin(), null);
        return usuarioDAO.salvar(usuario);
    }

    public void atualizar(Usuario usuario) {
        validarCamposObrigatorios(usuario);
        buscarPorIdOuFalhar(usuario.getId());
        validarLoginDisponivel(usuario.getLogin(), usuario.getId());
        usuarioDAO.atualizar(usuario);
    }

    public void excluir(int id) {
        buscarPorIdOuFalhar(id);
        usuarioDAO.excluir(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorIdOuFalhar(int id) {
        return usuarioDAO.buscarPorId(id)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado (id=" + id + ")."));
    }

    /**
     * Autentica um usuário pelo par login/senha.
     * Substitui o retorno "null em caso de falha" do UsuarioDAO original
     * (que obrigava a view a checar null a cada chamada) por uma
     * exceção de domínio explícita.
     */
    public Usuario autenticar(String login, String senha) {
        if (isVazio(login) || isVazio(senha)) {
            throw new ValidacaoException("Informe usuário e senha.");
        }

        Optional<Usuario> usuario = usuarioDAO.autenticar(login, senha);
        return usuario.orElseThrow(() -> new AutenticacaoException("Usuário ou senha inválidos."));
    }

    private void validarCamposObrigatorios(Usuario usuario) {
        if (usuario == null) {
            throw new ValidacaoException("Usuário não pode ser nulo.");
        }
        if (isVazio(usuario.getNome())) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        if (isVazio(usuario.getEmail())) {
            throw new ValidacaoException("O e-mail é obrigatório.");
        }
        if (isVazio(usuario.getLogin())) {
            throw new ValidacaoException("O login é obrigatório.");
        }
        if (isVazio(usuario.getSenha())) {
            throw new ValidacaoException("A senha é obrigatória.");
        }
        if (usuario.getPerfil() == null) {
            throw new ValidacaoException("O perfil é obrigatório.");
        }
    }

    /**
     * Garante que o login é único. No sistema original essa checagem não
     * existia no código Java (apenas a constraint UNIQUE do banco), o
     * que fazia o usuário só descobrir o problema pela SQLException
     * impressa no console — um code smell de tratamento de erro.
     */
    private void validarLoginDisponivel(String login, Integer idIgnorado) {
        Optional<Usuario> existente = usuarioDAO.buscarPorLogin(login);
        boolean loginEmUsoPorOutroUsuario = existente.isPresent()
            && (idIgnorado == null || existente.get().getId() != idIgnorado);

        if (loginEmUsoPorOutroUsuario) {
            throw new ValidacaoException("Já existe um usuário com o login '" + login + "'.");
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
