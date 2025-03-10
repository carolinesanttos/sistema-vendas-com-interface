/**
 * Sistema Operacional: Windows 10 - 64 Bits
 * IDE: IntelliJ
 * Versão Da Linguagem: Java JDK 22
 * Autor: Caroline Santos de Jesus
 * Componente Curricular: Algoritmos II
 * Concluído em: 28/10/2024
 * Declaro que este código foi elaborado por mim de forma individual e não contém nenhum trecho de código de outro
 * colega ou de outro autor, tais como provindos de livros e apostilas, e páginas ou documentos eletrônicos da Internet.
 * Qualquer trecho de código de outra autoria que não a minha está destacado com uma citação para o autor e a fonte do
 * código, e estou ciente que estes trechos não serão considerados para fins de avaliação.
 */

package uefs.vendaingressos.model;

import com.google.gson.annotations.Expose;
import uefs.vendaingressos.model.Ingresso;
import uefs.vendaingressos.model.excecoes.*;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.Feedback;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Classe que representa os eventos no sistema de venda de ingressos.
 * Contém informações como nome, descrição, data e status do evento.
 * Permite o cadastro, busca, adição e remoção de ingressos e assentos.
 * Gerencia feedbacks e as compras realizadas pelos usuários.
 */
public class Evento {
    @Expose
    private String nome;
    @Expose
    private String descricao;
    @Expose
    private Date data;
    @Expose
    private double valor;
    @Expose
    private boolean status;
    @Expose
    private Usuario usuario;
    @Expose
    private static List<Evento> eventosCadastrados = new ArrayList<>();
    @Expose
    private List<String> assentosDisponiveis = new ArrayList<>();
    @Expose
    private List<String> assentosReservados = new ArrayList<>();
    @Expose
    private List<Ingresso> ingressosDisponiveis = new ArrayList<>();
    @Expose
    private List<Ingresso> ingressosComprados = new ArrayList<>();
    @Expose
    private List<Feedback> feedbacks = new ArrayList<>();
    private PersistenciaEventos persistenciaEventos  = new PersistenciaEventos("detalhes-do-evento.json");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    public Evento(String nome, String descricao, Date data) {
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.status = true;
    }

    public Evento(Usuario usuario, String nome, String descricao, Date data) {
        this.usuario = usuario;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.status = true;
    }

    public Evento(String nome, String descricao, Date data, double valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.status = true;
        this.valor = valor;
    }

    public Evento() {
    }

    /**
     * Cadastra evento no sistema
     * Somente usuários administradores podem cadastrar.
     *
     * @param evento evento que será cadastrado.
     * @throws SecurityException se o usuário não for um administrador.
     */
    public void cadastroDeEventos(Evento evento) {
//        if (!evento.getUsuario().isAdmin()) {
//            throw new SecurityException("Somente administradores podem cadastrar eventos.");
//        }
        adicionarEvento(evento);

    }

    /**
     * Adiciona evento à lista de eventos cadastrados.
     *
     * @param evento evento que será adicionado.
     * @throws CadastroException se o evento já estiver cadastrado.
     */
    public void adicionarEvento(Evento evento) {
        boolean contemEvento = eventosCadastrados.contains(evento);
        if (!contemEvento) {
            eventosCadastrados.add(evento);
        } else {
            throw new CadastroException("Evento já cadastrado.");
        }
    }

    /**
     * Adiciona assento à lista de assentos disponíveis.
     *
     * @param assento assento que será adicionado.
     * @throws CadastroException se o assento já estiver na lista.
     */
    public void adicionarAssento(String assento) {
        boolean contemAssento = assentosDisponiveis.contains(assento);
        if (!contemAssento) {
            assentosDisponiveis.add(assento);
        } else {
            throw new CadastroException("Assento já adicionado.");
        }
    }

    /**
     * Gera os assentos do evento de forma automatizada, alternando entre linhas A, B, C, etc.
     * A quantidade de assentos é limitada pela quantidade especificada.
     *
     * @param quantidade quantidade de assentos a serem gerados.
     */
    public void gerarAssentos(int quantidade) {
        assentosDisponiveis.clear();

        int colunasPorLinha = 25;
        int totalLinhas = (int) Math.ceil((double) quantidade / colunasPorLinha);

        // Geração dos assentos alternando entre linhas A, B, C..
        for (int linha = 0; linha < totalLinhas; linha++) {
            char letraLinha = (char) ('A' + linha); // Converte índice para letra (A, B, C..)
            for (int coluna = 1; coluna <= colunasPorLinha; coluna++) {
                int numeroAssento = linha * colunasPorLinha + coluna; // Gera o número do assento
                if (numeroAssento > quantidade) {
                    break;
                }
                assentosDisponiveis.add(letraLinha + String.valueOf(coluna)); // Exemplo A1, B2, C3
            }
        }
    }

