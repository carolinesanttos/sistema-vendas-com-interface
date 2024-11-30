package uefs.vendaingressos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import uefs.vendaingressos.model.Evento;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class TelaPaineldoAdmin implements Initializable {

    @FXML
    private Button botaoVoltar;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoDescricao;

    @FXML
    private DatePicker campoData;

    @FXML
    private TextField campoQuantidadeAssentos;

    @FXML
    private Button botaoSalvarEvento;

    @FXML
    private Button botaoCadastrarEvento;

    @FXML
    private Button botaoListarEventos;

    @FXML
    private Button botaoSair;

    @FXML
    private ListView<Evento> listViewEventos = new ListView<>();

    private Evento evento = new Evento();

    PersistenciaEventos persistenciaEventos = new PersistenciaEventos("detalhes-do-evento.json");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarEventos();
    }

    @FXML
    public void abrirCadastrarEvento(ActionEvent event) {
        App.abrirTela("telaCadastrarEvento.fxml", "Cadastra de evento");
    }

    @FXML
    public void fazerCadastro(ActionEvent event) {
        String nome = campoNome.getText();
        String descricao = campoDescricao.getText();
        LocalDate data = campoData.getValue();
        int qntdAssentos = Integer.parseInt(campoQuantidadeAssentos.getText());

        if (nome.isEmpty() || descricao.isEmpty() || campoQuantidadeAssentos.getText().isEmpty() || campoData.getValue() == null) {
            exibirMensagemdeErro("Erro ao fazer cadastro", "É necessário preencher todos os campos.");
        }
        // Convertendo de LocalDate para Date
        Date dataConvertida = Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
        evento.cadastroDeEventos(new Evento(nome, descricao, dataConvertida));

        // Adicionando assentos
        evento.gerarAssentos(qntdAssentos);

        abrirPaineldoAdmin(event);
    }

    @FXML
    public void abrirListarEventos(ActionEvent event) {
        App.abrirTela("telaListarEventosAdmin.fxml", "Lista de eventos");
    }

    public void carregarEventos () {
        List<Evento> eventosCadastrados = persistenciaEventos.carregarDados();

        if (eventosCadastrados != null && !eventosCadastrados.isEmpty()) {
            // Configurar eventos na ListView
            ObservableList<Evento> obsListaEventos = FXCollections.observableArrayList(eventosCadastrados);
            listViewEventos.setItems(obsListaEventos);
        } else {
            System.out.println("Nenhum evento foi encontrado no JSON.");
        }
    }
    @FXML
    public void editarEvento(ActionEvent event) {

    }

    @FXML
    public void removerEvento(ActionEvent event) {


    }

    @FXML
    public void abrirPaineldoAdmin(ActionEvent event) {
        App.abrirTela("telaPaineldoAdmin.fxml", "Painel do administrador");
    }

    @FXML
    public void sair(ActionEvent event) {
        App.abrirTela("telaLogin.fxml", "Login");
    }

    public void exibirMensagemdeErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);  // Cria um alerta de erro
        alerta.setTitle(titulo);  // Define o título do alerta
        alerta.setContentText(mensagemErro);  // Define a mensagem do alerta
        alerta.showAndWait();  // Mostra o alerta e espera o usuário fechar
    }




}
