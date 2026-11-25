package giroblu.negocio;

/**
 * Veículo com bateria. A carga é um percentual entre 0 e 100 e diminui conforme
 * o consumo por minuto de cada tipo.
 */
public abstract class Eletrico extends Veiculo {

    private static final long serialVersionUID = 1L;

    /** Carga mínima, em percentual, para o veículo ser liberado. */
    public static final int CARGA_MINIMA = 20;

    private int percentualBateria;

    /**
     * @param codigoIdentificacao código único do veículo
     * @param modelo modelo do veículo
     * @param percentualBateria carga inicial, de 0 a 100
     * @throws RegraNegocioException se algum dado for inválido
     */
    public Eletrico(int codigoIdentificacao, String modelo, int percentualBateria)
            throws RegraNegocioException {
        super(codigoIdentificacao, modelo);
        if (percentualBateria < 0 || percentualBateria > 100) {
            throw new RegraNegocioException("A bateria deve estar entre 0% e 100%.");
        }
        this.percentualBateria = percentualBateria;
    }

    /**
     * @return quantos pontos percentuais de bateria são gastos por minuto
     */
    public abstract int getConsumoPorMinuto();

    /**
     * @return true se a carga atual permite liberar o veículo
     */
    public boolean minimoOk() {
        return percentualBateria >= CARGA_MINIMA;
    }

    @Override
    protected void validarLiberacao() throws VeiculoIndisponivelException {
        super.validarLiberacao();
        if (!minimoOk()) {
            throw new VeiculoIndisponivelException("O veículo " + getCodigoIdentificacao()
                    + " está com " + percentualBateria + "% de bateria. O mínimo é "
                    + CARGA_MINIMA + "%.");
        }
    }

    @Override
    protected void registrarUso(int minutos) {
        percentualBateria = Math.max(0, percentualBateria - minutos * getConsumoPorMinuto());
    }

    /**
     * Recarrega a bateria para 100%.
     *
     * @throws VeiculoIndisponivelException se o veículo estiver em uso
     */
    @Override
    public void recarregarBateria() throws VeiculoIndisponivelException {
        if (getStatus() == StatusVeiculo.EM_USO) {
            throw new VeiculoIndisponivelException("O veículo " + getCodigoIdentificacao()
                    + " está em uso e não pode ser recarregado.");
        }
        percentualBateria = 100;
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " - bateria " + percentualBateria + "%";
    }

    public int getPercentualBateria() {
        return percentualBateria;
    }
}