    /**
     * Remove assento da lista de assentos disponíveis.
     *
     * @param assento assento a ser removido.
     */
    public boolean reservarAssento(String assento) {
        if (assentosDisponiveis.contains(assento)) {
            assentosDisponiveis.remove(assento);
            assentosReservados.add(assento);
            return true;
        }
        return false;
    }

    /**
     * Libera um assento reservado, retornando-o à lista de assentos disponíveis.
     *
     * @param assento o identificador do assento a ser liberado.
     */
    public void liberarAssento(String assento) {
        if (assentosReservados.contains(assento)) {
            assentosReservados.remove(assento);
            assentosDisponiveis.add(assento);
        }
    }

    /**
     * Remove um assento da lista de assentos disponíveis.
     *
     * @param assento o identificador do assento a ser removido.
     * @throws CadastroException se o assento não estiver na lista de assentos disponíveis.
     */
    public void removerAssentoDisponivel(String assento)     {
        boolean contemAssento = assentosDisponiveis.contains(assento);
        if (contemAssento) {
            assentosDisponiveis.remove(assento);
        } else {
            System.out.println("Esse assento já foi removido.");
        }
    }

    /**
     * Verifica se assento está disponível.
     *
     * @param assento assento buscado.
     * @return true se o assento estiver disponível, false caso contrário.
     */
    public boolean buscaAssento (String assento) {
        boolean contemAssento = assentosDisponiveis.contains(assento);
        if (contemAssento) {
            return true;
        }
        return false;
    }

    /**
     * Adiciona ingresso à lista de ingressos disponíveis, caso ele ainda não esteja cadastrado.
     * Verifica se o ingresso já existe na lista, baseado no nome do evento e no assento.
     *
     * @param ingresso ingresso a ser adicionado.
     * @throws CadastroException se o ingresso já estiver cadastrado.
     */
    public void adicionarIngresso(Ingresso ingresso) {
        for (Ingresso ing : ingressosDisponiveis) {
            if (ing.getEvento().getNome().equals(ingresso.getEvento().getNome()) &&
                    ing.getAssento().equals(ingresso.getAssento())) {
                throw new CadastroException("Ingresso já adicionado.");
            }
        }
        ingressosDisponiveis.add(ingresso);
    }

    /**
     * Remove ingresso da lista de ingressos disponíveis.
     *
     * @param ingresso ingresso a ser removido.
     */
    public void removerIngressoDisponivel(Ingresso ingresso) {
        boolean contemIngresso = ingressosDisponiveis.contains(ingresso);
        if (contemIngresso) {
            ingressosDisponiveis.remove(ingresso);
        } else {
            System.out.println("Esse ingresso já foi removido.");
        }
    }

    /**
     * Verifica se o evento está ativo.
     *
     * @return true se o evento estiver ativo, false caso contrário.
     */
    public boolean isAtivo() {
        Calendar atualData = Calendar.getInstance(); // Pega data atual.
        Calendar dataEvento = Calendar.getInstance();
        dataEvento.setTime(getData()); // Data do evento.
        int valor = atualData.compareTo(dataEvento); // Compara a data atual com a data do evento.
        if (valor == 0) { // Se o evento ocorrer no mesmo dia, define como inativo.
            return false;
        } else if (valor < 0) { // Se o evento ainda não aconteceu, define como ativo.
            setStatus(false); // Marca o ingresso como cancelado.
            return true; // Retorna true indicando que o cancelamento foi bem-sucedido.
        } else { // Se a data do evento já passou, não permite cancelamento.
            return false;
        }
    }

    /**
     * Busca evento pelo seu nome.
     *
     * @param name nome do evento a ser buscado.
     * @return evento correspondente ao nome passado.
     * @throws NaoEncontradoException se o evento não for encontrado.
     */
    public Evento buscarEventoPorNome(String name) {
        for (Evento evento : getEventosCadastrados()) {
            if (evento.getNome().equalsIgnoreCase(name)) {
                return evento;
            }
        }
        throw new NaoEncontradoException("Evento não encontrado.");
    }

