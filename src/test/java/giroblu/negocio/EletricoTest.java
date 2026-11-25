package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EletricoTest {

    private void usar(Eletrico veiculo, int minutos) throws Exception {
        veiculo.liberarVeiculo();
        veiculo.devolverVeiculo(new Estacao("Destino", 1), minutos);
    }

    @Test
    void naoAceitaBateriaForaDaFaixa() {
        assertThrows(RegraNegocioException.class, () -> new BicicletaEletrica(1, "E-Bike", 101));
        assertThrows(RegraNegocioException.class, () -> new PatineteEletrico(1, "Xiaomi", -1));
    }

    @Test
    void minimoOkCom20PorCento() throws Exception {
        assertTrue(new BicicletaEletrica(1, "E-Bike", 20).minimoOk());
        assertFalse(new BicicletaEletrica(2, "E-Bike", 19).minimoOk());
    }

    @Test
    void naoLiberaComMenosDe20PorCento() throws Exception {
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 19);
        assertThrows(VeiculoIndisponivelException.class, () -> patinete.liberarVeiculo());
        assertEquals(StatusVeiculo.DISPONIVEL, patinete.getStatus());
    }

    @Test
    void bicicletaEletricaGasta1PorCentoPorMinuto() throws Exception {
        BicicletaEletrica bicicleta = new BicicletaEletrica(1, "E-Bike", 80);
        usar(bicicleta, 25);
        assertEquals(55, bicicleta.getPercentualBateria());
    }

    @Test
    void patineteGasta2PorCentoPorMinuto() throws Exception {
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 60);
        usar(patinete, 20);
        assertEquals(20, patinete.getPercentualBateria());
    }

    @Test
    void bateriaNuncaFicaNegativa() throws Exception {
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 30);
        usar(patinete, 40);
        assertEquals(0, patinete.getPercentualBateria());
    }

    @Test
    void consumoPorMinuto() throws Exception {
        assertEquals(1, new BicicletaEletrica(1, "E-Bike", 100).getConsumoPorMinuto());
        assertEquals(2, new PatineteEletrico(2, "Xiaomi", 100).getConsumoPorMinuto());
    }

    @Test
    void recarregarBateriaVoltaPara100() throws Exception {
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 10);
        patinete.recarregarBateria();
        assertEquals(100, patinete.getPercentualBateria());
    }

    @Test
    void recarregaMesmoEmManutencao() throws Exception {
        BicicletaEletrica bicicleta = new BicicletaEletrica(1, "E-Bike", 5);
        bicicleta.enviarParaManutencao();
        bicicleta.recarregarBateria();
        assertEquals(100, bicicleta.getPercentualBateria());
    }

    @Test
    void naoRecarregaVeiculoEmUso() throws Exception {
        BicicletaEletrica bicicleta = new BicicletaEletrica(1, "E-Bike", 50);
        bicicleta.liberarVeiculo();
        assertThrows(VeiculoIndisponivelException.class, () -> bicicleta.recarregarBateria());
        assertEquals(50, bicicleta.getPercentualBateria());
    }

    @Test
    void descricaoMostraBateria() throws Exception {
        assertEquals("7 - Patinete elétrico - Xiaomi - Disponível - bateria 60%",
                new PatineteEletrico(7, "Xiaomi", 60).getDescricao());
    }
}
