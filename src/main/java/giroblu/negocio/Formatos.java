package giroblu.negocio;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Formatos de data, hora e dinheiro usados nos textos do sistema.
 */
public final class Formatos {

    /** Locale usado para mostrar valores em reais. */
    public static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    /** Data no formato dd/MM/aaaa. */
    public static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Hora no formato HH:mm. */
    public static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    /** Data e hora no formato dd/MM/aaaa HH:mm. */
    public static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Formatos() {
    }

    /**
     * @param valor valor em reais
     * @return o valor formatado, por exemplo "R$ 4,75"
     */
    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %.2f", valor);
    }
}
