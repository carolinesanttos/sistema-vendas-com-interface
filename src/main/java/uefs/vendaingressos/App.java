package uefs.vendaingressos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
//        System.out.println(getClass().getResource("logo.png"));
        FXMLLoader fxmlLoader = new FXMLLoader(TelaLoginController.class.getResource("telaLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 652, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
