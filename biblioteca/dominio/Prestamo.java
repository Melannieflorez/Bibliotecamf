package biblioteca.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Entidad Préstamo: solo guarda su estado y sus reglas propias (alta cohesión). */
public class Prestamo {
    private final String id;
    private final Prestable material;
    private final Usuario usuario;
    private final LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion;
    private int renovaciones;

    public Prestamo(String id, Prestable material, Usuario usuario, LocalDate fechaPrestamo) {
        this.id = id;
        this.material = material;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaPrestamo.plusDays(material.getDiasPrestamo());
    }

    public void renovar(int dias) {
        fechaVencimiento = fechaVencimiento.plusDays(dias);
        renovaciones++;
    }

    public void registrarDevolucion(LocalDate fecha) { this.fechaDevolucion = fecha; }

    public boolean estaActivo() { return fechaDevolucion == null; }

    /** Estado del préstamo para mostrarlo en pantalla. */
    public String getEstado() { return estaActivo() ? "ACTIVO" : "FINALIZADO"; }

    public long diasDeRetraso(LocalDate fechaReferencia) {
        long dias = ChronoUnit.DAYS.between(fechaVencimiento, fechaReferencia);
        return Math.max(0, dias);
    }

    public String getId() { return id; }
    public Prestable getMaterial() { return material; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public int getRenovaciones() { return renovaciones; }
}
