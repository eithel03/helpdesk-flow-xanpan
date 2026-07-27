package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class RepositorioIncidenciasMemoriaTest {

    private final RepositorioIncidencias repositorio =
            new RepositorioIncidenciasMemoria();

    @Test
    void debeGuardarUnaIncidencia() {
        Incidencia incidencia = crearIncidenciaNormal();

        repositorio.guardar(incidencia);

        assertEquals(1, repositorio.listarTodas().size());
        assertSame(incidencia, repositorio.listarTodas().get(0));
    }

    @Test
    void debeGuardarVariasIncidencias() {
        Incidencia primera = crearIncidenciaNormal();
        Incidencia segunda = crearIncidenciaCritica();

        repositorio.guardar(primera);
        repositorio.guardar(segunda);

        assertEquals(List.of(primera, segunda), repositorio.listarTodas());
    }

    @Test
    void listarTodasDebeDevolverTodasLasIncidenciasEnOrdenDeInsercion() {
        Incidencia primera = crearIncidenciaNormal();
        Incidencia segunda = crearIncidenciaAlta();
        Incidencia tercera = crearIncidenciaCritica();

        repositorio.guardar(primera);
        repositorio.guardar(segunda);
        repositorio.guardar(tercera);

        assertEquals(List.of(primera, segunda, tercera),
                repositorio.listarTodas());
    }

    @Test
    void listarTodasDebeDevolverListaVaciaCuandoNoHayIncidencias() {
        assertTrue(repositorio.listarTodas().isEmpty());
    }

    @Test
    void debeBuscarUnaIncidenciaPorIdentificador() {
        Incidencia incidencia = crearIncidenciaNormal();
        repositorio.guardar(incidencia);

        Optional<Incidencia> resultado =
                repositorio.buscarPorId(incidencia.getId());

        assertTrue(resultado.isPresent());
        assertSame(incidencia, resultado.get());
    }

    @Test
    void debeDevolverOptionalVacioCuandoElIdentificadorNoExiste() {
        Optional<Incidencia> resultado =
                repositorio.buscarPorId(UUID.randomUUID());

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeRechazarIncidenciaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> repositorio.guardar(null));
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> repositorio.buscarPorId(null));
    }

    @Test
    void debeFiltrarPorEstadoConCoincidencias() {
        Incidencia registrada = crearIncidenciaNormal();
        Incidencia lista = crearIncidenciaAlta();
        lista.avanzarA(EstadoIncidencia.LISTA);
        repositorio.guardar(registrada);
        repositorio.guardar(lista);

        List<Incidencia> resultado =
                repositorio.filtrarPorEstado(EstadoIncidencia.LISTA);

        assertEquals(List.of(lista), resultado);
    }

    @Test
    void debeFiltrarPorEstadoSinCoincidencias() {
        repositorio.guardar(crearIncidenciaNormal());

        List<Incidencia> resultado =
                repositorio.filtrarPorEstado(EstadoIncidencia.FINALIZADA);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeFiltrarPorPrioridadConCoincidencias() {
        Incidencia normal = crearIncidenciaNormal();
        Incidencia critica = crearIncidenciaCritica();
        repositorio.guardar(normal);
        repositorio.guardar(critica);

        List<Incidencia> resultado =
                repositorio.filtrarPorPrioridad(Prioridad.CRITICA);

        assertEquals(List.of(critica), resultado);
    }

    @Test
    void debeFiltrarPorPrioridadSinCoincidencias() {
        repositorio.guardar(crearIncidenciaNormal());

        List<Incidencia> resultado =
                repositorio.filtrarPorPrioridad(Prioridad.CRITICA);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarAbiertasDebeDevolverIncidenciasRegistradas() {
        Incidencia registrada = crearIncidenciaNormal();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(registrada);
        repositorio.guardar(finalizada);

        List<Incidencia> resultado = repositorio.listarAbiertas();

        assertEquals(List.of(registrada), resultado);
    }
    @Test
    void listarAbiertasDebeDevolverIncidenciasListas() {
        Incidencia lista = crearIncidenciaLista();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(lista);
        repositorio.guardar(finalizada);

        List<Incidencia> resultado = repositorio.listarAbiertas();

        assertEquals(List.of(lista), resultado);
    }
    @Test
    void listarAbiertasDebeDevolverIncidenciasEnDesarrollo() {
        Incidencia enDesarrollo = crearIncidenciaEnDesarrollo();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(enDesarrollo);
        repositorio.guardar(finalizada);

        List<Incidencia> resultado = repositorio.listarAbiertas();

        assertEquals(List.of(enDesarrollo), resultado);
    }
    @Test
    void listarAbiertasDebeDevolverIncidenciasEnValidacion() {
        Incidencia enValidacion = crearIncidenciaEnValidacion();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(enValidacion);
        repositorio.guardar(finalizada);

        List<Incidencia> resultado = repositorio.listarAbiertas();

        assertEquals(List.of(enValidacion), resultado);
    }
    @Test
    void listarAbiertasDebeExcluirIncidenciasFinalizadas() {
        Incidencia registrada = crearIncidenciaNormal();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(registrada);
        repositorio.guardar(finalizada);

        List<Incidencia> resultado = repositorio.listarAbiertas();

        assertEquals(List.of(registrada), resultado);
    }
    @Test
    void listarAbiertasDebeDevolverListaVaciaCuandoTodasEstanFinalizadas() {
        repositorio.guardar(crearIncidenciaFinalizada());
        repositorio.guardar(crearIncidenciaFinalizada());

        List<Incidencia> resultado = repositorio.listarAbiertas();

        assertTrue(resultado.isEmpty());
    }
    @Test
    void listarFinalizadasDebeDevolverSolamenteIncidenciasFinalizadas() {
        Incidencia registrada = crearIncidenciaNormal();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(registrada);
        repositorio.guardar(finalizada);

        List<Incidencia> resultado = repositorio.listarFinalizadas();

        assertEquals(List.of(finalizada), resultado);
    }
    @Test
    void listarFinalizadasDebeDevolverListaVaciaCuandoNoHayFinalizadas() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaEnValidacion());

        List<Incidencia> resultado = repositorio.listarFinalizadas();

        assertTrue(resultado.isEmpty());
    }
    @Test
    void listarAbiertasYFinalizadasDebenConservarOrdenDeInsercion() {
        Incidencia registrada = crearIncidenciaNormal();
        Incidencia finalizadaPrimera = crearIncidenciaFinalizada();
        Incidencia lista = crearIncidenciaLista();
        Incidencia finalizadaSegunda = crearIncidenciaFinalizada();
        Incidencia enDesarrollo = crearIncidenciaEnDesarrollo();
        repositorio.guardar(registrada);
        repositorio.guardar(finalizadaPrimera);
        repositorio.guardar(lista);
        repositorio.guardar(finalizadaSegunda);
        repositorio.guardar(enDesarrollo);

        assertEquals(List.of(registrada, lista, enDesarrollo),
                repositorio.listarAbiertas());
        assertEquals(List.of(finalizadaPrimera, finalizadaSegunda),
                repositorio.listarFinalizadas());
    }
    @Test
    void listasDeAbiertasYFinalizadasNoDebenModificarElRepositorio() {
        Incidencia abierta = crearIncidenciaNormal();
        Incidencia finalizada = crearIncidenciaFinalizada();
        repositorio.guardar(abierta);
        repositorio.guardar(finalizada);
        List<Incidencia> abiertas = repositorio.listarAbiertas();
        List<Incidencia> finalizadas = repositorio.listarFinalizadas();

        assertThrows(UnsupportedOperationException.class, abiertas::clear);
        assertThrows(UnsupportedOperationException.class, finalizadas::clear);

        assertEquals(List.of(abierta), repositorio.listarAbiertas());
        assertEquals(List.of(finalizada), repositorio.listarFinalizadas());
    }
    @Test
    void debeRechazarEstadoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> repositorio.filtrarPorEstado(null));
    }

    @Test
    void debeRechazarPrioridadNula() {
        assertThrows(IllegalArgumentException.class,
                () -> repositorio.filtrarPorPrioridad(null));
    }

    @Test
    void modificarLaListaDevueltaNoDebeModificarElRepositorio() {
        Incidencia incidencia = crearIncidenciaNormal();
        repositorio.guardar(incidencia);
        List<Incidencia> incidencias = repositorio.listarTodas();

        assertThrows(UnsupportedOperationException.class, incidencias::clear);

        assertEquals(List.of(incidencia), repositorio.listarTodas());
    }

    @Test
    void debeRechazarIncidenciaConIdentificadorDuplicado() {
        Incidencia incidencia = crearIncidenciaNormal();
        repositorio.guardar(incidencia);

        assertThrows(IllegalArgumentException.class,
                () -> repositorio.guardar(incidencia));

        assertEquals(1, repositorio.listarTodas().size());
        assertSame(incidencia, repositorio.listarTodas().get(0));
        assertEquals(Optional.of(incidencia),
                repositorio.buscarPorId(incidencia.getId()));
    }

    private Incidencia crearIncidenciaNormal() {
        return new Incidencia(
                "Problema de monitor",
                "El monitor principal no muestra imagen.",
                "Hardware",
                Impacto.BAJO,
                Urgencia.BAJA
        );
    }

    private Incidencia crearIncidenciaAlta() {
        return new Incidencia(
                "Equipo sin Internet",
                "El equipo no logra conectarse a Internet.",
                "Redes",
                Impacto.ALTO,
                Urgencia.MEDIA
        );
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
    private Incidencia crearIncidenciaLista() {
        Incidencia incidencia = crearIncidenciaAlta();
        incidencia.avanzarA(EstadoIncidencia.LISTA);
        return incidencia;
    }

    private Incidencia crearIncidenciaEnDesarrollo() {
        Incidencia incidencia = crearIncidenciaLista();
        incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);
        return incidencia;
    }

    private Incidencia crearIncidenciaEnValidacion() {
        Incidencia incidencia = crearIncidenciaEnDesarrollo();
        incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);
        return incidencia;
    }

    private Incidencia crearIncidenciaFinalizada() {
        Incidencia incidencia = crearIncidenciaEnValidacion();
        incidencia.finalizar("Se aplico la solucion correspondiente.");
        return incidencia;
    }
}
