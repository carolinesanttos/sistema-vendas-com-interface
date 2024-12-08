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

    PersistenciaUsuarios persistenciaUsuarios = new PersistenciaUsuarios("usuarios.json");
    PersistenciaEventos persistenciaEventos = new PersistenciaEventos("detalhes-do-evento.json");

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
                // Remove visualmente do ListView, mas não reserva no evento ainda
                listAssentosDisponiveis.getItems().remove(assentoSelecionado);
                App.exibirMensagemInfo("Assento Selecionado", "Você selecionou o assento: " + assentoSelecionado);
            } else {
                App.exibirMensagemErro("Erro", "Por favor, selecione um assento.");
            }
        });

        btnAdicionarPagamento.setOnAction(e -> {
            String novaFormaPagamento = App.exibirDialogoTexto("Adicionar Forma de Pagamento", "Digite a nova forma de pagamento:");
            if (novaFormaPagamento != null && !novaFormaPagamento.isEmpty() &&
                    (novaFormaPagamento.equalsIgnoreCase("Cartão") || novaFormaPagamento.equalsIgnoreCase("Boleto"))) {

                // Carrega os dados existentes
                List<Usuario> usuariosCadastrados = persistenciaUsuarios.carregarDados();

                // Cria a nova forma de pagamento
                Pagamento novoPagamento = criarPagamento(novaFormaPagamento);

                // Adiciona a nova forma de pagamento ao usuário logado
                usuarioLogado.adicionarFormaDePagamento(novoPagamento);

                // Atualiza apenas o usuário logado na lista carregada
                for (int i = 0; i < usuariosCadastrados.size(); i++) {
                    Usuario usuario = usuariosCadastrados.get(i);
                    if (usuario.getLogin().equals(usuarioLogado.getLogin())) {
                        // Substitui o usuário antigo pelo atualizado
                        usuariosCadastrados.set(i, usuarioLogado);
                        break;
                    }
                }

                // Salva a lista de usuários atualizada
                persistenciaUsuarios.salvarDados(usuariosCadastrados);

                // Atualiza a interface do usuário
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

            // Cria os objetos relacionados à compra
            Pagamento pagamento = criarPagamento(formaPagamento);
            Ingresso ingresso = new Ingresso(eventoSelecionado, eventoSelecionado.getValor(), assentoSelecionado);
            Compra compra = new Compra(usuarioLogado, ingresso);
            compra.setPagamento(pagamento);

            // Atualiza os dados do sistema
            boolean assentoReservado = eventoSelecionado.reservarAssento(assentoSelecionado);

            if (!assentoReservado) {
                App.exibirMensagemErro("Erro", "O assento já foi reservado.");
                return;
            }

            usuarioLogado.adicionarCompras(compra);

            // Carrega os dados existentes
            List<Usuario> usuariosCadastrados = persistenciaUsuarios.carregarDados();

            // Atualiza a lista de usuários
            for (Usuario usuario : usuariosCadastrados) {
                if (usuario.getLogin().equals(usuarioLogado.getLogin())) {
                    usuario.setIngressosComprados(usuarioLogado.getIngressosComprados());
                    break;
                }
            }

            // Salva os dados atualizados
            persistenciaUsuarios.salvarDados(usuariosCadastrados);

            // Atualiza os dados do evento na persistência
            List<Evento> eventosCadastrados = persistenciaEventos.carregarDados();

            for (Evento evento : eventosCadastrados) {
                if (evento.getNome().equals(eventoSelecionado.getNome())) {
                    evento.setAssentosReservados(eventoSelecionado.getAssentosReservados());
                    evento.setIngressosDisponiveis(eventoSelecionado.getIngressosDisponiveis());
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

        btnCancelarCompra.setOnAction(e -> {
            App.exibirMensagemInfo("Compra Cancelada", "A operação foi cancelada com sucesso.");
            App.abrirTela("telaEventos.fxml", "Tela Eventos"); // Ajuste o nome da tela para onde o usuário deve ser redirecionado.
        });

    }

    private Pagamento criarPagamento(String formaPagamento) {
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
