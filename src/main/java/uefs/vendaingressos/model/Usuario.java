/**
 * Sistema Operacional: Windows 10 - 64 Bits
 * IDE: IntelliJ
 * Versão Da Linguagem: Java JDK 22
 * Autor: Caroline Santos de Jesus
 * Componente Curricular: Algoritmos II
 * Concluído em: 28/10/2024
 * Declaro que este código foi elaborado por mim de forma individual e não contém nenhum trecho de código de outro
 * colega ou de outro autor, tais como provindos de livros e apostilas, e páginas ou documentos eletrônicos da Internet.
 * Qualquer trecho de código de outra autoria que não a minha está destacado com uma citação para o autor e a fonte do
 * código, e estou ciente que estes trechos não serão considerados para fins de avaliação.
 */

package uefs.vendaingressos.model;

import com.google.gson.annotations.Expose;
import uefs.vendaingressos.model.excecoes.CadastroException;
import uefs.vendaingressos.model.excecoes.FormaDePagamentoInvalidaException;
import uefs.vendaingressos.model.excecoes.NaoEncontradoException;
import uefs.vendaingressos.model.excecoes.ReembolsoException;
import uefs.vendaingressos.model.persistencia.PersistenciaUsuarios;

import java.util.*;
import java.util.List;

/**
 * A classe Usuario representa um usuário no sistema de venda de ingressos.
 *
 * Gerencia informações do usuário, como login, senha, nome, CPF e email. Permite:
 * - Cadastrar novos usuários.
 * - Verificar credenciais de login.
 * - Adicionar e remover formas de pagamento.
 * - Realizar e cancelar compras de ingressos.
 * - Verificar se o usuário é administrador.
 *
 * Exceções são lançadas em caso de erros, como tentativas de adicionar
 * formas de pagamento inválidas ou cancelar ingressos já reembolsados.
 */
public class Usuario {
    @Expose
    private String login;
    @Expose
    private String senha;
    @Expose
    private String nome;
    @Expose
    private String cpf;
    @Expose
    private String email;
    @Expose
    private boolean adm;
    @Expose
    private boolean isLogado;
    @Expose
    private Compra compra;
    @Expose
    Evento evento;
    @Expose
    private static List<Usuario> usuariosCadastrados = new ArrayList<>();
    @Expose
    private List<Pagamento> formasDePagamento = new ArrayList<>();
    @Expose
    private List <Compra> ingressosComprados = new ArrayList<>();

    private static PersistenciaUsuarios persistenciaUsuarios = new PersistenciaUsuarios("usuarios.json");

    public Usuario () {
    }

