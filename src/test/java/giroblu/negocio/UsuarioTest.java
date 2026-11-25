package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private Usuario carla;

    @BeforeEach
    void setUp() throws Exception {
        carla = new Usuario("Carla", "carla@email.com", new PlanoAvulso());
    }

    private static LocalDateTime dh(String texto) {
        return LocalDateTime.parse(texto, Formatos.DATA_HORA);
    }

    @Test
    void carteiraComecaZerada() {
        assertEquals(0.0, carla.getSaldo());
        assertFalse(carla.estaViajando());
    }

    @Test
    void naoAceitaDadosInvalidos() {
        assertThrows(RegraNegocioException.class, () -> new Usuario("", "a@b.com", new PlanoAvulso()));
        assertThrows(RegraNegocioException.class, () -> new Usuario("Ana", "sem-arroba", new PlanoAvulso()));
        assertThrows(RegraNegocioException.class, () -> new Usuario("Ana", "ana@email.com", null));
    }

    @Test
    void addCreditosAumentaSaldo() throws Exception {
        carla.addCreditos(dh("01/11/2026 10:00"), 10.00);
        carla.addCreditos(dh("02/11/2026 10:00"), 0.10);
        assertEquals(10.10, carla.getSaldo());
        assertEquals(2, carla.getRecargas().size());
    }

    @Test
    void addCreditosRecusaValorZeroOuNegativo() {
        assertThrows(RegraNegocioException.class, () -> carla.addCreditos(dh("01/11/2026 10:00"), 0));
        assertThrows(RegraNegocioException.class, () -> carla.addCreditos(dh("01/11/2026 10:00"), -5));
        assertEquals(0.0, carla.getSaldo());
    }

    @Test
    void validarInicioViagemExigeSaldoMinimo() throws Exception {
        carla.addCreditos(dh("01/11/2026 10:00"), 4.99);
        assertThrows(SaldoInsuficienteException.class, () -> carla.validarInicioViagem());
        carla.addCreditos(dh("01/11/2026 10:01"), 0.01);
        assertDoesNotThrow(() -> carla.validarInicioViagem());
    }

    @Test
    void validarInicioViagemRecusaQuemJaEstaViajando() throws Exception {
        carla.addCreditos(dh("01/11/2026 10:00"), 50);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        carla.iniciarViagem(new Viagem(carla, bicicleta, null, dh("01/11/2026 11:00")));
        assertTrue(carla.estaViajando());
        assertThrows(RegraNegocioException.class, () -> carla.validarInicioViagem());
    }

    @Test
    void extratoEmOrdemCronologicaComSaldoAposCadaLancamento() throws Exception {
        Estacao estacao = new Estacao("Prainha", 5);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        estacao.addVeiculo(bicicleta);

        carla.addCreditos(dh("01/11/2026 08:00"), 10.00);
        bicicleta.liberarVeiculo();
        estacao.removerVeiculo(bicicleta);
        Viagem viagem = new Viagem(carla, bicicleta, estacao, dh("01/11/2026 09:00"));
        carla.iniciarViagem(viagem);
        carla.addCreditos(dh("01/11/2026 09:05"), 5.00);
        viagem.finalizarViagem(estacao, dh("01/11/2026 09:12"));

        String extrato = carla.emitirExtrato();
        String[] linhas = extrato.split("\n");
        assertTrue(linhas[1].contains("01/11/2026 08:00") && linhas[1].contains("Recarga") && linhas[1].contains("R$ 10,00"));
        assertTrue(linhas[2].contains("09:05") && linhas[2].contains("R$ 15,00"));
        assertTrue(linhas[3].contains("09:12") && linhas[3].contains(" D ") && linhas[3].contains("R$ 2,80")
                && linhas[3].contains("R$ 12,20"));
        assertTrue(extrato.endsWith("Saldo atual: R$ 12,20"));
    }

    @Test
    void extratoNaoMostraViagemEmAndamento() throws Exception {
        carla.addCreditos(dh("01/11/2026 08:00"), 10.00);
        carla.iniciarViagem(new Viagem(carla, new BicicletaMecanica(1, "Caloi"), null, dh("01/11/2026 09:00")));
        String extrato = carla.emitirExtrato();
        assertFalse(extrato.contains("Viagem Bicicleta"));
        assertTrue(extrato.contains("Viagem em andamento desde 01/11/2026 09:00"));
    }

    @Test
    void extratoSemLancamentos() {
        assertTrue(carla.emitirExtrato().contains("Nenhum lançamento."));
    }

    @Test
    void toStringMostraNomeEEmail() throws Exception {
        assertEquals("Bruno <bruno@email.com>", new Usuario("Bruno", "Bruno@Email.com", new PlanoMensal()).toString());
    }
}
