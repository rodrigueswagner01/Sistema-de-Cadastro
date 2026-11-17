/* Tela de login: validação dos campos, autenticação simulada e atalhos de exemplo. */
(function () {
  "use strict";
  if (Dados.sessaoAtual()) { location.replace("dashboard.html"); return; }

  var form = document.getElementById("form-login");
  var campoLogin = document.getElementById("login");
  var campoSenha = document.getElementById("senha");
  var erroGeral = document.getElementById("erro-geral");
  var botaoMostrar = document.getElementById("mostrar-senha");

  function mostrarErro(mensagem) {
    erroGeral.textContent = mensagem;
    erroGeral.hidden = false;
  }

  botaoMostrar.addEventListener("click", function () {
    var oculta = campoSenha.type === "password";
    campoSenha.type = oculta ? "text" : "password";
    botaoMostrar.textContent = oculta ? "Ocultar" : "Mostrar";
    botaoMostrar.setAttribute("aria-pressed", String(oculta));
  });

  form.addEventListener("submit", function (evento) {
    evento.preventDefault();
    erroGeral.hidden = true;
    try {
      Dados.autenticar(campoLogin.value, campoSenha.value);
      location.href = "dashboard.html";
    } catch (e) {
      if (e.name !== "ValidacaoErro") throw e;
      mostrarErro(e.message);
      (campoLogin.value.trim() ? campoSenha : campoLogin).focus();
    }
  });

  // Atalhos: preenchem o formulário com os usuários de exemplo do script SQL.
  document.querySelectorAll("[data-exemplo]").forEach(function (botao) {
    botao.addEventListener("click", function () {
      campoLogin.value = botao.dataset.exemplo;
      campoSenha.value = "123";
      erroGeral.hidden = true;
      campoSenha.focus();
    });
  });
})();
