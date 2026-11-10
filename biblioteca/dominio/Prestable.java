package biblioteca.dominio;

/** Contrato mínimo de un material que se puede prestar (ISP: interfaz pequeña y cohesiva). */
public interface Prestable {
    String getId();
    String getTitulo();
    boolean estaDisponible();
    void marcarPrestado();
    void marcarDevuelto();
    int getDiasPrestamo();
}
