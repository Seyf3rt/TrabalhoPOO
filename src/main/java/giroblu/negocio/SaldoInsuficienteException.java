package giroblu.negocio;

/**
 * Lançada quando o usuário não tem o saldo mínimo para iniciar uma viagem.
 */
public class SaldoInsuficienteException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensagem descrição do problema
     */
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
