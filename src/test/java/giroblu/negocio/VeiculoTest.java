package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VeiculoTest {

    private Estacao estacao;
    private BicicletaMecanica bicicleta;

    @BeforeEach
    void setUp() throws Exception {
        estacao = new Estacao("Prainha", 5);
        bicicleta = new BicicletaMecanica(10, "Caloi");
        estacao.addVeiculo(bicicleta);
    }

    @Test
    void novoVeiculoComecaDisponivel() {
        assertEquals(StatusVeiculo.DISPONIVEL, bicicleta.getStatus());
        assertSame(estacao, bicicleta.getEstacao());
    }

    @Test
    void naoAceitaCodigoOuModeloInvalido() {
        assertThrows(RegraNegocioException.class, () -> new BicicletaMecanica(0, "Caloi"));
        assertThrows(RegraNegocioException.class, () -> new BicicletaMecanica(1, " "));
    }

    @Test
    void tarifasPorTipo() throws Exception {
        assertEquals(1.00, bicicleta.getTaxaDesbloqueio());
        assertEquals(0.15, bicicleta.getTaxaPorMinuto());
        BicicletaEletrica eletrica = new BicicletaEletrica(11, "E-Bike", 100);
        assertEquals(2.00, eletrica.getTaxaDesbloqueio());
        assertEquals(0.30, eletrica.getTaxaPorMinuto());
        PatineteEletrico patinete = new PatineteEletrico(12, "Xiaomi", 100);
        assertEquals(3.00, patinete.getTaxaDesbloqueio());
        assertEquals(0.40, patinete.getTaxaPorMinuto());
    }

    @Test
    void tipos() throws Exception {
        assertEquals("Bicicleta mecânica", bicicleta.getTipo());
        assertEquals("Bicicleta elétrica", new BicicletaEletrica(11, "E-Bike", 100).getTipo());
        assertEquals("Patinete elétrico", new PatineteEletrico(12, "Xiaomi", 100).getTipo());
    }

    @Test
    void somentePatineteNaoAceitaDescontoUniversitario() throws Exception {
        assertTrue(bicicleta.aceitaDescontoUniversitario());
        assertTrue(new BicicletaEletrica(11, "E-Bike", 100).aceitaDescontoUniversitario());
        assertFalse(new PatineteEletrico(12, "Xiaomi", 100).aceitaDescontoUniversitario());
    }

    @Test
    void liberarVeiculoDisponivel() throws Exception {
        bicicleta.liberarVeiculo();
        assertEquals(StatusVeiculo.EM_USO, bicicleta.getStatus());
    }

    @Test
    void naoLiberaVeiculoEmUsoOuEmManutencao() throws Exception {
        bicicleta.enviarParaManutencao();
        assertThrows(VeiculoIndisponivelException.class, () -> bicicleta.liberarVeiculo());
        bicicleta.voltarParaOperacao();
        bicicleta.liberarVeiculo();
        assertThrows(VeiculoIndisponivelException.class, () -> bicicleta.liberarVeiculo());
    }

    @Test
    void devolverVeiculoNaEstacaoDeDestino() throws Exception {
        Estacao destino = new Estacao("Vila Germânica", 2);
        estacao.removerVeiculo(bicicleta);
        bicicleta.liberarVeiculo();
        bicicleta.devolverVeiculo(destino, 10);
        assertEquals(StatusVeiculo.DISPONIVEL, bicicleta.getStatus());
        assertSame(destino, bicicleta.getEstacao());
        assertTrue(destino.getVeiculos().contains(bicicleta));
    }

    @Test
    void naoDevolveVeiculoQueNaoEstaEmUso() throws Exception {
        Estacao destino = new Estacao("Vila Germânica", 2);
        assertThrows(RegraNegocioException.class, () -> bicicleta.devolverVeiculo(destino, 10));
    }

    @Test
    void naoDevolveEmEstacaoLotada() throws Exception {
        Estacao lotada = new Estacao("FURB Campus 1", 1);
        lotada.addVeiculo(new BicicletaMecanica(20, "Caloi"));
        estacao.removerVeiculo(bicicleta);
        bicicleta.liberarVeiculo();
        assertThrows(EstacaoLotadaException.class, () -> bicicleta.devolverVeiculo(lotada, 10));
        assertEquals(StatusVeiculo.EM_USO, bicicleta.getStatus());
        assertNull(bicicleta.getEstacao());
    }

    @Test
    void enviarParaManutencaoEVoltar() throws Exception {
        bicicleta.enviarParaManutencao();
        assertEquals(StatusVeiculo.MANUTENCAO, bicicleta.getStatus());
        assertThrows(VeiculoIndisponivelException.class, () -> bicicleta.enviarParaManutencao());
        bicicleta.voltarParaOperacao();
        assertEquals(StatusVeiculo.DISPONIVEL, bicicleta.getStatus());
    }

    @Test
    void naoEnviaParaManutencaoVeiculoEmUso() throws Exception {
        bicicleta.liberarVeiculo();
        assertThrows(VeiculoIndisponivelException.class, () -> bicicleta.enviarParaManutencao());
    }

    @Test
    void voltarParaOperacaoSoSeEstiverEmManutencao() {
        assertThrows(RegraNegocioException.class, () -> bicicleta.voltarParaOperacao());
    }

    @Test
    void bicicletaMecanicaNaoTemBateria() {
        assertThrows(RegraNegocioException.class, () -> bicicleta.recarregarBateria());
    }

    @Test
    void descricaoMostraCodigoTipoModeloESituacao() {
        assertEquals("10 - Bicicleta mecânica - Caloi - Disponível", bicicleta.getDescricao());
    }

    @Test
    void toStringIgualADescricao() {
        assertEquals(bicicleta.getDescricao(), bicicleta.toString());
    }

    @Test
    void statusTemNomeLegivel() {
        assertEquals("Em manutenção", StatusVeiculo.MANUTENCAO.toString());
        assertEquals("Em uso", StatusVeiculo.EM_USO.toString());
    }
}
