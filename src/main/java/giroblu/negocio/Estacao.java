package giroblu.negocio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Estação onde os veículos ficam parados. Veículos disponíveis e em manutenção
 * ocupam vaga; veículos em uso não ficam na estação.
 */
public class Estacao implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nome;
    private final int quantidadeVagas;
    private final List<Veiculo> veiculos = new ArrayList<>();

    /**
     * @param nome nome da estação
     * @param quantidadeVagas número de vagas (maior que zero)
     * @throws RegraNegocioException se algum dado for inválido
     */
    public Estacao(String nome, int quantidadeVagas) throws RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe o nome da estação.");
        }
        if (quantidadeVagas <= 0) {
            throw new RegraNegocioException("A estação deve ter pelo menos uma vaga.");
        }
        this.nome = nome.trim();
        this.quantidadeVagas = quantidadeVagas;
    }

    /**
     * @return true se ainda existe vaga livre
     */
    public boolean verificarVagas() {
        return veiculos.size() < quantidadeVagas;
    }

    void addVeiculo(Veiculo veiculo) throws EstacaoLotadaException {
        if (!verificarVagas()) {
            throw new EstacaoLotadaException("A estação " + nome + " está lotada ("
                    + quantidadeVagas + " vagas).");
        }
        veiculos.add(veiculo);
        veiculo.setEstacao(this);
    }

    void removerVeiculo(Veiculo veiculo) {
        if (veiculos.remove(veiculo)) {
            veiculo.setEstacao(null);
        }
    }

    /**
     * Monta a lista dos veículos disponíveis na estação, mostrando o tipo de cada um
     * e a carga dos elétricos.
     *
     * @return texto com um veículo por linha
     */
    public String listarDisponiveis() {
        StringBuilder sb = new StringBuilder("Veículos disponíveis em " + nome + ":\n");
        int qtd = 0;
        for (Veiculo v : veiculos) {
            if (v.getStatus() == StatusVeiculo.DISPONIVEL) {
                sb.append("  ").append(v.getDescricao()).append('\n');
                qtd++;
            }
        }
        if (qtd == 0) {
            sb.append("  nenhum veículo disponível\n");
        }
        sb.append("Vagas livres: ").append(getVagasLivres()).append(" de ").append(quantidadeVagas);
        return sb.toString();
    }

    public int getVagasLivres() {
        return quantidadeVagas - veiculos.size();
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeVagas() {
        return quantidadeVagas;
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(veiculos);
    }

    @Override
    public String toString() {
        return nome;
    }
}
