package biblioteca.persistencia;

import biblioteca.dominio.Material;
import biblioteca.dominio.Usuario;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Guarda en memoria los usuarios y materiales que el bibliotecario registra desde el menú. */
public class CatalogoMemoria {
    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    private final Map<String, Material> materiales = new LinkedHashMap<>();
    private int consecutivoUsuario = 0;
    private int consecutivoMaterial = 0;

    public String nuevoIdUsuario() { return "U" + (++consecutivoUsuario); }
    public String nuevoIdMaterial() { return "M" + (++consecutivoMaterial); }

    public void agregarUsuario(Usuario u) { usuarios.put(u.getId(), u); }
    public void agregarMaterial(Material m) { materiales.put(m.getId(), m); }

    public Usuario buscarUsuario(String id) { return usuarios.get(id.toUpperCase()); }
    public Material buscarMaterial(String id) { return materiales.get(id.toUpperCase()); }

    public List<Usuario> listarUsuarios() { return new ArrayList<>(usuarios.values()); }
    public List<Material> listarMateriales() { return new ArrayList<>(materiales.values()); }
}
