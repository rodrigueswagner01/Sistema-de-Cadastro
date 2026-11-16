package br.com.sistemacadastro.main;

import br.com.sistemacadastro.teste.TesteServicos;
import br.com.sistemacadastro.view.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada do sistema.
 *
 * Antes de abrir a interface, executa os testes automáticos das regras de
 * negócio (camada de serviço, sem banco de dados), conforme pedido na
 * Etapa 6. Para rodar somente os testes, sem abrir a tela, use o argumento
 * --somente-testes (por exemplo, em Propriedades do projeto > Run >
 * Arguments no NetBeans).
 */
public class Main {

    public static void main(String[] args) {
        boolean somenteTestes = args.length > 0 && args[0].equals("--somente-testes");

        boolean testesOk = TesteServicos.executar();

        if (somenteTestes) {
            System.exit(testesOk ? 0 : 1);
            return;
        }

        if (!testesOk) {
            System.out.println("Atenção: há testes falhando. Verifique o console antes de usar o sistema.");
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
