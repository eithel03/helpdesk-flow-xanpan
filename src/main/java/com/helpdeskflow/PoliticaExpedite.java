package com.helpdeskflow;

public class PoliticaExpedite {

    public void validarAvance(
            Incidencia incidencia,
            EstadoIncidencia nuevoEstado,
            RepositorioIncidencias repositorio) {

        validarDatos(incidencia, nuevoEstado, repositorio);

        if (!incidencia.esExpedite()) {
            return;
        }

        if (!esEstadoActivo(nuevoEstado)) {
            return;
        }

        boolean existeOtraExpediteActiva = repositorio.listarTodas().stream()
                .filter(Incidencia::esExpedite)
                .filter(this::estaActiva)
                .anyMatch(otra -> !otra.getId().equals(
                        incidencia.getId()));

        if (existeOtraExpediteActiva) {
            throw new IllegalStateException(
                    "Ya existe una incidencia EXPEDITE activa.");
        }
    }

    private boolean estaActiva(Incidencia incidencia) {
        return esEstadoActivo(incidencia.getEstado());
    }

    private boolean esEstadoActivo(
            EstadoIncidencia estado) {

        return estado == EstadoIncidencia.EN_DESARROLLO
                || estado == EstadoIncidencia.EN_VALIDACION;
    }

    private void validarDatos(
            Incidencia incidencia,
            EstadoIncidencia nuevoEstado,
            RepositorioIncidencias repositorio) {

        if (incidencia == null) {
            throw new IllegalArgumentException(
                    "La incidencia es obligatoria.");
        }

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado es obligatorio.");
        }

        if (repositorio == null) {
            throw new IllegalArgumentException(
                    "El repositorio es obligatorio.");
        }
    }
}