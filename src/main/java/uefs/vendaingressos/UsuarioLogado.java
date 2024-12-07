package uefs.vendaingressos;

import uefs.vendaingressos.model.Usuario;

public class UsuarioLogado {

    private static Usuario usuarioAtual;

    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public static void setUsuarioAtual(Usuario usuario) {
        usuarioAtual = usuario;
    }
}
