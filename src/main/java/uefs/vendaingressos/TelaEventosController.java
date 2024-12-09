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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uefs.vendaingressos.model.*;
import uefs.vendaingressos.model.excecoes.NaoEncontradoException;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;
import uefs.vendaingressos.model.persistencia.PersistenciaUsuarios;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Controller que gerencia tela de eventos.
 * Gerencia a exibição de eventos, perfil do usuário e interações com a interface.
 */
public class TelaEventosController {

    @FXML
    private VBox eventosContainer;

    @FXML
    private Button botaoMenu;

    @FXML
    private VBox menuLateral; // Contêiner do menu

    private boolean menuAberto = false; // Estado do menu

    private List<Evento> eventos;

    private Evento evento = new Evento();

    PersistenciaEventos persistenciaEventos = new PersistenciaEventos("detalhes-do-evento.json");

    SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Método inicializado automaticamente.
     * Carrega os eventos da persistência e exibe no container.
     */
    @FXML
    public void initialize() {

        eventos = persistenciaEventos.carregarDados();

        for (Evento evento : eventos) {
            String dataEvento = formatar.format(evento.getData());

            VBox eventoBox = criarEvento(evento.getNome(), evento.getDescricao(), dataEvento, String.valueOf(evento.getValor()));
            eventosContainer.getChildren().add(eventoBox);
        }
    }

    /**
     * Cria componentes visuais para exibir detalhes dos evento.
     * @param nome Nome do evento
     * @param descricao Descrição do evento
     * @param data Data do evento
     * @param valor Valor do ingresso
     * @return Um VBox contendo os dados do evento
     */
    public VBox criarEvento(String nome, String descricao, String data, String valor) {
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
            try {
                Evento eventoSelecionado = evento.buscarEventoPorNome(nome);
                if (eventoSelecionado != null) {
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

    /**
     * Responsável pela isibilidade do menu lateral.
     */
    @FXML
    public void abrirMenu() {
        if (!menuAberto) {
            // Exibe o menu lateral
            menuLateral.setVisible(true);
            botaoMenu.setLayoutX(menuLateral.getPrefWidth() + 10);
        } else {
            // Esconde o menu lateral
            menuLateral.setVisible(false);
            botaoMenu.setLayoutX(10);
        }
        menuAberto = !menuAberto;
    }

    /**
     * Exibe a tela de eventos.
     */
    @FXML
    public void exibirTelaEventos() {
        eventosContainer.getChildren().clear(); // Limpar container

        for (Evento evento : eventos) {
            String dataEvento = formatar.format(evento.getData());
            VBox eventoBox = criarEvento(evento.getNome(), evento.getDescricao(), dataEvento, String.valueOf(evento.getValor()));
            eventosContainer.getChildren().add(eventoBox);
        }
    }

    /**
     * Exibe o perfil do usuário logado, permitindo edição de informações.
     */
    @FXML
    public void exibirPerfilUsuario() {
        eventosContainer.getChildren().clear(); // Limpar o container

        VBox perfilBox = new VBox(10);
        perfilBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");

        Usuario usuarioAtual = UsuarioLogado.getUsuarioAtual();

        if (usuarioAtual != null) {
            Label labelTitulo = new Label("Perfil do Usuário");
            labelTitulo.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            TextField campoNome = new TextField(usuarioAtual.getNome());
            campoNome.setPromptText("Atualize seu nome");

            TextField campoEmail = new TextField(usuarioAtual.getEmail());
            campoEmail.setPromptText("Atualize seu email");

            PasswordField campoSenha = new PasswordField();
            campoSenha.setPromptText("Atualize sua senha");

            Button botaoSalvar = new Button("Salvar Alterações");
            botaoSalvar.setStyle("-fx-background-color: #827fe3; -fx-text-fill: #ffffff;");
            botaoSalvar.setOnAction(e -> {
                usuarioAtual.setNome(campoNome.getText());
                usuarioAtual.setEmail(campoEmail.getText());
                if (!campoSenha.getText().isEmpty()) {
                    usuarioAtual.setSenha(campoSenha.getText());
                }

                PersistenciaUsuarios persistencia = new PersistenciaUsuarios("usuarios.json");
                List<Usuario> usuarios = persistencia.carregarDados();

                for (Usuario u : usuarios) {
                    if (u.equals(usuarioAtual)) {
                        u.setNome(usuarioAtual.getNome());
                        u.setEmail(usuarioAtual.getEmail());
                        u.setSenha(usuarioAtual.getSenha());
                        break;
                    }
                }

                persistencia.salvarDados(usuarios);

                App.exibirMensagemInfo("Sucesso", "Dados atualizados com sucesso!");
            });

            // Listar eventos comprados
            Label labelEventos = new Label("Eventos Comprados:");
            labelEventos.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            VBox eventosCompradosBox = new VBox(10);
            eventosCompradosBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #cccccc;");

            List<Compra> ingressosComprados = usuarioAtual.getIngressosComprados();
            if (ingressosComprados.isEmpty()) {
                eventosCompradosBox.getChildren().add(new Label("Nenhum evento comprado."));
            } else {
                for (Compra compra : ingressosComprados) {
                    Ingresso ingresso = compra.getIngresso();
                    if (ingresso != null) {
                        Evento ingressoComprado = null;
                        for (Evento evento : Evento.getEventosCadastrados()) {
                            if (evento.getNome().equalsIgnoreCase(ingresso.getNomeEvento())) {
                                ingressoComprado = evento;
                                break;
                            }
                        }
                        if (ingressoComprado != null) {
                            String dataEvento = formatar.format(ingressoComprado.getData());
                            Label eventoLabel = new Label(
                                    ingressoComprado.getNome() + " - " + ingressoComprado.getValor() + " - " + dataEvento + " - " + ingresso.getAssento()
                            );

                            Button botaoAvaliar = new Button("Avaliar Evento");
                            Evento compraDeIngresso = ingressoComprado;

                            List<Evento> eventosAtivos = persistenciaEventos.carregarDados();

                            botaoAvaliar.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    exibirTelaAvaliacao(usuarioAtual, compraDeIngresso);
                                }
                            });

                            eventosCompradosBox.getChildren().addAll(eventoLabel, botaoAvaliar);

                        } else {
                            eventosCompradosBox.getChildren().add(new Label("Evento não encontrado."));
                        }
                    } else {
                        eventosCompradosBox.getChildren().add(new Label("Detalhes do ingresso indisponíveis."));
                    }
                }
            }
            perfilBox.getChildren().addAll(labelTitulo, campoNome, campoEmail, campoSenha, botaoSalvar, labelEventos, eventosCompradosBox);
        } else {
            Label labelErro = new Label("Usuário não logado");
            perfilBox.getChildren().add(labelErro);
        }
        eventosContainer.getChildren().add(perfilBox);
    }

