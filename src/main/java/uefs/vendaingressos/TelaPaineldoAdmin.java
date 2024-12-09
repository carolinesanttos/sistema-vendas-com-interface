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
import java.util.*;

/**
 * Classe responsável por gerenciar as interações da interface do painel do administrador.
 * Permite funcionalidades como cadastro, edição, listagem, remoção e busca de eventos.
 */
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

    /**
     * Método executado durante a inicialização da interface.
     * Carrega os eventos disponíveis a partir da persistência.
     *
     * @param url O URL de localização.
     * @param resourceBundle O conjunto de recursos associados.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarEventos();
    }

    /**
     * Abre a tela de cadastro de evento.
     *
     * @param event O evento de ação disparado pelo botão.
     */
    @FXML
    public void abrirCadastrarEvento(ActionEvent event) {
        App.abrirTela("telaCadastrarEvento.fxml", "Cadastra de evento");
    }

    /**
     * Realiza o cadastro de um novo evento.
     * Valida os campos preenchidos e salva os dados na persistência.
     *
     * @param event O evento de ação disparado pelo botão.
     */
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

        Evento novoEvento = new Evento(nome, descricao, dataConvertida, valor);

        novoEvento.gerarAssentos(qntdAssentos);

        evento.cadastroDeEventos(novoEvento);

        persistenciaEventos.salvarDados(evento.getEventosCadastrados());

        abrirPaineldoAdmin(event);
    }

    /**
     * Carrega os eventos cadastrados na lista exibida na interface.
     */
    public void carregarEventos () {
        List<Evento> eventosCadastrados = persistenciaEventos.carregarDados();

        if (eventosCadastrados != null && !eventosCadastrados.isEmpty()) {
            ObservableList<Evento> obsListaEventos = FXCollections.observableArrayList(eventosCadastrados);
            listViewEventos.setItems(obsListaEventos);
        } else {
            exibirMensagemdeErro("Aviso", "Nenhum evento foi encontrado no JSON.");
        }
    }

    /**
     * Define a ação a ser realizada com um evento selecionado (editar ou remover).
     *
     * @param event O evento de ação disparado pelo botão.
     */
    @FXML
    public void acaoEventoSelecionado(ActionEvent event) {
        eventoSelecionado = listViewEventos.getSelectionModel().getSelectedItem();

        if (eventoSelecionado != null) {
            Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
            dialogo.setTitle("Escolher ação");
            dialogo.setHeaderText("Você selecionou o evento: " + eventoSelecionado.getNome());
            dialogo.setContentText("O que deseja fazer com esse evento?");

            ButtonType editar = new ButtonType("Editar");
            ButtonType remover = new ButtonType("Remover");
            ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialogo.getButtonTypes().setAll(editar, remover, cancelar);

            Optional<ButtonType> resultado = dialogo.showAndWait();

            if (resultado.isPresent()) {
                if (resultado.get() == editar) {
                    App.abrirTela("telaEditarEvento.fxml", "Buscar evento");

                } else if (resultado.get() == remover) {
                    boolean confirmado = confirmarRemocao(eventoSelecionado);
                    if (confirmado) {
                        removerEvento(eventoSelecionado);
                    }
                }
            }
        } else {
            exibirMensagemdeErro("Aviso: nenhum evento selecionado", "Por favor, selecione um evento na lista para realizar uma ação.");
        }
    }

    /**
     * Realiza a busca de um evento pelo nome fornecido.
     *
     * @param event O evento de ação disparado pelo botão.
     */
    @FXML
    public void buscarPorEvento(ActionEvent event) {
        String nomeBuscado = campoNome.getText();
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

    /**
     * Atualiza os dados de um evento editado e salva as alterações.
     */
    @FXML
    public void salvarEdicaodeEvento() {
        if (campoNome.getText().isEmpty() || campoDescricao.getText().isEmpty() || campoData.getValue() == null || campoQuantidadeAssentos.getText().isEmpty() || campoValor.getText().isEmpty()) {
            exibirMensagemdeErro("Erro ao fazer cadastro", "É necessário preencher todos os campos.");
            return;
        }

        String nome = campoNomeNovo.getText();
        String descricao = campoDescricao.getText();
        LocalDate data = campoData.getValue();
        int qntdAssentos = Integer.parseInt(campoQuantidadeAssentos.getText());
        double valor;

        try {
            valor = Double.parseDouble(campoValor.getText());
        } catch (NumberFormatException ex) {
            exibirMensagemdeErro("Erro de formato", "O campo Valor deve conter um número válido.");
            return;
        }

        // Atualiza os dados do evento encontrado
        eventoEncontrado.setNome(nome);
        eventoEncontrado.setDescricao(descricao);
        eventoEncontrado.setData(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        eventoEncontrado.gerarAssentos(qntdAssentos);
        eventoEncontrado.setValor(valor);

        List<Evento> eventosAtivos = persistenciaEventos.carregarDados();

        // Atualiza o evento na lista
        for (int i = 0; i < eventosAtivos.size(); i++) {
            if (eventosAtivos.get(i).getNome().equals(eventoEncontrado.getNome())) {
                eventosAtivos.set(i, eventoEncontrado);
                break;
            }
        }

        persistenciaEventos.salvarDados(eventosAtivos);

        App.abrirTela("telaListarEventosAdmin.fxml", "Lista de eventos");
    }

    /**
     * Preenche os campos de edição com os dados de um evento.
     *
     * @param eventoEncontrado O evento cujos dados serão preenchidos nos campos.
     */
    public void preencherCamposComDadosEvento(Evento eventoEncontrado) {
        campoNomeNovo.setText(eventoEncontrado.getNome());
        campoDescricao.setText(eventoEncontrado.getDescricao());
        campoData.setValue(eventoEncontrado.getData().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        campoQuantidadeAssentos.setText(String.valueOf(eventoEncontrado.getQuantidadeAssentos()));
        campoValor.setText(String.valueOf(eventoEncontrado.getValor()));
    }

    /**
     * Confirma a remoção de um evento.
     *
     * @param eventoSelecionado O evento a ser removido.
     * @return {@code true} se a remoção for confirmada; caso contrário, {@code false}.
     */
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

    /**
     * Remove um evento da lista e atualiza a persistência.
     *
     * @param eventoSelecionado O evento a ser removido.
     */
    public void removerEvento(Evento eventoSelecionado) {
        if (eventoSelecionado != null) {
            listViewEventos.getItems().remove(eventoSelecionado);

            List<Evento> eventos = persistenciaEventos.carregarDados();

            if (eventos != null && eventos.remove(eventoSelecionado)) {
                persistenciaEventos.salvarDados(eventos);
            } else {
                exibirMensagemdeErro("Aviso", "O evento não foi encontrado no arquivo JSON.");
            }
        } else {
            exibirMensagemdeErro("Aviso", "Nenhum evento selecionado para remover.");

        }
    }

    /**
     * Abre a tela de listagem de eventos cadastrados.
     *
     * @param event O evento de ação disparado pelo botão.
     */
    @FXML
    public void abrirListarEventos(ActionEvent event) {
        App.abrirTela("telaListarEventosAdmin.fxml", "Lista de eventos");
    }

    /**
     * Abre a tela do painel do administrador.
     *
     * @param event O evento de ação disparado pelo botão.
     */
    @FXML
    public void abrirPaineldoAdmin(ActionEvent event) {
        App.abrirTela("telaPaineldoAdmin.fxml", "Painel do administrador");
    }

    /**
     * Sai do sistema e retorna à tela de login.
     *
     * @param event O evento de ação disparado pelo botão.
     */

    @FXML
    public void sair(ActionEvent event) {
        App.abrirTela("telaLogin.fxml", "Login");
    }

    /**
     * Exibe uma mensagem de erro ao usuário.
     *
     * @param titulo O título do alerta de erro.
     * @param mensagemErro A mensagem de erro a ser exibida.
     */
    public void exibirMensagemdeErro(String titulo, String mensagemErro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensagemErro);
        alerta.showAndWait();
    }
}
