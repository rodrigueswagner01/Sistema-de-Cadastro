/* Consulta de produtos (todos os perfis): busca por nome e filtro por nível, somente leitura. */
(function () {
  "use strict";
  var sessao = Layout.iniciar({ pagina: "consulta", permissao: "consultarProdutos" });
  if (!sessao) return;
  var E = Util.el;
  var campoBusca = document.getElementById("busca");
  var filtro = document.getElementById("filtro-nivel");
  var corpo = document.getElementById("corpo-tabela");
  var vazio = document.getElementById("vazio");
  var contador = document.getElementById("contador");

  function desenhar() {
    var lista = Dados.buscarProdutosPorNome(campoBusca.value).filter(function (p) {
      return !filtro.value || Dados.calcularNivelEstoque(p.quantidade) === filtro.value;
    });
    Util.limpar(corpo);
    document.getElementById("tabela").hidden = lista.length === 0;
    vazio.hidden = lista.length > 0;
    contador.textContent = lista.length === 0 ? "" : lista.length + (lista.length === 1 ? " produto encontrado" : " produtos encontrados");
    lista.forEach(function (p) {
      corpo.appendChild(E("tr", {}, [
        E("td", { attrs: { "data-label": "Produto" } }, [E("span", { className: "tabela__nome", text: p.nome })]),
        E("td", { text: p.descricao, attrs: { "data-label": "Descrição" } }),
        E("td", { attrs: { "data-label": "Disponibilidade" } }, [Layout.etiquetaNivel(Dados.calcularNivelEstoque(p.quantidade))]),
        E("td", { attrs: { "data-label": "Status" } }, [Layout.etiquetaStatus(p.status)])
      ]));
    });
  }

  campoBusca.addEventListener("input", Util.debounce(desenhar, 120));
  filtro.addEventListener("change", desenhar);
  document.getElementById("limpar").addEventListener("click", function () {
    campoBusca.value = ""; filtro.value = ""; desenhar(); campoBusca.focus();
  });
  desenhar();
})();
