package modelo;

public class SensorRadiacao extends DispositivoMonitoramento {

    private final ZonaRestrita zonaMonitorada;
    private final double limiteSeguranca;
    private double leituraAtual;

    public SensorRadiacao(String codigoIdentificador, ZonaRestrita zonaMonitorada) {
        super(codigoIdentificador, descricaoDa(zonaMonitorada));
        this.zonaMonitorada = zonaMonitorada;
        this.limiteSeguranca = zonaMonitorada.getLimiteRadiacao();
        this.leituraAtual = 0.0;
    }

    // registra a leitura na zona (que valida) e, caso seja aceita, atualiza o sensor
    public void registrarLeitura(double leitura) {
        zonaMonitorada.registrarLeituraRadiacao(leitura);
        this.leituraAtual = leitura;
    }

    public double getLeituraAtual() {
        return leituraAtual;
    }

    public double getLimiteSeguranca() {
        return limiteSeguranca;
    }

    public ZonaRestrita getZonaMonitorada() {
        return zonaMonitorada;
    }

    private static String descricaoDa(ZonaRestrita zona) {
        if (zona == null) {
            throw new IllegalArgumentException("A zona monitorada é obrigatória.");
        }
        return zona.getDescricao();
    }
}
