package giroblu.negocio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Usuário do serviço, com seu plano e sua carteira de créditos.
 * O saldo só muda por recargas e pelo débito das viagens.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Saldo mínimo para começar uma viagem. */
    public static final double SALDO_MINIMO = 5.00;

    private final String nome;
    private final String email;
    private final Plano plano;
    private double saldo;
    private Viagem viagemAtual;
    private final List<Viagem> viagens = new ArrayList<>();
    private final List<Recarga> recargas = new ArrayList<>();

    /**
     * Cria um usuário com a carteira zerada.
     *
     * @param nome nome do usuário
     * @param email e-mail, usado para identificar o usuário
     * @param plano plano escolhido
     * @throws RegraNegocioException se algum dado for inválido
     */
    public Usuario(String nome, String email, Plano plano) throws RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe o nome do usuário.");
        }
        if (email == null || !email.contains("@")) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        if (plano == null) {
            throw new RegraNegocioException("Escolha um plano para o usuário.");
        }
        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();
        this.plano = plano;
    }

    /**
     * Registra uma recarga na carteira.
     *
     * @param dataHora data e hora da recarga
     * @param valor valor recarregado, maior que zero
     * @return a recarga registrada
     * @throws RegraNegocioException se a data ou o valor forem inválidos
     */
    public Recarga addCreditos(LocalDateTime dataHora, double valor) throws RegraNegocioException {
        Recarga recarga = new Recarga(dataHora, valor);
        recargas.add(recarga);
        saldo = somar(saldo, valor);
        return recarga;
    }

    /**
     * @return true se o usuário tem uma viagem em andamento
     */
    public boolean estaViajando() {
        return viagemAtual != null;
    }

    /**
     * Verifica se o usuário pode começar uma viagem.
     *
     * @throws RegraNegocioException se já existe uma viagem em andamento
     * @throws SaldoInsuficienteException se o saldo for menor que o mínimo
     */
    public void validarInicioViagem() throws RegraNegocioException {
        if (estaViajando()) {
            throw new RegraNegocioException(nome + " já tem uma viagem em andamento.");
        }
        if (saldo < SALDO_MINIMO) {
            throw new SaldoInsuficienteException("Saldo insuficiente: " + Formatos.moeda(saldo)
                    + ". É preciso ter pelo menos " + Formatos.moeda(SALDO_MINIMO) + " para iniciar uma viagem.");
        }
    }

    void iniciarViagem(Viagem viagem) {
        viagemAtual = viagem;
        viagens.add(viagem);
    }

    void concluirViagem(Viagem viagem) {
        saldo = somar(saldo, -viagem.getValor());
        viagemAtual = null;
    }

    /**
     * Monta o extrato da carteira com recargas e viagens encerradas em ordem cronológica,
     * mostrando o saldo depois de cada lançamento.
     *
     * @return texto do extrato
     */
    public String emitirExtrato() {
        List<Lancamento> lancamentos = new ArrayList<>(recargas);
        for (Viagem v : viagens) {
            if (v.isEncerrada()) {
                lancamentos.add(v);
            }
        }
        lancamentos.sort(Comparator.comparing(Lancamento::getDataHora));

        StringBuilder sb = new StringBuilder();
        sb.append("Extrato de ").append(nome).append(" <").append(email).append("> - plano ")
                .append(plano.getNome()).append('\n');
        double saldoParcial = 0;
        for (Lancamento l : lancamentos) {
            double movimento = l.isCredito() ? l.getValor() : -l.getValor();
            saldoParcial = somar(saldoParcial, movimento);
            sb.append(String.format(Formatos.BRASIL, "%s  %-40s %s %10s   saldo %10s%n",
                    l.getDataHora().format(Formatos.DATA_HORA), l.getDescricao(),
                    l.isCredito() ? "C" : "D", Formatos.moeda(l.getValor()), Formatos.moeda(saldoParcial)));
        }
        if (lancamentos.isEmpty()) {
            sb.append("Nenhum lançamento.\n");
        }
        sb.append("Saldo atual: ").append(Formatos.moeda(saldo));
        if (estaViajando()) {
            sb.append("\nViagem em andamento desde ")
                    .append(viagemAtual.getDataHoraInicio().format(Formatos.DATA_HORA));
        }
        return sb.toString();
    }

    private static double somar(double a, double b) {
        return BigDecimal.valueOf(a).add(BigDecimal.valueOf(b)).doubleValue();
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Plano getPlano() {
        return plano;
    }

    public double getSaldo() {
        return saldo;
    }

    public Viagem getViagemAtual() {
        return viagemAtual;
    }

    public List<Viagem> getViagens() {
        return Collections.unmodifiableList(viagens);
    }

    public List<Recarga> getRecargas() {
        return Collections.unmodifiableList(recargas);
    }

    @Override
    public String toString() {
        return nome + " <" + email + ">";
    }
}
