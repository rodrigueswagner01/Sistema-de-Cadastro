package br.com.sistemacadastro.service;

import br.com.sistemacadastro.exception.RegistroNaoEncontradoException;
import br.com.sistemacadastro.exception.ValidacaoException;
import br.com.sistemacadastro.model.NivelEstoque;
import br.com.sistemacadastro.model.Produto;
import br.com.sistemacadastro.model.StatusProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários (JUnit 5) das regras de ProdutoService.
 *
 * Foco principal da Etapa 7: os métodos de cálculo puro, sem banco de dados
 * (calcularNivelEstoque, calcularQuantidadeAposVenda e
 * definirStatusPorQuantidade). Também cobrem registrarVenda e o cadastro,
 * usando o ProdutoDAOFake (memória) no lugar do DAO JDBC.
 */
class ProdutoServiceTest {

    private ProdutoService service;

    @BeforeEach
    void setUp() {
        service = new ProdutoService(new ProdutoDAOFake());
    }

    // ---------- calcularNivelEstoque ----------

    @Test
    @DisplayName("Quantidade zero deve resultar em ESGOTADO")
    void quantidadeZeroDeveSerEsgotado() {
        assertEquals(NivelEstoque.ESGOTADO, service.calcularNivelEstoque(0));
    }

    @ParameterizedTest(name = "quantidade={0} -> CRITICO")
    @CsvSource({"1", "2", "3"})
    @DisplayName("Quantidades de 1 a 3 devem resultar em CRITICO")
    void quantidadesDeUmATresSaoCriticas(int quantidade) {
        assertEquals(NivelEstoque.CRITICO, service.calcularNivelEstoque(quantidade));
    }

    @ParameterizedTest(name = "quantidade={0} -> BAIXO")
    @CsvSource({"4", "7", "10"})
    @DisplayName("Quantidades de 4 a 10 devem resultar em BAIXO")
    void quantidadesDeQuatroADezSaoBaixas(int quantidade) {
        assertEquals(NivelEstoque.BAIXO, service.calcularNivelEstoque(quantidade));
    }

    @ParameterizedTest(name = "quantidade={0} -> NORMAL")
    @CsvSource({"11", "50", "1000"})
    @DisplayName("Quantidades a partir de 11 devem resultar em NORMAL")
    void quantidadesAPartirDeOnzeSaoNormais(int quantidade) {
        assertEquals(NivelEstoque.NORMAL, service.calcularNivelEstoque(quantidade));
    }

    @Test
    @DisplayName("Quantidade negativa no cálculo de nível deve lançar ValidacaoException")
    void quantidadeNegativaNoNivelDeveFalhar() {
        ValidacaoException erro = assertThrows(ValidacaoException.class, () -> service.calcularNivelEstoque(-1));
        assertTrue(erro.getMessage().toLowerCase().contains("negativa"));
    }

    // ---------- calcularQuantidadeAposVenda ----------

    @Test
    @DisplayName("Venda parcial deve subtrair do estoque")
    void vendaParcialSubtraiDoEstoque() {
        assertEquals(6, service.calcularQuantidadeAposVenda(10, 4));
    }

    @Test
    @DisplayName("Venda de todo o estoque deve resultar em zero")
    void vendaTotalResultaEmZero() {
        assertEquals(0, service.calcularQuantidadeAposVenda(5, 5));
    }

    @Test
    @DisplayName("Venda maior que o estoque deve lançar ValidacaoException")
    void vendaMaiorQueEstoqueDeveFalhar() {
        assertThrows(ValidacaoException.class, () -> service.calcularQuantidadeAposVenda(3, 4));
    }

    @ParameterizedTest(name = "vendida={0}")
    @CsvSource({"0", "-1", "-10"})
    @DisplayName("Quantidade vendida zero ou negativa deve lançar ValidacaoException")
    void quantidadeVendidaInvalidaDeveFalhar(int vendida) {
        assertThrows(ValidacaoException.class, () -> service.calcularQuantidadeAposVenda(10, vendida));
    }

    @Test
    @DisplayName("Estoque atual negativo deve lançar ValidacaoException")
    void estoqueAtualNegativoDeveFalhar() {
        assertThrows(ValidacaoException.class, () -> service.calcularQuantidadeAposVenda(-2, 1));
    }

    // ---------- definirStatusPorQuantidade ----------

    @Test
    @DisplayName("Quantidade zero deve gerar status VENDIDO")
    void quantidadeZeroGeraVendido() {
        assertEquals(StatusProduto.VENDIDO, service.definirStatusPorQuantidade(0));
    }

