package giroblu.negocio;

/**
 * Situação em que um veículo pode estar em um determinado momento.
 */
public enum StatusVeiculo {
    DISPONIVEL("Disponível"),
    EM_USO("Em uso"),
    MANUTENCAO("Em manutenção");

    private final String descricao;

    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
