package com.helpdeskflow;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.Duration;

public class ControladorConsola {

    private final RepositorioIncidencias repositorio;
    private final ServicioExpedite servicioExpedite;
    private final ServicioMetricasIncidencias servicioMetricas;
    private final FormateadorIncidenciasConsola formateadorIncidencias;

    public ControladorConsola(
            RepositorioIncidencias repositorio) {

        if (repositorio == null) {
            throw new IllegalArgumentException(
                    "El repositorio es obligatorio.");
        }

        this.repositorio = repositorio;
        this.servicioExpedite = new ServicioExpedite(repositorio);
        this.servicioMetricas = new ServicioMetricasIncidencias(repositorio);
        this.formateadorIncidencias = new FormateadorIncidenciasConsola();
    }

    public Incidencia registrarIncidencia(
            String titulo,
            String descripcion,
            String categoria,
            String impactoTexto,
            String urgenciaTexto) {

        Impacto impacto = convertirImpacto(impactoTexto);
        Urgencia urgencia = convertirUrgencia(urgenciaTexto);

        Incidencia incidencia = new Incidencia(
                titulo,
                descripcion,
                categoria,
                impacto,
                urgencia);

        repositorio.guardar(incidencia);

        return incidencia;
    }

    public void mostrarIncidencias(
            List<Incidencia> incidencias,
            PrintStream salida) {

        formateadorIncidencias.mostrarIncidencias(
                incidencias,
                salida);
    }

    public List<Incidencia> listarTodas() {
        return repositorio.listarTodas();
    }

    private Impacto convertirImpacto(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "El impacto es obligatorio.");
        }

        try {
            return Impacto.valueOf(
                    valor.trim().toUpperCase());
        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "Impacto inv\u00e1lido. Use BAJO, MEDIO o ALTO.");
        }
    }

    private Urgencia convertirUrgencia(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "La urgencia es obligatoria.");
        }

        try {
            return Urgencia.valueOf(
                    valor.trim().toUpperCase());
        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "Urgencia inv\u00e1lida. Use BAJA, MEDIA o ALTA.");
        }
    }

    public Optional<Incidencia> buscarPorIdentificador(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador es obligatorio.");
        }

        try {
            UUID id = UUID.fromString(identificador.trim());
            return repositorio.buscarPorId(id);
        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "El identificador ingresado no tiene un formato v\u00e1lido.");
        }
    }

    public List<Incidencia> filtrarPorEstado(String estadoTexto) {
        if (estadoTexto == null || estadoTexto.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio.");
        }

        try {
            EstadoIncidencia estado = EstadoIncidencia.valueOf(
                    estadoTexto.trim().toUpperCase());

            return repositorio.filtrarPorEstado(estado);

        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "Estado inv\u00e1lido. Use REGISTRADA, LISTA, "
                            + "EN_DESARROLLO, EN_VALIDACION o FINALIZADA.");
        }
    }

    public List<Incidencia> filtrarPorPrioridad(
            String prioridadTexto) {

        if (prioridadTexto == null || prioridadTexto.isBlank()) {
            throw new IllegalArgumentException(
                    "La prioridad es obligatoria.");
        }

        try {
            Prioridad prioridad = Prioridad.valueOf(
                    prioridadTexto.trim().toUpperCase());

            return repositorio.filtrarPorPrioridad(prioridad);

        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "Prioridad inv\u00e1lida. Use NORMAL, ALTA o CRITICA.");
        }
    }

    public List<Incidencia> listarAbiertas() {
        return repositorio.listarAbiertas();
    }

    public List<Incidencia> listarFinalizadas() {
        return repositorio.listarFinalizadas();
    }

    public void mostrarIncidencia(
            Incidencia incidencia,
            PrintStream salida) {

        formateadorIncidencias.mostrarIncidencia(
                incidencia,
                salida);
    }

    public Incidencia avanzarEstado(
            String identificador,
            String nuevoEstadoTexto) {

        Incidencia incidencia = obtenerIncidenciaExistente(identificador);

        EstadoIncidencia nuevoEstado = convertirEstado(nuevoEstadoTexto);

        servicioExpedite.avanzarA(
                incidencia,
                nuevoEstado);

        repositorio.actualizar(incidencia);

        return incidencia;
    }

    public Incidencia finalizarIncidencia(
            String identificador,
            String descripcionSolucion) {

        Incidencia incidencia = obtenerIncidenciaExistente(identificador);

        incidencia.finalizar(descripcionSolucion);

        repositorio.actualizar(incidencia);

        return incidencia;
    }

    private Incidencia obtenerIncidenciaExistente(
            String identificador) {

        if (identificador == null
                || identificador.isBlank()) {

            throw new IllegalArgumentException(
                    "El identificador es obligatorio.");
        }

        UUID id;

        try {
            id = UUID.fromString(
                    identificador.trim());
        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "El identificador ingresado no tiene un formato v\u00e1lido.");
        }

        return repositorio.buscarPorId(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No se encontr\u00f3 una incidencia "
                                        + "con ese identificador."));
    }

    private EstadoIncidencia convertirEstado(
            String estadoTexto) {

        if (estadoTexto == null
                || estadoTexto.isBlank()) {

            throw new IllegalArgumentException(
                    "El nuevo estado es obligatorio.");
        }

        try {
            return EstadoIncidencia.valueOf(
                    estadoTexto.trim().toUpperCase());
        } catch (IllegalArgumentException excepcion) {
            throw new IllegalArgumentException(
                    "Estado inv\u00e1lido. Use LISTA, EN_DESARROLLO "
                            + "o EN_VALIDACION.");
        }
    }

    public Incidencia marcarComoExpedite(String identificador) {
        Incidencia incidencia = obtenerIncidenciaExistente(identificador);

        incidencia.marcarComoExpedite();
        repositorio.actualizar(incidencia);

        return incidencia;
    }

    public List<Incidencia> listarExpedite() {
        return repositorio.listarTodas()
                .stream()
                .filter(Incidencia::esExpedite)
                .toList();
    }

    public long obtenerCantidadTotal() {
        return servicioMetricas.obtenerCantidadTotal();
    }

    public long obtenerCantidadAbiertas() {
        return servicioMetricas.obtenerCantidadAbiertas();
    }

    public long obtenerCantidadFinalizadas() {
        return servicioMetricas.obtenerCantidadFinalizadas();
    }

    public long calcularThroughput(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        return servicioMetricas.calcularThroughput(
                fechaInicio,
                fechaFin);
    }

    public Optional<Duration> calcularLeadTimePromedio() {
        return servicioMetricas.calcularLeadTimePromedio();
    }

    public Map<Prioridad, Long> obtenerCantidadPorPrioridad() {
        return servicioMetricas.obtenerCantidadPorPrioridad();
    }

}
