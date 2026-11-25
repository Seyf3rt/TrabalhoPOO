package giroblu.negocio;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Recarga de créditos feita na carteira de um usuário.
 */
public class Recarga implements Lancamento, Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime dataHora;
    private final double valor;

    /**
     * @param dataHora data e hora da recarga
     * @param valor valor recarregado (positivo)
     * @throws RegraNegocioException se a data não for informada ou o valor não for positivo
     */
    public Recarga(LocalDateTime dataHora, double valor) throws RegraNegocioException {
        if (dataHora == null) {
            throw new RegraNegocioException("Informe a data e a hora da recarga.");
        }
        if (valor <= 0) {
            throw new RegraNegocioException("O valor da recarga deve ser positivo.");
        }
        this.dataHora = dataHora;
        this.valor = valor;
    }

    @Override
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public double getValor() {
        return valor;
    }

    @Override
    public boolean isCredito() {
        return true;
    }

    @Override
    public String getDescricao() {
        return "Recarga";
    }
}
