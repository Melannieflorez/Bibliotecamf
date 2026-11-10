package biblioteca.politicas;

public class MultaEstudiante implements PoliticaMulta {
    private static final double VALOR_DIA = 500;   // COP por día de retraso

    @Override
    public double calcular(long diasRetraso) { return diasRetraso * VALOR_DIA; }
}
