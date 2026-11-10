package biblioteca.persistencia;

import biblioteca.dominio.Prestamo;
import java.util.List;
import java.util.Optional;

/** Abstracción de persistencia: el servicio depende de esta interfaz, no de una base de datos concreta (DIP). */
public interface PrestamoRepositorio {
    void guardar(Prestamo prestamo);
    Optional<Prestamo> buscarPorId(String id);
    List<Prestamo> buscarActivosPorUsuario(String idUsuario);
    List<Prestamo> listarActivos();
    List<Prestamo> listarTodos();
}
