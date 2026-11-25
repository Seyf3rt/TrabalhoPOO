package giroblu.negocio;

/**
 * Exceção lançada quando alguma regra de negócio da GiroBlu é violada.
 * A mensagem é pensada para ser mostrada diretamente ao usuário.
 */
public class RegraNegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensagem descrição da regra violada
     */
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
