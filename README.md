# Sistema de Cadastro — Etapa 6 (Refatoração)

## Status do Projeto
Refatorado — preparado para reutilização em uma futura camada web (Etapas 7 a 9).

## Tecnologias Aplicadas
- Java 21 (NetBeans, projeto Ant)
- Java Swing (somente na camada `view`)
- MySQL + JDBC (`mysql-connector-j-9.6.0`)
- Git e GitHub

## Autor
- Wagner Rodrigues

## Objetivo do Software
Auxiliar na organização e no controle de produtos em estoque, com login de usuários e controle de acesso por perfil.

## Funcionalidades
- Login e controle de acesso por perfil (Administrador, Técnico, Operador)
- Cadastro, edição e exclusão de usuários
- Cadastro, edição e exclusão de produtos
- Consulta de produtos e busca por nome

## Arquitetura (camadas)
```
br.com.sistemacadastro
 ├── model       Usuario, Produto, PerfilUsuario (enum), StatusProduto (enum)
 ├── dao         CrudDAO, UsuarioDAO, ProdutoDAO (interfaces) + UsuarioDAOJdbc, ProdutoDAOJdbc
 ├── service     UsuarioService, ProdutoService, AutorizacaoService (regras de negócio, sem Swing)
 ├── exception   ValidacaoException, AutenticacaoException, RegistroNaoEncontradoException
 ├── util        ConnectionFactory, MySqlConnectionFactory, ServiceFactory
 ├── view        LoginFrame, MenuPrincipalFrame, UsuarioFrame, ProdutosFrame, ConsultaProdutosFrame
 ├── teste       TesteServicos + DAOs falsos em memória (testes executados pelo main)
 └── main        Main
```
Somente o pacote `view` depende de Java Swing. Os demais pacotes (`model`, `dao`, `service`, `exception`, `util`) podem ser reaproveitados no projeto web (Spring) das próximas etapas.

## Como executar
1. Execute o script `UC_15_Etapa6.sql` no MySQL.
2. Abra a pasta do projeto no NetBeans (File > Open Project).
3. Confira se a biblioteca `lib/mysql-connector-j-9.6.0.jar` está no classpath (já configurada).
4. A conexão usa `jdbc:mysql://localhost:3306/SistemadeCadastro` com usuário `root`. Se precisar mudar, defina as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`.
5. Execute o projeto (F6). O `main()` roda primeiro os testes automáticos e depois abre a tela de login.

Para rodar **somente os testes** (sem abrir a tela), use o argumento `--somente-testes` (Propriedades do projeto > Run > Arguments).

Usuários de exemplo (do script SQL): `admin`, `tecnico` e `operador`, todos com senha `123`.

## Observações
Projeto desenvolvido com auxílio de ferramentas digitais de pesquisa. Arquitetura em camadas baseada em um projeto de referência aprovado, adaptada ao domínio do Sistema de Cadastro.
