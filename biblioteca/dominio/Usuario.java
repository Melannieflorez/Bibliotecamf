package biblioteca.dominio;

import biblioteca.politicas.PoliticaMulta;

/** Usuario de la biblioteca. La política de multa se inyecta, no se decide con if/else por tipo de usuario. */
public class Usuario {
    private final String id;
    private final String nombre;
    private final String correo;
    private final PoliticaMulta politicaMulta;

    public Usuario(String id, String nombre, String correo, PoliticaMulta politicaMulta) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.politicaMulta = politicaMulta;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public PoliticaMulta getPoliticaMulta() { return politicaMulta; }
}
