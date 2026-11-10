package biblioteca.dominio;

/**
 * Material de consulta en sala. NO implementa Prestable, así que nunca podrá llegar a PrestamoService.prestar().
 * Con esto se respeta Liskov: ningún "Prestable" lanza UnsupportedOperationException.
 */
public class EnciclopediaReferencia extends Material {
    private final String salaConsulta;

    public EnciclopediaReferencia(String id, String titulo, String salaConsulta) {
        super(id, titulo);
        this.salaConsulta = salaConsulta;
    }

    public String getSalaConsulta() { return salaConsulta; }
}