    @Test
    @DisplayName("Quantidade maior que zero deve gerar status DISPONIVEL")
    void quantidadePositivaGeraDisponivel() {
        assertEquals(StatusProduto.DISPONIVEL, service.definirStatusPorQuantidade(1));
        assertEquals(StatusProduto.DISPONIVEL, service.definirStatusPorQuantidade(99));
    }

    @Test
    @DisplayName("Quantidade negativa ao definir status deve lançar ValidacaoException")
    void quantidadeNegativaAoDefinirStatusDeveFalhar() {
        assertThrows(ValidacaoException.class, () -> service.definirStatusPorQuantidade(-1));
    }

    // ---------- registrarVenda (usa o DAO fake) ----------

    @Test
    @DisplayName("Venda parcial atualiza a quantidade e mantém o produto DISPONIVEL")
    void vendaParcialMantemDisponivel() {
        Produto salvo = service.cadastrar(new Produto("Celular", "Nokia", 5, StatusProduto.DISPONIVEL));

        Produto atualizado = service.registrarVenda(salvo.getId(), 2);

        assertEquals(3, atualizado.getQuantidade());
        assertEquals(StatusProduto.DISPONIVEL, atualizado.getStatus());
        assertEquals(3, service.buscarPorIdOuFalhar(salvo.getId()).getQuantidade());
    }

    @Test
    @DisplayName("Venda de todo o estoque muda o status para VENDIDO")
    void vendaTotalMudaParaVendido() {
        Produto salvo = service.cadastrar(new Produto("Cafeteira", "Philco", 2, StatusProduto.DISPONIVEL));

        Produto atualizado = service.registrarVenda(salvo.getId(), 2);

        assertEquals(0, atualizado.getQuantidade());
        assertEquals(StatusProduto.VENDIDO, atualizado.getStatus());
    }

    @Test
    @DisplayName("Venda acima do estoque não altera o produto")
    void vendaAcimaDoEstoqueNaoAlteraProduto() {
        Produto salvo = service.cadastrar(new Produto("Serra", "Makita", 2, StatusProduto.DISPONIVEL));

        assertThrows(ValidacaoException.class, () -> service.registrarVenda(salvo.getId(), 5));
        assertEquals(2, service.buscarPorIdOuFalhar(salvo.getId()).getQuantidade());
    }

    @Test
    @DisplayName("Venda de produto inexistente deve lançar RegistroNaoEncontradoException")
    void vendaDeProdutoInexistenteDeveFalhar() {
        assertThrows(RegistroNaoEncontradoException.class, () -> service.registrarVenda(999, 1));
    }

    // ---------- cadastro e consulta (regras já existentes) ----------

    @Test
    @DisplayName("Cadastro válido deve gerar id")
    void cadastroValidoGeraId() {
        Produto salvo = service.cadastrar(new Produto("Balança", "Cozinha", 4, StatusProduto.DISPONIVEL));
        assertTrue(salvo.getId() > 0);
    }

    @Test
    @DisplayName("Cadastro com quantidade negativa deve falhar")
    void cadastroComQuantidadeNegativaDeveFalhar() {
        Produto invalido = new Produto("Balança", "Cozinha", -1, StatusProduto.DISPONIVEL);
        assertThrows(ValidacaoException.class, () -> service.cadastrar(invalido));
    }

    @Test
    @DisplayName("Cadastro com nome em branco deve falhar")
    void cadastroComNomeEmBrancoDeveFalhar() {
        Produto invalido = new Produto("   ", "Cozinha", 1, StatusProduto.DISPONIVEL);
        assertThrows(ValidacaoException.class, () -> service.cadastrar(invalido));
    }

    @Test
    @DisplayName("Busca por nome ignora maiúsculas e aceita parte do nome")
    void buscaPorNomeParcial() {
        service.cadastrar(new Produto("Cafeteira", "Philco", 3, StatusProduto.DISPONIVEL));
        service.cadastrar(new Produto("Celular", "Nokia", 5, StatusProduto.DISPONIVEL));

        assertEquals(1, service.buscarPorNome("CAFE").size());
        assertEquals(2, service.buscarPorNome("").size());
        assertTrue(service.buscarPorNome("xyz").isEmpty());
    }

    @Test
    @DisplayName("Exclusão de produto inexistente deve lançar RegistroNaoEncontradoException")
    void exclusaoDeInexistenteDeveFalhar() {
        assertThrows(RegistroNaoEncontradoException.class, () -> service.excluir(123));
    }
}
