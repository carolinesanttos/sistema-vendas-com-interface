package uefs.vendaingressos;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TelaHomeEventos {

    @FXML
    private Button botaoMenu; // O botão para abrir o menu

    @FXML
    private VBox containerMenu; // O menu de opções (inicialmente oculto)

    @FXML
    private Button botaoHome;

    @FXML
    private Button botaoPerfil;

    @FXML
    private Button botaoSair;

    @FXML
    private VBox containerDeEvento;

    @FXML
    private Label labelNomeEvento;

    @FXML
    private Label labelDescricaoEvento;

    @FXML
    private Label labelDataEvento;

    @FXML
    private Button botaoCompra;

    @FXML
    public void initialize() {
        // Inicializa a visibilidade do menu como falsa (oculto)
        containerMenu.setVisible(false);

        // Ação para alternar a visibilidade do menu ao clicar no botão
        botaoMenu.setOnAction(e -> {
            // Alterna a visibilidade do menu
            containerMenu.setVisible(!containerMenu.isVisible());
        });
    }

    @FXML
    void abrirMenuOpcoes(ActionEvent event) {

    }

    @FXML
    void alternarParaPerfil(ActionEvent event) {

    }

    @FXML
    void alternaParaEventos(ActionEvent event) {

    }

    @FXML
    void alternarParaSair(ActionEvent event) {

    }

    @FXML
    void alternarParaCompra(ActionEvent event) {

    }

}
