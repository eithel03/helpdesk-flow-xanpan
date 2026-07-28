package com.helpdeskflow;

import java.io.PrintStream;
import java.util.List;

final class FormateadorIncidenciasConsola {

    void mostrarIncidencias(
            List<Incidencia> incidencias,
            PrintStream salida) {

        validarIncidencias(incidencias);
        validarSalida(salida);

        if (incidencias.isEmpty()) {
            salida.println("No existen incidencias para mostrar.");
            return;
        }

        for (Incidencia incidencia : incidencias) {
            mostrarIncidencia(incidencia, salida);
        }
    }

    void mostrarIncidencia(
            Incidencia incidencia,
            PrintStream salida) {

        validarIncidencia(incidencia);
        validarSalida(salida);

        salida.println("----------------------------------------");
        salida.println("ID: " + incidencia.getId());
        salida.println("T\u00edtulo: " + incidencia.getTitulo());
        salida.println("Categor\u00eda: " + incidencia.getCategoria());
        salida.println("Impacto: " + incidencia.getImpacto());
        salida.println("Urgencia: " + incidencia.getUrgencia());
        salida.println("Prioridad: " + incidencia.getPrioridad());
        salida.println("Estado: " + incidencia.getEstado());
        salida.println(
                "EXPEDITE: "
                        + (incidencia.esExpedite() ? "S\u00ed" : "No"));
        salida.println(
                "Fecha de creaci\u00f3n: "
                        + incidencia.getFechaCreacion());
    }

    private void validarIncidencias(List<Incidencia> incidencias) {
        if (incidencias == null) {
            throw new IllegalArgumentException(
                    "La lista de incidencias es obligatoria.");
        }
    }

    private void validarIncidencia(Incidencia incidencia) {
        if (incidencia == null) {
            throw new IllegalArgumentException(
                    "La incidencia es obligatoria.");
        }
    }

    private void validarSalida(PrintStream salida) {
        if (salida == null) {
            throw new IllegalArgumentException(
                    "La salida es obligatoria.");
        }
    }
}
