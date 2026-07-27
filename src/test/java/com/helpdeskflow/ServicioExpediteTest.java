package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                incidencia::marcarComoExpedite);

        assertFalse(incidencia.esExpedite());
    }

    @Test
    void incidenciaAltaNoDebePoderMarcarseComoExpedite() {
        Incidencia incidencia = crearIncidenciaAlta();

        assertThrows(
                IllegalStateException.class,
                incidencia::marcarComoExpedite);

        assertFalse(incidencia.esExpedite());
    }

    @Test
    void debePermitirQueUnaExpediteEntreEnDesarrollo() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia incidencia = crearIncidenciaCritica();
        incidencia.marcarComoExpedite();
        incidencia.avanzarA(EstadoIncidencia.LISTA);

        repositorio.guardar(incidencia);

        servicio.avanzarA(
                incidencia,
                EstadoIncidencia.EN_DESARROLLO);

        assertEquals(
                EstadoIncidencia.EN_DESARROLLO,
                incidencia.getEstado());
    }

    @Test
    void debeRechazarSegundaExpediteActivaEnDesarrollo() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia primera = crearIncidenciaCritica();
        primera.marcarComoExpedite();
        primera.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(primera);

        servicio.avanzarA(
                primera,
                EstadoIncidencia.EN_DESARROLLO);

        Incidencia segunda = crearIncidenciaCritica();
        segunda.marcarComoExpedite();
        segunda.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(segunda);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.avanzarA(
                        segunda,
                        EstadoIncidencia.EN_DESARROLLO));

        assertEquals(
                EstadoIncidencia.LISTA,
                segunda.getEstado());
    }

    @Test
    void expediteActivaDebePoderAvanzarAValidacion() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia incidencia = crearIncidenciaCritica();
        incidencia.marcarComoExpedite();
        incidencia.avanzarA(EstadoIncidencia.LISTA);

        repositorio.guardar(incidencia);

        servicio.avanzarA(
                incidencia,
                EstadoIncidencia.EN_DESARROLLO);

        servicio.avanzarA(
                incidencia,
                EstadoIncidencia.EN_VALIDACION);

        assertEquals(
                EstadoIncidencia.EN_VALIDACION,
                incidencia.getEstado());
    }

    @Test
    void debeRechazarOtraExpediteCuandoExisteUnaEnValidacion() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia primera = crearIncidenciaCritica();
        primera.marcarComoExpedite();
        primera.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(primera);

        servicio.avanzarA(
                primera,
                EstadoIncidencia.EN_DESARROLLO);

        servicio.avanzarA(
                primera,
                EstadoIncidencia.EN_VALIDACION);

        Incidencia segunda = crearIncidenciaCritica();
        segunda.marcarComoExpedite();
        segunda.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(segunda);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.avanzarA(
                        segunda,
                        EstadoIncidencia.EN_DESARROLLO));
    }

    @Test
    void incidenciasNormalesDebenConservarSuFlujo() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia primeraExpedite = crearIncidenciaCritica();
        primeraExpedite.marcarComoExpedite();
        primeraExpedite.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(primeraExpedite);

        servicio.avanzarA(
                primeraExpedite,
                EstadoIncidencia.EN_DESARROLLO);

        Incidencia incidenciaNormal = crearIncidenciaNormal();
        incidenciaNormal.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(incidenciaNormal);

        servicio.avanzarA(
                incidenciaNormal,
                EstadoIncidencia.EN_DESARROLLO);

        assertEquals(
                EstadoIncidencia.EN_DESARROLLO,
                incidenciaNormal.getEstado());
    }

    @Test
    void otraExpediteDebePoderAvanzarCuandoLaPrimeraFinaliza() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia primera = crearIncidenciaCritica();
        primera.marcarComoExpedite();
        primera.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(primera);

        servicio.avanzarA(
                primera,
                EstadoIncidencia.EN_DESARROLLO);

        servicio.avanzarA(
                primera,
                EstadoIncidencia.EN_VALIDACION);

        primera.finalizar(
                "Se restauró el servicio del servidor principal.");

        Incidencia segunda = crearIncidenciaCritica();
        segunda.marcarComoExpedite();
        segunda.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(segunda);

        servicio.avanzarA(
                segunda,
                EstadoIncidencia.EN_DESARROLLO);

        assertEquals(
                EstadoIncidencia.EN_DESARROLLO,
                segunda.getEstado());
    }

    @Test
    void servicioExpediteNoDebePermitirSaltosDeEstado() {
        RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

        ServicioExpedite servicio = new ServicioExpedite(repositorio);

        Incidencia incidencia = crearIncidenciaCritica();
        incidencia.marcarComoExpedite();
        repositorio.guardar(incidencia);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.avanzarA(
                        incidencia,
                        EstadoIncidencia.EN_DESARROLLO));

        assertEquals(
                EstadoIncidencia.REGISTRADA,
                incidencia.getEstado());
    }

    private Incidencia crearIncidenciaCritica() {
        return new Incidencia(
                "Servidor principal caido",
                "El servidor principal no responde solicitudes.",
                "Servidores",
                Impacto.ALTO,
                Urgencia.ALTA);
    }

    private Incidencia crearIncidenciaAlta() {
        return new Incidencia(
                "Problema de conectividad",
                "El equipo no logra conectarse correctamente.",
                "Redes",
                Impacto.ALTO,
                Urgencia.MEDIA);
    }

    private Incidencia crearIncidenciaNormal() {
        return new Incidencia(
                "Problema con monitor",
                "El monitor secundario presenta una falla.",
                "Hardware",
                Impacto.BAJO,
                Urgencia.BAJA);
    }
}