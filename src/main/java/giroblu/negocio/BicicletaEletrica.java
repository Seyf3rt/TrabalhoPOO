package giroblu.negocio;

/**
 * Bicicleta elétrica: R$ 2,00 de desbloqueio, R$ 0,30 por minuto e consumo de 1% de bateria por minuto.
 */
public class BicicletaEletrica extends Eletrico {

    private static final long serialVersionUID = 1L;

    /**
     * @param codigoIdentificacao código único do veículo
     * @param modelo modelo da bicicleta
     * @param percentualBateria carga inicial, de 0 a 100
     * @throws RegraNegocioException se algum dado for inválido
     */
    public BicicletaEletrica(int codigoIdentificacao, String modelo, int percentualBateria)
            throws RegraNegocioException {
        super(codigoIdentificacao, modelo, percentualBateria);
    }

    @Override
    public String getTipo() {
        return "Bicicleta elétrica";
    }

    @Override
    public double getTaxaDesbloqueio() {
        return 2.00;
    }

    @Override
    public double getTaxaPorMinuto() {
        return 0.30;
    }

    @Override
    public int getConsumoPorMinuto() {
        return 1;
    }
}
