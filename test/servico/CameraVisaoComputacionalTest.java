package servico;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraVisaoComputacionalTest {

    @Test
    void deveRetornarNenhumaDeteccaoPorPadrao() {
        assertEquals(ResultadoVisao.NENHUMA_DETECCAO, new CameraVisaoComputacional().analisarImagem());
    }

    @Test
    void deveRetornarVeiculoNaoIdentificadoQuandoCenaSimuladaForConfigurada() {
        CameraVisaoComputacional camera = new CameraVisaoComputacional();

        camera.simularCena(ResultadoVisao.VEICULO_NAO_IDENTIFICADO);

        assertEquals(ResultadoVisao.VEICULO_NAO_IDENTIFICADO, camera.analisarImagem());
        assertEquals("VEICULO_NAO_IDENTIFICADO", camera.analisarImagem().name());
    }

    @Test
    void deveRejeitarCenaNula() {
        assertThrows(IllegalArgumentException.class, () -> new CameraVisaoComputacional().simularCena(null));
    }
}
