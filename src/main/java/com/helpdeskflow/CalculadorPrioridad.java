package com.helpdeskflow;

public final class CalculadorPrioridad {

    private CalculadorPrioridad() {
        // Evita crear objetos de esta clase utilitaria.
    }

    public static Prioridad calcular(Impacto impacto, Urgencia urgencia) {
    validarDatos(impacto, urgencia);

    if (impacto == Impacto.ALTO && urgencia == Urgencia.ALTA) {
        return Prioridad.CRITICA;
    }

    if (impacto == Impacto.ALTO || urgencia == Urgencia.ALTA) {
        return Prioridad.ALTA;
    }

    return Prioridad.NORMAL;
}

private static void validarDatos(Impacto impacto, Urgencia urgencia) {
    if (impacto == null) {
        throw new IllegalArgumentException(
                "El impacto es obligatorio para calcular la prioridad."
        );
    }

    if (urgencia == null) {
        throw new IllegalArgumentException(
                "La urgencia es obligatoria para calcular la prioridad."
        );
    }
}
}