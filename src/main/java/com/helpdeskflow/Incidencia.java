package com.helpdeskflow;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;


public class Incidencia {

    private final UUID id;
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Impacto impacto;
    private final Prioridad prioridad;
    private final Urgencia urgencia;
    private EstadoIncidencia estado;
    private final LocalDateTime fechaCreacion;
    private final Clock reloj;

    private LocalDateTime fechaCierre;
    private String descripcionSolucion;

    public Incidencia(
        String titulo,
        String descripcion,
        String categoria,
        Impacto impacto,
        Urgencia urgencia) {

    this(
            titulo,
            descripcion,
            categoria,
            impacto,
            urgencia,
            Clock.systemDefaultZone()
    );
}

Incidencia(
        String titulo,
        String descripcion,
        String categoria,
        Impacto impacto,
        Urgencia urgencia,
        Clock reloj) {

    validarTitulo(titulo);
    validarDescripcion(descripcion);
    validarCategoria(categoria);
    validarImpacto(impacto);
    validarUrgencia(urgencia);
    validarReloj(reloj);

    this.id = UUID.randomUUID();
    this.titulo = titulo.trim();
    this.descripcion = descripcion.trim();
    this.categoria = categoria.trim();
    this.impacto = impacto;
    this.urgencia = urgencia;
    this.prioridad = CalculadorPrioridad.calcular(impacto, urgencia);
    this.estado = EstadoIncidencia.REGISTRADA;
    this.reloj = reloj;
    this.fechaCreacion = LocalDateTime.now(reloj);
}

Incidencia(
        UUID id,
        String titulo,
        String descripcion,
        String categoria,
        Impacto impacto,
        Urgencia urgencia,
        Prioridad prioridad,
        EstadoIncidencia estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaCierre,
        String descripcionSolucion) {

    validarId(id);
    validarTitulo(titulo);
    validarDescripcion(descripcion);
    validarCategoria(categoria);
    validarImpacto(impacto);
    validarUrgencia(urgencia);
    validarPrioridad(prioridad);
    validarEstado(estado);
    validarFechaCreacion(fechaCreacion);
    validarDatosReconstruidos(
            impacto,
            urgencia,
            prioridad,
            estado,
            fechaCreacion,
            fechaCierre,
            descripcionSolucion
    );

    this.id = id;
    this.titulo = titulo.trim();
    this.descripcion = descripcion.trim();
    this.categoria = categoria.trim();
    this.impacto = impacto;
    this.urgencia = urgencia;
    this.prioridad = prioridad;
    this.estado = estado;
    this.fechaCreacion = fechaCreacion;
    this.fechaCierre = fechaCierre;
    this.descripcionSolucion = descripcionSolucion == null
            ? null
            : descripcionSolucion.trim();
    this.reloj = Clock.systemDefaultZone();
}

    private static void validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "El título no puede estar vacío."
            );
        }
    }

    private static void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().length() < 10) {
            throw new IllegalArgumentException(
                    "La descripción debe contener al menos 10 caracteres."
            );
        }
    }

    private static void validarCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException(
                    "La categoría no puede estar vacía."
            );
        }
    }

    private static void validarImpacto(Impacto impacto) {
        if (impacto == null) {
            throw new IllegalArgumentException(
                    "El impacto es obligatorio."
            );
        }
    }

    private static void validarUrgencia(Urgencia urgencia) {
        if (urgencia == null) {
            throw new IllegalArgumentException(
                    "La urgencia es obligatoria."
            );
        }
    }

    private static void validarReloj(Clock reloj) {
        if (reloj == null) {
            throw new IllegalArgumentException(
                    "El reloj es obligatorio."
            );
        }
    }


    private static void validarDescripcionSolucion(String descripcionSolucion) {
        if (descripcionSolucion == null || descripcionSolucion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción de solución es obligatoria."
            );
        }
    }

    private static void validarId(UUID id) {
    if (id == null) {
        throw new IllegalArgumentException(
                "El identificador es obligatorio."
        );
    }
}

private static void validarPrioridad(Prioridad prioridad) {
    if (prioridad == null) {
        throw new IllegalArgumentException(
                "La prioridad es obligatoria."
        );
    }
}

private static void validarEstado(EstadoIncidencia estado) {
    if (estado == null) {
        throw new IllegalArgumentException(
                "El estado es obligatorio."
        );
    }
}

private static void validarFechaCreacion(
        LocalDateTime fechaCreacion) {

    if (fechaCreacion == null) {
        throw new IllegalArgumentException(
                "La fecha de creación es obligatoria."
        );
    }
}

private static void validarDatosReconstruidos(
        Impacto impacto,
        Urgencia urgencia,
        Prioridad prioridad,
        EstadoIncidencia estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaCierre,
        String descripcionSolucion) {

    Prioridad prioridadEsperada =
            CalculadorPrioridad.calcular(impacto, urgencia);

    if (prioridad != prioridadEsperada) {
        throw new IllegalArgumentException(
                "La prioridad almacenada no coincide con impacto y urgencia."
        );
    }

    if (estado == EstadoIncidencia.FINALIZADA) {
        validarDescripcionSolucion(descripcionSolucion);

        if (fechaCierre == null) {
            throw new IllegalArgumentException(
                    "Una incidencia finalizada debe tener fecha de cierre."
            );
        }
    } else if (fechaCierre != null || descripcionSolucion != null) {
        throw new IllegalArgumentException(
                "Una incidencia abierta no debe tener datos de cierre."
        );
    }

    if (fechaCierre != null && fechaCierre.isBefore(fechaCreacion)) {
        throw new IllegalArgumentException(
                "La fecha de cierre no puede ser anterior a la creación."
        );
    }
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

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void avanzarA(EstadoIncidencia nuevoEstado) {
        validarNuevoEstado(nuevoEstado);

        if (nuevoEstado == EstadoIncidencia.FINALIZADA) {
            throw new IllegalStateException(
                    "Para finalizar una incidencia debe indicar una solución."
            );
        }

        validarTransicionConsecutiva(nuevoEstado);
        this.estado = nuevoEstado;
    }

    public void finalizar(String descripcionSolucion) {
        validarDescripcionSolucion(descripcionSolucion);
        validarTransicionConsecutiva(EstadoIncidencia.FINALIZADA);

        this.estado = EstadoIncidencia.FINALIZADA;
        this.descripcionSolucion = descripcionSolucion.trim();
        this.fechaCierre = LocalDateTime.now(reloj);
    }

    private void validarNuevoEstado(EstadoIncidencia nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado es obligatorio."
            );
        }
    }

    private void validarTransicionConsecutiva(EstadoIncidencia nuevoEstado) {
        if (nuevoEstado.ordinal() != estado.ordinal() + 1) {
            throw new IllegalStateException(
                    "Transición inválida de " + estado + " a " + nuevoEstado
                            + ". Solo se permite avanzar al estado siguiente."
            );
        }
    }
}
