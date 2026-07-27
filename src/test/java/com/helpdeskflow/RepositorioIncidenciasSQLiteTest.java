package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositorioIncidenciasSQLiteTest {

    @TempDir
    Path directorioTemporal;

    @Test
    void debeCrearArchivoDeBaseDatosAutomaticamente() {
        Path archivoBaseDatos =
                directorioTemporal.resolve("helpdesk-test.db");

        new RepositorioIncidenciasSQLite(archivoBaseDatos);

        assertTrue(Files.exists(archivoBaseDatos));
    }
}