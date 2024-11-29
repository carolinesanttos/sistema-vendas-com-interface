package uefs.vendaingressos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uefs.vendaingressos.model.Usuario;
import uefs.vendaingressos.model.excecoes.NaoEncontradoException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TelaLoginController {

    @FXML
    private Button botaoEntrar;

    @FXML
    private Hyperlink botaoEsqueciSenha;

    @FXML
    private Hyperlink botaoIrCriarConta;

    @FXML
    private TextField campoLogin;

    @FXML
    private TextField campoSenha;

//    @FXML
//    private Button botaoCriaConta;

    @FXML
    private Hyperlink botaoVoltar;


    @FXML
    private TextField campoCpf;

    @FXML
    private TextField campoEmail;

    @FXML
    private TextField campoNomeCompleto;

    private Usuario usuario = new Usuario();

    @FXML
    public void fazerLogin(ActionEvent event) {
        String user = campoLogin.getText();
        String senha = campoSenha.getText();

        boolean usuarioAtivo = usuario.validarLogin(user, senha);

        if (!user.isEmpty() && !senha.isEmpty() && usuarioAtivo) {
            System.out.println("Fez o login com sucesso.");
            // Login válido - Redireciona para TelaCadastroEvento
            App.abrirTela("telaCadastroEvento.fxml", "Cadastro de Eventos");
        } else {
            exibirMensagemdeErro( "Erro ao fazer login","Usuário ou senha incorretos.\nTente novamente.");
        }
    }

//    @FXML
//    public void abrirTelaCadastro(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("telaCadastro.fxml"));
//            Parent telaCadastro = loader.load();
//
//            Stage stage = (Stage) botaoIrCriarConta.getScene().getWindow();
//            stage.setScene(new Scene(telaCadastro));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            exibirMensagemdeErro("Erro", "Não foi possível abrir a tela de cadastro.");
//        }
//    }

    @FXML
    public void abrirTelaCadastro(ActionEvent event) {
        App.abrirTela("telaCadastro.fxml", "Cadastro");
    }

    @FXML
    void fazerCadastro(ActionEvent event) {

        String nome = campoNomeCompleto.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String login = campoLogin.getText();
        String senha = campoSenha.getText();

        boolean usuarioCadastrado = usuario.validarCadastro(login, email);

        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            exibirMensagemdeErro( "Erro ao fazer cadastro","É necessário preencher todos os campos.");
        } else if (usuarioCadastrado) {
            exibirMensagemdeErro( "Erro ao fazer cadastro","Já existe um cadastro com este e-mail ou login.");
        } else {
            usuario.cadastroDeUsuarios(new Usuario(login, senha, nome, cpf, email));
            System.out.println("Fez cadastro com sucesso.");
            abrirTelaLogin();
        }
    }

//    @FXML
//    void abrirTelaLogin() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("telaLogin.fxml"));
//            Parent telaLogin = loader.load();
//
//            Stage stage = (Stage) botaoVoltar.getScene().getWindow();
//            stage.setScene(new Scene(telaLogin));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            exibirMensagemdeErro("Erro", "Não foi possível voltar para tela de login.");
//        }
//    }

    @FXML
    void abrirTelaLogin() {
        App.abrirTela("telaLogin.fxml", "Login");
    }


    @FXML
    void alterarSenha(ActionEvent event) {

    }

    public void exibirMensagemdeErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);  // Cria um alerta de erro
        alerta.setTitle(titulo);  // Define o título do alerta
        alerta.setContentText(mensagemErro);  // Define a mensagem do alerta
        alerta.showAndWait();  // Mostra o alerta e espera o usuário fechar
    }



//    public void abrirTelaEvento(String arquivoFxml) {
//        try {
//            // Carrega o arquivo da próxima tela (certifique-se que o nome está correto)
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivoFxml));
//            Parent proximaCena = loader.load();
//
//            // Pega a janela atual e troca a cena para a nova tela
//            Stage stage = (Stage) botaoLogin.getScene().getWindow();
//            stage.setScene(new Scene(proximaCena));  // Define a nova cena
//            stage.show();
//        } catch (NullPointerException e) {
//            e.printStackTrace();  // Imprime o erro se algo der errado
//            exibirMensagemdeErro("Erro", "Insira seus dados para continuar.");
//        } catch (IOException e) {
//            e.printStackTrace();  // Imprime o erro se algo der errado
//            exibirMensagemdeErro("Erro", "Não foi possível abrir a próxima tela.");
//        }
//    }

}
