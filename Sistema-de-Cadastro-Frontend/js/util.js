/* Funções utilitárias compartilhadas pelas telas (sem dependências). */
(function (global) {
  "use strict";

  /** Remove acentos e converte para minúsculas — usado nas buscas. */
  function normalizar(texto) {
    return String(texto == null ? "" : texto)
      .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
      .toLowerCase().trim();
  }

  /**
   * Cria um elemento DOM. Os textos entram sempre como textContent
   * (nunca como HTML), o que evita injeção de código pelos dados.
   * props: className, text, attrs{}, dataset{}, onclick etc.
   */
  function el(tag, props, filhos) {
    var no = document.createElement(tag);
    props = props || {};
    Object.keys(props).forEach(function (chave) {
      var valor = props[chave];
      if (chave === "className") no.className = valor;
      else if (chave === "text") no.textContent = valor;
      else if (chave === "attrs") Object.keys(valor).forEach(function (a) { no.setAttribute(a, valor[a]); });
      else if (chave === "dataset") Object.keys(valor).forEach(function (d) { no.dataset[d] = valor[d]; });
      else if (chave.indexOf("on") === 0) no.addEventListener(chave.slice(2), valor);
    });
    (filhos || []).forEach(function (f) {
      if (f == null) return;
      no.appendChild(typeof f === "string" ? document.createTextNode(f) : f);
    });
    return no;
  }

  function limpar(no) { while (no.firstChild) no.removeChild(no.firstChild); }

  function debounce(fn, ms) {
    var t;
    return function () {
      var args = arguments, ctx = this;
      clearTimeout(t);
      t = setTimeout(function () { fn.apply(ctx, args); }, ms);
    };
  }

  function parametros() { return new URLSearchParams(global.location.search); }

  /** Notificação rápida no canto da tela. tipo: "ok" | "erro" | undefined */
  function toast(mensagem, tipo) {
    var area = document.getElementById("toasts");
    if (!area) {
      area = el("div", { attrs: { id: "toasts", role: "status", "aria-live": "polite" } });
      document.body.appendChild(area);
    }
    var item = el("div", { className: "toast" + (tipo ? " toast--" + tipo : ""), text: mensagem });
    area.appendChild(item);
    setTimeout(function () { if (item.parentNode) item.parentNode.removeChild(item); }, 4200);
  }

  /**
   * Abre um diálogo modal.
   * opcoes: { titulo, corpo: Node, botoes: [{texto, classe, aoClicar(fechar)}] }
   * Retorna { fechar, dialogo }.
   */
  function modal(opcoes) {
    var dialogo = el("dialog", { className: "modal", attrs: { "aria-labelledby": "modal-titulo" } });
    var fechar = function () {
      if (dialogo.open) dialogo.close();
      if (dialogo.parentNode) dialogo.parentNode.removeChild(dialogo);
    };
    var rodape = el("div", { className: "modal__rodape" });
    opcoes.botoes.forEach(function (b) {
      rodape.appendChild(el("button", {
        className: "botao " + (b.classe || "botao--secundario"), text: b.texto, attrs: { type: "button" },
        onclick: function () { b.aoClicar(fechar); }
      }));
    });
    dialogo.appendChild(el("div", { className: "modal__cabecalho" }, [el("h2", { text: opcoes.titulo, attrs: { id: "modal-titulo" } })]));
    dialogo.appendChild(el("div", { className: "modal__corpo" }, [opcoes.corpo]));
    dialogo.appendChild(rodape);
    dialogo.addEventListener("cancel", function (e) { e.preventDefault(); fechar(); });
    document.body.appendChild(dialogo);
    dialogo.showModal();
    return { fechar: fechar, dialogo: dialogo };
  }

  /** Pede confirmação. Retorna uma Promise<boolean>. */
  function confirmar(titulo, texto, textoBotao, perigoso) {
    return new Promise(function (resolver) {
      modal({
        titulo: titulo,
        corpo: el("p", { text: texto }),
        botoes: [
          { texto: "Cancelar", classe: "botao--secundario", aoClicar: function (f) { f(); resolver(false); } },
          { texto: textoBotao, classe: perigoso ? "botao--perigo" : "botao--primario", aoClicar: function (f) { f(); resolver(true); } }
        ]
      });
    });
  }

  global.Util = { normalizar: normalizar, el: el, limpar: limpar, debounce: debounce, parametros: parametros, toast: toast, modal: modal, confirmar: confirmar };
})(window);
