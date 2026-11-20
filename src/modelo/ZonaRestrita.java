package modelo;

public class ZonaRestrita {

    public static final String RISCO_NORMAL = "NORMAL";
    public static final String RISCO_ATENCAO = "ATENCAO";
    public static final String RISCO_CRITICO = "CRITICO";

    // se for acima disso a leitura é tratada como falha do sensor (em µSv/h)
    public static final double LEITURA_MAXIMA_PLAUSIVEL = 1_000_000.0;

    private static final double MULTIPLICADOR_CRITICO = 2.0;

    private final String identificador;
    private final String descricao;
    private final double limiteRadiacao;
    private double nivelRadiacaoAtual;

    public ZonaRestrita(String identificador, String descricao, double limiteRadiacao) {
        validarTextoObrigatorio(identificador, "O identificador da zona é obrigatório.");
        validarTextoObrigatorio(descricao, "A descrição da zona é obrigatória.");
        validarLimiteRadiacao(limiteRadiacao);

        this.identificador = identificador;
        this.descricao = descricao;
        this.limiteRadiacao = limiteRadiacao;
        this.nivelRadiacaoAtual = 0.0;
    }

    // atualiza o nivel atual de radiacao (µSv/h)
    // leituras invalidas lancam IllegalArgumentException
    // o ultimo valor valido é mantido
    public void registrarLeituraRadiacao(double leitura) {
        if (Double.isNaN(leitura)) {
            throw new IllegalArgumentException("Leitura de radiação inválida (NaN): possível falha de sensor.");
        }
        if (leitura < 0) {
            throw new IllegalArgumentException("A radiação não pode ser negativa.");
        }
        if (leitura > LEITURA_MAXIMA_PLAUSIVEL) {
            throw new IllegalArgumentException(
                    "Leitura acima do limite físico aceitável: possível falha de sensor.");
        }
        this.nivelRadiacaoAtual = leitura;
    }

    // NORMAL: até o limite
    // ATENCAO: acima do limite até 2x o limite
    // CRITICO: acima de 2x
    public String classificarRisco() {
        if (nivelRadiacaoAtual > limiteRadiacao * MULTIPLICADOR_CRITICO) {
            return RISCO_CRITICO;
        }
        if (nivelRadiacaoAtual > limiteRadiacao) {
            return RISCO_ATENCAO;
        }
        return RISCO_NORMAL;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getLimiteRadiacao() {
        return limiteRadiacao;
    }

    public double getNivelRadiacaoAtual() {
        return nivelRadiacaoAtual;
    }

    private static void validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private static void validarLimiteRadiacao(double limite) {
        if (!(limite > 0) || Double.isInfinite(limite)) {
            throw new IllegalArgumentException("O limite de radiação deve ser maior que zero.");
        }
    }
}
