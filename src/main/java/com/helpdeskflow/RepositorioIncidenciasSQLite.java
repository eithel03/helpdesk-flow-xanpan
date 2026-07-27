package com.helpdeskflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RepositorioIncidenciasSQLite
        implements RepositorioIncidencias {

    private final ConexionSQLite conexionSQLite;

    public RepositorioIncidenciasSQLite(Path archivoBaseDatos) {
        prepararDirectorio(archivoBaseDatos);

        this.conexionSQLite = new ConexionSQLite(archivoBaseDatos);
        new InicializadorBaseDatos(conexionSQLite).inicializar();
    }

    private static void prepararDirectorio(Path archivoBaseDatos) {
        if (archivoBaseDatos == null) {
            throw new IllegalArgumentException(
                    "La ruta de la base de datos es obligatoria."
            );
        }

        Path rutaAbsoluta = archivoBaseDatos.toAbsolutePath().normalize();
        Path directorio = rutaAbsoluta.getParent();

        if (directorio == null) {
            return;
        }

        try {
            Files.createDirectories(directorio);
        } catch (IOException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible crear el directorio de la base de datos.",
                    excepcion
            );
        }
    }

    @Override
    public void guardar(Incidencia incidencia) {
        throw new UnsupportedOperationException("Pendiente.");
    }

    @Override
    public Optional<Incidencia> buscarPorId(UUID id) {
        throw new UnsupportedOperationException("Pendiente.");
    }

    @Override
    public List<Incidencia> listarTodas() {
        throw new UnsupportedOperationException("Pendiente.");
    }

    @Override
    public List<Incidencia> listarAbiertas() {
        throw new UnsupportedOperationException("Pendiente.");
    }

    @Override
    public List<Incidencia> listarFinalizadas() {
        throw new UnsupportedOperationException("Pendiente.");
    }

    @Override
    public List<Incidencia> filtrarPorEstado(
            EstadoIncidencia estado) {
        throw new UnsupportedOperationException("Pendiente.");
    }

    @Override
    public List<Incidencia> filtrarPorPrioridad(
            Prioridad prioridad) {
        throw new UnsupportedOperationException("Pendiente.");
    }
}