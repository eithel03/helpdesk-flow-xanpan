package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class ControladorConsolaTest {

    @Test
    void metodoPublicoDebeMantenerFormatoActualDeIncidencia() {
        ControladorConsola controlador =
                new ControladorConsola(new RepositorioIncidenciasMemoria());
        Incidencia incidencia = crearIncidenciaDeterminista();
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

        controlador.mostrarIncidencia(
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
