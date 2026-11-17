/* Listagem de produtos (Administrador e Técnico): busca, filtro por nível, venda, edição e exclusão. */
(function () {
  "use strict";
  var sessao = Layout.iniciar({ pagina: "produtos", permissao: "gerenciarProdutos" });
  if (!sessao) return;
  var E = Util.el;
  var corpo = document.getElementById("corpo-tabela");
  var vazio = document.getElementById("vazio");
  var contador = document.getElementById("contador");
  var busca = document.getElementById("busca");
  var filtroNivel = document.getElementById("filtro-nivel");

  function desenhar() {
    var lista = Dados.buscarProdutosPorNome(busca.value).filter(function (p) {
      return !filtroNivel.value || Dados.calcularNivelEstoque(p.quantidade) === filtroNivel.value;
    });
    Util.limpar(corpo);
    vazio.hidden = lista.length > 0;
    document.getElementById("tabela").hidden = lista.length === 0;
    contador.textContent = lista.length + (lista.length === 1 ? " produto" : " produtos");

    lista.forEach(function (p) {
      corpo.appendChild(E("tr", {}, [
        E("td", { attrs: { "data-label": "Produto" } }, [E("span", { className: "tabela__nome", text: p.nome })]),
        E("td", { text: p.descricao, attrs: { "data-label": "Descrição" } }),
        E("td", { className: "numero", text: String(p.quantidade), attrs: { "data-label": "Qtd." } }),
        E("td", { attrs: { "data-label": "Nível" } }, [Layout.etiquetaNivel(Dados.calcularNivelEstoque(p.quantidade))]),
        E("td", { attrs: { "data-label": "Status" } }, [Layout.etiquetaStatus(p.status)]),
        E("td", { className: "tabela__acoes-celula" }, [E("div", { className: "tabela__acoes" }, [
          E("button", { className: "botao botao--primario botao--pequeno", text: "Vender", attrs: { type: "button", "aria-label": "Registrar venda de " + p.nome }, onclick: function () { abrirVenda(p.id); } }),
          E("a", { className: "botao botao--secundario botao--pequeno", text: "Editar", attrs: { href: "produto-form.html?id=" + p.id, "aria-label": "Editar " + p.nome } }),
          E("button", { className: "botao botao--secundario botao--pequeno", text: "Excluir", attrs: { type: "button", "aria-label": "Excluir " + p.nome }, onclick: function () { excluir(p); } })
        ])])
      ]));
    });
  }

  function excluir(produto) {
    Util.confirmar("Excluir produto?", "“" + produto.nome + "” será removido do estoque. Essa ação não pode ser desfeita.", "Excluir", true)
      .then(function (ok) {
        if (!ok) return;
        try { Dados.excluirProduto(produto.id); Util.toast("Produto excluído.", "ok"); } catch (e) { Util.toast(e.message, "erro"); }
        desenhar();
      });
  }

  /** Diálogo de venda: mostra em tempo real o que restará (regra de registrarVenda). */
  function abrirVenda(id) {
    var produto = Dados.buscarProduto(id);
    if (!produto) { Util.toast("Produto não encontrado.", "erro"); desenhar(); return; }

    var campo = E("input", { attrs: { type: "number", id: "qtd-venda", min: "1", step: "1", inputmode: "numeric", autocomplete: "off" } });
    var erro = E("span", { className: "mensagem-campo", attrs: { id: "erro-qtd-venda", "aria-live": "polite" } });
    var restaria = E("span", { text: "—" });
    var novoNivel = E("span", {});
    var novoStatus = E("span", {});
    var grupo = E("div", { className: "campo" }, [E("label", { text: "Quantidade vendida", attrs: { for: "qtd-venda" } }), campo, erro]);
    var resumo = E("div", { className: "resumo-venda" }, [
      E("div", {}, [E("span", { text: "Em estoque agora" }), E("strong", { text: String(produto.quantidade) })]),
      E("div", {}, [E("span", { text: "Restará" }), E("strong", {}, [restaria])]),
      E("div", {}, [E("span", { text: "Nível" }), novoNivel]),
      E("div", {}, [E("span", { text: "Status" }), novoStatus])
    ]);
    var corpoModal = E("div", {}, [E("p", { text: produto.nome + " — " + produto.descricao, attrs: { style: "margin-bottom:16px;color:var(--texto-suave)" } }), grupo, resumo]);

    function atualizar() {
      var q = Dados.lerQuantidade(campo.value);
      var mensagem = "";
      novoNivel.textContent = ""; novoStatus.textContent = ""; restaria.textContent = "—";
      if (campo.value.trim() === "") mensagem = "";
      else if (isNaN(q)) mensagem = "Informe um número inteiro.";
      else {
        try {
          var restante = Dados.calcularQuantidadeAposVenda(produto.quantidade, q);
          restaria.textContent = String(restante);
          novoNivel.appendChild(Layout.etiquetaNivel(Dados.calcularNivelEstoque(restante)));
          novoStatus.appendChild(Layout.etiquetaStatus(Dados.definirStatusPorQuantidade(restante)));
        } catch (e) { mensagem = e.message; }
      }
      erro.textContent = mensagem;
      grupo.classList.toggle("campo--erro", !!mensagem);
      return mensagem;
    }
    campo.addEventListener("input", atualizar);

    var m = Util.modal({
      titulo: "Registrar venda",
      corpo: corpoModal,
      botoes: [
        { texto: "Cancelar", classe: "botao--secundario", aoClicar: function (fechar) { fechar(); } },
        { texto: "Confirmar venda", classe: "botao--primario", aoClicar: function (fechar) {
            if (campo.value.trim() === "") { erro.textContent = "Informe a quantidade vendida."; grupo.classList.add("campo--erro"); campo.focus(); return; }
            if (atualizar()) { campo.focus(); return; }
            try {
              var atualizado = Dados.registrarVenda(produto.id, Dados.lerQuantidade(campo.value));
              fechar();
              Util.toast("Venda registrada. Restam " + atualizado.quantidade + " em estoque.", "ok");
              desenhar();
            } catch (e) { erro.textContent = e.message; grupo.classList.add("campo--erro"); }
        } }
      ]
    });
    campo.focus();
    return m;
  }

  busca.addEventListener("input", Util.debounce(desenhar, 120));
  filtroNivel.addEventListener("change", desenhar);
  desenhar();
})();
