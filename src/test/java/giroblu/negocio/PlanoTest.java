package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlanoTest {

    @Test
    void avulsoBicicletaMecanica12Minutos() throws Exception {
        assertEquals(2.80, new PlanoAvulso().calcularValor(new BicicletaMecanica(1, "Caloi"), 12), 0.001);
    }

    @Test
    void universitarioBicicletaEletrica25Minutos() throws Exception {
        assertEquals(4.75, new PlanoUniversitario().calcularValor(new BicicletaEletrica(2, "E-Bike", 80), 25), 0.001);
    }

    @Test
    void mensalPatinete20Minutos() throws Exception {
        assertEquals(2.00, new PlanoMensal().calcularValor(new PatineteEletrico(3, "Xiaomi", 60), 20), 0.001);
    }

    @Test
    void universitarioPatinetePagaPrecoCheio() throws Exception {
        assertEquals(7.00, new PlanoUniversitario().calcularValor(new PatineteEletrico(4, "Xiaomi", 100), 10), 0.001);
    }

    @Test
    void mensalBicicletaMecanica10MinutosEhGratis() throws Exception {
        assertEquals(0.00, new PlanoMensal().calcularValor(new BicicletaMecanica(5, "Caloi"), 10), 0.001);
    }

    @Test
    void mensalExatamente15MinutosEhGratis() throws Exception {
        assertEquals(0.00, new PlanoMensal().calcularValor(new BicicletaEletrica(6, "E-Bike", 100), 15), 0.001);
    }

    @Test
    void mensalCobraSoOsMinutosDepoisDos15() throws Exception {
        assertEquals(0.30, new PlanoMensal().calcularValor(new BicicletaEletrica(7, "E-Bike", 100), 16), 0.001);
    }

    @Test
    void universitarioArredondaParaCima() throws Exception {
        assertEquals(1.48, new PlanoUniversitario().calcularValor(new BicicletaMecanica(8, "Caloi"), 13), 0.001);
    }

    @Test
    void avulsoViagemDeZeroMinutosPagaSoDesbloqueio() throws Exception {
        assertEquals(3.00, new PlanoAvulso().calcularValor(new PatineteEletrico(9, "Xiaomi", 100), 0), 0.001);
    }

    @Test
    void nomesDosPlanos() {
        assertEquals("Avulso", new PlanoAvulso().getNome());
        assertEquals("Mensal", new PlanoMensal().getNome());
        assertEquals("Universitário", new PlanoUniversitario().getNome());
    }

    @Test
    void toStringMostraNomeDoPlano() {
        assertEquals("Mensal", new PlanoMensal().toString());
    }
}
