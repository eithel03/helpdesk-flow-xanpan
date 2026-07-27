package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositorioIncidenciasSQLiteTest {

        @TempDir
        Path directorioTemporal;

        @Test
        void debeCrearArchivoDeBaseDatosAutomaticamente() {
                Path archivoBaseDatos = directorioTemporal.resolve("helpdesk-test.db");

                new RepositorioIncidenciasSQLite(archivoBaseDatos);

                assertTrue(Files.exists(archivoBaseDatos));
        }

        @Test
        void debeGuardarYRecuperarUnaIncidenciaPorIdentificador() {
                Path archivoBaseDatos = directorioTemporal.resolve("guardar-buscar.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivoBaseDatos);

                Incidencia incidencia = crearIncidenciaNormal();
                repositorio.guardar(incidencia);

                Incidencia recuperada = repositorio
                                .buscarPorId(incidencia.getId())
                                .orElseThrow();

                assertEquals(incidencia.getId(), recuperada.getId());
                assertEquals(incidencia.getTitulo(), recuperada.getTitulo());
                assertEquals(incidencia.getDescripcion(),
                                recuperada.getDescripcion());
                assertEquals(incidencia.getCategoria(), recuperada.getCategoria());
                assertEquals(incidencia.getImpacto(), recuperada.getImpacto());
                assertEquals(incidencia.getUrgencia(), recuperada.getUrgencia());
                assertEquals(incidencia.getPrioridad(), recuperada.getPrioridad());
                assertEquals(incidencia.getEstado(), recuperada.getEstado());
                assertEquals(incidencia.getFechaCreacion(),
                                recuperada.getFechaCreacion());
        }

        @Test
        void listarTodasDebeRecuperarIncidenciasEnOrdenDeInsercion() {
                Path archivo = directorioTemporal.resolve("listar-todas.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia primera = crearIncidenciaNormal();
                Incidencia segunda = crearIncidenciaCritica();

                repositorio.guardar(primera);
                repositorio.guardar(segunda);

                List<Incidencia> resultado = repositorio.listarTodas();

                assertEquals(2, resultado.size());
                assertEquals(primera.getId(), resultado.get(0).getId());
                assertEquals(segunda.getId(), resultado.get(1).getId());
        }

        @Test
        void listarTodasDebeDevolverListaVaciaSinIncidencias() {
                Path archivo = directorioTemporal.resolve("listar-vacia.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                assertTrue(repositorio.listarTodas().isEmpty());
        }

        @Test
        void listarTodasDebeDevolverListaNoModificable() {
                Path archivo = directorioTemporal.resolve("lista-inmutable.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                repositorio.guardar(crearIncidenciaNormal());

                List<Incidencia> resultado = repositorio.listarTodas();

                assertThrows(
                                UnsupportedOperationException.class,
                                resultado::clear);
        }

        @Test
        void debeFiltrarIncidenciasPorEstado() {
                Path archivo = directorioTemporal.resolve("filtro-estado.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia registrada = crearIncidenciaNormal();
                Incidencia lista = crearIncidenciaAlta();
                lista.avanzarA(EstadoIncidencia.LISTA);

                repositorio.guardar(registrada);
                repositorio.guardar(lista);

                List<Incidencia> resultado = repositorio.filtrarPorEstado(
                                EstadoIncidencia.LISTA);

                assertEquals(1, resultado.size());
                assertEquals(lista.getId(), resultado.get(0).getId());
        }

        @Test
        void filtrarPorEstadoDebeDevolverVacioSinCoincidencias() {
                Path archivo = directorioTemporal.resolve("estado-sin-resultados.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                repositorio.guardar(crearIncidenciaNormal());

                assertTrue(
                                repositorio
                                                .filtrarPorEstado(EstadoIncidencia.FINALIZADA)
                                                .isEmpty());
        }

        @Test
        void debeRechazarEstadoNulo() {
                Path archivo = directorioTemporal.resolve("estado-nulo.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> repositorio.filtrarPorEstado(null));
        }

        @Test
        void debeFiltrarIncidenciasPorPrioridad() {
                Path archivo = directorioTemporal.resolve("filtro-prioridad.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia normal = crearIncidenciaNormal();
                Incidencia critica = crearIncidenciaCritica();

                repositorio.guardar(normal);
                repositorio.guardar(critica);

                List<Incidencia> resultado = repositorio.filtrarPorPrioridad(
                                Prioridad.CRITICA);

                assertEquals(1, resultado.size());
                assertEquals(critica.getId(), resultado.get(0).getId());
        }

        @Test
        void debeRechazarPrioridadNula() {
                Path archivo = directorioTemporal.resolve("prioridad-nula.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> repositorio.filtrarPorPrioridad(null));
        }

        @Test
        void listarAbiertasDebeExcluirIncidenciasFinalizadas() {
                Path archivo = directorioTemporal.resolve("abiertas.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia abierta = crearIncidenciaNormal();
                Incidencia finalizada = crearIncidenciaFinalizada();

                repositorio.guardar(abierta);
                repositorio.guardar(finalizada);

                List<Incidencia> resultado = repositorio.listarAbiertas();

                assertEquals(1, resultado.size());
                assertEquals(abierta.getId(), resultado.get(0).getId());
        }

        @Test
        void listarFinalizadasDebeExcluirIncidenciasAbiertas() {
                Path archivo = directorioTemporal.resolve("finalizadas.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia abierta = crearIncidenciaNormal();
                Incidencia finalizada = crearIncidenciaFinalizada();

                repositorio.guardar(abierta);
                repositorio.guardar(finalizada);

                List<Incidencia> resultado = repositorio.listarFinalizadas();

                assertEquals(1, resultado.size());
                assertEquals(
                                finalizada.getId(),
                                resultado.get(0).getId());
        }

        @Test
        void debeMantenerDatosAlCrearUnaNuevaInstanciaDelRepositorio() {
                Path archivo = directorioTemporal.resolve("persistencia.db");

                Incidencia incidencia = crearIncidenciaNormal();

                RepositorioIncidencias primerRepositorio = new RepositorioIncidenciasSQLite(archivo);

                primerRepositorio.guardar(incidencia);

                RepositorioIncidencias segundoRepositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia recuperada = segundoRepositorio
                                .buscarPorId(incidencia.getId())
                                .orElseThrow();

                assertEquals(incidencia.getId(), recuperada.getId());
                assertEquals(incidencia.getTitulo(), recuperada.getTitulo());
                assertEquals(
                                incidencia.getFechaCreacion(),
                                recuperada.getFechaCreacion());
        }

        @Test
        void debeReconstruirIncidenciaFinalizadaConSusDatosDeCierre() {
                Path archivo = directorioTemporal.resolve("reconstruir-finalizada.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia original = crearIncidenciaFinalizada();
                repositorio.guardar(original);

                Incidencia recuperada = repositorio
                                .buscarPorId(original.getId())
                                .orElseThrow();

                assertEquals(
                                EstadoIncidencia.FINALIZADA,
                                recuperada.getEstado());
                assertNotNull(recuperada.getFechaCierre());
                assertEquals(
                                original.getFechaCierre(),
                                recuperada.getFechaCierre());
                assertEquals(
                                original.getDescripcionSolucion(),
                                recuperada.getDescripcionSolucion());
                assertEquals(
                                original.getPrioridad(),
                                recuperada.getPrioridad());
        }

        @Test
        void buscarPorIdDebeDevolverVacioCuandoNoExiste() {
                Path archivo = directorioTemporal.resolve("id-inexistente.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                assertTrue(
                                repositorio.buscarPorId(UUID.randomUUID()).isEmpty());
        }

        @Test
        void guardarDebeRechazarIncidenciaNula() {
                Path archivo = directorioTemporal.resolve("incidencia-nula.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> repositorio.guardar(null));
        }

        @Test
        void buscarDebeRechazarIdentificadorNulo() {
                Path archivo = directorioTemporal.resolve("id-nulo.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> repositorio.buscarPorId(null));
        }

        @Test
        void guardarDebeRechazarIdentificadorDuplicado() {
                Path archivo = directorioTemporal.resolve("duplicado.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia incidencia = crearIncidenciaNormal();

                repositorio.guardar(incidencia);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> repositorio.guardar(incidencia));

                assertEquals(1, repositorio.listarTodas().size());
        }

        @Test
        void metricasDebenFuncionarConRepositorioSQLite() {
                Path archivo = directorioTemporal.resolve("metricas.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                repositorio.guardar(crearIncidenciaNormal());
                repositorio.guardar(crearIncidenciaFinalizada());

                ServicioMetricasIncidencias servicio = new ServicioMetricasIncidencias(repositorio);

                assertEquals(2, servicio.obtenerCantidadTotal());
                assertEquals(1, servicio.obtenerCantidadAbiertas());
                assertEquals(1, servicio.obtenerCantidadFinalizadas());
                assertEquals(
                                1L,
                                servicio.obtenerCantidadPorPrioridad()
                                                .get(Prioridad.NORMAL));
                assertEquals(
                                1L,
                                servicio.obtenerCantidadPorPrioridad()
                                                .get(Prioridad.CRITICA));
        }

        @Test
        void debeConservarIndicadorExpediteAlRecuperarIncidencia() {
                Path archivo = directorioTemporal.resolve("expedite.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia original = crearIncidenciaCritica();
                original.marcarComoExpedite();

                repositorio.guardar(original);

                Incidencia recuperada = repositorio
                                .buscarPorId(original.getId())
                                .orElseThrow();

                assertTrue(recuperada.esExpedite());
                assertEquals(
                                Prioridad.CRITICA,
                                recuperada.getPrioridad());
        }

        @Test
        void incidenciaNormalRecuperadaNoDebeSerExpedite() {
                Path archivo = directorioTemporal.resolve("no-expedite.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia original = crearIncidenciaNormal();

                repositorio.guardar(original);

                Incidencia recuperada = repositorio
                                .buscarPorId(original.getId())
                                .orElseThrow();

                assertFalse(recuperada.esExpedite());
        }

        @Test
        void debeActualizarElEstadoDeUnaIncidencia() {
                Path archivo = directorioTemporal.resolve(
                                "actualizar-estado.db");

                RepositorioIncidencias repositorio = new RepositorioIncidenciasSQLite(archivo);

                Incidencia incidencia = new Incidencia(
                                "Problema de conexión",
                                "La conexión presenta interrupciones frecuentes.",
                                "Redes",
                                Impacto.MEDIO,
                                Urgencia.MEDIA);

                repositorio.guardar(incidencia);

                incidencia.avanzarA(
                                EstadoIncidencia.LISTA);

                repositorio.actualizar(incidencia);

                Incidencia recuperada = repositorio
                                .buscarPorId(incidencia.getId())
                                .orElseThrow();

                assertEquals(
                                EstadoIncidencia.LISTA,
                                recuperada.getEstado());
        }

        @Test
void actualizarDebeRechazarIncidenciaInexistente() {
    Path archivo = directorioTemporal.resolve(
            "actualizar-inexistente.db"
    );

    RepositorioIncidencias repositorio =
            new RepositorioIncidenciasSQLite(archivo);

    Incidencia incidencia = crearIncidenciaNormal();

    IllegalArgumentException excepcion =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> repositorio.actualizar(incidencia)
            );

    assertEquals(
            "No existe una incidencia con el identificador indicado.",
            excepcion.getMessage()
    );
}

        private Incidencia crearIncidenciaNormal() {
                return new Incidencia(
                                "Problema de monitor",
                                "El monitor principal no muestra imagen.",
                                "Hardware",
                                Impacto.BAJO,
                                Urgencia.BAJA);
        }

        private Incidencia crearIncidenciaAlta() {
                return new Incidencia(
                                "Equipo sin Internet",
                                "El equipo no logra conectarse a Internet.",
                                "Redes",
                                Impacto.ALTO,
                                Urgencia.MEDIA);
        }

        private Incidencia crearIncidenciaCritica() {
                return new Incidencia(
                                "Servidor principal caido",
                                "El servidor principal no responde solicitudes.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);
        }

        private Incidencia crearIncidenciaFinalizada() {
                Incidencia incidencia = crearIncidenciaCritica();

                incidencia.avanzarA(EstadoIncidencia.LISTA);
                incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);
                incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);
                incidencia.finalizar(
                                "Se reinició y configuró correctamente el servidor.");

                return incidencia;
        }

}