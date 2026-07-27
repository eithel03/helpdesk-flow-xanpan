package com.helpdeskflow;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class ConexionSQLite {

    private final String urlConexion;

    ConexionSQLite(Path archivoBaseDatos) {
        if (archivoBaseDatos == null) {
            throw new IllegalArgumentException(
                    "La ruta de la base de datos es obligatoria."
            );
        }

        Path rutaAbsoluta = archivoBaseDatos.toAbsolutePath().normalize();
        this.urlConexion = "jdbc:sqlite:" + rutaAbsoluta;
    }

    Connection abrir() throws SQLException {
        return DriverManager.getConnection(urlConexion);
    }
}