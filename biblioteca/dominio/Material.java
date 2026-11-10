package biblioteca.dominio;

/** Información básica de cualquier material de la biblioteca (SRP: solo describe el material). */
public abstract class Material {
    private final String id;
    private final String titulo;

    protected Material(String id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
}
