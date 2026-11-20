import modelo.AlertaSeguranca;
import modelo.CentralMonitoramento;
import modelo.DronePatrulha;
import modelo.SensorPerimetral;
import modelo.SensorRadiacao;
import modelo.ZonaRestrita;
import servico.ResultadoVisao;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Main {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public static void main(String[] args) {
        // garante acentos e "µ" corretos mesmo quando o terminal nao usa UTF-8 por padrao
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // "zona fantasma" nao compila
        // ZonaRestrita zonaFantasma = new ZonaRestrita(); // erro de compilacao

        executarCenarioRotinaNormal();
        executarCenarioPicoRadiacao();
        executarCenarioAcessoIndevido();
    }

    // CENARIO 1
    private static void executarCenarioRotinaNormal() {
        System.out.println("Cenário 1 — Rotina normal");
        Instalacao instalacao = new Instalacao();

        instalacao.sensorCasaDoReator.registrarLeitura(0.8);
        instalacao.sensorPatioCombustivel.registrarLeitura(0.5);

        imprimirZona(instalacao.casaDoReator);
        imprimirZona(instalacao.patioCombustivel);

        List<AlertaSeguranca> alertas = instalacao.central.analisarLeituras();

        System.out.println(instalacao.sensorPerimetral.isViolacaoDetectada()
                ? "Perímetro violado."
                : "Perímetro íntegro.");
        System.out.println(instalacao.drone.analisarImagem() == ResultadoVisao.NENHUMA_DETECCAO
                ? "Drone em patrulha. Nenhuma detecção."
                : "Drone em patrulha. Detecção registrada.");
        imprimirAlertas(alertas);
        System.out.println();
    }

    // CENARIO 2
    private static void executarCenarioPicoRadiacao() {
        System.out.println("Cenário 2 — Pico de radiação");
        Instalacao instalacao = new Instalacao();

        instalacao.sensorCasaDoReator.registrarLeitura(4.7);
        imprimirZona(instalacao.casaDoReator);

        imprimirAlertas(instalacao.central.analisarLeituras());
        System.out.println();
    }

    // CENARIO 3
    private static void executarCenarioAcessoIndevido() {
        System.out.println("Cenário 3 — Acesso indevido");
        Instalacao instalacao = new Instalacao();
        SensorPerimetral sensor = instalacao.sensorPerimetral;
        DronePatrulha drone = instalacao.drone;

        sensor.detectarViolacao();
        System.out.println("Sensor perimetral " + sensor.getCodigoIdentificador()
                + " detectou violação no " + sensor.getSetorVigiado() + ".");
        imprimirAlertas(instalacao.central.analisarLeituras());

        // cena que a camera simluada vai enxergar quando o drone chegar
        drone.simularCena(ResultadoVisao.VEICULO_NAO_IDENTIFICADO);

        Optional<AlertaSeguranca> confirmacao = instalacao.central.confirmarViolacaoComDrone(sensor, drone);
        System.out.println("Drone " + drone.getCodigoIdentificador() + " enviado para patrulha.");
        System.out.println("Câmera de visão computacional simulada analisou a imagem.");
        System.out.println("Resultado: " + drone.analisarImagem());
        System.out.println();

        if (confirmacao.isPresent()) {
            imprimirAlerta(confirmacao.get());
        } else {
            System.out.println("Violação não confirmada pelo drone.");
        }
        System.out.println();
    }

    // APRESENTACAO
    private static void imprimirZona(ZonaRestrita zona) {
        System.out.println("Zona " + zona.getIdentificador() + " - " + zona.getDescricao());
        System.out.println(String.format(PT_BR, "Leitura registrada: %.1f µSv/h", zona.getNivelRadiacaoAtual()));
        System.out.println("Classificação: " + zona.classificarRisco());
        System.out.println();
    }

    private static void imprimirAlertas(List<AlertaSeguranca> alertas) {
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta gerado.");
            return;
        }
        for (AlertaSeguranca alerta : alertas) {
            imprimirAlerta(alerta);
        }
    }

    private static void imprimirAlerta(AlertaSeguranca alerta) {
        System.out.println(ZonaRestrita.RISCO_CRITICO.equals(alerta.getSeveridade())
                ? "ALERTA CRÍTICO"
                : "ALERTA DE ATENÇÃO");
        System.out.println("Origem: " + alerta.getSensorOrigem().getCodigoIdentificador());
        System.out.println("Localização: " + alerta.getSensorOrigem().getLocalizacao());
        System.out.println("Mensagem: " + alerta.getMensagem());
    }

    // MONTAGEM DO CENARIO FICTICIO
    private static class Instalacao {
        final ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
        final ZonaRestrita patioCombustivel =
                new ZonaRestrita("ZNR-02", "Pátio de Armazenamento de Combustível Irradiado", 2.0);
        final ZonaRestrita perimetroNorte = new ZonaRestrita("ZNR-03", "Perímetro Norte", 0.5);

        final SensorRadiacao sensorCasaDoReator = new SensorRadiacao("RAD-01", casaDoReator);
        final SensorRadiacao sensorPatioCombustivel = new SensorRadiacao("RAD-02", patioCombustivel);
        final SensorPerimetral sensorPerimetral = new SensorPerimetral("PER-01", perimetroNorte, "Setor Norte");
        final DronePatrulha drone = new DronePatrulha("DRN-01", "Rota Perimetral", 90);

        final CentralMonitoramento central = new CentralMonitoramento();

        Instalacao() {
            central.adicionarDispositivo(sensorCasaDoReator);
            central.adicionarDispositivo(sensorPatioCombustivel);
            central.adicionarDispositivo(sensorPerimetral);
            central.adicionarDispositivo(drone);
        }
    }
}
