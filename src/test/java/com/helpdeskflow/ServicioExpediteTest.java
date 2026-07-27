package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ServicioExpediteTest {

    @Test
    void incidenciaCriticaDebePoderMarcarseComoExpedite() {
        Incidencia incidencia = crearIncidenciaCritica();

        incidencia.marcarComoExpedite();

        assertTrue(incidencia.esExpedite());
    }

    @Test
    void incidenciaNormalNoDebePoderMarcarseComoExpedite() {
        Incidencia incidencia = crearIncidenciaNormal();

        assertThrows(
                IllegalStateException.class,
                incidencia::marcarComoExpedite
        );

        assertFalse(incidencia.esExpedite());
    }

    @Test
    void incidenciaAltaNoDebePoderMarcarseComoExpedite() {
        Incidencia incidencia = crearIncidenciaAlta();

        assertThrows(
                IllegalStateException.class,
                incidencia::marcarComoExpedite
        );

        assertFalse(incidencia.esExpedite());
    }

    private Incidencia crearIncidenciaCritica() {
        return new Incidencia(
                "Servidor principal caido",
                "El servidor principal no responde solicitudes.",
                "Servidores",
                Impacto.ALTO,
                Urgencia.ALTA
        );
    }

    private Incidencia crearIncidenciaAlta() {
        return new Incidencia(
                "Problema de conectividad",
                "El equipo no logra conectarse correctamente.",
                "Redes",
                Impacto.ALTO,
                Urgencia.MEDIA
        );
    }

    private Incidencia crearIncidenciaNormal() {
        return new Incidencia(
                "Problema con monitor",
                "El monitor secundario presenta una falla.",
                "Hardware",
                Impacto.BAJO,
                Urgencia.BAJA
        );
    }
}