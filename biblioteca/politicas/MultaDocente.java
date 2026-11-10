package biblioteca.politicas;

public class MultaDocente implements PoliticaMulta {
    private static final double VALOR_DIA = 200;   // COP por día de retraso

    @Override
    public double calcular(long diasRetraso) { return diasRetraso * VALOR_DIA; }
}
