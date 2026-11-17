/*
 * Camada de dados SIMULADA do front-end (Etapa 8).
 *
 * Nesta etapa não há back-end: os dados ficam no localStorage do navegador
 * e a sessão no sessionStorage. As regras abaixo REPLICAM em JavaScript as
 * regras já implementadas e testadas em Java nas Etapas 6 e 7:
 *   - ProdutoService.calcularNivelEstoque / calcularQuantidadeAposVenda /
 *     definirStatusPorQuantidade / registrarVenda
 *   - UsuarioService e ProdutoService (validações de campos, login único)
 *   - AutorizacaoService (permissões por perfil)
 * Na Etapa 9 as funções públicas deste arquivo serão trocadas por chamadas
 * fetch() ao back-end Spring, mantendo a mesma interface para as telas.
 */
(function (global) {
  "use strict";

  var CHAVE_DADOS = "sc_dados_v1";
  var CHAVE_SESSAO = "sc_sessao_v1";
  var LIMITE_CRITICO = 3;   // até 3 = Crítico   (ProdutoService.LIMITE_CRITICO)
  var LIMITE_BAIXO = 10;    // até 10 = Baixo    (ProdutoService.LIMITE_BAIXO)

  var PERFIS = { ADMINISTRADOR: "Administrador", TECNICO: "Técnico", OPERADOR: "Operador" };
  var STATUS = { DISPONIVEL: "Disponível", VENDIDO: "Vendido" };
  var NIVEIS = { ESGOTADO: "Esgotado", CRITICO: "Crítico", BAIXO: "Baixo", NORMAL: "Normal" };

  /** Erro de validação com mensagens por campo (equivale à ValidacaoException). */
  function ValidacaoErro(mensagem, erros) {
    var e = new Error(mensagem);
    e.name = "ValidacaoErro";
    e.erros = erros || {};
    return e;
  }

  // ---------- armazenamento (com reserva em memória se o navegador bloquear) ----------
  var memoria = {};
  function ler(armazem, chave) {
    try { return armazem.getItem(chave); } catch (e) { return memoria[chave] || null; }
  }
  function gravar(armazem, chave, valor) {
    try { armazem.setItem(chave, valor); } catch (e) { memoria[chave] = valor; }
  }
  function remover(armazem, chave) {
    try { armazem.removeItem(chave); } catch (e) { delete memoria[chave]; }
  }

  function dadosIniciais() {
    return {
      proximoUsuario: 4,
      proximoProduto: 7,
      usuarios: [
        { id: 1, nome: "Administrador Geral", email: "admin@email.com", login: "admin", senha: "123", perfil: "ADMINISTRADOR" },
        { id: 2, nome: "Carlos Técnico", email: "tecnico@email.com", login: "tecnico", senha: "123", perfil: "TECNICO" },
        { id: 3, nome: "Ana Operadora", email: "operador@email.com", login: "operador", senha: "123", perfil: "OPERADOR" }
      ],
      produtos: [
        { id: 1, nome: "Celular", descricao: "Nokia", quantidade: 5, status: "DISPONIVEL" },
        { id: 2, nome: "Cafeteira", descricao: "Philco", quantidade: 3, status: "DISPONIVEL" },
        { id: 3, nome: "Máquina de serra", descricao: "Makita", quantidade: 2, status: "DISPONIVEL" },
        { id: 4, nome: "Fone de ouvido", descricao: "Bluetooth com cancelamento de ruído", quantidade: 24, status: "DISPONIVEL" },
        { id: 5, nome: "Liquidificador", descricao: "Arno 900 W", quantidade: 0, status: "VENDIDO" },
        { id: 6, nome: "Ventilador de mesa", descricao: "Mondial 30 cm", quantidade: 8, status: "DISPONIVEL" }
      ]
    };
  }

  function carregar() {
    var texto = ler(global.localStorage, CHAVE_DADOS);
    if (texto) {
      try { return JSON.parse(texto); } catch (e) { /* dados corrompidos: recria abaixo */ }
    }
    var inicial = dadosIniciais();
    salvar(inicial);
    return inicial;
  }
  function salvar(dados) { gravar(global.localStorage, CHAVE_DADOS, JSON.stringify(dados)); }
  function restaurarExemplo() { salvar(dadosIniciais()); }

  function copia(obj) { return JSON.parse(JSON.stringify(obj)); }

  // ---------- regras de estoque (espelham ProdutoService) ----------
  function calcularNivelEstoque(quantidade) {
    if (quantidade < 0) throw ValidacaoErro("A quantidade não pode ser negativa.", { quantidade: "A quantidade não pode ser negativa." });
    if (quantidade === 0) return "ESGOTADO";
    if (quantidade <= LIMITE_CRITICO) return "CRITICO";
    if (quantidade <= LIMITE_BAIXO) return "BAIXO";
    return "NORMAL";
  }

  function calcularQuantidadeAposVenda(quantidadeAtual, quantidadeVendida) {
    if (quantidadeAtual < 0) throw ValidacaoErro("A quantidade em estoque não pode ser negativa.");
    if (!(quantidadeVendida > 0)) throw ValidacaoErro("A quantidade vendida deve ser maior que zero.");
    if (quantidadeVendida > quantidadeAtual) throw ValidacaoErro("Estoque insuficiente para essa venda.");
    return quantidadeAtual - quantidadeVendida;
  }

  function definirStatusPorQuantidade(quantidade) {
    if (quantidade < 0) throw ValidacaoErro("A quantidade não pode ser negativa.");
    return quantidade === 0 ? "VENDIDO" : "DISPONIVEL";
  }

  /** Converte o texto digitado em inteiro; devolve NaN se não for um inteiro válido. */
  function lerQuantidade(texto) {
    var t = String(texto == null ? "" : texto).trim();
    if (!/^-?\d+$/.test(t)) return NaN;
    return parseInt(t, 10);
  }

  // ---------- validações (devolvem {campo: mensagem}; vazio = válido) ----------
  var REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  function validarUsuario(u, idAtual) {
    var erros = {};
    if (!String(u.nome || "").trim()) erros.nome = "Informe o nome.";
    if (!String(u.email || "").trim()) erros.email = "Informe o e-mail.";
    else if (!REGEX_EMAIL.test(String(u.email).trim())) erros.email = "Informe um e-mail válido (ex.: nome@email.com).";
    var login = String(u.login || "").trim();
    if (!login) erros.login = "Informe o login.";
    else if (/\s/.test(login)) erros.login = "O login não pode ter espaços.";
    else {
      var existente = carregar().usuarios.some(function (x) {
        return x.id !== idAtual && x.login.toLowerCase() === login.toLowerCase();
      });
      if (existente) erros.login = "Esse login já está em uso.";
    }
    if (idAtual == null && !String(u.senha || "")) erros.senha = "Informe a senha.";
    if (!PERFIS[u.perfil]) erros.perfil = "Selecione o perfil.";
    return erros;
  }

  function validarProduto(p) {
    var erros = {};
    if (!String(p.nome || "").trim()) erros.nome = "Informe o nome do produto.";
    if (!String(p.descricao || "").trim()) erros.descricao = "Informe a descrição.";
    var q = lerQuantidade(p.quantidade);
    if (String(p.quantidade == null ? "" : p.quantidade).trim() === "") erros.quantidade = "Informe a quantidade.";
    else if (isNaN(q)) erros.quantidade = "Informe um número inteiro (sem vírgula ou letras).";
    else if (q < 0) erros.quantidade = "A quantidade não pode ser negativa.";
    if (!STATUS[p.status]) erros.status = "Selecione o status.";
    return erros;
  }

  function exigirValido(erros) {
    var campos = Object.keys(erros);
    if (campos.length) throw ValidacaoErro(erros[campos[0]], erros);
  }

  // ---------- usuários ----------
  function listarUsuarios() {
    return copia(carregar().usuarios).sort(function (a, b) { return a.nome.localeCompare(b.nome, "pt-BR"); });
  }
  function buscarUsuario(id) {
    var u = carregar().usuarios.filter(function (x) { return x.id === id; })[0];
    return u ? copia(u) : null;
  }
  function salvarUsuario(entrada) {
    var dados = carregar();
    var id = entrada.id == null ? null : Number(entrada.id);
    exigirValido(validarUsuario(entrada, id));
    var registro = {
      nome: entrada.nome.trim(), email: entrada.email.trim(),
      login: entrada.login.trim(), perfil: entrada.perfil
    };
    if (id == null) {
      registro.id = dados.proximoUsuario++;
      registro.senha = entrada.senha;
      dados.usuarios.push(registro);
    } else {
      var atual = dados.usuarios.filter(function (x) { return x.id === id; })[0];
      if (!atual) throw ValidacaoErro("Usuário não encontrado.");
      registro.id = id;
      registro.senha = entrada.senha ? entrada.senha : atual.senha; // em branco = mantém a senha
      dados.usuarios[dados.usuarios.indexOf(atual)] = registro;
    }
    salvar(dados);
    return copia(registro);
  }
  function excluirUsuario(id) {
    var dados = carregar();
    var antes = dados.usuarios.length;
    dados.usuarios = dados.usuarios.filter(function (x) { return x.id !== id; });
    if (dados.usuarios.length === antes) throw ValidacaoErro("Usuário não encontrado.");
    salvar(dados);
  }

  // ---------- produtos ----------
  function listarProdutos() {
    return copia(carregar().produtos).sort(function (a, b) { return a.nome.localeCompare(b.nome, "pt-BR"); });
  }
  function buscarProduto(id) {
    var p = carregar().produtos.filter(function (x) { return x.id === id; })[0];
    return p ? copia(p) : null;
  }
  /** Busca por parte do nome, sem diferenciar maiúsculas nem acentos. */
  function buscarProdutosPorNome(texto) {
    var alvo = global.Util.normalizar(texto);
    return listarProdutos().filter(function (p) { return global.Util.normalizar(p.nome).indexOf(alvo) !== -1; });
  }
  function salvarProduto(entrada) {
    var dados = carregar();
    exigirValido(validarProduto(entrada));
    var id = entrada.id == null ? null : Number(entrada.id);
    var registro = {
      nome: entrada.nome.trim(), descricao: entrada.descricao.trim(),
      quantidade: lerQuantidade(entrada.quantidade), status: entrada.status
    };
    if (id == null) {
      registro.id = dados.proximoProduto++;
      dados.produtos.push(registro);
    } else {
      var atual = dados.produtos.filter(function (x) { return x.id === id; })[0];
      if (!atual) throw ValidacaoErro("Produto não encontrado.");
      registro.id = id;
      dados.produtos[dados.produtos.indexOf(atual)] = registro;
    }
    salvar(dados);
    return copia(registro);
  }
  function excluirProduto(id) {
    var dados = carregar();
    var antes = dados.produtos.length;
    dados.produtos = dados.produtos.filter(function (x) { return x.id !== id; });
    if (dados.produtos.length === antes) throw ValidacaoErro("Produto não encontrado.");
    salvar(dados);
  }
  /** Equivale a ProdutoService.registrarVenda. */
  function registrarVenda(id, quantidadeVendida) {
    var dados = carregar();
    var produto = dados.produtos.filter(function (x) { return x.id === id; })[0];
    if (!produto) throw ValidacaoErro("Produto não encontrado.");
    var restante = calcularQuantidadeAposVenda(produto.quantidade, quantidadeVendida);
    produto.quantidade = restante;
    produto.status = definirStatusPorQuantidade(restante);
    salvar(dados);
    return copia(produto);
  }

  // ---------- sessão e permissões (espelham UsuarioService.autenticar e AutorizacaoService) ----------
  function autenticar(login, senha) {
    if (!String(login || "").trim() || !String(senha || "")) throw ValidacaoErro("Informe o usuário e a senha.");
    var u = carregar().usuarios.filter(function (x) {
      return x.login.toLowerCase() === String(login).trim().toLowerCase() && x.senha === senha;
    })[0];
    if (!u) throw ValidacaoErro("Usuário ou senha inválidos.");
    var sessao = { id: u.id, nome: u.nome, login: u.login, perfil: u.perfil };
    gravar(global.sessionStorage, CHAVE_SESSAO, JSON.stringify(sessao));
    return sessao;
  }
  function sessaoAtual() {
    var t = ler(global.sessionStorage, CHAVE_SESSAO);
    if (!t) return null;
    try { return JSON.parse(t); } catch (e) { return null; }
  }
  function encerrarSessao() { remover(global.sessionStorage, CHAVE_SESSAO); }

  var PERMISSOES = {
    gerenciarUsuarios: function (p) { return p === "ADMINISTRADOR"; },
    gerenciarProdutos: function (p) { return p === "ADMINISTRADOR" || p === "TECNICO"; },
    consultarProdutos: function (p) { return !!PERFIS[p]; }
  };
  function pode(permissao, perfil) { return !!PERMISSOES[permissao] && PERMISSOES[permissao](perfil); }

  global.Dados = {
    PERFIS: PERFIS, STATUS: STATUS, NIVEIS: NIVEIS, LIMITE_CRITICO: LIMITE_CRITICO, LIMITE_BAIXO: LIMITE_BAIXO,
    calcularNivelEstoque: calcularNivelEstoque, calcularQuantidadeAposVenda: calcularQuantidadeAposVenda,
    definirStatusPorQuantidade: definirStatusPorQuantidade, lerQuantidade: lerQuantidade,
    validarUsuario: validarUsuario, validarProduto: validarProduto,
    listarUsuarios: listarUsuarios, buscarUsuario: buscarUsuario, salvarUsuario: salvarUsuario, excluirUsuario: excluirUsuario,
    listarProdutos: listarProdutos, buscarProduto: buscarProduto, buscarProdutosPorNome: buscarProdutosPorNome,
    salvarProduto: salvarProduto, excluirProduto: excluirProduto, registrarVenda: registrarVenda,
    autenticar: autenticar, sessaoAtual: sessaoAtual, encerrarSessao: encerrarSessao, pode: pode,
    restaurarExemplo: restaurarExemplo
  };
})(window);
