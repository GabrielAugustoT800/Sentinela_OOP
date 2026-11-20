package servico;

public class CameraVisaoComputacional {

    private ResultadoVisao cenaSimulada = ResultadoVisao.NENHUMA_DETECCAO;

    public void simularCena(ResultadoVisao cena) {
        if (cena == null) {
            throw new IllegalArgumentException("A cena simulada é obrigatória.");
        }
        this.cenaSimulada = cena;
    }

    public ResultadoVisao analisarImagem() {
        return cenaSimulada;
    }
}
