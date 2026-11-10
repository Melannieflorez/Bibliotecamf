package biblioteca.dominio;

/** Revista: solo se presta por poco tiempo. No es renovable ni reservable (por eso no implementa esas interfaces). */
public class Revista extends Material implements Prestable {
    private final String edicion;
    private boolean prestada;

    public Revista(String id, String titulo, String edicion) {
        super(id, titulo);
        this.edicion = edicion;
    }

    public String getEdicion() { return edicion; }

    @Override public boolean estaDisponible() { return !prestada; }
    @Override public void marcarPrestado() { prestada = true; }
    @Override public void marcarDevuelto() { prestada = false; }
    @Override public int getDiasPrestamo() { return 3; }
}
