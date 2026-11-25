package giroblu.negocio;

import java.time.LocalDateTime;

/**
 * Movimentação que aparece no extrato da carteira do usuário.
 */
public interface Lancamento {

    /**
     * @return data e hora em que o lançamento entra no extrato
     */
    LocalDateTime getDataHora();

    /**
     * @return valor do lançamento (sempre positivo)
     */
    double getValor();

    /**
     * @return true se o valor entra na carteira, false se sai
     */
    boolean isCredito();

    /**
     * @return descrição curta para o extrato
     */
    String getDescricao();
}
