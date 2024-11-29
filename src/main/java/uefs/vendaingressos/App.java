package uefs.vendaingressos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

//    @Override
//    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(TelaLoginController.class.getResource("telaLogin.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 652, 400);
//        stage.setTitle("Login");
//        stage.setScene(scene);
//        stage.show();
//    }
private static Stage mainStage; // Referência ao Stage principal

    @Override
    public void start(Stage stage) throws Exception {
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
