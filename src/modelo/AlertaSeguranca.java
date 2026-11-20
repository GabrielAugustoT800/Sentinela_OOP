package modelo;

public class AlertaSeguranca {

    private final DispositivoMonitoramento sensorOrigem;
    private final ZonaRestrita zonaAfetada;
    private final String severidade;
    private final String mensagem;

    public AlertaSeguranca(
            DispositivoMonitoramento sensorOrigem,
            ZonaRestrita zonaAfetada,
            String severidade,
            String mensagem) {
        if (sensorOrigem == null) {
            throw new IllegalArgumentException("O dispositivo de origem é obrigatório.");
        }
        if (zonaAfetada == null) {
            throw new IllegalArgumentException("A zona afetada é obrigatória.");
        }
        if (severidade == null || severidade.isBlank()) {
            throw new IllegalArgumentException("A severidade é obrigatória.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem é obrigatória.");
        }
        this.sensorOrigem = sensorOrigem;
        this.zonaAfetada = zonaAfetada;
        this.severidade = severidade;
        this.mensagem = mensagem;
    }

    public DispositivoMonitoramento getSensorOrigem() {
        return sensorOrigem;
    }

    public ZonaRestrita getZonaAfetada() {
        return zonaAfetada;
    }

    public String getSeveridade() {
        return severidade;
    }

    public String getMensagem() {
        return mensagem;
    }
}
