package com.helpdeskflow;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServicioMetricasIncidencias {

    private final RepositorioIncidencias repositorio;

    public ServicioMetricasIncidencias(RepositorioIncidencias repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException(
                    "El repositorio de incidencias es obligatorio."
            );
        }

        this.repositorio = repositorio;
    }

    public long obtenerCantidadTotal() {
        return repositorio.listarTodas().size();
    }

    public long obtenerCantidadAbiertas() {
        return repositorio.listarAbiertas().size();
    }

    public long obtenerCantidadFinalizadas() {
        return repositorio.listarFinalizadas().size();
    }

    public long calcularThroughput(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        validarPeriodo(fechaInicio, fechaFin);

        return repositorio.listarFinalizadas().stream()
                .filter(incidencia -> estaDentroDelPeriodo(
                        incidencia.getFechaCierre(), fechaInicio, fechaFin))
                .count();
    }

    public Optional<Duration> calcularLeadTimePromedio() {
        List<Incidencia> finalizadas = repositorio.listarFinalizadas();

        if (finalizadas.isEmpty()) {
            return Optional.empty();
        }

        Duration total = finalizadas.stream()
                .map(this::calcularLeadTime)
                .reduce(Duration.ZERO, Duration::plus);

        return Optional.of(total.dividedBy(finalizadas.size()));
    }

    public Map<Prioridad, Long> obtenerCantidadPorPrioridad() {
        Map<Prioridad, Long> conteo = new EnumMap<>(Prioridad.class);

        for (Prioridad prioridad : Prioridad.values()) {
            conteo.put(prioridad, 0L);
        }

        for (Incidencia incidencia : repositorio.listarTodas()) {
            conteo.merge(incidencia.getPrioridad(), 1L, Long::sum);
        }

        return Collections.unmodifiableMap(conteo);
    }

    private void validarPeriodo(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        if (fechaInicio == null) {
            throw new IllegalArgumentException(
                    "La fecha inicial es obligatoria."
            );
        }

        if (fechaFin == null) {
            throw new IllegalArgumentException(
                    "La fecha final es obligatoria."
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final."
            );
        }
    }

    private boolean estaDentroDelPeriodo(
            LocalDateTime fechaCierre,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        return fechaCierre != null
                && !fechaCierre.isBefore(fechaInicio)
                && !fechaCierre.isAfter(fechaFin);
    }

    private Duration calcularLeadTime(Incidencia incidencia) {
        Duration leadTime = Duration.between(
                incidencia.getFechaCreacion(),
                incidencia.getFechaCierre()
        );

        if (leadTime.isNegative()) {
            throw new IllegalStateException(
                    "El lead time no puede ser negativo."
            );
        }

        return leadTime;
    }
}
