package biblioteca.notificacion;

import biblioteca.dominio.Usuario;

public class NotificadorConsola implements Notificador {
    @Override
    public void notificar(Usuario usuario, String mensaje) {
        System.out.println("[Notificación para " + usuario.getNombre() + "] " + mensaje);
    }
}
