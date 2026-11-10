package biblioteca.ui;

import biblioteca.dominio.EnciclopediaReferencia;
import biblioteca.dominio.Libro;
import biblioteca.dominio.Material;
import biblioteca.dominio.Prestable;
import biblioteca.dominio.Prestamo;
import biblioteca.dominio.Revista;
import biblioteca.dominio.Reservable;
import biblioteca.dominio.Usuario;
import biblioteca.persistencia.CatalogoMemoria;
import biblioteca.persistencia.PrestamoRepositorio;
import biblioteca.politicas.MultaDocente;
import biblioteca.politicas.MultaEstudiante;
import biblioteca.politicas.PoliticaMulta;
import biblioteca.servicio.ComprobanteFormateador;
import biblioteca.servicio.PrestamoService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/** Interfaz de usuario por consola (SRP: solo lee datos y muestra resultados; las reglas están en PrestamoService). */
public class MenuConsola {
    private final Scanner in = new Scanner(System.in);
    private final PrestamoService servicio;
    private final PrestamoRepositorio repositorio;
    private final CatalogoMemoria catalogo;
    private final ComprobanteFormateador formateador;

    public MenuConsola(PrestamoService servicio, PrestamoRepositorio repositorio,
                       CatalogoMemoria catalogo, ComprobanteFormateador formateador) {
        this.servicio = servicio;
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        this.formateador = formateador;
    }

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = leerTexto("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case "1": registrarUsuario(); break;
                    case "2": registrarMaterial(); break;
                    case "3": listarUsuarios(); break;
                    case "4": listarMateriales(); break;
                    case "5": prestar(); break;
                    case "6": renovar(); break;
                    case "7": devolver(); break;
                    case "8": reservar(); break;
                    case "9": listarPrestamosActivos(); break;
                    case "0": salir = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (IllegalStateException | IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("¡Hasta pronto!");
    }

    private void mostrarMenu() {
        System.out.println("\n===== BIBLIOTECA - CONTROL DE PRÉSTAMOS =====");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Registrar material");
        System.out.println("3. Listar usuarios");
        System.out.println("4. Listar materiales");
        System.out.println("5. Prestar material");
        System.out.println("6. Renovar préstamo");
        System.out.println("7. Devolver material");
        System.out.println("8. Reservar libro");
        System.out.println("9. Ver préstamos activos");
        System.out.println("0. Salir");
    }

    // ---------- registros ----------
    private void registrarUsuario() {
        String nombre = leerTexto("Nombre del usuario: ");
        String correo = leerTexto("Correo electrónico: ");
        System.out.println("Tipo: 1. Estudiante  2. Docente");
        String tipo = leerTexto("Seleccione el tipo: ");
        PoliticaMulta politica;
        String etiqueta;
        if (tipo.equals("1")) {
            politica = new MultaEstudiante();
            etiqueta = "estudiante";
        } else if (tipo.equals("2")) {
            politica = new MultaDocente();
            etiqueta = "docente";
        } else {
            System.out.println("Tipo no válido.");
            return;
        }
        Usuario u = new Usuario(catalogo.nuevoIdUsuario(), nombre + " (" + etiqueta + ")", correo, politica);
        catalogo.agregarUsuario(u);
        System.out.println("Usuario registrado con código " + u.getId() + ".");
    }

    private void registrarMaterial() {
        System.out.println("Tipo: 1. Libro  2. Revista  3. Material de solo consulta (enciclopedia, etc.)");
        String tipo = leerTexto("Seleccione el tipo: ");
        String id = catalogo.nuevoIdMaterial();
        Material m;
        if (tipo.equals("1")) {
            String titulo = leerTexto("Título: ");
            String autor = leerTexto("Autor: ");
            String isbn = leerTexto("ISBN: ");
            m = new Libro(id, titulo, autor, isbn);
        } else if (tipo.equals("2")) {
            String titulo = leerTexto("Título: ");
            String edicion = leerTexto("Edición / volumen: ");
            m = new Revista(id, titulo, edicion);
        } else if (tipo.equals("3")) {
            String titulo = leerTexto("Título: ");
            String sala = leerTexto("Sala de consulta: ");
            m = new EnciclopediaReferencia(id, titulo, sala);
        } else {
            System.out.println("Tipo no válido.");
            return;
        }
        catalogo.agregarMaterial(m);
        System.out.println("Material registrado con código " + m.getId() + ".");
    }

    // ---------- listados ----------
    private void listarUsuarios() {
        List<Usuario> lista = catalogo.listarUsuarios();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (Usuario u : lista) {
            System.out.println(u.getId() + " - " + u.getNombre() + " - " + u.getCorreo());
        }
    }

    private void listarMateriales() {
        List<Material> lista = catalogo.listarMateriales();
        if (lista.isEmpty()) {
            System.out.println("No hay materiales registrados.");
            return;
        }
        for (Material m : lista) {
            System.out.println(m.getId() + " - [" + tipoDe(m) + "] " + m.getTitulo() + " - " + estadoDe(m));
        }
    }

    private String tipoDe(Material m) {
        if (m instanceof Libro) return "Libro";
        if (m instanceof Revista) return "Revista";
        return "Consulta";
    }

    private String estadoDe(Material m) {
        if (m instanceof Prestable) {
            return ((Prestable) m).estaDisponible() ? "Disponible" : "Prestado";
        }
        return "Solo consulta en sala";
    }

    // ---------- operaciones ----------
    private void prestar() {
        Usuario usuario = pedirUsuario();
        if (usuario == null) return;
        Material material = pedirMaterial();
        if (material == null) return;
        if (!(material instanceof Prestable)) {
            System.out.println("Este material es de solo consulta y no se puede prestar.");
            return;
        }
        Prestamo p = servicio.prestar(usuario, (Prestable) material, LocalDate.now());
        System.out.println(Colores.verde("Préstamo registrado correctamente."));
        System.out.println(formateador.formatear(p));
    }

    private void renovar() {
        String id = leerTexto("Código del préstamo (ej. P1): ");
        servicio.renovar(id, LocalDate.now());
    }

    private void devolver() {
        String id = leerTexto("Código del préstamo (ej. P1): ");
        String fecha = leerTexto("Fecha de devolución (aaaa-mm-dd) o Enter para hoy: ", true);
        LocalDate dia = fecha.isEmpty() ? LocalDate.now() : LocalDate.parse(fecha);
        double multa = servicio.devolver(id, dia);
        System.out.println(Colores.verde("Préstamo finalizado: devolución registrada."));
        if (multa == 0) {
            System.out.println("Devolución sin multa.");
        }
    }

    private void reservar() {
        Material material = pedirMaterial();
        if (material == null) return;
        if (!(material instanceof Reservable)) {
            System.out.println("Este material no admite reservas.");
            return;
        }
        Usuario usuario = pedirUsuario();
        if (usuario == null) return;
        ((Reservable) material).reservar(usuario.getId());
        System.out.println("Reserva registrada para " + usuario.getNombre() + ".");
    }

    private void listarPrestamosActivos() {
        List<Prestamo> lista = repositorio.listarActivos();
        if (lista.isEmpty()) {
            System.out.println("No hay préstamos activos.");
            return;
        }
        LocalDate hoy = LocalDate.now();
        for (Prestamo p : lista) {
            String vencido = p.diasDeRetraso(hoy) > 0 ? "  ** VENCIDO **" : "";
            System.out.println(p.getId() + " | " + p.getUsuario().getNombre() + " | "
                    + p.getMaterial().getTitulo()
                    + " | fecha préstamo: " + Colores.rojo(p.getFechaPrestamo().toString())
                    + " | estado: " + Colores.amarillo(p.getEstado() + vencido)
                    + " | vence " + p.getFechaVencimiento()
                    + " | renovaciones: " + p.getRenovaciones());
        }
    }

    // ---------- utilidades de entrada ----------
    private Usuario pedirUsuario() {
        listarUsuarios();
        Usuario u = catalogo.buscarUsuario(leerTexto("Código del usuario (ej. U1): "));
        if (u == null) System.out.println("Usuario no encontrado.");
        return u;
    }

    private Material pedirMaterial() {
        listarMateriales();
        Material m = catalogo.buscarMaterial(leerTexto("Código del material (ej. M1): "));
        if (m == null) System.out.println("Material no encontrado.");
        return m;
    }

    private String leerTexto(String mensaje) { return leerTexto(mensaje, false); }

    private String leerTexto(String mensaje, boolean permitirVacio) {
        while (true) {
            System.out.print(mensaje);
            if (!in.hasNextLine()) return "0";   // fin de la entrada: salir
            String linea = in.nextLine().trim();
            if (!linea.isEmpty() || permitirVacio) return linea;
            System.out.println("Este dato no puede estar vacío.");
        }
    }
}
