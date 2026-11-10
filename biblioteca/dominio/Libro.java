package biblioteca.dominio;

/** Libro: se puede prestar, renovar y reservar. */
public class Libro extends Material implements Prestable, Renovable, Reservable {
    private final String autor;
    private final String isbn;
    private boolean prestado;
    private String reservadoPor;

    public Libro(String id, String titulo, String autor, String isbn) {
        super(id, titulo);
        this.autor = autor;
        this.isbn = isbn;
    }

    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }

    @Override public boolean estaDisponible() { return !prestado; }
    @Override public void marcarPrestado() { prestado = true; }
    @Override public void marcarDevuelto() { prestado = false; }
    @Override public int getDiasPrestamo() { return 14; }

    @Override public int getDiasRenovacion() { return 7; }
    @Override public int getMaxRenovaciones() { return 2; }

    @Override public void reservar(String idUsuario) { this.reservadoPor = idUsuario; }
    @Override public boolean estaReservado() { return reservadoPor != null; }
}
