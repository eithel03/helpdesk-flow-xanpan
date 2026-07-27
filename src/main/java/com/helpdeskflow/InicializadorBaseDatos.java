package com.helpdeskflow;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

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
                descripcion_solucion TEXT,
                expedite INTEGER NOT NULL DEFAULT 0
            )
            """;

    private static final String AGREGAR_COLUMNA_EXPEDITE = """
            ALTER TABLE incidencias
            ADD COLUMN expedite INTEGER NOT NULL DEFAULT 0
            """;

    private final ConexionSQLite conexionSQLite;

    InicializadorBaseDatos(ConexionSQLite conexionSQLite) {
        if (conexionSQLite == null) {
            throw new IllegalArgumentException(
                    "La conexión SQLite es obligatoria.");
        }

        this.conexionSQLite = conexionSQLite;
    }

    void inicializar() {
        try (Connection conexion = conexionSQLite.abrir();
                Statement sentencia = conexion.createStatement()) {

            sentencia.executeUpdate(CREAR_TABLA);

            if (!existeColumnaExpedite(conexion)) {
                sentencia.executeUpdate(AGREGAR_COLUMNA_EXPEDITE);
            }

        } catch (SQLException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible inicializar la base de datos.",
                    excepcion);
        }
    }

    private boolean existeColumnaExpedite(
            Connection conexion) throws SQLException {

        try (Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(
                        "PRAGMA table_info(incidencias)")) {

            while (resultado.next()) {
                if ("expedite".equalsIgnoreCase(
                        resultado.getString("name"))) {
                    return true;
                }
            }

            return false;
        }
    }

}