    /**
     * Exibe uma janela para o usuário avaliar um evento.
     * Permite que o usuário adicione um comentário e atribua uma nota ao evento.
     * Após a avaliação, o feedback é associado ao evento e os dados são persistidos.
     *
     * @param usuarioAtual O usuário que está avaliando o evento.
     * @param evento O evento a ser avaliado.
     */
    public void exibirTelaAvaliacao(Usuario usuarioAtual, Evento evento) {
        Stage avaliacaoStage = new Stage();
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Label titulo = new Label("Avaliar Evento: " + evento.getNome());
        titulo.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField campoComentario = new TextField();
        campoComentario.setPromptText("Adicione seu comentário sobre o evento");

        ChoiceBox<Integer> campoNota = new ChoiceBox<>();
        campoNota.getItems().addAll(1, 2, 3, 4, 5);
        campoNota.setValue(1);

        Button botaoEnviar = new Button("Enviar Avaliação");
        botaoEnviar.setStyle("-fx-background-color: #827fe3; -fx-text-fill: #ffffff;");
        botaoEnviar.setOnAction(e -> {
            int nota = campoNota.getValue();
            String comentario = campoComentario.getText();

            evento.adicionarFeedbacks(new Feedback(usuarioAtual, evento, nota, comentario));

            persistenciaEventos.salvarDados(evento.listaEventosCadastrados());

            avaliacaoStage.close();
        });

        vbox.getChildren().addAll(titulo, campoComentario, campoNota, botaoEnviar);
        Scene cena = new Scene(vbox);
        avaliacaoStage.setScene(cena);
        avaliacaoStage.setTitle("Avaliar Evento");
        avaliacaoStage.show();
    }

    /**
     * Abre a tela de login.
     * Essa tela é carregada a partir do arquivo FXML especificado.
     *
     */
    @FXML
    public void abrirTelaLogin(ActionEvent event) {
        App.abrirTela("telaLogin.fxml", "Login");
    }

}
