package modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertaSegurancaTest {

    @Test
    void deveManterReferenciaDoSensorOrigemNoAlerta() {
        ZonaRestrita zona = new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
        SensorRadiacao sensor = new SensorRadiacao("RAD-01", zona);

        AlertaSeguranca alerta = new AlertaSeguranca(sensor, zona, "CRITICO", "Pico de radiação detectado.");

        assertSame(sensor, alerta.getSensorOrigem());
        assertSame(zona, alerta.getZonaAfetada());
        assertEquals("CRITICO", alerta.getSeveridade());
        assertEquals("Pico de radiação detectado.", alerta.getMensagem());
    }

    @Test
    void deveRejeitarAlertaSemOrigemZonaSeveridadeOuMensagem() {
        ZonaRestrita zona = new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
        SensorRadiacao sensor = new SensorRadiacao("RAD-01", zona);

        assertThrows(IllegalArgumentException.class, () -> new AlertaSeguranca(null, zona, "CRITICO", "x"));
        assertThrows(IllegalArgumentException.class, () -> new AlertaSeguranca(sensor, null, "CRITICO", "x"));
        assertThrows(IllegalArgumentException.class, () -> new AlertaSeguranca(sensor, zona, " ", "x"));
        assertThrows(IllegalArgumentException.class, () -> new AlertaSeguranca(sensor, zona, "CRITICO", ""));
    }

    @Test
    void alertaDevePermitirConsultarLocalizacaoEZonaAoEscalonarIncidente() {
        ZonaRestrita zona = new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
        SensorRadiacao sensor = new SensorRadiacao("RAD-01", zona);
        sensor.registrarLeitura(4.7);

        AlertaSeguranca alerta = new AlertaSeguranca(sensor, zona, "CRITICO", "Pico.");

        assertEquals("Casa do Reator", alerta.getSensorOrigem().getLocalizacao());
        assertEquals("ZNR-01", alerta.getZonaAfetada().getIdentificador());
        assertEquals(4.7, alerta.getZonaAfetada().getNivelRadiacaoAtual());
    }
}
