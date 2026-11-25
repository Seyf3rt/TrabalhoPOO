package giroblu.apresentacao;

import giroblu.negocio.Persistencia;
import giroblu.negocio.Sistema;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Classe que inicia o programa: carrega os dados salvos e abre a janela principal.
 */
public class Principal {

    /**
     * @param args não usado
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Persistencia persistencia = new Persistencia(Persistencia.ARQUIVO_PADRAO);
            try {
                Sistema sistema = persistencia.carregar();
                new JanelaPrincipal(sistema, persistencia).setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Não foi possível ler os dados: " + e.getMessage(),
                        "GiroBlu", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
