/* Formulário de produto: validação, nível de estoque em tempo real e status automático. */
(function () {
  "use strict";
  var sessao = Layout.iniciar({ pagina: "produtos", permissao: "gerenciarProdutos" });
  if (!sessao) return;

  var form = document.getElementById("form-produto");
  var CAMPOS = ["nome", "descricao", "quantidade", "status"];
  var idEdicao = Util.parametros().get("id");
  var original = null;
  var statusManual = false;   // vira true quando a pessoa escolhe o status à mão

  if (idEdicao != null) {
    original = Dados.buscarProduto(Number(idEdicao));
    if (!original) {
      Layout.avisarProximaTela("Produto não encontrado.", "erro");
      location.replace("produtos.html");
      return;
    }
    document.getElementById("titulo-pagina").textContent = "Editar produto";
    document.getElementById("subtitulo-pagina").textContent = "Altere os dados e salve.";
    CAMPOS.forEach(function (c) { document.getElementById(c).value = original[c]; });
  }

  function ler() {
    var d = { id: original ? original.id : null };
    CAMPOS.forEach(function (c) { d[c] = document.getElementById(c).value; });
    return d;
  }
  function mostrarErro(campo, mensagem) {
    document.getElementById("campo-" + campo).classList.toggle("campo--erro", !!mensagem);
    document.getElementById("erro-" + campo).textContent = mensagem || "";
    document.getElementById(campo).setAttribute("aria-invalid", mensagem ? "true" : "false");
  }
  function validar(apenasCampo) {
    var erros = Dados.validarProduto(ler());
    (apenasCampo ? [apenasCampo] : CAMPOS).forEach(function (c) { mostrarErro(c, erros[c]); });
    return erros;
  }

  /** Mostra o nível calculado e sugere o status coerente com a quantidade. */
  function atualizarPrevia(sincronizarStatus) {
    var previa = document.getElementById("previa-nivel");
    var q = Dados.lerQuantidade(document.getElementById("quantidade").value);
    Util.limpar(previa);
    if (isNaN(q) || q < 0) { previa.textContent = "O nível de estoque aparece aqui."; return; }
    previa.appendChild(document.createTextNode("Nível de estoque: "));
    previa.appendChild(Layout.etiquetaNivel(Dados.calcularNivelEstoque(q)));
    if (sincronizarStatus && !statusManual) {
      document.getElementById("status").value = Dados.definirStatusPorQuantidade(q);
      mostrarErro("status", "");
    }
  }

  CAMPOS.forEach(function (c) {
    var no = document.getElementById(c);
    no.addEventListener("blur", function () { if (no.dataset.tocado) validar(c); });
    no.addEventListener("input", function () { no.dataset.tocado = "1"; if (document.getElementById("campo-" + c).classList.contains("campo--erro")) validar(c); });
    no.addEventListener("change", function () { no.dataset.tocado = "1"; validar(c); });
  });
  document.getElementById("quantidade").addEventListener("input", function () { atualizarPrevia(true); });
  document.getElementById("status").addEventListener("change", function () { statusManual = true; });
  atualizarPrevia(false);

  form.addEventListener("submit", function (evento) {
    evento.preventDefault();
    var erros = validar();
    var invalidos = CAMPOS.filter(function (c) { return erros[c]; });
    if (invalidos.length) { document.getElementById(invalidos[0]).focus(); return; }
    try {
      Dados.salvarProduto(ler());
      Layout.avisarProximaTela(original ? "Produto atualizado." : "Produto cadastrado.", "ok");
      location.href = "produtos.html";
    } catch (e) {
      if (e.name !== "ValidacaoErro") throw e;
      Util.toast(e.message, "erro");
    }
  });
})();
