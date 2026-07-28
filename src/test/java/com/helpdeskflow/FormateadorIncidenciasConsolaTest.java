package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

class FormateadorIncidenciasConsolaTest {

    private final FormateadorIncidenciasConsola formateador =
            new FormateadorIncidenciasConsola();

    @Test
    void debeMantenerFormatoActualDeIncidenciaIndividual() {
        Incidencia incidencia = crearIncidenciaDeterminista();
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

        formateador.mostrarIncidencia(
                incidencia,
                new PrintStream(salidaCapturada));

        String salto = System.lineSeparator();
        String esperado = String.join(
                salto,
                "----------------------------------------",
                "ID: " + incidencia.getId(),
                "T\u00edtulo: Problema de monitor",
                "Categor\u00eda: Hardware",
                "Impacto: BAJO",
                "Urgencia: BAJA",
                "Prioridad: NORMAL",
                "Estado: REGISTRADA",
                "EXPEDITE: No",
                "Fecha de creaci\u00f3n: 2026-01-01T08:00")
                + salto;

        assertEquals(esperado, salidaCapturada.toString());
    }

    @Test
    void debeMantenerMensajeActualCuandoNoHayIncidencias() {
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

        formateador.mostrarIncidencias(
                List.of(),
                new PrintStream(salidaCapturada));

        assertEquals(
                "No existen incidencias para mostrar."
                        + System.lineSeparator(),
                salidaCapturada.toString());
    }

    private Incidencia crearIncidenciaDeterminista() {
        Clock reloj = Clock.fixed(
                Instant.parse("2026-01-01T08:00:00Z"),
                ZoneId.of("UTC"));

        return new Incidencia(
                "Problema de monitor",
                "El monitor principal no muestra imagen.",
                "Hardware",
                Impacto.BAJO,
                Urgencia.BAJA,
                reloj);
    }
}
