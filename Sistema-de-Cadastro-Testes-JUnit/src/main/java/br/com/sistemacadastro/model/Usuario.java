package br.com.sistemacadastro.model;

/**
 * Entidade que representa um usuário do sistema.
 *
 * Responsabilidade única (SRP): apenas armazenar e expor os dados de um
 * usuário. Nenhuma regra de negócio, validação ou acesso a dados deve
 * existir nesta classe — isso fica a cargo de UsuarioService e UsuarioDAO.
 */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private String login;
    private String senha;

    public Usuario() {
    }

    public Usuario(int id, String nome, String email, PerfilUsuario perfil, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.login = login;
        this.senha = senha;
    }

    public Usuario(String nome, String email, PerfilUsuario perfil, String login, String senha) {
        this(0, nome, email, perfil, login, senha);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', login='" + login + "', perfil=" + perfil + "}";
    }
}
