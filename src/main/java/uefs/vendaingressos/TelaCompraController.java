package uefs.vendaingressos;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import uefs.vendaingressos.model.*;

public class TelaCompraController {

    private static Evento eventoSelecionado;
    private Usuario usuarioLogado = UsuarioLogado.getUsuarioAtual();

    @FXML
    private VBox containerCompra;

    @FXML
    private ComboBox<String> comboTiposIngressos;

    @FXML
    private ListView<String> listAssentosDisponiveis;

    @FXML
    private ComboBox<String> comboFormasPagamento;

    @FXML
    private Button btnConfirmarAssento;

    @FXML
    private Button btnAdicionarPagamento;

    @FXML
    private Button btnFinalizarCompra;

    @FXML
    private Button btnCancelarCompra;

    @FXML
    private Label lblTutorial;

    // Assento selecionado
    private String assentoSelecionado;

    public static void setEventoSelecionado(Evento evento) {
        eventoSelecionado = evento;
    }

    @FXML
    public void initialize() {
        if (eventoSelecionado == null || usuarioLogado == null) {
            App.exibirMensagemErro("Erro", "Evento ou usuário não definido.");
            return;
        }

        lblTutorial.setText(
                "Bem-vindo à compra de ingressos!\n" +
                        "1. Selecione um tipo de ingresso.\n" +
                        "2. Escolha um assento disponível na lista.\n" +
                        "3. Escolha uma forma de pagamento ou adicione uma nova.\n" +
                        "4. Clique em \"Finalizar Compra\" para concluir.\n" +
                        "Se mudar de ideia, clique em \"Cancelar\"."
        );


        carregarDetalhesEvento();
        carregarTiposIngressos();
        carregarAssentosDisponiveis();
        carregarFormasPagamento();

        configurarBotoes();
    }

    private void carregarDetalhesEvento() {
        Label lblNome = new Label("Evento: " + eventoSelecionado.getNome());
        Label lblData = new Label("Data: " + eventoSelecionado.getData());
        Label lblValor = new Label("Preço Base: R$ " + eventoSelecionado.getValor());

        containerCompra.getChildren().addAll(lblNome, lblData, lblValor);
    }

    private void carregarTiposIngressos() {
        comboTiposIngressos.getItems().addAll("Inteira", "Meia", "VIP");
        comboTiposIngressos.getSelectionModel().selectFirst();
    }

    private void carregarAssentosDisponiveis() {
        listAssentosDisponiveis.getItems().addAll(eventoSelecionado.getAssentosDisponiveis());
    }

    private void carregarFormasPagamento() {
        usuarioLogado.getFormasDePagamento().forEach(pagamento ->
                comboFormasPagamento.getItems().add(pagamento.getFormaDePagamento())
        );
        comboFormasPagamento.getSelectionModel().selectFirst();
    }

    private void configurarBotoes() {
        btnConfirmarAssento.setOnAction(e -> {
            assentoSelecionado = listAssentosDisponiveis.getSelectionModel().getSelectedItem();
            if (assentoSelecionado != null) {
                boolean reservado = eventoSelecionado.reservarAssento(assentoSelecionado);
                if (reservado) {
                    listAssentosDisponiveis.getItems().remove(assentoSelecionado);
                    App.exibirMensagemInfo("Assento Selecionado", "Você selecionou o assento: " + assentoSelecionado);
                } else {
                    App.exibirMensagemErro("Erro", "Assento já reservado.");
                }
            } else {
                App.exibirMensagemErro("Erro", "Por favor, selecione um assento.");
            }
        });

        btnAdicionarPagamento.setOnAction(e -> {
            String novaFormaPagamento = App.exibirDialogoTexto("Adicionar Forma de Pagamento", "Digite a nova forma de pagamento:");
            if (novaFormaPagamento != null && !novaFormaPagamento.isEmpty() &&
                    (novaFormaPagamento.equalsIgnoreCase("Cartão") || novaFormaPagamento.equalsIgnoreCase("Boleto"))) {
                Pagamento novoPagamento = criarPagamento(novaFormaPagamento);
                usuarioLogado.adicionarFormaDePagamento(novoPagamento);
                comboFormasPagamento.getItems().add(novoPagamento.getFormaDePagamento());
                App.exibirMensagemInfo("Forma de Pagamento", "Forma de pagamento adicionada com sucesso.");
            } else {
                App.exibirMensagemErro("Erro", "Forma de pagamento inválida.");
            }
        });

        btnFinalizarCompra.setOnAction(e -> {
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

            eventoSelecionado.reservarAssento(assentoSelecionado);
            usuarioLogado.adicionarCompras(compra);

            App.exibirMensagemInfo("Compra Finalizada", "Compra realizada com sucesso para o evento: " +
                    eventoSelecionado.getNome() +
                    "\nIngresso: " + tipoIngresso +
                    "\nAssento: " + assentoSelecionado +
                    "\nPagamento: " + formaPagamento);

            App.abrirTela("telaEventos.fxml", "Tela Eventos"); // Ajuste o nome da tela para onde o usuário deve ser redirecionado.
        });

        btnCancelarCompra.setOnAction(e -> {
            App.exibirMensagemInfo("Compra Cancelada", "A operação foi cancelada com sucesso.");
            App.abrirTela("telaEventos.fxml", "Tela Eventos"); // Ajuste o nome da tela para onde o usuário deve ser redirecionado.
        });

    }

    private Pagamento criarPagamento(String formaPagamento) {
        if (formaPagamento.equalsIgnoreCase("Cartão")) {
            return new Pagamento("Nome Exemplo", "1234-5678-9012-3456", "12/24", "123");
        } else if (formaPagamento.equalsIgnoreCase("Boleto")) {
            return new Pagamento("12345678901234567890");
        }
        return null;
    }

}
