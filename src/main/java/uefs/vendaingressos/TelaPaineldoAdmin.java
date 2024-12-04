package uefs.vendaingressos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uefs.vendaingressos.model.Evento;
import uefs.vendaingressos.model.excecoes.NaoEncontradoException;
import uefs.vendaingressos.model.persistencia.PersistenciaEventos;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TelaPaineldoAdmin implements Initializable {

    @FXML
    private Button botaoVoltar;

    @FXML
    private Button botaoCancelar;

    @FXML
    private TextField campoNome, campoNomeNovo;

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
    private Label labelDetalhesEvento;

    @FXML
    private Button botaoBuscar;

    @FXML
    private ListView<Evento> listViewEventos = new ListView<>();

    private Evento evento = new Evento();
    private Evento eventoSelecionado = new Evento();
    private Evento eventoEncontrado = new Evento();

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
        } else {
            try {
                int quantidadeAssentos = Integer.parseInt(campoQuantidadeAssentos.getText());
            } catch (NumberFormatException e) {
                exibirMensagemdeErro("Erro ao fazer cadastro", "A quantidade de assentos deve ser um número válido.");
                return;
            }
        }
        // Convertendo de LocalDate para Date
        Date dataConvertida = Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Evento novoEvento = new Evento(nome, descricao, dataConvertida, valor);

        // Adicionando assentos
        novoEvento.gerarAssentos(qntdAssentos);

        evento.cadastroDeEventos(novoEvento);

        // Persistindo os dados, incluindo assentos
        persistenciaEventos.salvarDados(evento.getEventosCadastrados());

        abrirPaineldoAdmin(event);
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
        eventoSelecionado = listViewEventos.getSelectionModel().getSelectedItem();

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
                    App.abrirTela("telaEditarEvento.fxml", "Buscar evento");

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
        }
    }

    @FXML
    public void buscarPorEvento(ActionEvent event) {
        String nomeBuscado = campoNome.getText();
        System.out.println(nomeBuscado);
        try {
            Evento eventoEncontrado = evento.buscarEventoPorNome(nomeBuscado);

            if (eventoEncontrado != null) {
                labelDetalhesEvento.setText("Evento " + eventoEncontrado.getNome() + ", preencha os campos abaixo.");
                preencherCamposComDadosEvento(eventoEncontrado);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            labelDetalhesEvento.setText("Evento não encontrado.");
        }
    }

    @FXML
    public void salvarEdicaodeEvento() {
        // Valida se todos os campos estão preenchidos antes de processar os dados
        if (campoNome.getText().isEmpty() || campoDescricao.getText().isEmpty() || campoData.getValue() == null || campoQuantidadeAssentos.getText().isEmpty() || campoValor.getText().isEmpty()) {
            exibirMensagemdeErro("Erro ao fazer cadastro", "É necessário preencher todos os campos.");
            return; // Interrompe o método se os campos estiverem vazios
        }

        // Caso os campos estejam preenchidos, prossegue com a edição
        String nome = campoNomeNovo.getText();
        String descricao = campoDescricao.getText();
        LocalDate data = campoData.getValue();
        int qntdAssentos = Integer.parseInt(campoQuantidadeAssentos.getText());
        double valor = Double.parseDouble(campoValor.getText());

        // Atualiza os dados do evento encontrado
        eventoEncontrado.setNome(nome);
        eventoEncontrado.setDescricao(descricao);
        eventoEncontrado.setData(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        eventoEncontrado.gerarAssentos(qntdAssentos);
        eventoEncontrado.setValor(valor);

        System.out.println(evento.getValor());

        List<Evento> eventosAtivos = persistenciaEventos.carregarDados();

        // Atualiza o evento na lista
        for (int i = 0; i < eventosAtivos.size(); i++) {
            if (eventosAtivos.get(i).getNome().equals(eventoEncontrado.getNome())) {
                eventosAtivos.set(i, eventoEncontrado);
                break;
            }
        }

        // Salva a lista de eventos atualizada no arquivo
        persistenciaEventos.salvarDados(eventosAtivos);

        App.abrirTela("telaListarEventosAdmin.fxml", "Lista de eventos");
    }

    public void preencherCamposComDadosEvento(Evento eventoEncontrado) {
        campoNomeNovo.setText(eventoEncontrado.getNome());
        campoDescricao.setText(eventoEncontrado.getDescricao());
        campoData.setValue(eventoEncontrado.getData().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        campoQuantidadeAssentos.setText(String.valueOf(eventoEncontrado.getQuantidadeAssentos()));
        campoValor.setText(String.valueOf(eventoEncontrado.getValor()));
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
    public void abrirListarEventos(ActionEvent event) {
        App.abrirTela("telaListarEventosAdmin.fxml", "Lista de eventos");
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
