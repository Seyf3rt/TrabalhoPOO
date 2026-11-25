package giroblu.negocio;

/**
 * Plano mensal: não paga desbloqueio e os primeiros 15 minutos de cada viagem são gratuitos.
 */
public class PlanoMensal extends Plano {

    private static final long serialVersionUID = 1L;

    /** Minutos gratuitos em cada viagem. */
    public static final int MINUTOS_GRATIS = 15;

    @Override
    public String getNome() {
        return "Mensal";
    }

    @Override
    public double calcularValor(Veiculo veiculo, int minutos) {
        int minutosCobrados = Math.max(0, minutos - MINUTOS_GRATIS);
        return arredondar(valorMinutos(veiculo, minutosCobrados));
    }
}
