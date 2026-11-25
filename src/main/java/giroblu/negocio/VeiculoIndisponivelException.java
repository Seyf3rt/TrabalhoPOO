package giroblu.negocio;

/**
 * Lançada quando o veículo não pode ser usado na operação pedida
 * (em uso, em manutenção ou com pouca bateria).
 */
public class VeiculoIndisponivelException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensagem descrição do problema
     */
    public VeiculoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
