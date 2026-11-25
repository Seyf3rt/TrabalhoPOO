package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ViagemTest {

    private Usuario ana;
    private BicicletaEletrica bicicleta;
    private Estacao origem;
    private Estacao destino;
    private Viagem viagem;

    private static LocalDateTime dh(String texto) {
        return LocalDateTime.parse(texto, Formatos.DATA_HORA);
    }

    @BeforeEach
    void setUp() throws Exception {
        ana = new Usuario("Ana", "ana@email.com", new PlanoUniversitario());
        ana.addCreditos(dh("01/11/2026 08:00"), 20);
        origem = new Estacao("FURB Campus 1", 3);
        destino = new Estacao("Prainha", 3);
        bicicleta = new BicicletaEletrica(5, "E-Bike", 80);
        origem.addVeiculo(bicicleta);
        bicicleta.liberarVeiculo();
        origem.removerVeiculo(bicicleta);
        viagem = new Viagem(ana, bicicleta, origem, dh("01/11/2026 10:00"));
        ana.iniciarViagem(viagem);
    }

    @Test
    void calcularDuracaoEmMinutos() {
        assertEquals(25, viagem.calcularDuracao(dh("01/11/2026 10:25")));
        assertEquals(90, viagem.calcularDuracao(dh("01/11/2026 11:30")));
    }

    @Test
    void viagemEmAndamentoNaoEstaEncerrada() {
        assertFalse(viagem.isEncerrada());
        assertNull(viagem.getDataHora());
    }

    @Test
    void finalizarViagemCalculaValorBateriaESaldo() throws Exception {
        viagem.finalizarViagem(destino, dh("01/11/2026 10:25"));
        assertTrue(viagem.isEncerrada());
        assertEquals(25, viagem.getDuracaoMinutos());
        assertEquals(4.75, viagem.getValor(), 0.001);
        assertEquals(55, bicicleta.getPercentualBateria());
        assertEquals(15.25, ana.getSaldo(), 0.001);
        assertFalse(ana.estaViajando());
        assertSame(destino, bicicleta.getEstacao());
        assertSame(destino, viagem.getEstacaoFinal());
    }

    @Test
    void terminoPrecisaSerPosteriorAoInicio() {
        assertThrows(RegraNegocioException.class, () -> viagem.finalizarViagem(destino, dh("01/11/2026 10:00")));
        assertThrows(RegraNegocioException.class, () -> viagem.finalizarViagem(destino, dh("01/11/2026 09:00")));
        assertThrows(RegraNegocioException.class, () -> viagem.finalizarViagem(destino, null));
        assertFalse(viagem.isEncerrada());
        assertTrue(ana.estaViajando());
    }

    @Test
    void naoEncerraDuasVezes() throws Exception {
        viagem.finalizarViagem(destino, dh("01/11/2026 10:25"));
        assertThrows(RegraNegocioException.class, () -> viagem.finalizarViagem(destino, dh("01/11/2026 10:30")));
    }

    @Test
    void comoLancamentoEhDebitoNaDataDeTermino() throws Exception {
        viagem.finalizarViagem(destino, dh("01/11/2026 10:25"));
        Lancamento lancamento = viagem;
        assertFalse(lancamento.isCredito());
        assertEquals(dh("01/11/2026 10:25"), lancamento.getDataHora());
        assertEquals("Viagem Bicicleta elétrica 5 (25 min)", lancamento.getDescricao());
    }

    @Test
    void recargaEhCredito() throws Exception {
        Lancamento recarga = new Recarga(dh("01/11/2026 08:00"), 20);
        assertTrue(recarga.isCredito());
        assertEquals(20, recarga.getValor());
        assertEquals("Recarga", recarga.getDescricao());
    }

    @Test
    void recargaNaoAceitaValorInvalidoNemDataVazia() {
        assertThrows(RegraNegocioException.class, () -> new Recarga(dh("01/11/2026 08:00"), 0));
        assertThrows(RegraNegocioException.class, () -> new Recarga(null, 10));
    }
}
