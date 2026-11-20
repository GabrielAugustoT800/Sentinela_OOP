package modelo;

import servico.ResultadoVisao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CentralMonitoramento {

    private final List<DispositivoMonitoramento> dispositivos = new ArrayList<>();

    public void adicionarDispositivo(DispositivoMonitoramento dispositivo) {
        if (dispositivo == null) {
            throw new IllegalArgumentException("O dispositivo não pode ser nulo.");
        }
        dispositivos.add(dispositivo);
    }

    public List<DispositivoMonitoramento> getDispositivos() {
        return Collections.unmodifiableList(dispositivos);
    }

    // percorre os dispositivos e gera um alerta para cada situacao atipica encontrada
    public List<AlertaSeguranca> analisarLeituras() {
        List<AlertaSeguranca> alertas = new ArrayList<>();

        for (DispositivoMonitoramento dispositivo : dispositivos) {
            if (!dispositivo.isEmOperacao()) {
                continue;
            }
            if (dispositivo instanceof SensorRadiacao sensor) {
                analisarSensorRadiacao(sensor).ifPresent(alertas::add);
            } else if (dispositivo instanceof SensorPerimetral sensor) {
                analisarSensorPerimetral(sensor).ifPresent(alertas::add);
            }
        }
        return alertas;
    }

    public Optional<AlertaSeguranca> confirmarViolacaoComDrone(SensorPerimetral sensor, DronePatrulha drone) {
        if (sensor == null || drone == null) {
            throw new IllegalArgumentException("Sensor perimetral e drone são obrigatórios.");
        }
        if (!sensor.isViolacaoDetectada()) {
            return Optional.empty();
        }

        drone.enviarParaSetor(sensor.getSetorVigiado());

        if (drone.analisarImagem() != ResultadoVisao.VEICULO_NAO_IDENTIFICADO) {
            return Optional.empty();
        }
        return Optional.of(new AlertaSeguranca(
                sensor,
                sensor.getZonaVigiada(),
                ZonaRestrita.RISCO_CRITICO,
                "Violação perimetral confirmada por drone. Veículo não identificado detectado."));
    }

    private Optional<AlertaSeguranca> analisarSensorRadiacao(SensorRadiacao sensor) {
        ZonaRestrita zona = sensor.getZonaMonitorada();
        String risco = zona.classificarRisco();

        if (ZonaRestrita.RISCO_CRITICO.equals(risco)) {
            return Optional.of(new AlertaSeguranca(sensor, zona, risco,
                    "Pico de radiação detectado na " + zona.getDescricao() + "."));
        }
        if (ZonaRestrita.RISCO_ATENCAO.equals(risco)) {
            return Optional.of(new AlertaSeguranca(sensor, zona, risco,
                    "Radiação acima do limite de segurança na " + zona.getDescricao() + "."));
        }
        return Optional.empty();
    }

    private Optional<AlertaSeguranca> analisarSensorPerimetral(SensorPerimetral sensor) {
        if (!sensor.isViolacaoDetectada()) {
            return Optional.empty();
        }
        // ATENCAO: violacao ainda nao confirmada visualmente
        // CRITICO: violacao confirmada pelo drone
        return Optional.of(new AlertaSeguranca(sensor, sensor.getZonaVigiada(), ZonaRestrita.RISCO_ATENCAO,
                "Violação perimetral detectada no " + sensor.getSetorVigiado() + ". Aguardando confirmação."));
    }
}
