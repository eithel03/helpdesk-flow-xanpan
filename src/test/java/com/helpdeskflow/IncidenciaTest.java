package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

@Test
void debeRechazarDescripcionConMenosDeDiezCaracteres() {
    assertThrows(IllegalArgumentException.class, () ->
            new Incidencia(
                    "Problema de red",
                    "Muy corta",
                    "Redes",
                    Impacto.MEDIO,
                    Urgencia.MEDIA
            )
    );
}

@Test
void debeRechazarImpactoNulo() {
    assertThrows(IllegalArgumentException.class, () ->
            new Incidencia(
                    "Servidor sin conexión",
                    "El servidor principal perdió la conexión.",
                    "Servidores",
                    null,
                    Urgencia.ALTA
            )
    );
}

@Test
void debeRechazarUrgenciaNula() {
    assertThrows(IllegalArgumentException.class, () ->
            new Incidencia(
                    "Problema con impresora",
                    "La impresora no reconoce el papel cargado.",
                    "Impresoras",
                    Impacto.BAJO,
                    null
            )
    );
}

@Test
void debeGenerarIdentificadoresUnicos() {
    Incidencia primera = crearIncidenciaValida();
    Incidencia segunda = crearIncidenciaValida();

    assertNotEquals(primera.getId(), segunda.getId());
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

@Test
void debeIniciarConEstadoRegistrada() {
    Incidencia incidencia = crearIncidenciaValida();

    assertEquals(EstadoIncidencia.REGISTRADA, incidencia.getEstado());
}

@Test
void debeAsignarFechaDeCreacionAutomaticamente() {
    Incidencia incidencia = crearIncidenciaValida();

    assertNotNull(incidencia.getFechaCreacion());
}

@Test
void debeRechazarCategoriaVacia() {
    assertThrows(IllegalArgumentException.class, () ->
            new Incidencia(
                    "Problema de conexión",
                    "El equipo no logra conectarse a Internet.",
                    "   ",
                    Impacto.MEDIO,
                    Urgencia.MEDIA
            )
    );
}

@Test
void debeAsignarPrioridadAutomaticamente() {
    Incidencia incidencia = new Incidencia(
            "Servidor principal caído",
            "El servidor principal no responde a las solicitudes.",
            "Servidores",
            Impacto.ALTO,
            Urgencia.ALTA
    );

    assertEquals(Prioridad.CRITICA, incidencia.getPrioridad());
}


}