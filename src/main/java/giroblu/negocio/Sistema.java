package giroblu.negocio;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ponto de entrada da camada de negócio. Guarda estações, veículos, usuários e viagens
 * e coordena as operações da GiroBlu.
 */
public class Sistema implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Estacao> estacoes = new ArrayList<>();
    private final List<Veiculo> veiculos = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Viagem> viagens = new ArrayList<>();

    /**
     * Cadastra uma estação nova.
     *
     * @param nome nome da estação (não pode repetir)
     * @param quantidadeVagas quantidade de vagas
     * @return a estação cadastrada
     * @throws RegraNegocioException se os dados forem inválidos ou o nome já existir
     */
    public Estacao cadastrarEstacao(String nome, int quantidadeVagas) throws RegraNegocioException {
        Estacao estacao = new Estacao(nome, quantidadeVagas);
        for (Estacao e : estacoes) {
            if (e.getNome().equalsIgnoreCase(estacao.getNome())) {
                throw new RegraNegocioException("Já existe uma estação chamada " + e.getNome() + ".");
            }
        }
        estacoes.add(estacao);
        return estacao;
    }

    /**
     * Cadastra um veículo já posicionado em uma estação.
     *
     * @param veiculo veículo a cadastrar
     * @param estacao estação onde ele fica
     * @throws RegraNegocioException se o código já existir ou a estação estiver lotada
     */
    public void cadastrarVeiculo(Veiculo veiculo, Estacao estacao) throws RegraNegocioException {
        for (Veiculo v : veiculos) {
            if (v.getCodigoIdentificacao() == veiculo.getCodigoIdentificacao()) {
                throw new RegraNegocioException("Já existe um veículo com o código "
                        + veiculo.getCodigoIdentificacao() + ".");
            }
        }
        estacao.addVeiculo(veiculo);
        veiculos.add(veiculo);
    }

    /**
     * Cadastra um usuário novo com a carteira zerada.
     *
     * @param nome nome do usuário
     * @param email e-mail (não pode repetir)
     * @param plano plano escolhido
     * @return o usuário cadastrado
     * @throws RegraNegocioException se os dados forem inválidos ou o e-mail já existir
     */
    public Usuario cadastrarUsuario(String nome, String email, Plano plano) throws RegraNegocioException {
        Usuario usuario = new Usuario(nome, email, plano);
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(usuario.getEmail())) {
                throw new RegraNegocioException("Já existe um usuário com o e-mail " + u.getEmail() + ".");
            }
        }
        usuarios.add(usuario);
        return usuario;
    }

    /**
     * Registra uma recarga na carteira de um usuário.
     *
     * @param usuario usuário
     * @param dataHora data e hora da recarga
     * @param valor valor positivo
     * @return a recarga registrada
     * @throws RegraNegocioException se a data ou o valor forem inválidos
     */
    public Recarga registrarRecarga(Usuario usuario, LocalDateTime dataHora, double valor)
            throws RegraNegocioException {
        return usuario.addCreditos(dataHora, valor);
    }

    /**
     * Inicia uma viagem: libera o veículo e o retira da estação.
     *
     * @param usuario usuário que vai viajar
     * @param veiculo veículo escolhido
     * @param dataHoraInicio data e hora de início
     * @return a viagem iniciada
     * @throws RegraNegocioException se o usuário ou o veículo não puderem iniciar a viagem
     */
    public Viagem iniciarViagem(Usuario usuario, Veiculo veiculo, LocalDateTime dataHoraInicio)
            throws RegraNegocioException {
        if (dataHoraInicio == null) {
            throw new RegraNegocioException("Informe a data e a hora de início.");
        }
        usuario.validarInicioViagem();
        Estacao origem = veiculo.getEstacao();
        veiculo.liberarVeiculo();
        origem.removerVeiculo(veiculo);
        Viagem viagem = new Viagem(usuario, veiculo, origem, dataHoraInicio);
        usuario.iniciarViagem(viagem);
        viagens.add(viagem);
        return viagem;
    }

    /**
     * Encerra a viagem em andamento do usuário.
     *
     * @param usuario usuário que está viajando
     * @param destino estação de devolução
     * @param dataHoraFim data e hora de término
     * @return a viagem encerrada, com duração e valor calculados
     * @throws RegraNegocioException se não houver viagem, a data for inválida ou a estação estiver lotada
     */
    public Viagem encerrarViagem(Usuario usuario, Estacao destino, LocalDateTime dataHoraFim)
            throws RegraNegocioException {
        Viagem viagem = usuario.getViagemAtual();
        if (viagem == null) {
            throw new RegraNegocioException(usuario.getNome() + " não tem viagem em andamento.");
        }
        viagem.finalizarViagem(destino, dataHoraFim);
        return viagem;
    }

    /**
     * @param veiculo veículo que vai para manutenção
     * @throws RegraNegocioException se o veículo estiver em uso ou já em manutenção
     */
    public void enviarParaManutencao(Veiculo veiculo) throws RegraNegocioException {
        veiculo.enviarParaManutencao();
    }

    /**
     * @param veiculo veículo que volta da manutenção
     * @throws RegraNegocioException se o veículo não estiver em manutenção
     */
    public void voltarParaOperacao(Veiculo veiculo) throws RegraNegocioException {
        veiculo.voltarParaOperacao();
    }

    /**
     * @param veiculo veículo a recarregar
     * @throws RegraNegocioException se o veículo não tiver bateria ou estiver em uso
     */
    public void recarregarBateria(Veiculo veiculo) throws RegraNegocioException {
        veiculo.recarregarBateria();
    }

    /**
     * @param estacao estação consultada
     * @return lista dos veículos disponíveis na estação
     */
    public String consultarDisponiveis(Estacao estacao) {
        return estacao.listarDisponiveis();
    }

    /**
     * @param usuario usuário
     * @return extrato da carteira do usuário
     */
    public String emitirExtrato(Usuario usuario) {
        return usuario.emitirExtrato();
    }

    /**
     * Soma as viagens encerradas no período, separadas por tipo de veículo.
     *
     * @param dataInicial primeiro dia do período
     * @param dataFinal último dia do período
     * @return texto com quantidade e valor por tipo e o total geral
     * @throws RegraNegocioException se as datas forem inválidas
     */
    public String consultarFaturamento(LocalDate dataInicial, LocalDate dataFinal) throws RegraNegocioException {
        if (dataInicial == null || dataFinal == null || dataFinal.isBefore(dataInicial)) {
            throw new RegraNegocioException("Período inválido: a data final não pode ser anterior à inicial.");
        }
        Map<String, Integer> quantidades = new LinkedHashMap<>();
        Map<String, Double> valores = new LinkedHashMap<>();
        for (Veiculo v : veiculos) {
            quantidades.putIfAbsent(v.getTipo(), 0);
            valores.putIfAbsent(v.getTipo(), 0.0);
        }
        int totalViagens = 0;
        double totalValor = 0;
        for (Viagem viagem : viagens) {
            if (viagem.isEncerrada()) {
                LocalDate dia = viagem.getDataHoraFim().toLocalDate();
                if (!dia.isBefore(dataInicial) && !dia.isAfter(dataFinal)) {
                    String tipo = viagem.getVeiculo().getTipo();
                    quantidades.put(tipo, quantidades.getOrDefault(tipo, 0) + 1);
                    valores.put(tipo, valores.getOrDefault(tipo, 0.0) + viagem.getValor());
                    totalViagens++;
                    totalValor += viagem.getValor();
                }
            }
        }
        StringBuilder sb = new StringBuilder("Faturamento de " + dataInicial.format(Formatos.DATA)
                + " a " + dataFinal.format(Formatos.DATA) + "\n");
        for (String tipo : quantidades.keySet()) {
            sb.append(String.format("%-20s %4d viagem(ns)   %s%n", tipo, quantidades.get(tipo),
                    Formatos.moeda(valores.get(tipo))));
        }
        sb.append(String.format("%-20s %4d viagem(ns)   %s", "Total geral", totalViagens,
                Formatos.moeda(totalValor)));
        return sb.toString();
    }

    /**
     * @param nome nome da estação (sem diferenciar maiúsculas)
     * @return a estação encontrada
     * @throws RegraNegocioException se não existir
     */
    public Estacao buscarEstacao(String nome) throws RegraNegocioException {
        for (Estacao e : estacoes) {
            if (e.getNome().equalsIgnoreCase(nome == null ? "" : nome.trim())) {
                return e;
            }
        }
        throw new RegraNegocioException("Estação não encontrada: " + nome);
    }

    /**
     * @param codigo código do veículo
     * @return o veículo encontrado
     * @throws RegraNegocioException se não existir
     */
    public Veiculo buscarVeiculo(int codigo) throws RegraNegocioException {
        for (Veiculo v : veiculos) {
            if (v.getCodigoIdentificacao() == codigo) {
                return v;
            }
        }
        throw new RegraNegocioException("Veículo não encontrado: " + codigo);
    }

    /**
     * @param email e-mail do usuário
     * @return o usuário encontrado
     * @throws RegraNegocioException se não existir
     */
    public Usuario buscarUsuario(String email) throws RegraNegocioException {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email == null ? "" : email.trim())) {
                return u;
            }
        }
        throw new RegraNegocioException("Usuário não encontrado: " + email);
    }

    public List<Estacao> getEstacoes() {
        return Collections.unmodifiableList(estacoes);
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(veiculos);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public List<Viagem> getViagens() {
        return Collections.unmodifiableList(viagens);
    }
}
