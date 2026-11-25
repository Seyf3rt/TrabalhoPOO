package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EstacaoTest {

    @Test
    void naoAceitaDadosInvalidos() {
        assertThrows(RegraNegocioException.class, () -> new Estacao("", 3));
        assertThrows(RegraNegocioException.class, () -> new Estacao("Prainha", 0));
    }

    @Test
    void verificarVagas() throws Exception {
        Estacao estacao = new Estacao("Prainha", 1);
        assertTrue(estacao.verificarVagas());
        estacao.addVeiculo(new BicicletaMecanica(1, "Caloi"));
        assertFalse(estacao.verificarVagas());
        assertEquals(0, estacao.getVagasLivres());
    }

    @Test
    void naoGuardaMaisVeiculosQueVagas() throws Exception {
        Estacao estacao = new Estacao("Prainha", 1);
        estacao.addVeiculo(new BicicletaMecanica(1, "Caloi"));
        assertThrows(EstacaoLotadaException.class, () -> estacao.addVeiculo(new BicicletaMecanica(2, "Caloi")));
        assertEquals(1, estacao.getVeiculos().size());
    }

    @Test
    void veiculoEmManutencaoOcupaVaga() throws Exception {
        Estacao estacao = new Estacao("Prainha", 1);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        estacao.addVeiculo(bicicleta);
        bicicleta.enviarParaManutencao();
        assertFalse(estacao.verificarVagas());
    }

    @Test
    void removerVeiculoLiberaVaga() throws Exception {
        Estacao estacao = new Estacao("Prainha", 1);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        estacao.addVeiculo(bicicleta);
        estacao.removerVeiculo(bicicleta);
        assertTrue(estacao.verificarVagas());
        assertNull(bicicleta.getEstacao());
    }

    @Test
    void listarDisponiveisMostraTipoECargaESomenteDisponiveis() throws Exception {
        Estacao estacao = new Estacao("Prainha", 5);
        estacao.addVeiculo(new BicicletaMecanica(1, "Caloi"));
        estacao.addVeiculo(new PatineteEletrico(2, "Xiaomi", 45));
        BicicletaEletrica emManutencao = new BicicletaEletrica(3, "E-Bike", 90);
        estacao.addVeiculo(emManutencao);
        emManutencao.enviarParaManutencao();

        String lista = estacao.listarDisponiveis();
        assertTrue(lista.contains("1 - Bicicleta mecânica - Caloi"));
        assertTrue(lista.contains("2 - Patinete elétrico - Xiaomi - Disponível - bateria 45%"));
        assertFalse(lista.contains("3 - "));
        assertTrue(lista.contains("Vagas livres: 2 de 5"));
    }

    @Test
    void listarDisponiveisSemVeiculos() throws Exception {
        assertTrue(new Estacao("Prainha", 2).listarDisponiveis().contains("nenhum veículo disponível"));
    }

    @Test
    void toStringMostraNome() throws Exception {
        assertEquals("Prainha", new Estacao(" Prainha ", 2).toString());
    }
}
