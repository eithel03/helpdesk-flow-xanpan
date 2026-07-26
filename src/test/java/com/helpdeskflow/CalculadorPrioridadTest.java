package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculadorPrioridadTest {

    @Test
    void altoYAltaDebeSerCritica() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.ALTO, Urgencia.ALTA);

        assertEquals(Prioridad.CRITICA, resultado);
    }

    @Test
    void altoYMediaDebeSerAlta() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.ALTO, Urgencia.MEDIA);

        assertEquals(Prioridad.ALTA, resultado);
    }

    @Test
    void altoYBajaDebeSerAlta() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.ALTO, Urgencia.BAJA);

        assertEquals(Prioridad.ALTA, resultado);
    }

    @Test
    void medioYAltaDebeSerAlta() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.MEDIO, Urgencia.ALTA);

        assertEquals(Prioridad.ALTA, resultado);
    }

    @Test
    void bajoYAltaDebeSerAlta() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.BAJO, Urgencia.ALTA);

        assertEquals(Prioridad.ALTA, resultado);
    }

    @Test
    void medioYMediaDebeSerNormal() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.MEDIO, Urgencia.MEDIA);

        assertEquals(Prioridad.NORMAL, resultado);
    }

    @Test
    void bajoYBajaDebeSerNormal() {
        Prioridad resultado =
                CalculadorPrioridad.calcular(Impacto.BAJO, Urgencia.BAJA);

        assertEquals(Prioridad.NORMAL, resultado);
    }

    @Test
void debeRechazarImpactoNulo() {
    assertThrows(
            IllegalArgumentException.class,
            () -> CalculadorPrioridad.calcular(null, Urgencia.ALTA)
    );
}

@Test
void debeRechazarUrgenciaNula() {
    assertThrows(
            IllegalArgumentException.class,
            () -> CalculadorPrioridad.calcular(Impacto.ALTO, null)
    );
}


}