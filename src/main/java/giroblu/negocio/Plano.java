package giroblu.negocio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Plano de cobrança do usuário. Cada plano define como o valor da viagem é calculado.
 * Para criar um plano novo basta estender esta classe.
 */
public abstract class Plano implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @return nome do plano, ex.: "Avulso"
     */
    public abstract String getNome();

    /**
     * Calcula quanto o usuário paga pela viagem.
     *
     * @param veiculo veículo usado
     * @param minutos duração da viagem em minutos
     * @return valor a pagar, arredondado para duas casas decimais
     */
    public abstract double calcularValor(Veiculo veiculo, int minutos);

    /**
     * @param veiculo veículo usado
     * @return taxa de desbloqueio do veículo
     */
    protected BigDecimal desbloqueio(Veiculo veiculo) {
        return BigDecimal.valueOf(veiculo.getTaxaDesbloqueio());
    }

    /**
     * @param veiculo veículo usado
     * @param minutos minutos cobrados
     * @return valor dos minutos de uso
     */
    protected BigDecimal valorMinutos(Veiculo veiculo, int minutos) {
        return BigDecimal.valueOf(veiculo.getTaxaPorMinuto()).multiply(BigDecimal.valueOf(minutos));
    }

    /**
     * @param valor valor a arredondar
     * @return valor com duas casas decimais
     */
    protected double arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public String toString() {
        return getNome();
    }
}
