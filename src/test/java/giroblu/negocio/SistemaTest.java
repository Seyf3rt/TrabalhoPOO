package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SistemaTest {

    private Sistema sistema;
    private Estacao prainha;
    private Estacao vilaGermanica;

    private static LocalDateTime dh(String texto) {
        return LocalDateTime.parse(texto, Formatos.DATA_HORA);
    }

    @BeforeEach
    void setUp() throws Exception {
        sistema = new Sistema();
        prainha = sistema.cadastrarEstacao("Prainha", 3);
        vilaGermanica = sistema.cadastrarEstacao("Vila Germânica", 2);
    }

    private Usuario usuarioComSaldo(String nome, Plano plano, double saldo) throws Exception {
        Usuario u = sistema.cadastrarUsuario(nome, nome.toLowerCase() + "@email.com", plano);
        sistema.registrarRecarga(u, dh("01/11/2026 07:00"), saldo);
        return u;
    }

    private Viagem viajar(Usuario u, Veiculo v, String inicio, String fim) throws Exception {
        sistema.iniciarViagem(u, v, dh(inicio));
        return sistema.encerrarViagem(u, vilaGermanica, dh(fim));
    }

    @Test
    void cadastrarEstacaoNaoRepeteNome() {
        assertEquals(2, sistema.getEstacoes().size());
        assertThrows(RegraNegocioException.class, () -> sistema.cadastrarEstacao("prainha", 4));
    }

    @Test
    void cadastrarVeiculoPosicionaNaEstacao() throws Exception {
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        sistema.cadastrarVeiculo(bicicleta, prainha);
        assertSame(prainha, bicicleta.getEstacao());
        assertEquals(2, prainha.getVagasLivres());
    }

    @Test
    void cadastrarVeiculoNaoRepeteCodigo() throws Exception {
        sistema.cadastrarVeiculo(new BicicletaMecanica(1, "Caloi"), prainha);
        assertThrows(RegraNegocioException.class,
                () -> sistema.cadastrarVeiculo(new PatineteEletrico(1, "Xiaomi", 100), prainha));
    }

    @Test
    void cadastrarVeiculoEmEstacaoLotada() throws Exception {
        sistema.cadastrarVeiculo(new BicicletaMecanica(1, "Caloi"), vilaGermanica);
        sistema.cadastrarVeiculo(new BicicletaMecanica(2, "Caloi"), vilaGermanica);
        assertThrows(EstacaoLotadaException.class,
                () -> sistema.cadastrarVeiculo(new BicicletaMecanica(3, "Caloi"), vilaGermanica));
        assertEquals(2, sistema.getVeiculos().size());
    }

    @Test
    void cadastrarUsuarioNaoRepeteEmail() throws Exception {
        sistema.cadastrarUsuario("Carla", "carla@email.com", new PlanoAvulso());
        assertThrows(RegraNegocioException.class,
                () -> sistema.cadastrarUsuario("Outra Carla", "CARLA@email.com", new PlanoMensal()));
    }

    @Test
    void registrarRecarga() throws Exception {
        Usuario carla = sistema.cadastrarUsuario("Carla", "carla@email.com", new PlanoAvulso());
        sistema.registrarRecarga(carla, dh("01/11/2026 07:00"), 12.5);
        assertEquals(12.5, carla.getSaldo());
        assertThrows(RegraNegocioException.class, () -> sistema.registrarRecarga(carla, dh("01/11/2026 07:00"), -1));
    }

    @Test
    void iniciarViagemRetiraVeiculoDaEstacao() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 10);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        sistema.cadastrarVeiculo(bicicleta, prainha);

        Viagem viagem = sistema.iniciarViagem(carla, bicicleta, dh("02/11/2026 10:00"));

        assertTrue(carla.estaViajando());
        assertEquals(StatusVeiculo.EM_USO, bicicleta.getStatus());
        assertNull(bicicleta.getEstacao());
        assertEquals(3, prainha.getVagasLivres());
        assertSame(prainha, viagem.getEstacaoInicial());
        assertEquals(1, sistema.getViagens().size());
    }

    @Test
    void iniciarViagemComSaldoInsuficiente() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 4);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        sistema.cadastrarVeiculo(bicicleta, prainha);
        assertThrows(SaldoInsuficienteException.class,
                () -> sistema.iniciarViagem(carla, bicicleta, dh("02/11/2026 10:00")));
        assertEquals(StatusVeiculo.DISPONIVEL, bicicleta.getStatus());
        assertSame(prainha, bicicleta.getEstacao());
    }

    @Test
    void iniciarViagemComVeiculoIndisponivelNaoAlteraNada() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 10);
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 15);
        sistema.cadastrarVeiculo(patinete, prainha);
        assertThrows(VeiculoIndisponivelException.class,
                () -> sistema.iniciarViagem(carla, patinete, dh("02/11/2026 10:00")));
        assertFalse(carla.estaViajando());
        assertSame(prainha, patinete.getEstacao());
        assertTrue(sistema.getViagens().isEmpty());
    }

    @Test
    void naoIniciaDuasViagensAoMesmoTempo() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 20);
        BicicletaMecanica b1 = new BicicletaMecanica(1, "Caloi");
        BicicletaMecanica b2 = new BicicletaMecanica(2, "Caloi");
        sistema.cadastrarVeiculo(b1, prainha);
        sistema.cadastrarVeiculo(b2, prainha);
        sistema.iniciarViagem(carla, b1, dh("02/11/2026 10:00"));
        assertThrows(RegraNegocioException.class, () -> sistema.iniciarViagem(carla, b2, dh("02/11/2026 10:01")));
    }

    @Test
    void exemplosDoEnunciado() throws Exception {
        BicicletaMecanica mecanica1 = new BicicletaMecanica(1, "Caloi");
        BicicletaEletrica eletrica = new BicicletaEletrica(2, "E-Bike", 80);
        PatineteEletrico patinete60 = new PatineteEletrico(3, "Xiaomi", 60);
        sistema.cadastrarVeiculo(mecanica1, prainha);
        sistema.cadastrarVeiculo(eletrica, prainha);
        sistema.cadastrarVeiculo(patinete60, prainha);
        Estacao campus = sistema.cadastrarEstacao("FURB Campus 1", 5);
        PatineteEletrico patinete100 = new PatineteEletrico(4, "Xiaomi", 100);
        BicicletaMecanica mecanica2 = new BicicletaMecanica(5, "Caloi");
        sistema.cadastrarVeiculo(patinete100, campus);
        sistema.cadastrarVeiculo(mecanica2, campus);

        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 10);
        Usuario ana = usuarioComSaldo("Ana", new PlanoUniversitario(), 10);
        Usuario bruno = usuarioComSaldo("Bruno", new PlanoMensal(), 10);
        Usuario diego = usuarioComSaldo("Diego", new PlanoUniversitario(), 10);
        Usuario eva = usuarioComSaldo("Eva", new PlanoMensal(), 10);

        assertEquals(2.80, viajar(carla, mecanica1, "02/11/2026 10:00", "02/11/2026 10:12").getValor(), 0.001);
        assertEquals(4.75, viajar(ana, eletrica, "02/11/2026 10:00", "02/11/2026 10:25").getValor(), 0.001);
        assertEquals(55, eletrica.getPercentualBateria());

        sistema.iniciarViagem(bruno, patinete60, dh("02/11/2026 11:00"));
        assertEquals(2.00, sistema.encerrarViagem(bruno, campus, dh("02/11/2026 11:20")).getValor(), 0.001);
        assertEquals(20, patinete60.getPercentualBateria());

        sistema.iniciarViagem(diego, patinete100, dh("02/11/2026 12:00"));
        assertEquals(7.00, sistema.encerrarViagem(diego, campus, dh("02/11/2026 12:10")).getValor(), 0.001);
        assertEquals(80, patinete100.getPercentualBateria());

        sistema.iniciarViagem(eva, mecanica2, dh("02/11/2026 13:00"));
        assertEquals(0.00, sistema.encerrarViagem(eva, campus, dh("02/11/2026 13:10")).getValor(), 0.001);

        assertEquals(7.20, carla.getSaldo(), 0.001);
        assertEquals(5.25, ana.getSaldo(), 0.001);
        assertEquals(8.00, bruno.getSaldo(), 0.001);
        assertEquals(3.00, diego.getSaldo(), 0.001);
        assertEquals(10.00, eva.getSaldo(), 0.001);
    }

    @Test
    void debitoPodeDeixarSaldoNegativoEBloqueiaNovaViagem() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 5);
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 100);
        sistema.cadastrarVeiculo(patinete, prainha);

        Viagem viagem = viajar(carla, patinete, "02/11/2026 10:00", "02/11/2026 10:30");

        assertEquals(15.00, viagem.getValor(), 0.001);
        assertEquals(-10.00, carla.getSaldo(), 0.001);
        assertThrows(SaldoInsuficienteException.class,
                () -> sistema.iniciarViagem(carla, patinete, dh("02/11/2026 11:00")));
        sistema.registrarRecarga(carla, dh("02/11/2026 11:05"), 15);
        sistema.iniciarViagem(carla, patinete, dh("02/11/2026 11:10"));
        assertTrue(carla.estaViajando());
    }

    @Test
    void encerrarViagemSemViagemEmAndamento() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 10);
        assertThrows(RegraNegocioException.class,
                () -> sistema.encerrarViagem(carla, prainha, dh("02/11/2026 10:00")));
    }

    @Test
    void encerrarViagemEmEstacaoLotadaMantemViagemAberta() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 10);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        sistema.cadastrarVeiculo(bicicleta, prainha);
        sistema.cadastrarVeiculo(new BicicletaMecanica(2, "Caloi"), vilaGermanica);
        sistema.cadastrarVeiculo(new BicicletaMecanica(3, "Caloi"), vilaGermanica);

        sistema.iniciarViagem(carla, bicicleta, dh("02/11/2026 10:00"));
        assertThrows(EstacaoLotadaException.class,
                () -> sistema.encerrarViagem(carla, vilaGermanica, dh("02/11/2026 10:10")));

        assertTrue(carla.estaViajando());
        assertEquals(10.0, carla.getSaldo());
        assertEquals(StatusVeiculo.EM_USO, bicicleta.getStatus());
        assertEquals(1.15, sistema.encerrarViagem(carla, prainha, dh("02/11/2026 10:01")).getValor(), 0.001);
    }

    @Test
    void manutencaoERecargaDeBateria() throws Exception {
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 10);
        sistema.cadastrarVeiculo(patinete, prainha);
        sistema.enviarParaManutencao(patinete);
        assertEquals(StatusVeiculo.MANUTENCAO, patinete.getStatus());
        sistema.recarregarBateria(patinete);
        assertEquals(100, patinete.getPercentualBateria());
        sistema.voltarParaOperacao(patinete);
        assertEquals(StatusVeiculo.DISPONIVEL, patinete.getStatus());
        assertThrows(RegraNegocioException.class, () -> sistema.voltarParaOperacao(patinete));
        assertThrows(RegraNegocioException.class,
                () -> sistema.recarregarBateria(new BicicletaMecanica(9, "Caloi")));
    }

    @Test
    void consultarDisponiveis() throws Exception {
        sistema.cadastrarVeiculo(new BicicletaEletrica(1, "E-Bike", 70), prainha);
        String texto = sistema.consultarDisponiveis(prainha);
        assertTrue(texto.contains("Bicicleta elétrica"));
        assertTrue(texto.contains("bateria 70%"));
    }

    @Test
    void emitirExtrato() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 10);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        sistema.cadastrarVeiculo(bicicleta, prainha);
        viajar(carla, bicicleta, "02/11/2026 10:00", "02/11/2026 10:12");
        String extrato = sistema.emitirExtrato(carla);
        assertTrue(extrato.contains("R$ 2,80"));
        assertTrue(extrato.endsWith("Saldo atual: R$ 7,20"));
    }

    @Test
    void consultarFaturamentoPorTipoNoPeriodo() throws Exception {
        Usuario carla = usuarioComSaldo("Carla", new PlanoAvulso(), 100);
        BicicletaMecanica bicicleta = new BicicletaMecanica(1, "Caloi");
        PatineteEletrico patinete = new PatineteEletrico(2, "Xiaomi", 100);
        sistema.cadastrarVeiculo(bicicleta, prainha);
        sistema.cadastrarVeiculo(patinete, prainha);
        sistema.cadastrarVeiculo(new BicicletaEletrica(3, "E-Bike", 100), prainha);

        viajar(carla, bicicleta, "02/11/2026 10:00", "02/11/2026 10:12");
        sistema.iniciarViagem(carla, patinete, dh("02/11/2026 23:50"));
        sistema.encerrarViagem(carla, prainha, dh("03/11/2026 00:00"));
        sistema.iniciarViagem(carla, bicicleta, dh("05/11/2026 10:00"));
        sistema.encerrarViagem(carla, prainha, dh("05/11/2026 10:10"));

        String relatorio = sistema.consultarFaturamento(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 3));
        assertTrue(relatorio.contains("Bicicleta mecânica      1 viagem(ns)   R$ 2,80"));
        assertTrue(relatorio.contains("Patinete elétrico       1 viagem(ns)   R$ 7,00"));
        assertTrue(relatorio.contains("Bicicleta elétrica      0 viagem(ns)   R$ 0,00"));
        assertTrue(relatorio.contains("Total geral             2 viagem(ns)   R$ 9,80"));
    }

    @Test
    void consultarFaturamentoComDatasInvalidas() {
        assertThrows(RegraNegocioException.class,
                () -> sistema.consultarFaturamento(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 1)));
    }

    @Test
    void buscarPorChave() throws Exception {
        Usuario carla = sistema.cadastrarUsuario("Carla", "carla@email.com", new PlanoAvulso());
        BicicletaMecanica bicicleta = new BicicletaMecanica(7, "Caloi");
        sistema.cadastrarVeiculo(bicicleta, prainha);

        assertSame(prainha, sistema.buscarEstacao("  prainha "));
        assertSame(bicicleta, sistema.buscarVeiculo(7));
        assertSame(carla, sistema.buscarUsuario("Carla@Email.com"));
        assertThrows(RegraNegocioException.class, () -> sistema.buscarEstacao("Centro"));
        assertThrows(RegraNegocioException.class, () -> sistema.buscarVeiculo(99));
        assertThrows(RegraNegocioException.class, () -> sistema.buscarUsuario("x@y.com"));
    }
}
