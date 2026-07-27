package com.helpdeskflow;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class InicializadorBaseDatos {

    private static final String CREAR_TABLA = """
            CREATE TABLE IF NOT EXISTS incidencias (
                orden INTEGER PRIMARY KEY AUTOINCREMENT,
                id TEXT NOT NULL UNIQUE,
                titulo TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                categoria TEXT NOT NULL,
                impacto TEXT NOT NULL,
                urgencia TEXT NOT NULL,
                prioridad TEXT NOT NULL,
                estado TEXT NOT NULL,
                fecha_creacion TEXT NOT NULL,
                fecha_cierre TEXT,
                descripcion_solucion TEXT
            )
            """;

    private final ConexionSQLite conexionSQLite;

    InicializadorBaseDatos(ConexionSQLite conexionSQLite) {
        if (conexionSQLite == null) {
            throw new IllegalArgumentException(
                    "La conexión SQLite es obligatoria."
            );
        }

        this.conexionSQLite = conexionSQLite;
    }

    void inicializar() {
        try (Connection conexion = conexionSQLite.abrir();
             Statement sentencia = conexion.createStatement()) {

            sentencia.executeUpdate(CREAR_TABLA);

        } catch (SQLException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible inicializar la base de datos.",
                    excepcion
            );
        }
    }
}