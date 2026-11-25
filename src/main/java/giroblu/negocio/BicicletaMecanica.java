package giroblu.negocio;

/**
 * Bicicleta mecânica: R$ 1,00 de desbloqueio e R$ 0,15 por minuto.
 */
public class BicicletaMecanica extends Veiculo {

    private static final long serialVersionUID = 1L;

    /**
     * @param codigoIdentificacao código único do veículo
     * @param modelo modelo da bicicleta
     * @throws RegraNegocioException se algum dado for inválido
     */
    public BicicletaMecanica(int codigoIdentificacao, String modelo) throws RegraNegocioException {
        super(codigoIdentificacao, modelo);
    }

    @Override
    public String getTipo() {
        return "Bicicleta mecânica";
    }

    @Override
    public double getTaxaDesbloqueio() {
        return 1.00;
    }

    @Override
    public double getTaxaPorMinuto() {
        return 0.15;
    }
}
