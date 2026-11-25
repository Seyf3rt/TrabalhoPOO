package giroblu.negocio;

import java.io.Serializable;

/**
 * Veículo da frota da GiroBlu. Cada tipo concreto informa sua descrição e sua tarifa
 * (taxa de desbloqueio e valor por minuto). Para incluir um tipo novo basta criar
 * uma subclasse.
 */
public abstract class Veiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int codigoIdentificacao;
    private final String modelo;
    private Estacao estacao;
    private StatusVeiculo status;

    /**
     * Cria um veículo disponível, ainda sem estação.
     *
     * @param codigoIdentificacao código único do veículo (maior que zero)
     * @param modelo modelo do veículo
     * @throws RegraNegocioException se o código ou o modelo forem inválidos
     */
    public Veiculo(int codigoIdentificacao, String modelo) throws RegraNegocioException {
        if (codigoIdentificacao <= 0) {
            throw new RegraNegocioException("O código do veículo deve ser maior que zero.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new RegraNegocioException("Informe o modelo do veículo.");
        }
        this.codigoIdentificacao = codigoIdentificacao;
        this.modelo = modelo.trim();
        this.status = StatusVeiculo.DISPONIVEL;
    }

    /**
     * @return nome do tipo de veículo, ex.: "Bicicleta mecânica"
     */
    public abstract String getTipo();

    /**
     * @return taxa cobrada uma vez por viagem
     */
    public abstract double getTaxaDesbloqueio();

    /**
     * @return valor cobrado por minuto de uso
     */
    public abstract double getTaxaPorMinuto();

    /**
     * Indica se o plano universitário pode aplicar desconto neste veículo.
     *
     * @return true por padrão
     */
    public boolean aceitaDescontoUniversitario() {
        return true;
    }

    /**
     * Libera o veículo para uma viagem. Só funciona se ele puder ser liberado.
     *
     * @throws VeiculoIndisponivelException se o veículo não estiver em condições de uso
     */
    void liberarVeiculo() throws VeiculoIndisponivelException {
        validarLiberacao();
        status = StatusVeiculo.EM_USO;
    }

    /**
     * Verifica se o veículo pode ser liberado. Subclasses podem acrescentar regras.
     *
     * @throws VeiculoIndisponivelException se o veículo não estiver disponível
     */
    protected void validarLiberacao() throws VeiculoIndisponivelException {
        if (status != StatusVeiculo.DISPONIVEL) {
            throw new VeiculoIndisponivelException("O veículo " + codigoIdentificacao
                    + " não está disponível (" + status + ").");
        }
    }

    /**
     * Devolve o veículo em uma estação ao fim de uma viagem.
     *
     * @param destino estação onde o veículo é devolvido
     * @param minutos duração da viagem
     * @throws RegraNegocioException se o veículo não estiver em uso ou a estação estiver lotada
     */
    void devolverVeiculo(Estacao destino, int minutos) throws RegraNegocioException {
        if (status != StatusVeiculo.EM_USO) {
            throw new RegraNegocioException("O veículo " + codigoIdentificacao + " não está em uso.");
        }
        destino.addVeiculo(this);
        registrarUso(minutos);
        status = StatusVeiculo.DISPONIVEL;
    }

    /**
     * Aplica os efeitos do uso no veículo. Por padrão não faz nada.
     *
     * @param minutos minutos de uso
     */
    protected void registrarUso(int minutos) {
    }

    /**
     * Envia o veículo para manutenção. Ele continua ocupando a vaga na estação.
     *
     * @throws VeiculoIndisponivelException se o veículo estiver em uso ou já em manutenção
     */
    public void enviarParaManutencao() throws VeiculoIndisponivelException {
        if (status == StatusVeiculo.EM_USO) {
            throw new VeiculoIndisponivelException("O veículo " + codigoIdentificacao
                    + " está em uso e não pode ir para manutenção.");
        }
        if (status == StatusVeiculo.MANUTENCAO) {
            throw new VeiculoIndisponivelException("O veículo " + codigoIdentificacao
                    + " já está em manutenção.");
        }
        status = StatusVeiculo.MANUTENCAO;
    }

    /**
     * Devolve à operação um veículo que estava em manutenção.
     *
     * @throws RegraNegocioException se o veículo não estiver em manutenção
     */
    public void voltarParaOperacao() throws RegraNegocioException {
        if (status != StatusVeiculo.MANUTENCAO) {
            throw new RegraNegocioException("O veículo " + codigoIdentificacao
                    + " não está em manutenção.");
        }
        status = StatusVeiculo.DISPONIVEL;
    }

    /**
     * Recarrega a bateria do veículo.
     *
     * @throws RegraNegocioException sempre, pois este veículo não tem bateria
     */
    public void recarregarBateria() throws RegraNegocioException {
        throw new RegraNegocioException("O veículo " + codigoIdentificacao + " (" + getTipo()
                + ") não possui bateria.");
    }

    /**
     * @return linha com código, tipo, modelo e situação do veículo
     */
    public String getDescricao() {
        return codigoIdentificacao + " - " + getTipo() + " - " + modelo + " - " + status;
    }

    void setEstacao(Estacao estacao) {
        this.estacao = estacao;
    }

    public int getCodigoIdentificacao() {
        return codigoIdentificacao;
    }

    public String getModelo() {
        return modelo;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
