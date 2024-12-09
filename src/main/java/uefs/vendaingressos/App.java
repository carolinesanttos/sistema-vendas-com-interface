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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import uefs.vendaingressos.model.Evento;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;
import uefs.vendaingressos.model.persistencia.PersistenciaUsuarios;

import java.io.IOException;

/**
 * Classe principal do Sistema de vendas de ingressos.
 * Gerencia a inicialização da aplicação, o carregamento das telas e as mensagens ao usuário.
 */
public class App extends Application {

    private static Stage mainStage;

    private PersistenciaUsuarios persistenciaUsuarios = new PersistenciaUsuarios("usuarios.json");
    private PersistenciaEventos persistenciaEventos  = new PersistenciaEventos("detalhes-do-evento.json");

    /**
     * Inicializa a aplicação.
     * Esse método é executado ao iniciar a aplicação e configura o estado inicial.
     *
     * @param stage O palco principal fornecido pelo JavaFX.
     * @throws Exception Se ocorrer um erro ao carregar os dados ou inicializar a aplicação.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Usuario.setUsuariosCadastrados(persistenciaUsuarios.carregarDados());
        Evento.setEventosCadastrados(persistenciaEventos.carregarDados());

        mainStage = stage;
        abrirTela("telaLogin.fxml", "Login"); // Define a tela inicial
    }

    /**
     * Abre uma nova tela no palco principal da aplicação.
     *
     * @param arquivoFxml O caminho do arquivo FXML que define a interface da tela.
     * @param titulo O título a ser exibido na janela.
     */
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

    /**
     * Exibe uma mensagem informativa ao usuário.
     *
     * @param titulo O título da mensagem.
     * @param mensagem O conteúdo da mensagem.
     */
    public static void exibirMensagemInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.showAndWait();
    }

    /**
     * Exibe um diálogo de texto para entrada do usuário.
     *
     * @param titulo O título do diálogo.
     * @param mensagem A mensagem de instrução exibida no diálogo.
     * @return A entrada do usuário como texto ou {@code null} se o usuário cancelar o diálogo.
     */
    public static String exibirDialogoTexto(String titulo, String mensagem) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(mensagem);

        return dialog.showAndWait().orElse(null);
    }

    /**
     * Exibe uma mensagem de erro ao usuário.
     *
     * @param titulo O título do alerta de erro.
     * @param mensagemErro A mensagem de erro a ser exibida.
     */
    public static void exibirMensagemErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensagemErro);
        alerta.showAndWait();
    }

    /**
     * Método principal da aplicação.
     * Inicia a execução da aplicação JavaFX.
     *
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }


}
