package biblioteca;

import biblioteca.notificacion.Notificador;
import biblioteca.notificacion.NotificadorConsola;
import biblioteca.persistencia.CatalogoMemoria;
import biblioteca.persistencia.PrestamoRepositorio;
import biblioteca.persistencia.PrestamoRepositorioMemoria;
import biblioteca.servicio.ComprobanteFormateador;
import biblioteca.servicio.PrestamoService;
import biblioteca.ui.MenuConsola;
import biblioteca.ui.VentanaBiblioteca;
import javax.swing.SwingUtilities;

/** Punto de composición: aquí se eligen las implementaciones concretas (DIP). */
public class Main {
    public static void main(String[] args) {
        PrestamoRepositorio repositorio = new PrestamoRepositorioMemoria();
        Notificador notificador = new NotificadorConsola();
        PrestamoService servicio = new PrestamoService(repositorio, notificador);
        CatalogoMemoria catalogo = new CatalogoMemoria();

        if (args.length > 0 && args[0].equals("consola")) {
            // versión por consola
            new MenuConsola(servicio, repositorio, catalogo, new ComprobanteFormateador()).iniciar();
        } else {
            // por defecto se abre la ventana
            SwingUtilities.invokeLater(() -> new VentanaBiblioteca(servicio, repositorio, catalogo).setVisible(true));
        }
    }
}
