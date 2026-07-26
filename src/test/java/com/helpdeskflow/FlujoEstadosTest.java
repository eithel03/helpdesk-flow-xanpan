package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FlujoEstadosTest {

    @Test
    void debePermitirTransicionDeRegistradaALista() {
        Incidencia incidencia = crearIncidenciaValida();

        incidencia.avanzarA(EstadoIncidencia.LISTA);

        assertEquals(EstadoIncidencia.LISTA, incidencia.getEstado());
    }

    @Test
    void debePermitirTransicionDeListaAEnDesarrollo() {
        Incidencia incidencia = crearIncidenciaValida();
        incidencia.avanzarA(EstadoIncidencia.LISTA);

        incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);

        assertEquals(EstadoIncidencia.EN_DESARROLLO, incidencia.getEstado());
    }

    @Test
    void debePermitirTransicionDeEnDesarrolloAEnValidacion() {
        Incidencia incidencia = crearIncidenciaValida();
        incidencia.avanzarA(EstadoIncidencia.LISTA);
        incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);

        incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);

        assertEquals(EstadoIncidencia.EN_VALIDACION, incidencia.getEstado());
    }

    @Test
    void debePermitirTransicionDeEnValidacionAFinalizadaConSolucion() {
        Incidencia incidencia = crearIncidenciaValidaEnValidacion();

        incidencia.finalizar("Se reemplazo el disco duro danado.");

        assertEquals(EstadoIncidencia.FINALIZADA, incidencia.getEstado());
    }

    @Test
    void debeRechazarTransicionDeRegistradaAFinalizada() {
        Incidencia incidencia = crearIncidenciaValida();

        assertThrows(IllegalStateException.class,
                () -> incidencia.avanzarA(EstadoIncidencia.FINALIZADA));
    }

    @Test
    void debeRechazarTransicionDeRegistradaAEnDesarrollo() {
        Incidencia incidencia = crearIncidenciaValida();

        assertThrows(IllegalStateException.class,
                () -> incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO));
    }

    @Test
    void debeRechazarTransicionDeEnDesarrolloALista() {
        Incidencia incidencia = crearIncidenciaValida();
        incidencia.avanzarA(EstadoIncidencia.LISTA);
        incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);

        assertThrows(IllegalStateException.class,
                () -> incidencia.avanzarA(EstadoIncidencia.LISTA));
    }

    @Test
    void debeRechazarTransicionDeFinalizadaAEnDesarrollo() {
        Incidencia incidencia = crearIncidenciaFinalizada();

        assertThrows(IllegalStateException.class,
                () -> incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO));
    }

    @Test
    void debeRechazarFinalizarConSolucionNula() {
        Incidencia incidencia = crearIncidenciaValidaEnValidacion();

        assertThrows(IllegalArgumentException.class,
                () -> incidencia.finalizar(null));
    }

    @Test
    void debeRechazarFinalizarConSolucionVacia() {
        Incidencia incidencia = crearIncidenciaValidaEnValidacion();

        assertThrows(IllegalArgumentException.class,
                () -> incidencia.finalizar("   "));
    }

    @Test
    void debeRegistrarFechaDeCierreAlFinalizar() {
        Incidencia incidencia = crearIncidenciaValidaEnValidacion();

        incidencia.finalizar("Se actualizo el controlador de red.");

        assertNotNull(incidencia.getFechaCierre());
    }

    @Test
    void debeConservarDescripcionDeSolucionAlFinalizar() {
        Incidencia incidencia = crearIncidenciaValidaEnValidacion();

        incidencia.finalizar("Se reinstalo el sistema operativo.");

        assertEquals("Se reinstalo el sistema operativo.",
                incidencia.getDescripcionSolucion());
    }

    private Incidencia crearIncidenciaValida() {
        return new Incidencia(
                "Computadora no enciende",
                "La computadora del laboratorio no enciende.",
                "Hardware",
                Impacto.ALTO,
                Urgencia.ALTA
        );
    }

    private Incidencia crearIncidenciaValidaEnValidacion() {
        Incidencia incidencia = crearIncidenciaValida();
        incidencia.avanzarA(EstadoIncidencia.LISTA);
        incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);
        incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);
        return incidencia;
    }

    private Incidencia crearIncidenciaFinalizada() {
        Incidencia incidencia = crearIncidenciaValidaEnValidacion();
        incidencia.finalizar("Se reemplazo el disco duro danado.");
        return incidencia;
    }
}
