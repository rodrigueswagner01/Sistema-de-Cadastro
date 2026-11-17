/* Listagem de usuários (somente Administrador): busca, edição e exclusão com confirmação. */
(function () {
  "use strict";
  var sessao = Layout.iniciar({ pagina: "usuarios", permissao: "gerenciarUsuarios" });
  if (!sessao) return;
  var E = Util.el;
  var corpo = document.getElementById("corpo-tabela");
  var vazio = document.getElementById("vazio");
  var contador = document.getElementById("contador");
  var busca = document.getElementById("busca");

  function desenhar() {
    var filtro = Util.normalizar(busca.value);
    var lista = Dados.listarUsuarios().filter(function (u) {
      return !filtro || Util.normalizar(u.nome + " " + u.login + " " + u.email).indexOf(filtro) !== -1;
    });
    Util.limpar(corpo);
    vazio.hidden = lista.length > 0;
    document.getElementById("tabela").hidden = lista.length === 0;
    contador.textContent = lista.length + (lista.length === 1 ? " usuário" : " usuários");

    lista.forEach(function (u) {
      var eu = u.id === sessao.id;
      corpo.appendChild(E("tr", {}, [
        E("td", { attrs: { "data-label": "Nome" } }, [E("span", { className: "tabela__nome", text: u.nome }), eu ? E("span", { className: "etiqueta etiqueta--info", text: "você", attrs: { style: "margin-left:8px" } }) : null]),
        E("td", { text: u.email, attrs: { "data-label": "E-mail" } }),
        E("td", { text: u.login, attrs: { "data-label": "Login" } }),
        E("td", { attrs: { "data-label": "Perfil" } }, [E("span", { className: "etiqueta etiqueta--neutro", text: Dados.PERFIS[u.perfil] })]),
        E("td", { className: "tabela__acoes-celula" }, [E("div", { className: "tabela__acoes" }, [
          E("a", { className: "botao botao--secundario botao--pequeno", text: "Editar", attrs: { href: "usuario-form.html?id=" + u.id, "aria-label": "Editar " + u.nome } }),
          E("button", { className: "botao botao--secundario botao--pequeno", text: "Excluir", attrs: { type: "button", "aria-label": "Excluir " + u.nome }, onclick: function () { excluir(u, eu); } })
        ])])
      ]));
    });
  }

  function excluir(usuario, ehOLogado) {
    if (ehOLogado) { Util.toast("Você não pode excluir o usuário com o qual está logado.", "erro"); return; }
    Util.confirmar("Excluir usuário?", "“" + usuario.nome + "” perderá o acesso ao sistema. Essa ação não pode ser desfeita.", "Excluir", true)
      .then(function (ok) {
        if (!ok) return;
        try { Dados.excluirUsuario(usuario.id); Util.toast("Usuário excluído.", "ok"); } catch (e) { Util.toast(e.message, "erro"); }
        desenhar();
      });
  }

  busca.addEventListener("input", Util.debounce(desenhar, 120));
  desenhar();
})();
