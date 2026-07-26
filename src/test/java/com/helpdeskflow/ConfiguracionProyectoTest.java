package com.helpdeskflow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConfiguracionProyectoTest {

    @Test
    void mainPuedeEjecutarseSinLanzarExcepciones() {
        assertDoesNotThrow(() -> HelpDeskFlowApplication.main(new String[]{}));
    }
}
