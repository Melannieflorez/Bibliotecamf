package biblioteca.ui;

/** Códigos ANSI para pintar el texto en la consola (parte visual del taller). */
public class Colores {
    public static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";

    private Colores() { }

    public static String rojo(String t) { return ROJO + t + RESET; }
    public static String verde(String t) { return VERDE + t + RESET; }
    public static String amarillo(String t) { return AMARILLO + t + RESET; }
}
