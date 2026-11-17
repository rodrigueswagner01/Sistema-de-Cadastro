/*
 * Monta a barra superior das telas internas e protege o acesso:
 *  - sem sessão -> volta para o login;
 *  - perfil sem permissão -> volta para o painel com um aviso.
 * Na Etapa 9 este trecho pode virar um fragmento Thymeleaf.
 */
(function (global) {
  "use strict";
  var CHAVE_AVISO = "sc_aviso_v1";

  var ITENS = [
    { href: "dashboard.html", rotulo: "Início", id: "dashboard", permissao: "consultarProdutos" },
    { href: "produtos.html", rotulo: "Produtos", id: "produtos", permissao: "gerenciarProdutos" },
    { href: "consulta.html", rotulo: "Consulta", id: "consulta", permissao: "consultarProdutos" },
    { href: "usuarios.html", rotulo: "Usuários", id: "usuarios", permissao: "gerenciarUsuarios" }
  ];

  /** Guarda uma mensagem para ser mostrada na próxima tela. */
  function avisarProximaTela(mensagem, tipo) {
    try { sessionStorage.setItem(CHAVE_AVISO, JSON.stringify({ mensagem: mensagem, tipo: tipo })); } catch (e) { /* sem aviso */ }
  }
  function mostrarAvisoPendente() {
    try {
      var t = sessionStorage.getItem(CHAVE_AVISO);
      if (!t) return;
      sessionStorage.removeItem(CHAVE_AVISO);
      var a = JSON.parse(t);
      global.Util.toast(a.mensagem, a.tipo);
    } catch (e) { /* ignora */ }
  }

  /** opcoes: { pagina: "produtos", permissao: "gerenciarProdutos" } — retorna a sessão. */
  function iniciar(opcoes) {
    var sessao = global.Dados.sessaoAtual();
    if (!sessao) { global.location.replace("index.html"); return null; }
    if (!global.Dados.pode(opcoes.permissao || "consultarProdutos", sessao.perfil)) {
      avisarProximaTela("Seu perfil (" + global.Dados.PERFIS[sessao.perfil] + ") não tem acesso a essa tela.", "erro");
      global.location.replace("dashboard.html");
      return null;
    }
    var E = global.Util.el;
    var menu = E("nav", { className: "menu", attrs: { "aria-label": "Principal" } });
    ITENS.forEach(function (item) {
      if (!global.Dados.pode(item.permissao, sessao.perfil)) return;
      var a = E("a", { text: item.rotulo, attrs: { href: item.href } });
      if (item.id === opcoes.pagina) a.setAttribute("aria-current", "page");
      menu.appendChild(a);
    });
    var sair = E("button", { className: "botao botao--fantasma", text: "Sair", attrs: { type: "button" },
      onclick: function () { global.Dados.encerrarSessao(); global.location.href = "index.html"; } });
    var topo = document.getElementById("topo");
    topo.className = "topo";
    global.Util.limpar(topo);
    topo.appendChild(E("div", { className: "topo__interno" }, [
      E("a", { className: "marca", attrs: { href: "dashboard.html" } }, [
        E("span", { className: "marca__icone", text: "▣", attrs: { "aria-hidden": "true" } }),
        "Sistema de Cadastro"
      ]),
      menu,
      E("div", { className: "usuario-topo" }, [
        E("div", { className: "usuario-topo__texto" }, [
          E("div", { className: "usuario-topo__nome", text: sessao.nome }),
          E("div", { className: "usuario-topo__perfil", text: global.Dados.PERFIS[sessao.perfil] })
        ]),
        sair
      ])
    ]));
    mostrarAvisoPendente();
    return sessao;
  }

  /** Cria a etiqueta colorida do nível de estoque. */
  function etiquetaNivel(nivel) {
    var cls = { ESGOTADO: "perigo", CRITICO: "critico", BAIXO: "atencao", NORMAL: "ok" }[nivel];
    return global.Util.el("span", { className: "etiqueta etiqueta--" + cls, text: global.Dados.NIVEIS[nivel] });
  }
  function etiquetaStatus(status) {
    return global.Util.el("span", { className: "etiqueta etiqueta--" + (status === "DISPONIVEL" ? "info" : "neutro"), text: global.Dados.STATUS[status] });
  }

  global.Layout = { iniciar: iniciar, avisarProximaTela: avisarProximaTela, etiquetaNivel: etiquetaNivel, etiquetaStatus: etiquetaStatus };
})(window);
