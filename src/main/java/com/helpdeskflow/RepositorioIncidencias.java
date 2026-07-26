package com.helpdeskflow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioIncidencias {

    void guardar(Incidencia incidencia);

    Optional<Incidencia> buscarPorId(UUID id);

    List<Incidencia> listarTodas();

    List<Incidencia> filtrarPorEstado(EstadoIncidencia estado);

    List<Incidencia> filtrarPorPrioridad(Prioridad prioridad);
}
