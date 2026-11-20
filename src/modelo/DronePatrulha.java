package modelo;

import servico.CameraVisaoComputacional;
import servico.ResultadoVisao;

public class DronePatrulha extends DispositivoMonitoramento {

    public static final int BATERIA_MINIMA_PARA_MISSAO = 20;

    private final String rotaPatrulha;
    private final CameraVisaoComputacional camera;
    private int bateriaPercentual;
    private String destinoAtual;

    // a localizacao do drone é a sua rota de patrulha
    public DronePatrulha(String codigoIdentificador, String rotaPatrulha, int bateriaPercentual) {
        super(codigoIdentificador, rotaPatrulha);
        if (bateriaPercentual < 0 || bateriaPercentual > 100) {
            throw new IllegalArgumentException("A bateria deve estar entre 0% e 100%.");
        }
        this.rotaPatrulha = rotaPatrulha;
        this.bateriaPercentual = bateriaPercentual;
        this.camera = new CameraVisaoComputacional();
        this.destinoAtual = null;
    }

    // desloca o drone ate um setor
    // exige drone em operacao e bateria sufciente
    public void enviarParaSetor(String setor) {
        if (setor == null || setor.isBlank()) {
            throw new IllegalArgumentException("O setor de destino é obrigatório.");
        }
        if (!isEmOperacao()) {
            throw new IllegalStateException("O drone " + getCodigoIdentificador() + " está fora de operação.");
        }
        if (bateriaPercentual < BATERIA_MINIMA_PARA_MISSAO) {
            throw new IllegalStateException("Bateria insuficiente para missão: " + bateriaPercentual + "%.");
        }
        this.destinoAtual = setor;
    }

    public void simularCena(ResultadoVisao cena) {
        camera.simularCena(cena);
    }

    public ResultadoVisao analisarImagem() {
        return camera.analisarImagem();
    }

    public String getRotaPatrulha() {
        return rotaPatrulha;
    }

    public int getBateriaPercentual() {
        return bateriaPercentual;
    }

    // setor para o qual o drone foi enviado, ou null se estiver apenas na rota normal
    public String getDestinoAtual() {
        return destinoAtual;
    }
}
