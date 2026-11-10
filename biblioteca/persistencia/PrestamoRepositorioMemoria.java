package biblioteca.persistencia;

import biblioteca.dominio.Prestamo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Implementación en memoria. Mañana puede cambiarse por MySQL o archivos sin tocar PrestamoService. */
public class PrestamoRepositorioMemoria implements PrestamoRepositorio {
    private final Map<String, Prestamo> datos = new LinkedHashMap<>();

    @Override
    public void guardar(Prestamo prestamo) { datos.put(prestamo.getId(), prestamo); }

    @Override
    public Optional<Prestamo> buscarPorId(String id) { return Optional.ofNullable(datos.get(id.toUpperCase())); }

    @Override
    public List<Prestamo> buscarActivosPorUsuario(String idUsuario) {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : datos.values()) {
            if (p.estaActivo() && p.getUsuario().getId().equals(idUsuario)) {
                activos.add(p);
            }
        }
        return activos;
    }

    @Override
    public List<Prestamo> listarActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : datos.values()) {
            if (p.estaActivo()) {
                activos.add(p);
            }
        }
        return activos;
    }

    @Override
    public List<Prestamo> listarTodos() { return new ArrayList<>(datos.values()); }
}
