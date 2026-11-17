# Sistema de Cadastro — Testes JUnit (Etapa 7)

Projeto de testes unitários com **JUnit 5**, criado na **Etapa 7** do Projeto Integrador (Técnico em Desenvolvimento de Sistemas – Senac) a partir do projeto refatorado na Etapa 6.

## O que foi feito
1. **Novas regras de cálculo** em `ProdutoService`, em métodos próprios (responsabilidade única) e sem acesso a banco:
   - `calcularNivelEstoque(int)` → `NivelEstoque`: 0 = ESGOTADO; 1 a 3 = CRITICO; 4 a 10 = BAIXO; 11 ou mais = NORMAL.
   - `calcularQuantidadeAposVenda(int, int)` → quantidade restante, validando venda maior que zero e não superior ao estoque.
   - `definirStatusPorQuantidade(int)` → VENDIDO quando a quantidade é zero; DISPONIVEL nos demais casos.
   - `registrarVenda(int, int)` → junta as regras acima e grava a alteração pelo DAO.
2. **43 métodos de teste** (57 execuções, contando as parametrizadas) em 4 classes:
   `ProdutoServiceTest`, `UsuarioServiceTest`, `AutorizacaoServiceTest` e `EnumsTest`.
3. **DAOs falsos em memória** (`ProdutoDAOFake`, `UsuarioDAOFake`) em `src/test`, para que nenhum teste dependa de MySQL.

## Estrutura
```
Sistema-de-Cadastro-Testes-JUnit/
├── pom.xml
├── src/main/java/br/com/sistemacadastro/
│   ├── model/      Usuario, Produto, PerfilUsuario, StatusProduto, NivelEstoque
│   ├── exception/  ValidacaoException, AutenticacaoException, RegistroNaoEncontradoException
│   ├── dao/        CrudDAO, UsuarioDAO, ProdutoDAO (interfaces)
│   └── service/    UsuarioService, ProdutoService, AutorizacaoService
└── src/test/java/br/com/sistemacadastro/
    ├── service/    ProdutoServiceTest, UsuarioServiceTest, AutorizacaoServiceTest, UsuarioDAOFake, ProdutoDAOFake
    └── model/      EnumsTest
```
As classes de `src/main` são cópias das classes de negócio do projeto da Etapa 6 (sem telas Swing e sem JDBC). `ProdutoService.java` e `NivelEstoque.java` também foram atualizados no projeto principal (pasta `atualizacao_projeto_principal` da entrega), para os dois projetos ficarem iguais.

## Como executar
- **NetBeans:** File > Open Project, escolha esta pasta; botão direito no projeto > **Test** (ou Run > Test Project). Na primeira vez, o Maven baixa o JUnit da internet.
- **Terminal:** `mvn test`

## Resultado esperado
`Tests run: 57, Failures: 0, Errors: 0, Skipped: 0`
