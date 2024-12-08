package uefs.vendaingressos;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uefs.vendaingressos.model.Evento;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.excecoes.NaoEncontradoException;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;

import java.text.SimpleDateFormat;
import java.util.List;

import static javafx.application.Application.launch;

public class TelaEventosController {

    @FXML
    private VBox eventosContainer;

    @FXML
    private Button menuButton;

    @FXML
    private VBox menuLateral; // Contêiner do menu

    private boolean menuAberto = false; // Estado do menu

    private List<Evento> eventos;

    private Evento evento = new Evento();

    PersistenciaEventos persistenciaEventos = new PersistenciaEventos("detalhes-do-evento.json");

    @FXML
    public void initialize() {
        // Carregar eventos da persistência
        eventos = persistenciaEventos.carregarDados();  // Método fictício para carregar eventos da persistência

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Exibir eventos carregados
        for (Evento evento : eventos) {
            String dataEvento = formatter.format(evento.getData());

            VBox eventoBox = criarEvento(evento.getNome(), evento.getDescricao(), dataEvento, String.valueOf(evento.getValor()));
            eventosContainer.getChildren().add(eventoBox);
        }
    }

    // Método auxiliar para criar um evento
    private VBox criarEvento(String nome, String descricao, String data, String valor) {
        VBox eventoBox = new VBox(10);
        eventoBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label labelNome = new Label(nome);
        labelNome.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        Label labelDescricao = new Label(descricao);
        Label labelData = new Label(data);
        Label labelValor = new Label(valor);
        Button botaoComprar = new Button("Comprar");
        botaoComprar.setStyle("-fx-background-color: #827fe3; -fx-text-fill: #ffffff;");

        botaoComprar.setOnAction(e -> {
            // Buscar o evento correspondente
            try {
                Evento eventoSelecionado = evento.buscarEventoPorNome(nome);
                if (eventoSelecionado != null) {
                    // Passar o evento para a próxima tela
                    TelaCompraController.setEventoSelecionado(eventoSelecionado);
                    App.abrirTela("telaCompra.fxml", "Compra de Ingressos");
                }
            } catch (NaoEncontradoException naoEncontrado) {
                App.exibirMensagemErro("Erro", "Evento não encontrado.");
            }
        });

        eventoBox.getChildren().addAll(labelNome, labelDescricao, labelData, labelValor, botaoComprar);

        return eventoBox;
    }

    // Método do menu para mostrar a interação com a tela (ainda sem funcionalidades implementadas)
    @FXML
    public void abrirMenu() {
        if (!menuAberto) {
            // Exibe o menu lateral
            menuLateral.setVisible(true);
            menuButton.setLayoutX(menuLateral.getPrefWidth() + 10); // Move o botão para o lado direito do menu
        } else {
            // Esconde o menu lateral
            menuLateral.setVisible(false);
            menuButton.setLayoutX(10); // Retorna o botão para o canto esquerdo
        }
        menuAberto = !menuAberto;
    }

    // Método para criar o menu lateral
    private VBox criarMenu() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-background-color: #333333; -fx-padding: 20;");
        menu.setPrefWidth(200);

        Label lblTitulo = new Label("Menu");
        lblTitulo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18; -fx-font-weight: bold;");

        Button btnHome = new Button("Home");
        btnHome.setStyle("-fx-background-color: #827fe3; -fx-text-fill: #ffffff;");
        btnHome.setOnAction(e -> exibirTelaEventos());

        Button btnPerfil = new Button("Perfil do Usuário");
        btnPerfil.setStyle("-fx-background-color: #827fe3; -fx-text-fill: #ffffff;");
        btnPerfil.setOnAction(e -> exibirPerfilUsuario());

        Button btnSair = new Button("Sair");
        btnSair.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: #ffffff;");
        btnSair.setOnAction(e -> {
            App.abrirTela("telaLogin.fxml", "Login");
        });

        menu.getChildren().addAll(lblTitulo, btnHome, btnPerfil, btnSair);

        // Inicialmente o menu estará invisível
        menu.setVisible(false);

        return menu;
    }

    // Método para exibir a tela de eventos
    @FXML
    private void exibirTelaEventos() {
        eventosContainer.getChildren().clear(); // Limpar container
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        for (Evento evento : eventos) {
            String dataEvento = formatter.format(evento.getData());
            VBox eventoBox = criarEvento(evento.getNome(), evento.getDescricao(), dataEvento, String.valueOf(evento.getValor()));
            eventosContainer.getChildren().add(eventoBox);
        }
    }

    // Método para exibir a tela do perfil do usuário
    @FXML
    private void exibirPerfilUsuario() {
        eventosContainer.getChildren().clear(); // Limpar container

        VBox perfilBox = new VBox(10);
        perfilBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");

        Usuario usuarioAtual = UsuarioLogado.getUsuarioAtual(); // Obtém o usuário logado da Sessão

        if (usuarioAtual != null) {
            Label labelTitulo = new Label("Perfil do Usuário");
            labelTitulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            Label labelNome = new Label("Nome: " + usuarioAtual.getNome());
            Label labelEmail = new Label("Email: " + usuarioAtual.getEmail());

            perfilBox.getChildren().addAll(labelTitulo, labelNome, labelEmail);
        } else {
            Label labelErro = new Label("Usuário não logado");
            perfilBox.getChildren().add(labelErro);
        }

        eventosContainer.getChildren().add(perfilBox);
    }

    @FXML
    public void abrirTelaLogin(ActionEvent event) {
        App.abrirTela("telaLogin.fxml", "Login");
    }

}
