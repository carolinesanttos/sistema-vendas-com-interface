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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import uefs.vendaingressos.model.Usuario;

/**
 * Controlador que gerencia as interações da tela de login e cadastro.
 * Permite realizar login, cadastrar novos usuários e navegar entre as telas.
 */
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

    @FXML
    private Hyperlink botaoVoltar;

    @FXML
    private TextField campoCpf;

    @FXML
    private TextField campoEmail;

    @FXML
    private TextField campoNomeCompleto;

    private Usuario usuario = new Usuario();

    /**
     * Realiza o processo de login verificando os dados fornecidos pelo usuário.
     * Redireciona para o painel do administrador (se for admin) ou para o painel de eventos (se for usuário comum).
     * @param event evento gerado pelo botão de login.
     */
    @FXML
    public void fazerLogin(ActionEvent event) {
        String user = campoLogin.getText();
        String senha = campoSenha.getText();

        Usuario usuarioAtivo = usuario.validarLogin2(user, senha);

        if (!user.isEmpty() && !senha.isEmpty() && usuarioAtivo != null) {
            if (user.equals("admin") && senha.equals("senha123")) {
                App.abrirTela("telaPaineldoAdmin.fxml", "Painel do administrador");
            } else {
                UsuarioLogado.setUsuarioAtual(usuarioAtivo);
                App.abrirTela("telaEventos.fxml", "Painel de eventos");
            }
        } else {
            exibirMensagemdeErro( "Erro ao fazer login","Usuário ou senha incorretos.\nTente novamente.");
        }
    }

    /**
     * Abre a tela de cadastro de usuário.
     * @param event evento gerado pelo botão de ir para a tela de cadastro.
     */
    @FXML
    public void abrirTelaCadastro(ActionEvent event) {
        App.abrirTela("telaCadastro.fxml", "Cadastro");
    }

    /**
     * Realiza o processo de cadastro de um novo usuário, validando os dados fornecidos.
     * Exibe mensagens de erro em caso de falha ou redireciona para a tela de login após o cadastro.
     * @param event evento gerado pelo botão de cadastro.
     */
    @FXML
    public void fazerCadastro(ActionEvent event) {

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
        }
        usuario.cadastroDeUsuarios(new Usuario(login, senha, nome, cpf, email));
        abrirTelaLogin();

    }

    /**
     * Abre a tela de login.
     */
    @FXML
    public void abrirTelaLogin() {
        App.abrirTela("telaLogin.fxml", "Login");
    }

    /**
     * Exibe uma mensagem de erro utilizando um alerta.
     * @param titulo título do alerta.
     * @param mensagemErro mensagem detalhada do erro a ser exibido.
     */
    public void exibirMensagemdeErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensagemErro);
        alerta.showAndWait();
    }

}
