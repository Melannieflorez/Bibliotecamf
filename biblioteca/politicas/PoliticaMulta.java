package biblioteca.politicas;

/** Abstracción para calcular multas. Agregar un nuevo tipo de usuario = agregar una clase, sin modificar el servicio (OCP). */
public interface PoliticaMulta {
    double calcular(long diasRetraso);
}
