package uefs.vendaingressos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import uefs.vendaingressos.model.Evento;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;
import uefs.vendaingressos.model.persistencia.PersistenciaUsuarios;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class App extends Application {

    private static Stage mainStage; // Referência ao Stage principal

    private PersistenciaUsuarios persistenciaUsuarios = new PersistenciaUsuarios("usuarios.json");
    private PersistenciaEventos persistenciaEventos  = new PersistenciaEventos("detalhes-do-evento.json");

    @Override
    public void start(Stage stage) throws Exception {
        Usuario.setUsuariosCadastrados(persistenciaUsuarios.carregarDados());
        Evento.setEventosCadastrados(persistenciaEventos.carregarDados());

        mainStage = stage; // Inicializa a referência ao Stage principal
        abrirTela("telaLogin.fxml", "Login"); // Define a tela inicial
    }

    public static void abrirTela(String arquivoFxml, String titulo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(arquivoFxml));
            Scene cena = new Scene(fxmlLoader.load());
            mainStage.setTitle(titulo);
            mainStage.setScene(cena);
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Erro", "Não foi possível carregar a tela " + arquivoFxml);
        }
    }

    public static void exibirMensagemErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensagemErro);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args); // Inicia o JavaFX
    }
}
