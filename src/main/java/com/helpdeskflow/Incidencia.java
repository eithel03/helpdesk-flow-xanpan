package com.helpdeskflow;

import java.time.LocalDateTime;
import java.util.UUID;

public class Incidencia {

    

    private final UUID id;
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Impacto impacto;
    private final Urgencia urgencia;
    private final EstadoIncidencia estado;
    private final LocalDateTime fechaCreacion;

    private LocalDateTime fechaCierre;
    private String descripcionSolucion;

    public Incidencia(

        
            String titulo,
            String descripcion,
            String categoria,
            Impacto impacto,
            Urgencia urgencia) {

                if (titulo == null || titulo.isBlank()) {
    throw new IllegalArgumentException("El título no puede estar vacío.");
}
if (descripcion == null || descripcion.trim().length() < 10) {
    throw new IllegalArgumentException(
            "La descripción debe contener al menos 10 caracteres."
    );
}

if (impacto == null) {
    throw new IllegalArgumentException("El impacto es obligatorio.");
}

if (urgencia == null) {
    throw new IllegalArgumentException("La urgencia es obligatoria.");
}

        this.id = UUID.randomUUID();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.impacto = impacto;
        this.urgencia = urgencia;
        this.estado = EstadoIncidencia.REGISTRADA;
        this.fechaCreacion = LocalDateTime.now();

        
    }

    

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public Impacto getImpacto() {
        return impacto;
    }

    public Urgencia getUrgencia() {
        return urgencia;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public String getDescripcionSolucion() {
        return descripcionSolucion;
    }
    
}