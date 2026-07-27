package com.helpdeskflow;

public class ServicioExpedite {

    private final RepositorioIncidencias repositorio;
    private final PoliticaExpedite politica;

    public ServicioExpedite(
            RepositorioIncidencias repositorio) {

        if (repositorio == null) {
            throw new IllegalArgumentException(
                    "El repositorio es obligatorio.");
        }

        this.repositorio = repositorio;
        this.politica = new PoliticaExpedite();
    }

    public void avanzarA(
            Incidencia incidencia,
            EstadoIncidencia nuevoEstado) {

        politica.validarAvance(
                incidencia,
                nuevoEstado,
                repositorio);

        incidencia.avanzarA(nuevoEstado);
    }
}