    public Usuario(String login, String senha, String nome, String cpf, String email, boolean adm) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.adm = adm;
        this.isLogado = false;
    }

    public Usuario(String login, String senha, String nome, String cpf, String email) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.isLogado = true;
        if (login.equals("admin") && senha.equals("senha123")) {
            this.adm = true;
        } else {
            this.adm = false;
        }
    }

    /**
     * Adiciona usuário à lista de cadastro.
     *
     * @param usuario será o usuário a ser cadastrado.
     */
    public void cadastroDeUsuarios (Usuario usuario) {
        if (usuariosCadastrados.contains(usuario)) {
            throw new CadastroException("Usuário já cadastrado!");
        }
        // Adicionar o novo usuário à lista
        usuariosCadastrados.add(usuario);
        // Salvar a lista atualizada
        persistenciaUsuarios.salvarDados(usuariosCadastrados);

    }

    /**
     * Verifica as credenciais de login do usuário.
     *
     * @param login  será o login do usuário.
     * @param senha  será a senha do usuário.
     * @return true se login e senha estiverem corretos, caso contrário, false.
     */
    public boolean login(String login, String senha) {
        if (this.login.equals(login) && this.senha.equals(senha)) {
            setLogado(true);
            return true;
        }
        return false;
    }

    /**
     * Verifica se usuário está cadastrado no sistema.
     *
     * @param login  será o login do usuário.
     * @param senha  será a senha do usuário.
     * @return true se usuário estiver cadastrado, caso contrário, false.
     */
    public boolean validarLogin(String login, String senha) {
        for (Usuario usuario : usuariosCadastrados) {
            if (usuario.getLogin().equals(login)) {
                // Se o login estiver cadastrado, verifica a senha
                if (usuario.getSenha().equals(senha)) {
                    return true; // Usuário encontrado
                }
            }
        }
        return false; // Usuário não encontrado
    }

    /**
     * Método que retorna um objeto Usuario caso o login e senha estejam corretos.
     *
     * @param login o login do usuário.
     * @param senha a senha do usuário.
     * @return o objeto Usuario se as credenciais estiverem corretas, caso contrário, retorna null.
     */
    public Usuario validarLogin2 (String login, String senha) {
        for (Usuario usuario : usuariosCadastrados) {
            if (usuario.getLogin().equals(login)) {
                // Se o login estiver cadastrado, verifica a senha
                if (usuario.getSenha().equals(senha)) {
                    usuario.setLogado(true);
                    return usuario; // Usuário encontrado
                }
            }
        }
        return null;
    }

    /**
     * Valida se um login ou email já está cadastrado no sistema.
     *
     * @param login o login do usuário.
     * @param email o email do usuário.
     * @return true se o login ou email já estiver cadastrado, caso contrário, false.
     */
    public boolean validarCadastro(String login, String email) {
        for (Usuario usuario : usuariosCadastrados) {
            if (usuario.getLogin().equals(login) || usuario.getEmail().equals(email)) {
                return true; // Usuário encontrado
            }
        }
        return false; // Usuário não encontrado
    }

    /**
     * Retorna true se usuário for administrador.
     *
     * @return true se usuário for administrador, senão, false.
     */
    public boolean isAdmin() {
        return adm;
    }

    /**
     * Desloga usuário.
     */
    public void logout() {
        setLogado(false);
    }

    /**
     * Adiciona uma forma de pagamento à lista de formas de pagamento do usuário.
     *
     * @param pagamento a forma de pagamento que será adicionada.
     * @throws FormaDePagamentoInvalidaException se a forma de pagamento for inválida.
     */
    public void adicionarFormaDePagamento(Pagamento pagamento) {
        if (pagamento.getFormaDePagamento().equals("Boleto bancário") || pagamento.getFormaDePagamento().equals("Cartão")) {
            formasDePagamento.add(pagamento);
        } else {
            throw new FormaDePagamentoInvalidaException("Forma de pagamento inválida.");
        }
    }

    /**
     * Remove forma de pagamento da lista de formas de pagamento do usuário.
     *
     * @param pagamento a forma de pagamento que será removida.
     * @throws NaoEncontradoException se a forma de pagamento não for encontrada.
     */
    public void removerFormaDePagamento (Pagamento pagamento) {
        if (formasDePagamento.contains(pagamento)) {
            formasDePagamento.remove(pagamento);
        } else {
            throw new NaoEncontradoException("Forma de pagamento não encontrada.");
        }
    }

    /**
     * Escolhe uma forma de pagamento da lista de formas de pagamento do usuário.
     *
     * @param pagamento forma de pagamento escolhida.
     * @return forma de pagamento escolhida.
     * @throws NaoEncontradoException caso a forma de pagamento não for encontrada.
     */
    public Pagamento escolherFormaPagamento(Pagamento pagamento) {
        boolean contemPagamento = formasDePagamento.contains(pagamento);
        if (contemPagamento) {
            formasDePagamento.add(pagamento);
            return pagamento;
        } else {
            throw new NaoEncontradoException("Forma de pagamento não cadastrada.");
        }
    }

    /**
     * Adiciona uma compra à lista de ingressos comprados pelo usuário.
     *
     * @param compra compra que será adicionada.
     */
    public void adicionarCompras(Compra compra) {
        this.compra = compra;
        ingressosComprados.add(compra);
    }

    /**
     * Cancela ingresso comprado pelo usuário. Remove o ingresso da lista de ingressos comprados
     * pelo usuário, da lista de ingressos comprados do evento e altera o status da compra para "Cancelado".
     *
     * @param usuario o usuário que cancela a compra.
     * @param ingresso o ingresso que será cancelado.
     * @return true se o ingresso foi cancelado com sucesso, caso contrário, false.
     *
     * @throws ReembolsoException se a compra já tiver sido cancelada anteriormente.
     */
    public boolean cancelarIngressoComprado(Usuario usuario, Ingresso ingresso) {
        Iterator<Compra> iterator = ingressosComprados.iterator();

        while (iterator.hasNext()) {
            Compra ing = iterator.next();

            if (ing.getIngresso().equals(ingresso)) { // Procura por ingresso comprado
                boolean cancelar = ingresso.cancelarIngresso();

                if (cancelar) { // Verifica se o ingresso foi encontrado
                    iterator.remove();
                    compra.cancelarCompra(usuario, compra); // Altera status da compra para cancelado
                    ingresso.getEvento().cancelarIngressoComprado(ingresso); // Cancela ingresso comprado do evento
                    return true; // Retorna true se o ingresso foi cancelado
                }
            }
        }
        throw new ReembolsoException("A compra já foi cancelada anteriormente, e o reembolso já foi processado."); // Não encontrado ou fora do prazo
    }

    /**
     * Método equals sobrescrito para comparar dois objetos Usuario.
     * Dois usuários são iguais se o login, CPF e email forem iguais.
     *
     * @param o objeto que será comparado.
     * @return true se os objetos forem iguais, caso contrário, false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) o;
        return Objects.equals(login, usuario.login) && Objects.equals(cpf, usuario.cpf) && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, cpf, email);
    }

    /**
     * Limpa a lista de usuários cadastrados.
     */
    public static void limparUsuariosCadastrados() {
        usuariosCadastrados.clear();
    }

    /**
     * Retorna a lista de ingressos comprados pelo usuário.
     *
     * @return lista de ingressos comprados.
     */
    public List<Ingresso> getIngressos() {
        List <Ingresso>ingressosComprados = new ArrayList<>();
        for (Compra compra : this.ingressosComprados) {
            Ingresso ingresso = compra.getIngresso();
            if (ingresso != null) {
                ingressosComprados.add(ingresso);
            }
        }
        return ingressosComprados;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public List<Pagamento> getFormasDePagamento() {
        return formasDePagamento;
    }

    public List<Compra> getIngressosComprados() {
        return ingressosComprados;
    }

    public List<Compra> getCompras() {
        return ingressosComprados;
    }

    public Compra getCompra() {
        return compra;
    }

    public static List<Usuario> getUsuariosCadastrados() {
        return usuariosCadastrados;
    }

    public List<Usuario> retornarUsuariosCadastrados() {
        return usuariosCadastrados;
    }

    public boolean isLogado() {
        return isLogado;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setLogado(boolean logado) {
        isLogado = logado;
    }

    public static void setUsuariosCadastrados(List<Usuario> usuariosCadastrados) {
        Usuario.usuariosCadastrados = usuariosCadastrados;
    }

    public void setFormasDePagamento(List<Pagamento> formasDePagamento) {
        this.formasDePagamento = formasDePagamento;
    }

    public void setIngressosComprados(List<Compra> ingressosComprados) {
        this.ingressosComprados = ingressosComprados;
    }
}

