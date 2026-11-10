package biblioteca.notificacion;

import biblioteca.dominio.Usuario;

public interface Notificador {
    void notificar(Usuario usuario, String mensaje);
}
