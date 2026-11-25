package giroblu.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PersistenciaTest {

    @TempDir
    Path pasta;

    @Test
    void carregarSemArquivoDevolveSistemaVazio() throws Exception {
        Sistema sistema = new Persistencia(pasta.resolve("nao-existe.dat")).carregar();
        assertTrue(sistema.getEstacoes().isEmpty());
    }

    @Test
    void salvarECarregarMantemViagemEmAndamento() throws Exception {
        Sistema sistema = new Sistema();
        Estacao prainha = sistema.cadastrarEstacao("Prainha", 3);
        PatineteEletrico patinete = new PatineteEletrico(1, "Xiaomi", 60);
        sistema.cadastrarVeiculo(patinete, prainha);
        Usuario bruno = sistema.cadastrarUsuario("Bruno", "bruno@email.com", new PlanoMensal());
        sistema.registrarRecarga(bruno, LocalDateTime.of(2026, 11, 2, 9, 0), 10);
        sistema.iniciarViagem(bruno, patinete, LocalDateTime.of(2026, 11, 2, 11, 0));

        Persistencia persistencia = new Persistencia(pasta.resolve("dados").resolve("giroblu.dat"));
        persistencia.salvar(sistema);
        Sistema lido = persistencia.carregar();

        Usuario brunoLido = lido.buscarUsuario("bruno@email.com");
        Veiculo patineteLido = lido.buscarVeiculo(1);
        assertTrue(brunoLido.estaViajando());
        assertSame(patineteLido, brunoLido.getViagemAtual().getVeiculo());
        assertEquals(StatusVeiculo.EM_USO, patineteLido.getStatus());

        Viagem viagem = lido.encerrarViagem(brunoLido, lido.buscarEstacao("Prainha"),
                LocalDateTime.of(2026, 11, 2, 11, 20));
        assertEquals(2.00, viagem.getValor(), 0.001);
        assertEquals(8.00, brunoLido.getSaldo(), 0.001);
        assertEquals(20, ((Eletrico) patineteLido).getPercentualBateria());
    }

    @Test
    void arquivoInvalidoGeraErro() throws Exception {
        Path arquivo = pasta.resolve("lixo.dat");
        Files.writeString(arquivo, "isto não é um arquivo de dados");
        assertThrows(java.io.IOException.class, () -> new Persistencia(arquivo).carregar());
    }
}
