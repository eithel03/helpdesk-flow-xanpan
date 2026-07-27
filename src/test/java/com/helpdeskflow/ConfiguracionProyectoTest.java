package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConfiguracionProyectoTest {

    @Test
    void debeExistirLaClasePrincipalDelProyecto() {
        assertNotNull(HelpDeskFlowApplication.class);
    }

    @Test
    void debeExistirLaClaseMenuConsola() {
        assertNotNull(MenuConsola.class);
    }
}