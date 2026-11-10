package biblioteca.ui;

import biblioteca.dominio.EnciclopediaReferencia;
import biblioteca.dominio.Libro;
import biblioteca.dominio.Material;
import biblioteca.dominio.Prestable;
import biblioteca.dominio.Prestamo;
import biblioteca.dominio.Reservable;
import biblioteca.dominio.Revista;
import biblioteca.dominio.Usuario;
import biblioteca.persistencia.CatalogoMemoria;
import biblioteca.persistencia.PrestamoRepositorio;
import biblioteca.politicas.MultaDocente;
import biblioteca.politicas.MultaEstudiante;
import biblioteca.politicas.PoliticaMulta;
import biblioteca.servicio.PrestamoService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal (Swing). Solo muestra datos y recoge lo que escribe el usuario;
 * las reglas del préstamo siguen en PrestamoService.
 */
public class VentanaBiblioteca extends JFrame {
    // colores pedidos en el taller
    private static final Color ROJO = new Color(255, 170, 170);      // fecha del préstamo
    private static final Color AMARILLO = new Color(255, 235, 100);  // estado del préstamo
    private static final Color VERDE = new Color(120, 210, 120);     // registrar / finalizar préstamo

    private final PrestamoService servicio;
    private final PrestamoRepositorio repositorio;
    private final CatalogoMemoria catalogo;

    // usuarios
    private final JTextField txtNombre = new JTextField(14);
    private final JTextField txtCorreo = new JTextField(16);
    private final JComboBox<String> cmbTipoUsuario = new JComboBox<>(new String[]{"Estudiante", "Docente"});
    private final DefaultTableModel modeloUsuarios = modelo("Código", "Nombre", "Correo");

    // materiales
    private final JComboBox<String> cmbTipoMaterial = new JComboBox<>(new String[]{"Libro", "Revista", "Material de consulta"});
    private final JTextField txtTitulo = new JTextField(14);
    private final JLabel lblExtra1 = new JLabel("Autor:");
    private final JTextField txtExtra1 = new JTextField(12);
    private final JLabel lblExtra2 = new JLabel("ISBN:");
    private final JTextField txtExtra2 = new JTextField(10);
    private final JComboBox<String> cmbUsuarioReserva = new JComboBox<>();
    private final DefaultTableModel modeloMateriales = modelo("Código", "Tipo", "Título", "Estado");
    private final JTable tablaMateriales = new JTable(modeloMateriales);

    // préstamos
    private final JComboBox<String> cmbUsuarioPrestamo = new JComboBox<>();
    private final JComboBox<String> cmbMaterialPrestamo = new JComboBox<>();
    private final DefaultTableModel modeloPrestamos = modelo("Código", "Usuario", "Material", "Fecha del préstamo", "Estado", "Vence", "Renovaciones");
    private final JTable tablaPrestamos = new JTable(modeloPrestamos);

    private final JLabel lblMensaje = new JLabel(" ");

