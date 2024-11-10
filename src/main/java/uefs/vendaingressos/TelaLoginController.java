package uefs.vendaingressos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.excecoes.NaoEncontradoException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TelaLoginController {

    @FXML
    private Button botaoLogin;

    @FXML
    private TextField campoSenha;

    @FXML
    private TextField campoUsuario;

    private List<Usuario> usuariosCadastrados = new ArrayList<>();

    @FXML
    public void fazerLogin(ActionEvent event) {
        String usuario = campoUsuario.getText();
        String senha = campoSenha.getText();

        Usuario usuarioAtivo = validarLogin(usuario, senha);


        if (usuarioAtivo != null) {
            System.out.println("Fez o login com sucesso.");
            //abrirProximaTela();
        } else {
            exibirMensagemdeErro( "Erro ao fazer login","Usuário ou senha incorretos.\nTente novamente.");
        }

    }

    public Usuario validarLogin(String login, String senha) {
        for (Usuario usuario : usuariosCadastrados) {  // Verifica cada usuário na lista
            if (usuario.getLogin().equals(login) && usuario.getSenha().equals(senha)) {
                return usuario;  // Se encontrou, retorna o usuário
            }
        }
        return null;  // Se não encontrou, retorna null
    }

    public void abrirProximaTela(String arquivoFxml) {
        try {
            // Carrega o arquivo da próxima tela (certifique-se que o nome está correto)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivoFxml));
            Parent proximaCena = loader.load();

            // Pega a janela atual e troca a cena para a nova tela
            Stage stage = (Stage) botaoLogin.getScene().getWindow();
            stage.setScene(new Scene(proximaCena));  // Define a nova cena
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Imprime o erro se algo der errado
            exibirMensagemdeErro("Erro", "Não foi possível abrir a próxima tela.");
        }
    }

    public void exibirMensagemdeErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);  // Cria um alerta de erro
        alerta.setTitle(titulo);  // Define o título do alerta
        alerta.setContentText(mensagemErro);  // Define a mensagem do alerta
        alerta.showAndWait();  // Mostra o alerta e espera o usuário fechar
    }


    public void alterarSenha(ActionEvent actionEvent) {

    }


    public void fazerCadastro(ActionEvent actionEvent) {
        abrirProximaTela("telaCadastro.fxml");
    }
}
