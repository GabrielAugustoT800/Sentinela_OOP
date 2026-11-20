package modelo;

import org.junit.jupiter.api.Test;
import servico.ResultadoVisao;

import static org.junit.jupiter.api.Assertions.*;

class DispositivoMonitoramentoTest {

    private final ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
    private final ZonaRestrita perimetroNorte = new ZonaRestrita("ZNR-03", "Perímetro Norte", 0.5);

    @Test
    void deveHerdarCodigoIdentificadorDaClasseMae() {
        SensorRadiacao sensorRadiacao = new SensorRadiacao("RAD-01", casaDoReator);
        SensorPerimetral sensorPerimetral = new SensorPerimetral("PER-01", perimetroNorte, "Setor Norte");
        DronePatrulha drone = new DronePatrulha("DRN-01", "Rota Perimetral", 90);

        assertEquals("RAD-01", sensorRadiacao.getCodigoIdentificador());
        assertEquals("PER-01", sensorPerimetral.getCodigoIdentificador());
        assertEquals("DRN-01", drone.getCodigoIdentificador());
    }

    @Test
    void deveTratarAsTresFilhasComoDispositivoMonitoramento() {
        DispositivoMonitoramento[] dispositivos = {
                new SensorRadiacao("RAD-01", casaDoReator),
                new SensorPerimetral("PER-01", perimetroNorte, "Setor Norte"),
                new DronePatrulha("DRN-01", "Rota Perimetral", 90)
        };

        for (DispositivoMonitoramento dispositivo : dispositivos) {
            assertTrue(dispositivo.isEmOperacao());
            assertNotNull(dispositivo.getLocalizacao());
        }
    }

    @Test
    void deveValidarCodigoNaClasseMaeAtravesDoSuper() {
        assertThrows(IllegalArgumentException.class, () -> new SensorRadiacao("", casaDoReator));
        assertThrows(IllegalArgumentException.class, () -> new SensorPerimetral(null, perimetroNorte, "Setor Norte"));
        assertThrows(IllegalArgumentException.class, () -> new DronePatrulha("  ", "Rota Perimetral", 90));
    }

    @Test
    void deveRejeitarFilhasSemSeusDadosObrigatorios() {
        assertThrows(IllegalArgumentException.class, () -> new SensorRadiacao("RAD-01", null));
        assertThrows(IllegalArgumentException.class, () -> new SensorPerimetral("PER-01", null, "Setor Norte"));
        assertThrows(IllegalArgumentException.class, () -> new SensorPerimetral("PER-01", perimetroNorte, ""));
        assertThrows(IllegalArgumentException.class, () -> new DronePatrulha("DRN-01", "", 90));
        assertThrows(IllegalArgumentException.class, () -> new DronePatrulha("DRN-01", "Rota Perimetral", 101));
        assertThrows(IllegalArgumentException.class, () -> new DronePatrulha("DRN-01", "Rota Perimetral", -1));
    }

    @Test
    void deveAlternarEstadoDeOperacao() {
        SensorRadiacao sensor = new SensorRadiacao("RAD-01", casaDoReator);

        sensor.colocarForaDeOperacao();
        assertFalse(sensor.isEmOperacao());

        sensor.colocarEmOperacao();
        assertTrue(sensor.isEmOperacao());
    }

    @Test
    void sensorDeRadiacaoDeveManterUltimaLeituraValidaQuandoLeituraForRejeitada() {
        SensorRadiacao sensor = new SensorRadiacao("RAD-01", casaDoReator);
        sensor.registrarLeitura(0.8);

        assertThrows(IllegalArgumentException.class, () -> sensor.registrarLeitura(-5));

        assertEquals(0.8, sensor.getLeituraAtual());
        assertEquals(0.8, casaDoReator.getNivelRadiacaoAtual());
        assertEquals(1.0, sensor.getLimiteSeguranca());
    }

    @Test
    void sensorPerimetralDeveDetectarELimparViolacao() {
        SensorPerimetral sensor = new SensorPerimetral("PER-01", perimetroNorte, "Setor Norte");
        assertFalse(sensor.isViolacaoDetectada());

        sensor.detectarViolacao();
        assertTrue(sensor.isViolacaoDetectada());

        sensor.limparViolacao();
        assertFalse(sensor.isViolacaoDetectada());
    }

    @Test
    void droneDeveSerEnviadoParaSetorQuandoApto() {
        DronePatrulha drone = new DronePatrulha("DRN-01", "Rota Perimetral", 90);
        assertNull(drone.getDestinoAtual());

        drone.enviarParaSetor("Setor Norte");

        assertEquals("Setor Norte", drone.getDestinoAtual());
    }

    @Test
    void droneNaoDeveSerEnviadoComBateriaBaixaOuForaDeOperacao() {
        DronePatrulha bateriaBaixa = new DronePatrulha("DRN-01", "Rota Perimetral", 10);
        assertThrows(IllegalStateException.class, () -> bateriaBaixa.enviarParaSetor("Setor Norte"));

        DronePatrulha foraDeOperacao = new DronePatrulha("DRN-02", "Rota Perimetral", 90);
        foraDeOperacao.colocarForaDeOperacao();
        assertThrows(IllegalStateException.class, () -> foraDeOperacao.enviarParaSetor("Setor Norte"));
    }

    @Test
    void droneSemDeteccaoPorPadraoEComVeiculoQuandoCenaSimulada() {
        DronePatrulha drone = new DronePatrulha("DRN-01", "Rota Perimetral", 90);
        assertEquals(ResultadoVisao.NENHUMA_DETECCAO, drone.analisarImagem());

        drone.simularCena(ResultadoVisao.VEICULO_NAO_IDENTIFICADO);
        assertEquals(ResultadoVisao.VEICULO_NAO_IDENTIFICADO, drone.analisarImagem());
    }
}
