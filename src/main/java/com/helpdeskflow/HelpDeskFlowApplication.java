package com.helpdeskflow;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class HelpDeskFlowApplication {

    public static void main(String[] args) {
        Path archivoBaseDatos =
                Path.of("helpdesk-flow.db");

        RepositorioIncidencias repositorio =
                new RepositorioIncidenciasSQLite(
                        archivoBaseDatos
                );

        ControladorConsola controlador =
                new ControladorConsola(repositorio);

        MenuConsola menu = new MenuConsola(
                new InputStreamReader(
                        System.in,
                        StandardCharsets.UTF_8
                ),
                System.out,
                controlador
        );

        menu.ejecutar();
    }
}