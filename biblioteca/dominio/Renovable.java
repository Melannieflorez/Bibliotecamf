package biblioteca.dominio;

/** Solo los materiales que admiten renovación implementan esta interfaz (ISP). */
public interface Renovable {
    int getDiasRenovacion();
    int getMaxRenovaciones();
}
