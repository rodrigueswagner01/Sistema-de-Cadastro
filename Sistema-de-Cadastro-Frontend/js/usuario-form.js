/* Formulário de usuário (cadastro e edição na mesma página): validação em JavaScript. */
(function () {
  "use strict";
  var sessao = Layout.iniciar({ pagina: "usuarios", permissao: "gerenciarUsuarios" });
  if (!sessao) return;

  var form = document.getElementById("form-usuario");
  var CAMPOS = ["nome", "email", "login", "senha", "perfil"];
  var idEdicao = Util.parametros().get("id");
  var original = null;

  if (idEdicao != null) {
    original = Dados.buscarUsuario(Number(idEdicao));
    if (!original) {
      Layout.avisarProximaTela("Usuário não encontrado.", "erro");
      location.replace("usuarios.html");
      return;
    }
    document.getElementById("titulo-pagina").textContent = "Editar usuário";
    document.getElementById("subtitulo-pagina").textContent = "Altere os dados e salve.";
    document.getElementById("dica-senha").textContent = "Deixe em branco para manter a senha atual.";
    document.getElementById("rotulo-senha").textContent = "Nova senha";
    CAMPOS.forEach(function (c) { if (c !== "senha") document.getElementById(c).value = original[c]; });
  }

  function ler() {
    var d = { id: original ? original.id : null };
    CAMPOS.forEach(function (c) { d[c] = document.getElementById(c).value; });
    return d;
  }
  function mostrarErro(campo, mensagem) {
    var grupo = document.getElementById("campo-" + campo);
    grupo.classList.toggle("campo--erro", !!mensagem);
    document.getElementById("erro-" + campo).textContent = mensagem || "";
    document.getElementById(campo).setAttribute("aria-invalid", mensagem ? "true" : "false");
  }
  function validar(apenasCampo) {
    var erros = Dados.validarUsuario(ler(), original ? original.id : null);
    (apenasCampo ? [apenasCampo] : CAMPOS).forEach(function (c) { mostrarErro(c, erros[c]); });
    return erros;
  }

  CAMPOS.forEach(function (c) {
    var no = document.getElementById(c);
    no.addEventListener("blur", function () { if (no.dataset.tocado) validar(c); });
    no.addEventListener("input", function () { no.dataset.tocado = "1"; if (document.getElementById("campo-" + c).classList.contains("campo--erro")) validar(c); });
    no.addEventListener("change", function () { no.dataset.tocado = "1"; validar(c); });
  });

  document.getElementById("mostrar-senha").addEventListener("click", function (e) {
    var campo = document.getElementById("senha");
    var oculta = campo.type === "password";
    campo.type = oculta ? "text" : "password";
    e.currentTarget.textContent = oculta ? "Ocultar" : "Mostrar";
    e.currentTarget.setAttribute("aria-pressed", String(oculta));
  });

  form.addEventListener("submit", function (evento) {
    evento.preventDefault();
    var erros = validar();
    var invalidos = CAMPOS.filter(function (c) { return erros[c]; });
    if (invalidos.length) { document.getElementById(invalidos[0]).focus(); return; }
    try {
      Dados.salvarUsuario(ler());
      Layout.avisarProximaTela(original ? "Usuário atualizado." : "Usuário cadastrado.", "ok");
      location.href = "usuarios.html";
    } catch (e) {
      if (e.name !== "ValidacaoErro") throw e;
      Util.toast(e.message, "erro");
    }
  });
})();
