package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class IncidenciaTest {

    @Test
    void debeCrearUnaIncidenciaValida() {
        Incidencia incidencia = new Incidencia(
                "Computadora no enciende",
                "La computadora del laboratorio no enciende.",
                "Hardware",
                Impacto.ALTO,
                Urgencia.ALTA
        );

        assertNotNull(incidencia.getId());
        assertEquals("Computadora no enciende", incidencia.getTitulo());
        assertEquals("La computadora del laboratorio no enciende.",
                incidencia.getDescripcion());
        assertEquals("Hardware", incidencia.getCategoria());
        assertEquals(Impacto.ALTO, incidencia.getImpacto());
        assertEquals(Urgencia.ALTA, incidencia.getUrgencia());
    }
    @Test
void debeRechazarTituloVacio() {
    assertThrows(IllegalArgumentException.class, () ->
            new Incidencia(
                    "   ",
                    "La computadora no logra encender correctamente.",
                    "Hardware",
                    Impacto.ALTO,
                    Urgencia.ALTA
            )
    );
}
}