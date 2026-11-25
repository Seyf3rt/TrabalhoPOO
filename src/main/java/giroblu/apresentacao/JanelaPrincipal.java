package giroblu.apresentacao;

import giroblu.negocio.BicicletaEletrica;
import giroblu.negocio.BicicletaMecanica;
import giroblu.negocio.Estacao;
import giroblu.negocio.Formatos;
import giroblu.negocio.PatineteEletrico;
import giroblu.negocio.Persistencia;
import giroblu.negocio.Plano;
import giroblu.negocio.PlanoAvulso;
import giroblu.negocio.PlanoMensal;
import giroblu.negocio.PlanoUniversitario;
import giroblu.negocio.RegraNegocioException;
import giroblu.negocio.Sistema;
import giroblu.negocio.Usuario;
import giroblu.negocio.Veiculo;
import giroblu.negocio.Viagem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Tela de operação da GiroBlu. Só lê os campos, chama o {@link Sistema} e mostra o resultado;
 * as regras ficam todas na camada de negócio. Depois de cada operação os dados são gravados.
 */
public class JanelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Sistema sistema;
    private final Persistencia persistencia;
    private final JTextArea areaResultado = new JTextArea(16, 80);

    private interface Acao {
        void executar() throws Exception;
    }

    /**
     * @param sistema dados carregados do arquivo
     * @param persistencia usada para gravar depois de cada operação
     */
    public JanelaPrincipal(Sistema sistema, Persistencia persistencia) {
        super("GiroBlu");
        this.sistema = sistema;
        this.persistencia = persistencia;

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Cadastros", painelCadastros());
        abas.addTab("Carteira", painelCarteira());
        abas.addTab("Viagens", painelViagens());
        abas.addTab("Frota", painelFrota());
        abas.addTab("Faturamento", painelFaturamento());

        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        setLayout(new BorderLayout());
        add(abas, BorderLayout.NORTH);
        add(new JScrollPane(areaResultado), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel painelCadastros() {
        JTextField nomeEstacao = new JTextField(15);
        JTextField vagas = new JTextField(4);
        JButton btnEstacao = new JButton("Cadastrar estação");
        btnEstacao.addActionListener(e -> executar(() -> {
            Estacao estacao = sistema.cadastrarEstacao(nomeEstacao.getText(), lerInteiro(vagas, "vagas"));
            areaResultado.setText("Estação cadastrada: " + estacao.getNome()
                    + " (" + estacao.getQuantidadeVagas() + " vagas)");
        }));

        JComboBox<String> tipo = new JComboBox<>(
                new String[] {"Bicicleta mecânica", "Bicicleta elétrica", "Patinete elétrico"});
        JTextField codigo = new JTextField(5);
        JTextField modelo = new JTextField(12);
        JTextField bateria = new JTextField("100", 4);
        JTextField estacaoVeiculo = new JTextField(12);
        JButton btnVeiculo = new JButton("Cadastrar veículo");
        btnVeiculo.addActionListener(e -> executar(() -> {
            Estacao estacao = sistema.buscarEstacao(estacaoVeiculo.getText());
            Veiculo veiculo = criarVeiculo((String) tipo.getSelectedItem(),
                    lerInteiro(codigo, "código"), modelo.getText(), bateria);
            sistema.cadastrarVeiculo(veiculo, estacao);
            areaResultado.setText("Veículo cadastrado em " + estacao.getNome() + ": " + veiculo.getDescricao());
        }));

        JTextField nomeUsuario = new JTextField(15);
        JTextField email = new JTextField(18);
        JComboBox<Plano> plano = new JComboBox<>(
                new Plano[] {new PlanoAvulso(), new PlanoMensal(), new PlanoUniversitario()});
        JButton btnUsuario = new JButton("Cadastrar usuário");
        btnUsuario.addActionListener(e -> executar(() -> {
            Usuario usuario = sistema.cadastrarUsuario(nomeUsuario.getText(), email.getText(),
                    (Plano) plano.getSelectedItem());
            areaResultado.setText("Usuário cadastrado: " + usuario + " - plano " + usuario.getPlano().getNome());
        }));

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(linha("Estação", new JLabel("Nome:"), nomeEstacao, new JLabel("Vagas:"), vagas, btnEstacao));
        painel.add(linha("Veículo", new JLabel("Tipo:"), tipo, new JLabel("Código:"), codigo,
                new JLabel("Modelo:"), modelo, new JLabel("Bateria %:"), bateria,
                new JLabel("Estação:"), estacaoVeiculo, btnVeiculo));
        painel.add(linha("Usuário", new JLabel("Nome:"), nomeUsuario, new JLabel("E-mail:"), email,
                new JLabel("Plano:"), plano, btnUsuario));
        return painel;
    }

    private JPanel painelCarteira() {
        JTextField email = new JTextField(18);
        JTextField data = campoData();
        JTextField hora = campoHora();
        JTextField valor = new JTextField(6);
        JButton btnRecarga = new JButton("Registrar recarga");
        btnRecarga.addActionListener(e -> executar(() -> {
            Usuario usuario = sistema.buscarUsuario(email.getText());
            sistema.registrarRecarga(usuario, lerDataHora(data, hora), lerValor(valor));
            areaResultado.setText("Recarga registrada para " + usuario.getNome()
                    + ". Saldo atual: " + Formatos.moeda(usuario.getSaldo()));
        }));

        JTextField emailExtrato = new JTextField(18);
        JButton btnExtrato = new JButton("Emitir extrato");
        btnExtrato.addActionListener(e -> executar(() ->
                areaResultado.setText(sistema.emitirExtrato(sistema.buscarUsuario(emailExtrato.getText())))));

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(linha("Recarga", new JLabel("E-mail:"), email, new JLabel("Data:"), data,
                new JLabel("Hora:"), hora, new JLabel("Valor R$:"), valor, btnRecarga));
        painel.add(linha("Extrato", new JLabel("E-mail:"), emailExtrato, btnExtrato));
        return painel;
    }

    private JPanel painelViagens() {
        JTextField emailInicio = new JTextField(18);
        JTextField codigo = new JTextField(5);
        JTextField dataInicio = campoData();
        JTextField horaInicio = campoHora();
        JButton btnIniciar = new JButton("Iniciar viagem");
        btnIniciar.addActionListener(e -> executar(() -> {
            Usuario usuario = sistema.buscarUsuario(emailInicio.getText());
            Veiculo veiculo = sistema.buscarVeiculo(lerInteiro(codigo, "código"));
            Viagem viagem = sistema.iniciarViagem(usuario, veiculo, lerDataHora(dataInicio, horaInicio));
            areaResultado.setText("Viagem iniciada: " + usuario.getNome() + " saiu de "
                    + viagem.getEstacaoInicial().getNome() + " com " + veiculo.getDescricao());
        }));

        JTextField emailFim = new JTextField(18);
        JTextField estacao = new JTextField(12);
        JTextField dataFim = campoData();
        JTextField horaFim = campoHora();
        JButton btnEncerrar = new JButton("Encerrar viagem");
        btnEncerrar.addActionListener(e -> executar(() -> {
            Usuario usuario = sistema.buscarUsuario(emailFim.getText());
            Viagem viagem = sistema.encerrarViagem(usuario, sistema.buscarEstacao(estacao.getText()),
                    lerDataHora(dataFim, horaFim));
            String msg = "Viagem encerrada em " + viagem.getEstacaoFinal().getNome()
                    + "\nDuração: " + viagem.getDuracaoMinutos() + " minuto(s)"
                    + "\nValor cobrado: " + Formatos.moeda(viagem.getValor())
                    + "\nSaldo de " + usuario.getNome() + ": " + Formatos.moeda(usuario.getSaldo())
                    + "\nVeículo: " + viagem.getVeiculo().getDescricao();
            areaResultado.setText(msg);
            JOptionPane.showMessageDialog(this, msg, "Viagem encerrada", JOptionPane.INFORMATION_MESSAGE);
        }));

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(linha("Iniciar", new JLabel("E-mail:"), emailInicio, new JLabel("Código do veículo:"), codigo,
                new JLabel("Data:"), dataInicio, new JLabel("Hora:"), horaInicio, btnIniciar));
        painel.add(linha("Encerrar", new JLabel("E-mail:"), emailFim, new JLabel("Estação de destino:"), estacao,
                new JLabel("Data:"), dataFim, new JLabel("Hora:"), horaFim, btnEncerrar));
        return painel;
    }

    private JPanel painelFrota() {
        JTextField codigo = new JTextField(5);
        JButton btnManutencao = new JButton("Enviar p/ manutenção");
        btnManutencao.addActionListener(e -> executar(() -> {
            Veiculo veiculo = sistema.buscarVeiculo(lerInteiro(codigo, "código"));
            sistema.enviarParaManutencao(veiculo);
            areaResultado.setText(veiculo.getDescricao());
        }));
        JButton btnOperacao = new JButton("Voltar à operação");
        btnOperacao.addActionListener(e -> executar(() -> {
            Veiculo veiculo = sistema.buscarVeiculo(lerInteiro(codigo, "código"));
            sistema.voltarParaOperacao(veiculo);
            areaResultado.setText(veiculo.getDescricao());
        }));
        JButton btnRecarregar = new JButton("Recarregar bateria");
        btnRecarregar.addActionListener(e -> executar(() -> {
            Veiculo veiculo = sistema.buscarVeiculo(lerInteiro(codigo, "código"));
            sistema.recarregarBateria(veiculo);
            areaResultado.setText(veiculo.getDescricao());
        }));

        JTextField estacao = new JTextField(12);
        JButton btnDisponiveis = new JButton("Veículos disponíveis");
        btnDisponiveis.addActionListener(e -> executar(() ->
                areaResultado.setText(sistema.consultarDisponiveis(sistema.buscarEstacao(estacao.getText())))));

        JButton btnListar = new JButton("Listar cadastros");
        btnListar.addActionListener(e -> areaResultado.setText(listarCadastros()));

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(linha("Veículo", new JLabel("Código:"), codigo, btnManutencao, btnOperacao, btnRecarregar));
        painel.add(linha("Consultas", new JLabel("Estação:"), estacao, btnDisponiveis, btnListar));
        return painel;
    }

    private JPanel painelFaturamento() {
        JTextField inicio = campoData();
        JTextField fim = campoData();
        JButton btnFaturamento = new JButton("Consultar faturamento");
        btnFaturamento.addActionListener(e -> executar(() ->
                areaResultado.setText(sistema.consultarFaturamento(lerData(inicio), lerData(fim)))));

        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(linha("Período", new JLabel("Data inicial:"), inicio, new JLabel("Data final:"), fim,
                btnFaturamento));
        return painel;
    }

    private String listarCadastros() {
        StringBuilder sb = new StringBuilder("ESTAÇÕES\n");
        for (Estacao e : sistema.getEstacoes()) {
            sb.append("  ").append(e.getNome()).append(" - ").append(e.getVagasLivres()).append(" de ")
                    .append(e.getQuantidadeVagas()).append(" vagas livres\n");
        }
        sb.append("\nVEÍCULOS\n");
        for (Veiculo v : sistema.getVeiculos()) {
            sb.append("  ").append(v.getDescricao()).append(" - ")
                    .append(v.getEstacao() == null ? "em viagem" : v.getEstacao().getNome()).append('\n');
        }
        sb.append("\nUSUÁRIOS\n");
        for (Usuario u : sistema.getUsuarios()) {
            sb.append("  ").append(u).append(" - ").append(u.getPlano().getNome()).append(" - saldo ")
                    .append(Formatos.moeda(u.getSaldo())).append(u.estaViajando() ? " - em viagem" : "")
                    .append('\n');
        }
        return sb.toString();
    }

    private Veiculo criarVeiculo(String tipo, int codigo, String modelo, JTextField bateria)
            throws RegraNegocioException {
        switch (tipo) {
            case "Bicicleta elétrica":
                return new BicicletaEletrica(codigo, modelo, lerInteiro(bateria, "bateria"));
            case "Patinete elétrico":
                return new PatineteEletrico(codigo, modelo, lerInteiro(bateria, "bateria"));
            default:
                return new BicicletaMecanica(codigo, modelo);
        }
    }

    private void executar(Acao acao) {
        try {
            acao.executar();
            persistencia.salvar(sistema);
        } catch (RegraNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Operação não permitida",
                    JOptionPane.WARNING_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data ou hora inválida. Use o formato dd/mm/aaaa e hh:mm.",
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar os dados: " + e.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JPanel linha(String titulo, Component... componentes) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createTitledBorder(titulo));
        for (Component c : componentes) {
            painel.add(c);
        }
        return painel;
    }

    private static JTextField campoData() {
        return new JTextField(LocalDate.now().format(Formatos.DATA), 8);
    }

    private static JTextField campoHora() {
        return new JTextField(LocalTime.now().format(Formatos.HORA), 4);
    }

    private static int lerInteiro(JTextField campo, String nome) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um número inteiro em \"" + nome + "\".");
        }
    }

    private static double lerValor(JTextField campo) {
        try {
            return Double.parseDouble(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um valor numérico, por exemplo 10,50.");
        }
    }

    private static LocalDate lerData(JTextField campo) {
        return LocalDate.parse(campo.getText().trim(), Formatos.DATA);
    }

    private static LocalDateTime lerDataHora(JTextField data, JTextField hora) {
        return LocalDateTime.of(lerData(data), LocalTime.parse(hora.getText().trim(), Formatos.HORA));
    }
}
