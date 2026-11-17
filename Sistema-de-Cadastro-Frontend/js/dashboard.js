/* Painel inicial: indicadores, distribuição por nível de estoque e itens em atenção. */
(function () {
  "use strict";
  var sessao = Layout.iniciar({ pagina: "dashboard", permissao: "consultarProdutos" });
  if (!sessao) return;
  var E = Util.el;

  var nomeCurto = sessao.nome.split(" ")[0];
  document.getElementById("saudacao").textContent = "Olá, " + nomeCurto;

  var produtos = Dados.listarProdutos();
  var usuarios = Dados.pode("gerenciarUsuarios", sessao.perfil) ? Dados.listarUsuarios() : null;

  var contagem = { ESGOTADO: 0, CRITICO: 0, BAIXO: 0, NORMAL: 0 };
  var totalItens = 0;
  produtos.forEach(function (p) {
    contagem[Dados.calcularNivelEstoque(p.quantidade)]++;
    totalItens += p.quantidade;
  });
  var emAtencao = contagem.ESGOTADO + contagem.CRITICO;

  // ----- indicadores -----
  var area = document.getElementById("indicadores");
  function indicador(rotulo, valor, nota, alerta) {
    return E("article", { className: "cartao indicador" + (alerta ? " indicador--alerta" : "") }, [
      E("div", { className: "indicador__rotulo", text: rotulo }),
      E("div", { className: "indicador__valor", text: String(valor) }),
      E("div", { className: "indicador__nota", text: nota })
    ]);
  }
  area.appendChild(indicador("Produtos cadastrados", produtos.length, "tipos de produto no estoque"));
  area.appendChild(indicador("Itens em estoque", totalItens, "soma das quantidades"));
  area.appendChild(indicador("Precisam de atenção", emAtencao, "esgotados ou em nível crítico", emAtencao > 0));
  if (usuarios) area.appendChild(indicador("Usuários", usuarios.length, "com acesso ao sistema"));

  // ----- distribuição por nível -----
  var cores = { ESGOTADO: "var(--perigo)", CRITICO: "var(--coral)", BAIXO: "#d9a21b", NORMAL: "var(--ok)" };
  var barra = document.getElementById("barra-niveis");
  var legenda = document.getElementById("legenda-niveis");
  var resumo = [];
  Object.keys(contagem).forEach(function (nivel) {
    var n = contagem[nivel];
    resumo.push(n + " " + Dados.NIVEIS[nivel].toLowerCase());
    if (n > 0 && produtos.length) {
      var parte = E("div", { className: "barra-niveis__parte", attrs: { title: Dados.NIVEIS[nivel] + ": " + n } });
      parte.style.width = (n / produtos.length * 100) + "%";
      parte.style.background = cores[nivel];
      barra.appendChild(parte);
    }
    var cor = E("span", { className: "legenda__cor", attrs: { "aria-hidden": "true" } });
    cor.style.background = cores[nivel];
    legenda.appendChild(E("li", {}, [cor, Dados.NIVEIS[nivel], E("strong", { text: String(n) })]));
  });
  barra.setAttribute("aria-label", "Distribuição dos produtos por nível de estoque: " + resumo.join(", "));

  // ----- lista de atenção -----
  var lista = document.getElementById("lista-atencao");
  var atencao = produtos.filter(function (p) { return p.quantidade <= Dados.LIMITE_CRITICO; })
    .sort(function (a, b) { return a.quantidade - b.quantidade; }).slice(0, 5);
  if (!atencao.length) {
    lista.appendChild(E("li", { text: "Nenhum produto em nível crítico. Estoque saudável." }));
  }
  atencao.forEach(function (p) {
    var nome = Dados.pode("gerenciarProdutos", sessao.perfil)
      ? E("a", { text: p.nome, attrs: { href: "produto-form.html?id=" + p.id } })
      : E("strong", { text: p.nome });
    lista.appendChild(E("li", {}, [
      E("div", {}, [nome, E("small", { text: p.quantidade + (p.quantidade === 1 ? " unidade" : " unidades") + " · " + p.descricao })]),
      Layout.etiquetaNivel(Dados.calcularNivelEstoque(p.quantidade))
    ]));
  });

  // ----- atalhos conforme o perfil -----
  var atalhos = document.getElementById("atalhos");
  if (Dados.pode("gerenciarProdutos", sessao.perfil)) atalhos.appendChild(E("a", { className: "botao botao--primario", text: "+ Novo produto", attrs: { href: "produto-form.html" } }));
  atalhos.appendChild(E("a", { className: "botao botao--secundario", text: "Consultar produtos", attrs: { href: "consulta.html" } }));
  if (usuarios) atalhos.appendChild(E("a", { className: "botao botao--secundario", text: "Gerenciar usuários", attrs: { href: "usuarios.html" } }));

  // ----- restaurar dados de exemplo -----
  document.getElementById("restaurar").addEventListener("click", function () {
    Util.confirmar("Restaurar dados de exemplo?", "Todos os produtos e usuários serão substituídos pelos dados iniciais.", "Restaurar", true)
      .then(function (ok) {
        if (!ok) return;
        Dados.restaurarExemplo();
        Layout.avisarProximaTela("Dados de exemplo restaurados.", "ok");
        location.reload();
      });
  });
})();
