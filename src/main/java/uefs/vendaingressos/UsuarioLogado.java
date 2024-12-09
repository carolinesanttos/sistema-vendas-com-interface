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

import uefs.vendaingressos.model.Usuario;

/**
 * Classe responsável por gerenciar o usuário atualmente logado na aplicação.
 * Utiliza um atributo estático para manter a referência ao usuário ativo.
 */
public class UsuarioLogado {

    private static Usuario usuarioAtual;

    /**
     * Obtém o usuário atualmente logado.
     *
     * @return O usuário logado atualmente.
     */
    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    /**
     * Define o usuário como o atual logado no sistema.
     *
     * @param usuario O usuário que será definido como logado.
     */
    public static void setUsuarioAtual(Usuario usuario) {
        usuarioAtual = usuario;
    }
}
