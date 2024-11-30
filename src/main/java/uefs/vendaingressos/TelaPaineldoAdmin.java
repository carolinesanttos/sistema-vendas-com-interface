package uefs.vendaingressos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import uefs.vendaingressos.model.Evento;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.persistencia.PersistenciaUsuarios;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaPaineldoAdmin {

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoDescricao;

    @FXML
    private DatePicker campoData;

    @FXML
    private Button botaoFazerCadastro;

    @FXML
    private Button botaoCadastrarEvento;

    @FXML
    private Button botaoListarEventos;

    @FXML
    private Button botaoSair;

    @FXML
    private Button botaoVoltar;

    private Evento evento = new Evento();


    @FXML
    void abrirCadastrarEvento(ActionEvent event) {
        App.abrirTela("telaCadastrarEvento.fxml", "Cadastra de evento");
    }

    @FXML
    void fazerCadastro(ActionEvent event) {
        String nome = campoNome.getText();
        String descricao = campoDescricao.getText();
        LocalDate data = campoData.getValue();

        if (nome.isEmpty() || descricao.isEmpty() || campoData.getValue() == null) {
            exibirMensagemdeErro("Erro ao fazer cadastro", "É necessário preencher todos os campos.");
        }
        // Convertendo de LocalDate para Date
        Date dataConvertida = Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
        evento.cadastroDeEventos(new Evento(nome, descricao, dataConvertida));
        abrirPaineldoAdmin(event);
    }

    @FXML
    void abrirListarEventos(ActionEvent event) {

    }

    @FXML
    void abrirPaineldoAdmin(ActionEvent event) {
        App.abrirTela("telaPaineldoAdmin.fxml", "Painel do administrador");
    }

    @FXML
    void sair(ActionEvent event) {

    }

    public void exibirMensagemdeErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);  // Cria um alerta de erro
        alerta.setTitle(titulo);  // Define o título do alerta
        alerta.setContentText(mensagemErro);  // Define a mensagem do alerta
        alerta.showAndWait();  // Mostra o alerta e espera o usuário fechar
    }
}
