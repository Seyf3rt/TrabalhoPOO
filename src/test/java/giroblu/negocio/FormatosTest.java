package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

class FormatosTest {

    @Test
    void moedaComVirgulaEDuasCasas() {
        assertEquals("R$ 4,75", Formatos.moeda(4.75));
        assertEquals("R$ 2,00", Formatos.moeda(2));
        assertEquals("R$ -10,00", Formatos.moeda(-10));
    }

    @Test
    void dataInexistenteNaoEhAceita() {
        assertEquals(LocalDate.of(2026, 2, 28), LocalDate.parse("28/02/2026", Formatos.DATA));
        assertThrows(DateTimeParseException.class, () -> LocalDate.parse("31/02/2026", Formatos.DATA));
    }
}
