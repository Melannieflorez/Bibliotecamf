package biblioteca.dominio;

/** Solo los materiales que admiten reserva implementan esta interfaz (ISP). */
public interface Reservable {
    void reservar(String idUsuario);
    boolean estaReservado();
}
