# Sistema de Cadastro — Front-end (Etapa 8)

Front-end estático (HTML + CSS + JavaScript) do Sistema de Cadastro, desenvolvido na **Etapa 8** do Projeto Integrador (Técnico em Desenvolvimento de Sistemas — Senac), a partir dos wireframes desta pasta e das regras de negócio já implementadas e testadas nas Etapas 6 e 7.

Como pede o enunciado, esta etapa cobre **somente o front-end**: não há conexão com banco. Os dados são simulados no navegador (`localStorage`) e a sessão fica em `sessionStorage`.

## Como abrir
Use um servidor local simples, que funciona em qualquer navegador (inclusive Firefox):

1. Abra o terminal **dentro da pasta `Sistema-de-Cadastro-Frontend`**.
2. Rode: `python3 -m http.server 8000` (no Windows, `python -m http.server 8000`).
3. Abra **http://localhost:8000/pages/index.html** no navegador.
4. Para encerrar o servidor, volte ao terminal e tecle `Ctrl + C`.

Por que não abrir o arquivo com duplo clique? O login e os dados ficam guardados no navegador, e alguns navegadores (como o Firefox) tratam cada arquivo aberto direto do disco como um "site" separado, então as páginas não compartilham o login. Pelo servidor local isso não acontece. Alternativa: extensão *Live Server* do VS Code.

Usuários de exemplo (mesmos do script `UC_15_Etapa6.sql`; senha **123**):

| Login | Perfil | O que enxerga |
|---|---|---|
| `admin` | Administrador | Início, Produtos, Consulta e Usuários |
| `tecnico` | Técnico | Início, Produtos e Consulta |
| `operador` | Operador | Início e Consulta |

A tela de login tem atalhos que preenchem esses usuários. Na tela inicial há o botão **Restaurar dados de exemplo**.

## Telas (7) e wireframes (9)
| Tela | Arquivo | Wireframe |
|---|---|---|
| Login | `pages/index.html` | `01-login.svg` |
| Início (painel) | `pages/dashboard.html` | `02-dashboard.svg` |
| Usuários (lista) | `pages/usuarios.html` | `03-usuarios-lista.svg` |
| Usuário (cadastro/edição) | `pages/usuario-form.html` | `04-usuario-form.svg` |
| Produtos (lista) | `pages/produtos.html` | `05-produtos-lista.svg` |
| Produto (cadastro/edição) | `pages/produto-form.html` | `06-produto-form.svg` |
| Consulta de produtos | `pages/consulta.html` | `07-consulta.svg` |
| Diálogo “Registrar venda” (dentro de Produtos) | — | `08-venda-dialogo.svg` |
| Produtos no celular | — | `09-mobile-produtos.svg` |

Os wireframes estão em `wireframes/` (SVG editável, e PNG em `wireframes/png/`). Para levá-los ao **Figma**: abra um arquivo, arraste os SVG para o canvas e cada um vira um quadro editável. A pasta `capturas/` tem prints do protótipo funcionando.

## Estrutura
```
Sistema-de-Cadastro-Frontend/
├── pages/    index, dashboard, usuarios, usuario-form, produtos, produto-form, consulta (.html)
├── css/      variables, base, layout, login, tabelas, formularios, dashboard (.css)
├── js/       util, dados, layout, login, dashboard, usuarios, usuario-form, produtos, produto-form, consulta (.js)
├── wireframes/   SVG + png/
└── capturas/     prints do protótipo
```
CSS e JavaScript ficam em arquivos próprios, separados do HTML.

## Validações e funções dinâmicas (JavaScript)
- **Login:** campos obrigatórios e mensagem para credenciais inválidas; botão mostrar/ocultar senha; atalhos de usuários de exemplo.
- **Controle de acesso:** o menu muda conforme o perfil e as páginas barram quem não tem permissão (equivale ao `AutorizacaoService`).
- **Usuário:** nome, e-mail (formato), login (sem espaços e único), senha (obrigatória no cadastro) e perfil. Os erros aparecem sob cada campo e o foco vai ao primeiro campo inválido.
- **Produto:** nome e descrição obrigatórios, quantidade inteira maior ou igual a zero e status obrigatório.
- **Nível de estoque em tempo real** ao digitar a quantidade e **status automático** (quantidade 0 vira Vendido) — espelham `calcularNivelEstoque` e `definirStatusPorQuantidade`.
- **Registrar venda:** diálogo que mostra, enquanto se digita, quanto restará, o nível e o status — espelha `calcularQuantidadeAposVenda` e `registrarVenda`.
- **Busca** por nome (sem diferenciar maiúsculas e acentos) e **filtro por nível**, atualizados enquanto se digita.
- **Painel:** indicadores, barra de distribuição por nível e lista de produtos em atenção, calculados com os dados atuais.
- Confirmação antes de excluir; avisos (toast) após cada operação.

## Regras replicadas do back-end (Etapas 6 e 7)
| Nível | Quantidade |
|---|---|
| Esgotado | 0 |
| Crítico | 1 a 3 |
| Baixo | 4 a 10 |
| Normal | 11 ou mais |

## Diferenças em relação ao desktop (decisões para a web)
- A tela de usuários **não exibe a senha** (o desktop mostrava). Na edição, deixar a senha em branco mantém a atual.
- O usuário logado não pode excluir a si mesmo.
- O e-mail passa a ter validação de formato.
- Estas diferenças são do front-end; para valer no sistema real, o back-end da Etapa 9 precisará aplicá-las também.

## Layout
- Paleta azul-tinta e coral sobre fundo areia; verde, âmbar e vermelho são usados apenas para níveis de estoque e erros.
- Tipografia: Sora (títulos) e DM Sans (texto), carregadas do Google Fonts, com fontes do sistema como reserva se estiver sem internet.
- Barra de navegação no topo; em telas estreitas (até 760 px) o menu rola na horizontal e as tabelas viram cartões empilhados.
- Acessibilidade: rótulos em todos os campos, mensagens de erro ligadas aos campos, foco visível, diálogos nativos e respeito a “reduzir movimento”.

## Testes realizados
Um roteiro automático no navegador Chromium percorreu login por perfil, bloqueio de páginas, validações, nível em tempo real, venda, edição, exclusão, busca, filtro, cadastro de usuário e a tela no celular (41 verificações, todas aprovadas, sem erros de JavaScript no console). Isso não substitui o teste manual no seu navegador.

## Preparação para a Etapa 9
- `js/dados.js` concentra toda a “camada de dados” simulada. Na Etapa 9, suas funções (`listarProdutos`, `salvarProduto`, `registrarVenda`, `autenticar` etc.) serão trocadas por chamadas `fetch()` a endpoints do Spring, sem mudar as telas.
- Cada página HTML pode virar um template Thymeleaf; a barra do topo, montada por `layout.js`, pode virar um fragmento.
- As regras de nível e venda em JavaScript existem só para a demonstração; com o back-end, o cálculo virá dele.