    public VentanaBiblioteca(PrestamoService servicio, PrestamoRepositorio repositorio, CatalogoMemoria catalogo) {
        super("Biblioteca - Control de préstamos");
        this.servicio = servicio;
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // si falla se deja el aspecto por defecto
        }
        construir();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(960, 600);
        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------------- construcción
    private void construir() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Usuarios", panelUsuarios());
        pestanas.addTab("Materiales", panelMateriales());
        pestanas.addTab("Préstamos", panelPrestamos());

        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.BOLD, 13f));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        setLayout(new BorderLayout());
        add(pestanas, BorderLayout.CENTER);
        add(lblMensaje, BorderLayout.SOUTH);
        refrescarTodo();
    }

    private JPanel panelUsuarios() {
        JButton btn = boton("Registrar usuario", null);
        btn.addActionListener(e -> registrarUsuario());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo usuario"));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Correo:"));
        form.add(txtCorreo);
        form.add(new JLabel("Tipo:"));
        form.add(cmbTipoUsuario);
        form.add(btn);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(modeloUsuarios)), BorderLayout.CENTER);
        return p;
    }

    private JPanel panelMateriales() {
        cmbTipoMaterial.addActionListener(e -> ajustarCamposMaterial());
        JButton btnRegistrar = boton("Registrar material", null);
        btnRegistrar.addActionListener(e -> registrarMaterial());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo material"));
        form.add(new JLabel("Tipo:"));
        form.add(cmbTipoMaterial);
        form.add(new JLabel("Título:"));
        form.add(txtTitulo);
        form.add(lblExtra1);
        form.add(txtExtra1);
        form.add(lblExtra2);
        form.add(txtExtra2);
        form.add(btnRegistrar);

        JButton btnReservar = boton("Reservar libro seleccionado", null);
        btnReservar.addActionListener(e -> reservar());
        JPanel reserva = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        reserva.setBorder(BorderFactory.createTitledBorder("Reserva"));
        reserva.add(new JLabel("Usuario:"));
        reserva.add(cmbUsuarioReserva);
        reserva.add(btnReservar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.NORTH);
        norte.add(reserva, BorderLayout.SOUTH);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaMateriales), BorderLayout.CENTER);
        ajustarCamposMaterial();
        return p;
    }

    private JPanel panelPrestamos() {
        JButton btnPrestar = boton("Prestar libro", VERDE);
        btnPrestar.addActionListener(e -> prestar());
        JButton btnDevolver = boton("Finalizar préstamo (devolver)", VERDE);
        btnDevolver.addActionListener(e -> devolver());
        JButton btnRenovar = boton("Renovar préstamo", null);
        btnRenovar.addActionListener(e -> renovar());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo préstamo"));
        form.add(new JLabel("Usuario:"));
        form.add(cmbUsuarioPrestamo);
        form.add(new JLabel("Material:"));
        form.add(cmbMaterialPrestamo);
        form.add(btnPrestar);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acciones.setBorder(BorderFactory.createTitledBorder("Sobre el préstamo seleccionado en la tabla"));
        acciones.add(btnDevolver);
        acciones.add(btnRenovar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.NORTH);
        norte.add(acciones, BorderLayout.SOUTH);

        tablaPrestamos.setRowHeight(24);
        tablaPrestamos.setDefaultRenderer(Object.class, new RenderizadorPrestamos());

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);
        return p;
    }

    /** Pinta la fecha de rojo y el estado de amarillo (control de préstamos con color). */
    private static class RenderizadorPrestamos extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int fila, int col) {
            Component c = super.getTableCellRendererComponent(t, v, sel, foco, fila, col);
            if (col == 3) {
                c.setBackground(ROJO);
                c.setForeground(Color.BLACK);
            } else if (col == 4) {
                c.setBackground(AMARILLO);
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(sel ? t.getSelectionBackground() : t.getBackground());
                c.setForeground(sel ? t.getSelectionForeground() : t.getForeground());
            }
            return c;
        }
    }

    // ---------------------------------------------------------------- acciones
    private void registrarUsuario() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        if (nombre.isEmpty() || correo.isEmpty()) {
            error("Debe escribir el nombre y el correo.");
            return;
        }
        if (!correo.contains("@")) {
            error("El correo electrónico no es válido.");
            return;
        }
        PoliticaMulta politica;
        String etiqueta;
        if (cmbTipoUsuario.getSelectedIndex() == 0) {
            politica = new MultaEstudiante();
            etiqueta = "estudiante";
        } else {
            politica = new MultaDocente();
            etiqueta = "docente";
        }
        Usuario u = new Usuario(catalogo.nuevoIdUsuario(), nombre + " (" + etiqueta + ")", correo, politica);
        catalogo.agregarUsuario(u);
        txtNombre.setText("");
        txtCorreo.setText("");
        refrescarTodo();
        ok("Usuario registrado con código " + u.getId() + ".");
    }

    private void registrarMaterial() {
        String titulo = txtTitulo.getText().trim();
        String extra1 = txtExtra1.getText().trim();
        String extra2 = txtExtra2.getText().trim();
        int tipo = cmbTipoMaterial.getSelectedIndex();
        if (titulo.isEmpty() || extra1.isEmpty() || (tipo == 0 && extra2.isEmpty())) {
            error("Complete todos los datos del material.");
            return;
        }
        String id = catalogo.nuevoIdMaterial();
        Material m;
        if (tipo == 0) {
            m = new Libro(id, titulo, extra1, extra2);
        } else if (tipo == 1) {
            m = new Revista(id, titulo, extra1);
        } else {
            m = new EnciclopediaReferencia(id, titulo, extra1);
        }
        catalogo.agregarMaterial(m);
        txtTitulo.setText("");
        txtExtra1.setText("");
        txtExtra2.setText("");
        refrescarTodo();
        ok("Material registrado con código " + m.getId() + ".");
    }

    private void reservar() {
        int fila = tablaMateriales.getSelectedRow();
        if (fila < 0) {
            error("Seleccione un libro en la tabla.");
            return;
        }
        Material m = catalogo.buscarMaterial((String) modeloMateriales.getValueAt(fila, 0));
        Usuario u = usuarioDe(cmbUsuarioReserva);
        if (u == null) {
            error("Primero registre un usuario.");
            return;
        }
        if (!(m instanceof Reservable)) {
            error("Este material no admite reservas.");
            return;
        }
        ((Reservable) m).reservar(u.getId());
        ok("Reserva registrada para " + u.getNombre() + ".");
    }

    private void prestar() {
        Usuario u = usuarioDe(cmbUsuarioPrestamo);
        Material m = materialDe(cmbMaterialPrestamo);
        if (u == null || m == null) {
            error("Debe haber al menos un usuario y un material registrados.");
            return;
        }
        if (!(m instanceof Prestable)) {
            error("Este material es de solo consulta y no se puede prestar.");
            return;
        }
        try {
            Prestamo p = servicio.prestar(u, (Prestable) m, LocalDate.now());
            refrescarTodo();
            ok("Préstamo " + p.getId() + " registrado. Debe devolverse el " + p.getFechaVencimiento() + ".");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void devolver() {
        String id = idPrestamoSeleccionado();
        if (id == null) return;
        try {
            if (!repositorio.buscarPorId(id).get().estaActivo()) {
                error("Ese préstamo ya fue finalizado.");
                return;
            }
            double multa = servicio.devolver(id, LocalDate.now());
            refrescarTodo();
            String texto = "Préstamo " + id + " finalizado.";
            if (multa > 0) texto += " Multa: $" + (long) multa + " COP.";
            ok(texto);
        } catch (IllegalStateException | IllegalArgumentException | DateTimeParseException ex) {
            error(ex.getMessage());
        }
    }

    private void renovar() {
        String id = idPrestamoSeleccionado();
        if (id == null) return;
        try {
            servicio.renovar(id, LocalDate.now());
            refrescarTodo();
            ok("Préstamo " + id + " renovado.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    // ---------------------------------------------------------------- refrescar pantalla
    private void refrescarTodo() {
        modeloUsuarios.setRowCount(0);
        for (Usuario u : catalogo.listarUsuarios()) {
            modeloUsuarios.addRow(new Object[]{u.getId(), u.getNombre(), u.getCorreo()});
        }
        modeloMateriales.setRowCount(0);
        for (Material m : catalogo.listarMateriales()) {
            modeloMateriales.addRow(new Object[]{m.getId(), tipoDe(m), m.getTitulo(), estadoDe(m)});
        }
        modeloPrestamos.setRowCount(0);
        for (Prestamo p : repositorio.listarTodos()) {
            modeloPrestamos.addRow(new Object[]{p.getId(), p.getUsuario().getNombre(), p.getMaterial().getTitulo(),
                    p.getFechaPrestamo(), p.getEstado(), p.getFechaVencimiento(), p.getRenovaciones()});
        }
        llenarCombos();
    }

    private void llenarCombos() {
        cmbUsuarioPrestamo.removeAllItems();
        cmbUsuarioReserva.removeAllItems();
        for (Usuario u : catalogo.listarUsuarios()) {
            cmbUsuarioPrestamo.addItem(u.getId() + " - " + u.getNombre());
            cmbUsuarioReserva.addItem(u.getId() + " - " + u.getNombre());
        }
        cmbMaterialPrestamo.removeAllItems();
        for (Material m : catalogo.listarMateriales()) {
            cmbMaterialPrestamo.addItem(m.getId() + " - " + m.getTitulo());
        }
    }

    private void ajustarCamposMaterial() {
        int tipo = cmbTipoMaterial.getSelectedIndex();
        lblExtra1.setText(tipo == 0 ? "Autor:" : tipo == 1 ? "Edición:" : "Sala:");
        lblExtra2.setVisible(tipo == 0);
        txtExtra2.setVisible(tipo == 0);
    }

    // ---------------------------------------------------------------- utilidades
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

    private Usuario usuarioDe(JComboBox<String> combo) {
        Object item = combo.getSelectedItem();
        if (item == null) return null;
        return catalogo.buscarUsuario(item.toString().split(" - ")[0]);
    }

    private Material materialDe(JComboBox<String> combo) {
        Object item = combo.getSelectedItem();
        if (item == null) return null;
        return catalogo.buscarMaterial(item.toString().split(" - ")[0]);
    }

    private String idPrestamoSeleccionado() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila < 0) {
            error("Seleccione un préstamo en la tabla.");
            return null;
        }
        return (String) modeloPrestamos.getValueAt(fila, 0);
    }

    private JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        if (fondo != null) {
            b.setBackground(fondo);
            b.setOpaque(true);
        }
        return b;
    }

    private static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int col) { return false; }
        };
    }

    private void ok(String texto) {
        lblMensaje.setOpaque(true);
        lblMensaje.setBackground(VERDE);
        lblMensaje.setForeground(Color.BLACK);
        lblMensaje.setText(texto);
    }

    private void error(String texto) {
        lblMensaje.setOpaque(true);
        lblMensaje.setBackground(new Color(255, 120, 120));
        lblMensaje.setForeground(Color.BLACK);
        lblMensaje.setText(texto);
    }
}
