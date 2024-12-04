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
import java.util.Optional;
import java.util.ResourceBundle;

public class TelaPaineldoAdmin implements Initializable {

    @FXML
    private Button botaoVoltar;

    @FXML
    private Button botaoCancelar;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoDescricao;

    @FXML
    private DatePicker campoData;

    @FXML
    private TextField campoQuantidadeAssentos;

    @FXML
    private TextField campoValor;

    @FXML
    private Button botaoSalvarEvento;

    @FXML
    private Button botaoCadastrarEvento;

    @FXML
    private Button botaoListarEventos;

    @FXML
    private Button botaoSair;

    @FXML
    private Button botaoSalvarEdicao;

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
        double valor = Double.parseDouble(campoValor.getText());

        if (nome.isEmpty() || descricao.isEmpty() || campoQuantidadeAssentos.getText().isEmpty() || campoData.getValue() == null || campoValor.getText().isEmpty()) {
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
            exibirMensagemdeErro("Aviso", "Nenhum evento foi encontrado no JSON.");
        }
    }

    @FXML
    public void acaoEventoSelecionado(ActionEvent event) {
        // Obter o evento selecionado na ListView
        Evento eventoSelecionado = listViewEventos.getSelectionModel().getSelectedItem();

        if (eventoSelecionado != null) {
            // Criar a caixa de diálogo de confirmação
            Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
            dialogo.setTitle("Escolher ação");
            dialogo.setHeaderText("Você selecionou o evento: " + eventoSelecionado.getNome());
            dialogo.setContentText("O que deseja fazer com esse evento?");

            // Botões disponíveis na caixa de diálogo
            ButtonType editar = new ButtonType("Editar");
            ButtonType remover = new ButtonType("Remover");
            ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            // Adicionar os botões no diálogo
            dialogo.getButtonTypes().setAll(editar, remover, cancelar);

            // Mostrar a caixa de diálogo e aguardar resposta
            Optional<ButtonType> resultado = dialogo.showAndWait();
            if (resultado.isPresent()) {
                if (resultado.get() == editar) {
                    // Chamar o método para abrir o formulário de edição
                    abrirCaixaEdicao(eventoSelecionado);
                } else if (resultado.get() == remover) {
                    // Confirmar a remoção do evento
                    boolean confirmado = confirmarRemocao(eventoSelecionado);
                    if (confirmado) {
                        // Remover o evento da lista
                        removerEvento(eventoSelecionado);
                    }
                }
            }
        } else {
            // Nenhum evento foi selecionado
            exibirMensagemdeErro("Aviso: nenhum evento selecionado", "Por favor, selecione um evento na lista para realizar uma ação.");
//            Alert alerta = new Alert(Alert.AlertType.WARNING);
//            alerta.setTitle("Aviso");
//            alerta.setHeaderText("Nenhum evento selecionado");
//            alerta.setContentText("Por favor, selecione um evento na lista para realizar uma ação.");
//            alerta.showAndWait();
        }
    }

    // Método para abrir o formulário de edição
    public void abrirCaixaEdicao(Evento eventoSelecionado) {
        App.abrirTela("telaEditarEvento.fxml", "Editar evento selecionado");
    }

    @FXML
    public void editarEvento(ActionEvent event) {
        String nome = campoNome.getText();
        String descricao = campoDescricao.getText();
        LocalDate data = campoData.getValue();
        int qntdAssentos = Integer.parseInt(campoQuantidadeAssentos.getText());
        double valor = Double.parseDouble(campoValor.getText());

        if (nome.isEmpty() || descricao.isEmpty() || campoQuantidadeAssentos.getText().isEmpty() || campoData.getValue() == null || campoValor.getText().isEmpty()) {
            exibirMensagemdeErro("Erro ao fazer edição", "É necessário preencher todos os campos.");
        }
        // Convertendo de LocalDate para Date
        Date dataConvertida = Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
        evento.cadastroDeEventos(new Evento(nome, descricao, dataConvertida));

        // Adicionando assentos
        evento.gerarAssentos(qntdAssentos);

        abrirPaineldoAdmin(event);
    }

    // Método para confirmar a remoção do evento
    public boolean confirmarRemocao(Evento eventoSelecionado) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar remoção");
        confirmacao.setHeaderText("Você realmente deseja remover este evento?");
        confirmacao.setContentText("Evento: " + eventoSelecionado.getNome());

        ButtonType sim = new ButtonType("Sim");
        ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacao.getButtonTypes().setAll(sim, nao);

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        return resultado.isPresent() && resultado.get() == sim;
    }

    // Método para remover o evento
    public void removerEvento(Evento eventoSelecionado) {
        if (eventoSelecionado != null) {
            listViewEventos.getItems().remove(eventoSelecionado);

            // Carregar os dados do arquivo JSON
            List<Evento> eventos = persistenciaEventos.carregarDados();

            if (eventos != null && eventos.remove(eventoSelecionado)) {
                // Salvar a lista atualizada no arquivo JSON
                persistenciaEventos.salvarDados(eventos);
            } else {
                exibirMensagemdeErro("Aviso", "O evento não foi encontrado no arquivo JSON.");
            }
        } else {
            exibirMensagemdeErro("Aviso", "Nenhum evento selecionado para remover.");

        }
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
