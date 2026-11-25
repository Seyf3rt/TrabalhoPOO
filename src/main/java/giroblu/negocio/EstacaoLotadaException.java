package giroblu.negocio;

/**
 * Lançada quando se tenta colocar um veículo em uma estação sem vaga livre.
 */
public class EstacaoLotadaException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensagem descrição do problema
     */
    public EstacaoLotadaException(String mensagem) {
        super(mensagem);
    }
}
