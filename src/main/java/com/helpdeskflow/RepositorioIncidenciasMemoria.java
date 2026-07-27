package com.helpdeskflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RepositorioIncidenciasMemoria implements RepositorioIncidencias {

    private final Map<UUID, Incidencia> incidencias = new LinkedHashMap<>();

    @Override
    public void guardar(Incidencia incidencia) {
        validarIncidencia(incidencia);

        UUID id = incidencia.getId();
        if (incidencias.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Ya existe una incidencia con el identificador indicado.");
        }

        incidencias.put(id, incidencia);
    }

    @Override
    public void actualizar(Incidencia incidencia) {
        validarIncidencia(incidencia);

        UUID id = incidencia.getId();

        if (!incidencias.containsKey(id)) {
            throw new IllegalArgumentException(
                    "No existe una incidencia con el identificador indicado.");
        }

        incidencias.put(id, incidencia);
    }

    @Override
    public Optional<Incidencia> buscarPorId(UUID id) {
        validarId(id);

        return Optional.ofNullable(incidencias.get(id));
    }

    @Override
    public List<Incidencia> listarTodas() {
        return List.copyOf(incidencias.values());
    }

    @Override
    public List<Incidencia> listarAbiertas() {
        return incidencias.values().stream()
                .filter(incidencia -> incidencia.getEstado() != EstadoIncidencia.FINALIZADA)
                .toList();
    }

    @Override
    public List<Incidencia> listarFinalizadas() {
        return incidencias.values().stream()
                .filter(incidencia -> incidencia.getEstado() == EstadoIncidencia.FINALIZADA)
                .toList();
    }

    @Override
    public List<Incidencia> filtrarPorEstado(EstadoIncidencia estado) {
        validarEstado(estado);

        return incidencias.values().stream()
                .filter(incidencia -> incidencia.getEstado() == estado)
                .toList();
    }

    @Override
    public List<Incidencia> filtrarPorPrioridad(Prioridad prioridad) {
        validarPrioridad(prioridad);

        return incidencias.values().stream()
                .filter(incidencia -> incidencia.getPrioridad() == prioridad)
                .toList();
    }

    private static void validarIncidencia(Incidencia incidencia) {
        if (incidencia == null) {
            throw new IllegalArgumentException(
                    "La incidencia es obligatoria.");
        }
    }

    private static void validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador es obligatorio.");
        }
    }

    private static void validarEstado(EstadoIncidencia estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio.");
        }
    }

    private static void validarPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException(
                    "La prioridad es obligatoria.");
        }
    }
}
