package biblioteca.servicio;

import biblioteca.dominio.Prestamo;
import biblioteca.ui.Colores;

/** Solo se encarga de dar formato al comprobante (SRP). Si cambia el formato, no se toca la lógica del préstamo. */
public class ComprobanteFormateador {
    public String formatear(Prestamo p) {
        return "=== COMPROBANTE DE PRÉSTAMO " + p.getId() + " ===\n"
             + "Usuario : " + p.getUsuario().getNombre() + "\n"
             + "Material: " + p.getMaterial().getTitulo() + "\n"
             + "Préstamo: " + Colores.rojo(p.getFechaPrestamo().toString()) + "\n"
             + "Entrega : " + p.getFechaVencimiento() + "\n"
             + "Estado  : " + Colores.amarillo(p.getEstado()) + "\n";
    }
}
