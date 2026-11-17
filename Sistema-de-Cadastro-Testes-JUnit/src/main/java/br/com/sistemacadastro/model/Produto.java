package br.com.sistemacadastro.model;

/**
 * Entidade que representa um produto do estoque.
 * Responsabilidade única (SRP): apenas dados, sem regra de negócio.
 */
public class Produto {

    private int id;
    private String nome;
    private String descricao;
    private int quantidade;
    private StatusProduto status;

    public Produto() {
    }

    public Produto(int id, String nome, String descricao, int quantidade, StatusProduto status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.status = status;
    }

    public Produto(String nome, String descricao, int quantidade, StatusProduto status) {
        this(0, nome, descricao, quantidade, status);
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public StatusProduto getStatus() {
        return status;
    }

    public void setStatus(StatusProduto status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', quantidade=" + quantidade + ", status=" + status + "}";
    }
}
