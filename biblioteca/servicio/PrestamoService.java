package biblioteca.servicio;

import biblioteca.dominio.Prestable;
import biblioteca.dominio.Prestamo;
import biblioteca.dominio.Renovable;
import biblioteca.dominio.Reservable;
import biblioteca.dominio.Usuario;
import biblioteca.notificacion.Notificador;
import biblioteca.persistencia.PrestamoRepositorio;
import java.time.LocalDate;

/**
 * Casos de uso del préstamo. Su única razón de cambio son las reglas de negocio del préstamo (SRP).
 * Solo conoce abstracciones: PrestamoRepositorio, Notificador, Prestable y PoliticaMulta (DIP).
 */
public class PrestamoService {
    private static final int MAX_PRESTAMOS_ACTIVOS = 3;

    private final PrestamoRepositorio repositorio;
    private final Notificador notificador;
    private int consecutivo = 0;

    // Inyección de dependencias por constructor
    public PrestamoService(PrestamoRepositorio repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public Prestamo prestar(Usuario usuario, Prestable material, LocalDate hoy) {
        if (!material.estaDisponible()) {
            throw new IllegalStateException("El material '" + material.getTitulo() + "' no está disponible.");
        }
        if (repositorio.buscarActivosPorUsuario(usuario.getId()).size() >= MAX_PRESTAMOS_ACTIVOS) {
            throw new IllegalStateException("El usuario alcanzó el máximo de préstamos activos.");
        }
        Prestamo prestamo = new Prestamo("P" + (++consecutivo), material, usuario, hoy);
        material.marcarPrestado();
        repositorio.guardar(prestamo);
        notificador.notificar(usuario, "Préstamo registrado. Debe devolver '" + material.getTitulo()
                + "' el " + prestamo.getFechaVencimiento() + ".");
        return prestamo;
    }

    public void renovar(String idPrestamo, LocalDate hoy) {
        Prestamo prestamo = buscar(idPrestamo);
        Prestable material = prestamo.getMaterial();

        if (!(material instanceof Renovable)) {
            throw new IllegalStateException("Este material no admite renovación.");
        }
        Renovable renovable = (Renovable) material;
        if (prestamo.getRenovaciones() >= renovable.getMaxRenovaciones()) {
            throw new IllegalStateException("Se alcanzó el máximo de renovaciones.");
        }
        if (material instanceof Reservable && ((Reservable) material).estaReservado()) {
            throw new IllegalStateException("El material tiene una reserva pendiente.");
        }
        prestamo.renovar(renovable.getDiasRenovacion());
        repositorio.guardar(prestamo);
        notificador.notificar(prestamo.getUsuario(), "Préstamo renovado. Nueva fecha de entrega: "
                + prestamo.getFechaVencimiento() + ".");
    }

    /** @return valor de la multa generada (0 si se devolvió a tiempo). */
    public double devolver(String idPrestamo, LocalDate fechaDevolucion) {
        Prestamo prestamo = buscar(idPrestamo);
        prestamo.registrarDevolucion(fechaDevolucion);
        prestamo.getMaterial().marcarDevuelto();
        repositorio.guardar(prestamo);

        long retraso = prestamo.diasDeRetraso(fechaDevolucion);
        // OCP: la política concreta (estudiante, docente, ...) viene del usuario; aquí no hay if/else por tipo.
        double multa = prestamo.getUsuario().getPoliticaMulta().calcular(retraso);
        if (multa > 0) {
            notificador.notificar(prestamo.getUsuario(), "Devolución con " + retraso
                    + " día(s) de retraso. Multa: $" + (long) multa + " COP.");
        }
        return multa;
    }

    private Prestamo buscar(String idPrestamo) {
        return repositorio.buscarPorId(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado: " + idPrestamo));
    }
}
