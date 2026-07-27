package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ServicioMetricasIncidenciasTest {

    private final RepositorioIncidencias repositorio =
            new RepositorioIncidenciasMemoria();
    private final ServicioMetricasIncidencias servicio =
            new ServicioMetricasIncidencias(repositorio);

    @Test
    void obtenerCantidadTotalDebeSerCeroCuandoRepositorioEstaVacio() {
        assertEquals(0, servicio.obtenerCantidadTotal());
    }

    @Test
    void obtenerCantidadTotalDebeContarVariasIncidencias() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaAlta());
        repositorio.guardar(crearIncidenciaCritica());

        assertEquals(3, servicio.obtenerCantidadTotal());
    }

    @Test
    void obtenerCantidadAbiertasDebeContarIncidenciasNoFinalizadas() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaLista());
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        assertEquals(2, servicio.obtenerCantidadAbiertas());
    }

    @Test
    void obtenerCantidadFinalizadasDebeContarIncidenciasFinalizadas() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-02T08:00:00", Duration.ofHours(1)));

        assertEquals(2, servicio.obtenerCantidadFinalizadas());
    }

    @Test
    void incidenciasFinalizadasNoDebenContarComoAbiertas() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        assertEquals(0, servicio.obtenerCantidadAbiertas());
    }

    @Test
    void calcularThroughputDebeContarFinalizadasDentroDelPeriodo() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-02T08:00:00", Duration.ofHours(3)));

        long resultado = servicio.calcularThroughput(
                fecha("2026-01-01T00:00:00"),
                fecha("2026-01-02T23:59:59"));

        assertEquals(2, resultado);
    }

    @Test
    void calcularThroughputDebeExcluirIncidenciasAbiertas() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        long resultado = servicio.calcularThroughput(
                fecha("2026-01-01T00:00:00"),
                fecha("2026-01-01T23:59:59"));

        assertEquals(1, resultado);
    }

    @Test
    void calcularThroughputDebeExcluirFinalizadasFueraDelPeriodo() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-03T08:00:00", Duration.ofHours(2)));

        long resultado = servicio.calcularThroughput(
                fecha("2026-01-01T00:00:00"),
                fecha("2026-01-01T23:59:59"));

        assertEquals(1, resultado);
    }

    @Test
    void calcularThroughputDebeSerCeroSinCoincidencias() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-03T08:00:00", Duration.ofHours(2)));

        long resultado = servicio.calcularThroughput(
                fecha("2026-01-01T00:00:00"),
                fecha("2026-01-01T23:59:59"));

        assertEquals(0, resultado);
    }

    @Test
    void calcularThroughputDebeIncluirExactamenteElLimiteInicial() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        long resultado = servicio.calcularThroughput(
                fecha("2026-01-01T10:00:00"),
                fecha("2026-01-01T23:59:59"));

        assertEquals(1, resultado);
    }

    @Test
    void calcularThroughputDebeIncluirExactamenteElLimiteFinal() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        long resultado = servicio.calcularThroughput(
                fecha("2026-01-01T00:00:00"),
                fecha("2026-01-01T10:00:00"));

        assertEquals(1, resultado);
    }

    @Test
    void calcularThroughputDebeRechazarFechaInicialNula() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularThroughput(null,
                        fecha("2026-01-01T23:59:59")));
    }

    @Test
    void calcularThroughputDebeRechazarFechaFinalNula() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularThroughput(
                        fecha("2026-01-01T00:00:00"), null));
    }

    @Test
    void calcularThroughputDebeRechazarPeriodoInvertido() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularThroughput(
                        fecha("2026-01-02T00:00:00"),
                        fecha("2026-01-01T00:00:00")));
    }

    @Test
    void calcularLeadTimePromedioDebeCalcularUnaIncidenciaFinalizada() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        Optional<Duration> resultado = servicio.calcularLeadTimePromedio();

        assertEquals(Optional.of(Duration.ofHours(2)), resultado);
    }

    @Test
    void calcularLeadTimePromedioDebeCalcularVariasIncidenciasFinalizadas() {
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-02T08:00:00", Duration.ofHours(4)));

        Optional<Duration> resultado = servicio.calcularLeadTimePromedio();

        assertEquals(Optional.of(Duration.ofHours(3)), resultado);
    }

    @Test
    void calcularLeadTimePromedioDebeExcluirIncidenciasAbiertas() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2)));

        Optional<Duration> resultado = servicio.calcularLeadTimePromedio();

        assertEquals(Optional.of(Duration.ofHours(2)), resultado);
    }

    @Test
    void calcularLeadTimePromedioDebeSerVacioSinFinalizadas() {
        repositorio.guardar(crearIncidenciaNormal());

        Optional<Duration> resultado = servicio.calcularLeadTimePromedio();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerCantidadPorPrioridadDebeAgruparIncidencias() {
        repositorio.guardar(crearIncidenciaNormal());
        repositorio.guardar(crearIncidenciaAlta());
        repositorio.guardar(crearIncidenciaCritica());
        repositorio.guardar(crearIncidenciaCritica());

        Map<Prioridad, Long> resultado = servicio.obtenerCantidadPorPrioridad();

        assertEquals(1, resultado.get(Prioridad.NORMAL));
        assertEquals(1, resultado.get(Prioridad.ALTA));
        assertEquals(2, resultado.get(Prioridad.CRITICA));
    }

    @Test
    void obtenerCantidadPorPrioridadDebeIncluirPrioridadesSinIncidencias() {
        repositorio.guardar(crearIncidenciaNormal());

        Map<Prioridad, Long> resultado = servicio.obtenerCantidadPorPrioridad();

        assertEquals(1, resultado.get(Prioridad.NORMAL));
        assertEquals(0, resultado.get(Prioridad.ALTA));
        assertEquals(0, resultado.get(Prioridad.CRITICA));
    }

    @Test
    void obtenerCantidadPorPrioridadDebeDevolverMapaNoModificable() {
        Map<Prioridad, Long> resultado = servicio.obtenerCantidadPorPrioridad();

        assertThrows(UnsupportedOperationException.class,
                () -> resultado.put(Prioridad.NORMAL, 99L));
    }

    @Test
    void metricasNoDebenModificarIncidenciasNiRepositorio() {
        Incidencia abierta = crearIncidenciaLista();
        Incidencia finalizada = crearIncidenciaFinalizada(
                "2026-01-01T08:00:00", Duration.ofHours(2));
        repositorio.guardar(abierta);
        repositorio.guardar(finalizada);

        servicio.obtenerCantidadTotal();
        servicio.obtenerCantidadAbiertas();
        servicio.obtenerCantidadFinalizadas();
        servicio.calcularThroughput(
                fecha("2026-01-01T00:00:00"),
                fecha("2026-01-01T23:59:59"));
        servicio.calcularLeadTimePromedio();
        servicio.obtenerCantidadPorPrioridad();

        assertEquals(2, repositorio.listarTodas().size());
        assertEquals(EstadoIncidencia.LISTA, abierta.getEstado());
        assertEquals(EstadoIncidencia.FINALIZADA, finalizada.getEstado());
        assertEquals(fecha("2026-01-01T08:00:00"),
                finalizada.getFechaCreacion());
        assertEquals(fecha("2026-01-01T10:00:00"),
                finalizada.getFechaCierre());
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

    private Incidencia crearIncidenciaFinalizada(
            String fechaCreacion,
            Duration leadTime) {

        RelojMutable reloj = new RelojMutable(fecha(fechaCreacion));
        Incidencia incidencia = new Incidencia(
                "Servidor principal caido",
                "El servidor principal no responde solicitudes.",
                "Servidores",
                Impacto.ALTO,
                Urgencia.ALTA,
                reloj
        );
        incidencia.avanzarA(EstadoIncidencia.LISTA);
        incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);
        incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);
        reloj.avanzar(leadTime);
        incidencia.finalizar("Se aplico la solucion correspondiente.");
        return incidencia;
    }

    private LocalDateTime fecha(String valor) {
        return LocalDateTime.parse(valor);
    }

    private static final class RelojMutable extends Clock {

        private static final ZoneId ZONA = ZoneId.of("UTC");

        private Instant instante;

        private RelojMutable(LocalDateTime fecha) {
            this.instante = fecha.atZone(ZONA).toInstant();
        }

        private void avanzar(Duration duracion) {
            instante = instante.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZONA;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(instante, zone);
        }

        @Override
        public Instant instant() {
            return instante;
        }
    }
}
