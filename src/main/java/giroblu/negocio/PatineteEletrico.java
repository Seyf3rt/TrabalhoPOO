package giroblu.negocio;

/**
 * Patinete elétrico: R$ 3,00 de desbloqueio, R$ 0,40 por minuto e consumo de 2% de bateria por minuto.
 * Não recebe o desconto do plano universitário.
 */
public class PatineteEletrico extends Eletrico {

    private static final long serialVersionUID = 1L;

    /**
     * @param codigoIdentificacao código único do veículo
     * @param modelo modelo do patinete
     * @param percentualBateria carga inicial, de 0 a 100
     * @throws RegraNegocioException se algum dado for inválido
     */
    public PatineteEletrico(int codigoIdentificacao, String modelo, int percentualBateria)
            throws RegraNegocioException {
        super(codigoIdentificacao, modelo, percentualBateria);
    }

    @Override
    public String getTipo() {
        return "Patinete elétrico";
    }

    @Override
    public double getTaxaDesbloqueio() {
        return 3.00;
    }

    @Override
    public double getTaxaPorMinuto() {
        return 0.40;
    }

    @Override
    public int getConsumoPorMinuto() {
        return 2;
    }

    @Override
    public boolean aceitaDescontoUniversitario() {
        return false;
    }
}