    /**
     * Vende um ingresso para o usuário, associando-o ao evento e ao pagamento.
     *
     * @param usuario   usuário que está comprando o ingresso.
     * @param pagamento método de pagamento utilizado.
     * @param evento    evento para o ingresso que está sendo vendido.
     * @param assento   assento do ingresso a ser vendido.
     * @return ingresso vendido.
     * @throws EventoForaDoPrazoException se o evento não estiver ativo.
     * @throws NaoEncontradoException      se o assento não estiver disponível.
     * @throws CompraNaoAutorizadaException se o pagamento não puder ser processado.
     */
    public Ingresso comprarIngresso(Usuario usuario, Pagamento pagamento, Evento evento, String assento) {
        // Verifica se o evento está ativo
        if (!isAtivo()) {
            throw new EventoForaDoPrazoException(evento.getNome());
        }
        // Verifica se o assento está disponível
        if (!buscaAssento(assento)) {
            throw new NaoEncontradoException("O assento " + assento + " não está disponível.");
        }

        Ingresso ingresso = buscarPorIngresso(evento, assento);

        Compra compra = new Compra(usuario, ingresso);

        boolean resultado = compra.processarCompra(pagamento);

        if (resultado) {
            ingressosComprados.add(ingresso); // Adiciona a lista de ingresso comprados do evento
            usuario.adicionarCompras(compra); // E também adiciona a lista de compras do usuário
            assentosReservados.add(assento); // Adiciona assento à lista de assentos reservados

            removerIngressoDisponivel(ingresso); // Remove ingresso da lista de disponíveis
            removerAssentoDisponivel(assento); // Remove assento de disponíveis

            return ingresso; // Retorna o ingresso vendido
        }

        throw new CompraNaoAutorizadaException("Não foi possível processar o pagamento.");
    }

    /**
     * Cancela ingresso comprado pelo usuário. Remove o ingresso da lista de ingressos comprados
     * caso o cancelamento seja bem-sucedido.
     *
     * @param ingresso o ingresso a ser cancelado.
     */
    public void cancelarIngressoComprado(Ingresso ingresso) {
        Iterator<Ingresso> iterator = ingressosComprados.iterator();

        while (iterator.hasNext()) {
            Ingresso ing = iterator.next();

            if (ing.equals(ingresso)) {
                boolean cancelar = ingresso.cancelarIngresso();

                if (cancelar) {
                    iterator.remove();
                }
            }
        }

    }

    /**
     * Busca ingresso na lista de ingressos disponíveis pelo evento e assento.
     *
     * @param evento  evento que ingresso pertence.
     * @param assento assento do ingresso a ser buscado.
     * @return ingresso correspondente ao evento e assento informados.
     * @throws NaoEncontradoException se o ingresso não for encontrado.
     */
    private Ingresso buscarPorIngresso(Evento evento, String assento) {
        for (Ingresso ingresso: ingressosDisponiveis) {
            if (ingresso.getEvento().equals(evento) && ingresso.getAssento().equals(assento)) {
                return ingresso;
            }
        }
        throw new NaoEncontradoException("Ingresso não encontrado.");
    }

    /**
     * Adiciona feedback à lista de feedbacks do evento.
     *
     * @param feedback feedback a ser adicionado.
     */
    public void adicionarFeedbacks (Feedback feedback) {
        feedbacks.add(feedback);
    }

    /**
     * Limpa a lista de eventos cadastrados.
     */
    public static void limparEventosCadastrados() {
        eventosCadastrados.clear();
    }

    @Override
    public String toString() {
        String dataFormatada = sdf.format(data);
        return nome + " - " + descricao + " - " + dataFormatada + " - R$ " + valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(nome, evento.nome) && Objects.equals(descricao, evento.descricao) && Objects.equals(data, evento.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, descricao, data);
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Date getData() {
        return data;
    }

    public boolean getStatus() {
        return status;
    }

    public List<String> getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public List<String> getAssentosReservados() {
        return assentosReservados;
    }

    public static List<Evento> getEventosCadastrados() {
        return eventosCadastrados;
    }

    public List<Evento> listaEventosCadastrados() {
        return eventosCadastrados;
    }

    public List<Ingresso> getIngressosComprados() {
        return ingressosComprados;
    }

    public List<Ingresso> getIngressosDisponiveis() {
        return ingressosDisponiveis;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static void setEventosCadastrados(List<Evento> eventosCadastrados) {
        Evento.eventosCadastrados = eventosCadastrados;
    }

    public void setAssentosDisponiveis(List<String> assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }

    public void adicionarAssentos (String assento) {
        assentosDisponiveis.add(assento);
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double novoValor) {
        this.valor = valor;
    }

    public int getQuantidadeAssentos() {
        return assentosDisponiveis.size();
    }

    public void setAssentosReservados(List<String> assentosReservados) {
        this.assentosReservados = assentosReservados;
    }

    public void setIngressosDisponiveis(List<Ingresso> ingressosDisponiveis) {
        this.ingressosDisponiveis = ingressosDisponiveis;
    }

    public void adicionarIngressoComprado(Ingresso ingressosComprado) {
        ingressosComprados.add(ingressosComprado);
    }
}
