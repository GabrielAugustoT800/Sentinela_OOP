package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servico.ResultadoVisao;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CentralMonitoramentoTest {

    private ZonaRestrita casaDoReator;
    private ZonaRestrita perimetroNorte;
    private SensorRadiacao sensorRadiacao;
    private SensorPerimetral sensorPerimetral;
    private DronePatrulha drone;
    private CentralMonitoramento central;

    @BeforeEach
    void montarInstalacao() {
        casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
        perimetroNorte = new ZonaRestrita("ZNR-03", "Perímetro Norte", 0.5);
        sensorRadiacao = new SensorRadiacao("RAD-01", casaDoReator);
        sensorPerimetral = new SensorPerimetral("PER-01", perimetroNorte, "Setor Norte");
        drone = new DronePatrulha("DRN-01", "Rota Perimetral", 90);

        central = new CentralMonitoramento();
        central.adicionarDispositivo(sensorRadiacao);
        central.adicionarDispositivo(sensorPerimetral);
        central.adicionarDispositivo(drone);
    }

    @Test
    void naoDeveGerarAlertaEmRotinaNormal() {
        sensorRadiacao.registrarLeitura(0.8);

        assertTrue(central.analisarLeituras().isEmpty());
    }

    @Test
    void deveGerarAlertaCriticoQuandoHouverPicoDeRadiacao() {
        sensorRadiacao.registrarLeitura(4.7);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        AlertaSeguranca alerta = alertas.get(0);
        assertEquals("CRITICO", alerta.getSeveridade());
        assertSame(sensorRadiacao, alerta.getSensorOrigem());
        assertSame(casaDoReator, alerta.getZonaAfetada());
        assertEquals("Pico de radiação detectado na Casa do Reator.", alerta.getMensagem());
    }

    @Test
    void deveGerarAlertaDeAtencaoQuandoRadiacaoPassarDoLimiteSemSerCritica() {
        sensorRadiacao.registrarLeitura(1.5);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        assertEquals("ATENCAO", alertas.get(0).getSeveridade());
    }

    @Test
    void deveGerarAlertaDeViolacaoPerimetral() {
        sensorPerimetral.detectarViolacao();

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        assertSame(sensorPerimetral, alertas.get(0).getSensorOrigem());
        assertSame(perimetroNorte, alertas.get(0).getZonaAfetada());
    }

    @Test
    void deveGerarUmAlertaParaCadaSituacaoAtipica() {
        sensorRadiacao.registrarLeitura(4.7);
        sensorPerimetral.detectarViolacao();

        assertEquals(2, central.analisarLeituras().size());
    }

    @Test
    void deveIgnorarDispositivoForaDeOperacao() {
        sensorRadiacao.registrarLeitura(4.7);
        sensorRadiacao.colocarForaDeOperacao();

        assertTrue(central.analisarLeituras().isEmpty());
    }

    @Test
    void deveConfirmarViolacaoQuandoCameraDoDroneVirVeiculoNaoIdentificado() {
        sensorPerimetral.detectarViolacao();
        drone.simularCena(ResultadoVisao.VEICULO_NAO_IDENTIFICADO);

        Optional<AlertaSeguranca> alerta = central.confirmarViolacaoComDrone(sensorPerimetral, drone);

        assertTrue(alerta.isPresent());
        assertEquals("CRITICO", alerta.get().getSeveridade());
        assertSame(sensorPerimetral, alerta.get().getSensorOrigem());
        assertEquals("Setor Norte", drone.getDestinoAtual());
    }

    @Test
    void naoDeveConfirmarViolacaoQuandoCameraNaoDetectarNada() {
        sensorPerimetral.detectarViolacao();

        assertTrue(central.confirmarViolacaoComDrone(sensorPerimetral, drone).isEmpty());
    }

    @Test
    void naoDeveEnviarDroneQuandoNaoHaViolacao() {
        drone.simularCena(ResultadoVisao.VEICULO_NAO_IDENTIFICADO);

        assertTrue(central.confirmarViolacaoComDrone(sensorPerimetral, drone).isEmpty());
        assertNull(drone.getDestinoAtual());
    }

    @Test
    void deveRejeitarDispositivoOuArgumentosNulos() {
        assertThrows(IllegalArgumentException.class, () -> central.adicionarDispositivo(null));
        assertThrows(IllegalArgumentException.class, () -> central.confirmarViolacaoComDrone(null, drone));
        assertThrows(IllegalArgumentException.class,
                () -> central.confirmarViolacaoComDrone(sensorPerimetral, null));
    }

    @Test
    void listaDeDispositivosExpostaNaoPodeSerAlteradaDeFora() {
        List<DispositivoMonitoramento> dispositivos = central.getDispositivos();

        assertEquals(3, dispositivos.size());
        assertThrows(UnsupportedOperationException.class, () -> dispositivos.add(drone));
    }
}
