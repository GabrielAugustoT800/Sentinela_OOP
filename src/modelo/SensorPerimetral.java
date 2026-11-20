package modelo;

public class SensorPerimetral extends DispositivoMonitoramento {

    private final ZonaRestrita zonaVigiada;
    private final String setorVigiado;
    private boolean violacaoDetectada;

    public SensorPerimetral(String codigoIdentificador, ZonaRestrita zonaVigiada, String setorVigiado) {
        super(codigoIdentificador, descricaoDa(zonaVigiada));
        if (setorVigiado == null || setorVigiado.isBlank()) {
            throw new IllegalArgumentException("O setor vigiado é obrigatório.");
        }
        this.zonaVigiada = zonaVigiada;
        this.setorVigiado = setorVigiado;
        this.violacaoDetectada = false;
    }

    public void detectarViolacao() {
        this.violacaoDetectada = true;
    }

    public void limparViolacao() {
        this.violacaoDetectada = false;
    }

    public boolean isViolacaoDetectada() {
        return violacaoDetectada;
    }

    public String getSetorVigiado() {
        return setorVigiado;
    }

    public ZonaRestrita getZonaVigiada() {
        return zonaVigiada;
    }

    private static String descricaoDa(ZonaRestrita zona) {
        if (zona == null) {
            throw new IllegalArgumentException("A zona vigiada é obrigatória.");
        }
        return zona.getDescricao();
    }
}
