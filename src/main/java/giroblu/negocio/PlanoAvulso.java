package giroblu.negocio;

/**
 * Plano avulso: paga o preço cheio da tabela.
 */
public class PlanoAvulso extends Plano {

    private static final long serialVersionUID = 1L;

    @Override
    public String getNome() {
        return "Avulso";
    }

    @Override
    public double calcularValor(Veiculo veiculo, int minutos) {
        return arredondar(desbloqueio(veiculo).add(valorMinutos(veiculo, minutos)));
    }
}
