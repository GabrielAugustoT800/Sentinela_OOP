package modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZonaRestritaTest {

    private ZonaRestrita criarCasaDoReator() {
        return new ZonaRestrita("ZNR-01", "Casa do Reator", 1.0);
    }

    // CLASSES E OBJETOS / CONSTRUTORES

    @Test
    void deveCriarZonaRestritaValida() {
        ZonaRestrita zona = criarCasaDoReator();

        assertNotNull(zona);
        assertEquals("ZNR-01", zona.getIdentificador());
        assertEquals("Casa do Reator", zona.getDescricao());
        assertEquals(1.0, zona.getLimiteRadiacao());
        assertEquals(0.0, zona.getNivelRadiacaoAtual());
    }

    @Test
    void deveCriarDuasZonasDiferentesComoObjetosIndependentes() {
        ZonaRestrita casaDoReator = criarCasaDoReator();
        ZonaRestrita patio = new ZonaRestrita("ZNR-02", "Pátio de Armazenamento de Combustível Irradiado", 2.0);

        casaDoReator.registrarLeituraRadiacao(4.7);

        assertNotSame(casaDoReator, patio);
        assertEquals(4.7, casaDoReator.getNivelRadiacaoAtual());
        assertEquals(0.0, patio.getNivelRadiacaoAtual());
    }

    @Test
    void deveRejeitarZonaComIdentificadorVazio() {
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("", "Casa do Reator", 1.0));
    }

    @Test
    void deveRejeitarZonaComIdentificadorNuloOuEmBranco() {
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita(null, "Casa do Reator", 1.0));
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("   ", "Casa do Reator", 1.0));
    }

    @Test
    void deveRejeitarZonaSemDescricao() {
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", "", 1.0));
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", null, 1.0));
    }

    @Test
    void deveRejeitarLimiteDeRadiacaoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", "Casa do Reator", 0));
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", "Casa do Reator", -1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new ZonaRestrita("ZNR-01", "Casa do Reator", Double.NaN));
    }

    // METODOS E COMPORTAMENTOS

    @Test
    void deveAtualizarRadiacaoEClassificarComoCritico() {
        ZonaRestrita zona = criarCasaDoReator();

        zona.registrarLeituraRadiacao(0.2);
        assertEquals("NORMAL", zona.classificarRisco());

        zona.registrarLeituraRadiacao(4.7);
        assertEquals(4.7, zona.getNivelRadiacaoAtual());
        assertEquals("CRITICO", zona.classificarRisco());
    }

    @Test
    void deveClassificarRiscoConformeFaixas() {
        ZonaRestrita zona = criarCasaDoReator(); // limite 1.0

        zona.registrarLeituraRadiacao(0.8);
        assertEquals("NORMAL", zona.classificarRisco());

        zona.registrarLeituraRadiacao(1.5);
        assertEquals("ATENCAO", zona.classificarRisco());

        zona.registrarLeituraRadiacao(2.5);
        assertEquals("CRITICO", zona.classificarRisco());
    }

    @Test
    void deveRespeitarFronteirasDasFaixas() {
        ZonaRestrita zona = criarCasaDoReator(); // limite 1.0

        zona.registrarLeituraRadiacao(1.0); // "até o limite" ainda é NORMAL
        assertEquals("NORMAL", zona.classificarRisco());

        zona.registrarLeituraRadiacao(2.0); // "até duas vezes o limite" ainda é ATENCAO
        assertEquals("ATENCAO", zona.classificarRisco());
    }

    // ENCAPSULAMENTO

    @Test
    void deveRejeitarLeituraNegativaEManterUltimoValorValido() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(0.8);

        assertThrows(IllegalArgumentException.class, () -> zona.registrarLeituraRadiacao(-5));

        assertEquals(0.8, zona.getNivelRadiacaoAtual());
    }

    @Test
    void deveRejeitarLeituraAbsurdaComoFalhaDeSensorEManterUltimoValorValido() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(0.8);

        assertThrows(IllegalArgumentException.class, () -> zona.registrarLeituraRadiacao(1_000_000.01));
        assertThrows(IllegalArgumentException.class,
                () -> zona.registrarLeituraRadiacao(Double.POSITIVE_INFINITY));

        assertEquals(0.8, zona.getNivelRadiacaoAtual());
    }

    @Test
    void deveRejeitarLeituraNaNEManterUltimoValorValido() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(0.8);

        assertThrows(IllegalArgumentException.class, () -> zona.registrarLeituraRadiacao(Double.NaN));

        assertEquals(0.8, zona.getNivelRadiacaoAtual());
    }

    @Test
    void deveAceitarLeituraNosExtremosValidos() {
        ZonaRestrita zona = criarCasaDoReator();

        zona.registrarLeituraRadiacao(0.0);
        assertEquals(0.0, zona.getNivelRadiacaoAtual());

        zona.registrarLeituraRadiacao(1_000_000.0);
        assertEquals(1_000_000.0, zona.getNivelRadiacaoAtual());
    }
}
