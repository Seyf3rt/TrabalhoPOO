package giroblu.negocio;

import java.math.BigDecimal;

/**
 * Plano universitário: 50% de desconto sobre o total da viagem, desde que o veículo
 * aceite o desconto (patinetes não aceitam).
 */
public class PlanoUniversitario extends Plano {

    private static final long serialVersionUID = 1L;

    private static final BigDecimal DESCONTO = new BigDecimal("0.5");

    @Override
    public String getNome() {
        return "Universitário";
    }

    @Override
    public double calcularValor(Veiculo veiculo, int minutos) {
        BigDecimal total = desbloqueio(veiculo).add(valorMinutos(veiculo, minutos));
        if (veiculo.aceitaDescontoUniversitario()) {
            total = total.multiply(DESCONTO);
        }
        return arredondar(total);
    }
}
