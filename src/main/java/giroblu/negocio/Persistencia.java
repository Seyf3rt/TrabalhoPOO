package giroblu.negocio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Grava e lê todos os dados do sistema em um arquivo binário.
 */
public class Persistencia {

    /** Arquivo usado pelo programa. */
    public static final Path ARQUIVO_PADRAO = Path.of("dados", "giroblu.dat");

    private final Path arquivo;

    /**
     * @param arquivo caminho do arquivo de dados
     */
    public Persistencia(Path arquivo) {
        this.arquivo = arquivo;
    }

    /**
     * Lê os dados gravados. Se o arquivo ainda não existe, devolve um sistema vazio.
     *
     * @return o sistema recuperado do disco
     * @throws IOException se o arquivo não puder ser lido
     */
    public Sistema carregar() throws IOException {
        if (!Files.exists(arquivo)) {
            return new Sistema();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(arquivo))) {
            return (Sistema) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Arquivo de dados inválido: " + arquivo, e);
        }
    }

    /**
     * Grava os dados em um arquivo temporário e depois substitui o arquivo principal,
     * para não corromper os dados se algo falhar no meio da gravação.
     *
     * @param sistema dados a gravar
     * @throws IOException se não for possível gravar
     */
    public void salvar(Sistema sistema) throws IOException {
        Path pasta = arquivo.toAbsolutePath().getParent();
        Files.createDirectories(pasta);
        Path temporario = pasta.resolve(arquivo.getFileName() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temporario))) {
            out.writeObject(sistema);
        }
        Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
    }
}
