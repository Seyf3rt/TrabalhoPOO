package giroblu.negocio;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Viagem de um usuário com um veículo. Começa quando o veículo é liberado em uma estação
 * e termina quando ele é devolvido em outra (ou na mesma). No extrato, a viagem
 * aparece como débito na data e hora em que foi encerrada.
 */
public class Viagem implements Lancamento, Serializable {

    private static final long serialVersionUID = 1L;

    private final Usuario usuario;
    private final Veiculo veiculo;
    private final Estacao estacaoInicial;
    private final LocalDateTime dataHoraInicio;
    private Estacao estacaoFinal;
    private LocalDateTime dataHoraFim;
    private int duracaoMinutos;
    private double valorViagem;

    Viagem(Usuario usuario, Veiculo veiculo, Estacao estacaoInicial, LocalDateTime dataHoraInicio) {
        this.usuario = usuario;
        this.veiculo = veiculo;
        this.estacaoInicial = estacaoInicial;
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Encerra a viagem: calcula duração e valor, devolve o veículo na estação de destino
     * e debita o valor da carteira do usuário.
     *
     * @param destino estação onde o veículo é devolvido
     * @param dataHoraFim data e hora de término, posterior ao início
     * @throws RegraNegocioException se a viagem já terminou, a data for inválida ou a estação estiver lotada
     */
    void finalizarViagem(Estacao destino, LocalDateTime dataHoraFim) throws RegraNegocioException {
        if (isEncerrada()) {
            throw new RegraNegocioException("Esta viagem já foi encerrada.");
        }
        if (dataHoraFim == null || !dataHoraFim.isAfter(dataHoraInicio)) {
            throw new RegraNegocioException("O término deve ser posterior ao início da viagem ("
                    + dataHoraInicio.format(Formatos.DATA_HORA) + ").");
        }
        int minutos = calcularDuracao(dataHoraFim);
        double valor = usuario.getPlano().calcularValor(veiculo, minutos);
        veiculo.devolverVeiculo(destino, minutos);
        this.estacaoFinal = destino;
        this.dataHoraFim = dataHoraFim;
        this.duracaoMinutos = minutos;
        this.valorViagem = valor;
        usuario.concluirViagem(this);
    }

    /**
     * @param fim momento final
     * @return minutos completos entre o início da viagem e o momento informado
     */
    public int calcularDuracao(LocalDateTime fim) {
        return (int) Duration.between(dataHoraInicio, fim).toMinutes();
    }

    /**
     * @return true se a viagem já foi encerrada
     */
    public boolean isEncerrada() {
        return dataHoraFim != null;
    }

    @Override
    public LocalDateTime getDataHora() {
        return dataHoraFim;
    }

    @Override
    public double getValor() {
        return valorViagem;
    }

    @Override
    public boolean isCredito() {
        return false;
    }

    @Override
    public String getDescricao() {
        return "Viagem " + veiculo.getTipo() + " " + veiculo.getCodigoIdentificacao()
                + " (" + duracaoMinutos + " min)";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Estacao getEstacaoInicial() {
        return estacaoInicial;
    }

    public Estacao getEstacaoFinal() {
        return estacaoFinal;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }
}
