/**
 * Sistema Operacional: Windows 10 - 64 Bits
 * IDE: IntelliJ
 * Versão Da Linguagem: Java JDK 22
 * Autor: Caroline Santos de Jesus
 * Componente Curricular: Algoritmos II
 * Concluído em: 08/12/2024
 * Declaro que este código foi elaborado por mim de forma individual e não contém nenhum trecho de código de outro
 * colega ou de outro autor, tais como provindos de livros e apostilas, e páginas ou documentos eletrônicos da Internet.
 * Qualquer trecho de código de outra autoria que não a minha está destacado com uma citação para o autor e a fonte do
 * código, e estou ciente que estes trechos não serão considerados para fins de avaliação.
 */

package uefs.vendaingressos;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import uefs.vendaingressos.model.*;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;
import uefs.vendaingressos.model.persistencia.PersistenciaUsuarios;

import java.util.List;

/**
 * Controller para gerenciar a tela de compra de ingressos.
 */
public class TelaCompraController {

    @FXML
    private VBox containerCompra;

    @FXML
    private ComboBox<String> comboTiposIngressos;

    @FXML
    private ListView<String> listAssentosDisponiveis;

    @FXML
    private ComboBox<String> comboFormasPagamento;

    @FXML
    private Button botaoConfirmarAssento;

    @FXML
    private Button botaoAdicionarPagamento;

    @FXML
    private Button botaoFinalizarCompra;

    @FXML
    private Button botaoCancelarCompra;

    @FXML
    private Label labelTutorial;

    PersistenciaUsuarios persistenciaUsuarios = new PersistenciaUsuarios("usuarios.json");

    PersistenciaEventos persistenciaEventos = new PersistenciaEventos("detalhes-do-evento.json");

    private String assentoSelecionado;

    private static Evento eventoSelecionado;

    private Usuario usuarioLogado = UsuarioLogado.getUsuarioAtual();

    /**
     * Método de inicialização para configurar os elementos da tela.
     */
    @FXML
    public void initialize() {
        if (eventoSelecionado == null || usuarioLogado == null) {
            App.exibirMensagemErro("Erro", "Evento ou usuário não definido.");
            return;
        }

        labelTutorial.setText(
                "Bem-vindo à compra de ingressos!\n" +
                        "1. Selecione um tipo de ingresso.\n" +
                        "2. Escolha um assento disponível na lista.\n" +
                        "3. Clique em confirmar assento.\n" +
                        "4. Adicione uma forma de pagamento: \"Cartão\" ou \"Boleto\".\n" +
                        "5. Escolha sua forma de pagamento.\n" +
                        "6. Clique em \"Finalizar Compra\" para concluir.\n"
        );

        carregarDetalhesEvento();
        carregarTiposIngressos();
        carregarAssentosDisponiveis();
        carregarFormasPagamento();
        configurarBotoes();
    }

    /**
     * Define o evento selecionado para a compra.
     *
     * @param evento O evento a ser selecionado.
     */
    public static void setEventoSelecionado(Evento evento) {
        eventoSelecionado = evento;
    }

    /**
     * Carrega os detalhes do evento selecionado na interface.
     */
    public void carregarDetalhesEvento() {
        Label labelNome = new Label("Evento: " + eventoSelecionado.getNome());
        Label labelData = new Label("Data: " + eventoSelecionado.getData());
        Label labelValor = new Label("Preço Base: R$ " + eventoSelecionado.getValor());

        containerCompra.getChildren().addAll(labelNome, labelData, labelValor);
    }

    /**
     * Carrega os tipos de ingressos disponíveis no combo box.
     */
    public void carregarTiposIngressos() {
        comboTiposIngressos.getItems().addAll("Inteira", "Meia", "VIP");
        comboTiposIngressos.getSelectionModel().selectFirst();
    }

    /**
     * Carrega os assentos disponíveis para o evento.
     */
    public void carregarAssentosDisponiveis() {
        listAssentosDisponiveis.getItems().addAll(eventoSelecionado.getAssentosDisponiveis());
    }

    /**
     * Carrega as formas de pagamento cadastradas pelo usuário.
     */
    public void carregarFormasPagamento() {
        usuarioLogado.getFormasDePagamento().forEach(pagamento ->
                comboFormasPagamento.getItems().add(pagamento.getFormaDePagamento())
        );
        comboFormasPagamento.getSelectionModel().selectFirst();
    }

    /**
     * Configura as ações dos botões da tela de compra.
     */
    public void configurarBotoes() {
        botaoConfirmarAssento.setOnAction(e -> {
            assentoSelecionado = listAssentosDisponiveis.getSelectionModel().getSelectedItem();
            if (assentoSelecionado != null) {
                listAssentosDisponiveis.getItems().remove(assentoSelecionado);
                App.exibirMensagemInfo("Assento Selecionado", "Você selecionou o assento: " + assentoSelecionado);
            } else {
                App.exibirMensagemErro("Erro", "Por favor, selecione um assento.");
            }
        });

        botaoAdicionarPagamento.setOnAction(e -> {
            String novaFormaPagamento = App.exibirDialogoTexto("Adicionar Forma de Pagamento", "Digite a nova forma de pagamento:");
            if (novaFormaPagamento != null && !novaFormaPagamento.isEmpty() &&
                    (novaFormaPagamento.equalsIgnoreCase("Cartão") || novaFormaPagamento.equalsIgnoreCase("Boleto"))) {

                List<Usuario> usuariosCadastrados = persistenciaUsuarios.carregarDados();

                Pagamento novoPagamento = criarPagamento(novaFormaPagamento);

                usuarioLogado.adicionarFormaDePagamento(novoPagamento);

                for (int i = 0; i < usuariosCadastrados.size(); i++) {
                    Usuario usuario = usuariosCadastrados.get(i);
                    if (usuario.getLogin().equals(usuarioLogado.getLogin())) {
                        usuariosCadastrados.set(i, usuarioLogado);
                        break;
                    }
                }

                persistenciaUsuarios.salvarDados(usuariosCadastrados);

                comboFormasPagamento.getItems().add(novoPagamento.getFormaDePagamento());
                App.exibirMensagemInfo("Forma de Pagamento", "Forma de pagamento adicionada com sucesso.");
            } else {
                App.exibirMensagemErro("Erro", "Forma de pagamento inválida.");
            }
        });

        botaoFinalizarCompra.setOnAction(e -> {
            String tipoIngresso = comboTiposIngressos.getSelectionModel().getSelectedItem();
            String formaPagamento = comboFormasPagamento.getSelectionModel().getSelectedItem();

            if (assentoSelecionado == null) {
                App.exibirMensagemErro("Erro", "Por favor, selecione um assento.");
                return;
            }

            if (formaPagamento == null || formaPagamento.isEmpty()) {
                App.exibirMensagemErro("Erro", "Por favor, selecione ou adicione uma forma de pagamento.");
                return;
            }

            Pagamento pagamento = criarPagamento(formaPagamento);
            Ingresso ingresso = new Ingresso(eventoSelecionado, eventoSelecionado.getValor(), assentoSelecionado);
            Compra compra = new Compra(usuarioLogado, ingresso);
            compra.setPagamento(pagamento);

            boolean assentoReservado = eventoSelecionado.reservarAssento(assentoSelecionado);

            if (!assentoReservado) {
                App.exibirMensagemErro("Erro", "O assento já foi reservado.");
                return;
            }

            usuarioLogado.adicionarCompras(compra);

            List<Usuario> usuariosCadastrados = persistenciaUsuarios.carregarDados();

            for (Usuario usuario : usuariosCadastrados) {
                if (usuario.getLogin().equals(usuarioLogado.getLogin())) {
                    usuario.setIngressosComprados(usuarioLogado.getIngressosComprados());
                    break;
                }
            }

            persistenciaUsuarios.salvarDados(usuariosCadastrados);

            List<Evento> eventosCadastrados = persistenciaEventos.carregarDados();

            for (Evento evento : eventosCadastrados) {
                if (evento.getNome().equals(eventoSelecionado.getNome())) {
                    evento.setAssentosReservados(eventoSelecionado.getAssentosReservados());
                    evento.setAssentosDisponiveis(eventoSelecionado.getAssentosDisponiveis());
                    evento.adicionarIngressoComprado(ingresso);
                    break;
                }
            }

            persistenciaEventos.salvarDados(eventosCadastrados);

            App.exibirMensagemInfo("Compra Finalizada", "Compra realizada com sucesso para o evento: " +
                    eventoSelecionado.getNome() +
                    "\nIngresso: " + tipoIngresso +
                    "\nAssento: " + assentoSelecionado +
                    "\nPagamento: " + formaPagamento);

            App.abrirTela("telaEventos.fxml", "Tela Eventos");
        });

        botaoCancelarCompra.setOnAction(e -> {
            App.exibirMensagemInfo("Compra Cancelada", "A operação foi cancelada com sucesso.");
            App.abrirTela("telaEventos.fxml", "Tela Eventos"); // Ajuste o nome da tela para onde o usuário deve ser redirecionado.
        });

    }

    /**
     * Cria um objeto de pagamento com base na forma escolhida.
     *
     * @param formaPagamento A forma de pagamento selecionada ("Cartão" ou "Boleto").
     * @return O objeto de pagamento correspondente à forma escolhida.
     */
    public Pagamento criarPagamento(String formaPagamento) {
        if (formaPagamento.equalsIgnoreCase("Cartão")) {
            Pagamento pagamentoCartao = new Pagamento(usuarioLogado.getNome(), "1234-5678-9012-3456", "12/24", "123");
            return pagamentoCartao;
        } else if (formaPagamento.equalsIgnoreCase("Boleto")) {
            Pagamento pagamentoBoleto = new Pagamento("12345678901234567890");
            return pagamentoBoleto;
        }
        return null;
    }

}
