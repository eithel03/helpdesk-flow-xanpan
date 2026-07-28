package com.helpdeskflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class MenuConsola {

        private final BufferedReader entrada;
        private final PrintStream salida;
        private final ControladorConsola controlador;
        private final FormateadorIncidenciasConsola formateadorIncidencias;
        private boolean ejecutando;

        public MenuConsola(
                        Reader entrada,
                        PrintStream salida) {
                this(
                                entrada,
                                salida,
                                new ControladorConsola(
                                                new RepositorioIncidenciasMemoria()));
        }

        public MenuConsola(
                        Reader entrada,
                        PrintStream salida,
                        ControladorConsola controlador) {

                if (entrada == null) {
                        throw new IllegalArgumentException(
                                        "La entrada de consola es obligatoria.");
                }

                if (salida == null) {
                        throw new IllegalArgumentException(
                                        "La salida de consola es obligatoria.");
                }
                if (controlador == null) {
                        throw new IllegalArgumentException(
                                        "El controlador de consola es obligatorio.");
                }

                this.entrada = new BufferedReader(entrada);
                this.salida = salida;
                this.controlador = controlador;
                this.formateadorIncidencias = new FormateadorIncidenciasConsola();

        }

        public void ejecutar() {
                ejecutando = true;

                while (ejecutando) {
                        mostrarMenu();

                        String opcion = leerLinea();

                        procesarOpcion(opcion);
                }
        }

        private void mostrarMenu() {
                salida.println();
                salida.println("HELPDESK FLOW");
                salida.println();
                salida.println("1. Registrar incidencia");
                salida.println("2. Listar incidencias");
                salida.println("3. Buscar incidencia por identificador");
                salida.println("4. Filtrar por estado");
                salida.println("5. Filtrar por prioridad");
                salida.println("6. Mostrar incidencias abiertas");
                salida.println("7. Mostrar incidencias finalizadas");
                salida.println("8. Avanzar estado de una incidencia");
                salida.println("9. Finalizar incidencia");
                salida.println("10. Marcar o gestionar EXPEDITE");
                salida.println("11. Mostrar métricas");
                salida.println("12. Salir");
                salida.print("Seleccione una opción: ");
        }

        private void procesarOpcion(String opcion) {
                try {
                        switch (opcion) {
                                case "1" -> registrarIncidencia();
                                case "2" -> listarIncidencias();
                                case "3" -> buscarIncidenciaPorIdentificador();
                                case "4" -> filtrarIncidenciasPorEstado();
                                case "5" -> filtrarIncidenciasPorPrioridad();
                                case "6" -> mostrarIncidenciasAbiertas();
                                case "7" -> mostrarIncidenciasFinalizadas();
                                case "8" -> avanzarEstadoIncidencia();
                                case "9" -> finalizarIncidencia();
                                case "10" -> gestionarExpedite();
                                case "11" -> mostrarMetricas();
                                case "12" -> finalizarAplicacion();
                                default -> salida.println(
                                                "Opción inválida. Seleccione un número entre 1 y 12.");
                        }
                } catch (IllegalArgumentException | IllegalStateException excepcion) {
                        salida.println("Error: " + excepcion.getMessage());
                }
        }

        private void registrarIncidencia() {
                salida.println();
                salida.println("REGISTRAR INCIDENCIA");

                salida.print("Título: ");
                String titulo = leerLinea();

                salida.print("Descripción: ");
                String descripcion = leerLinea();

                salida.print("Categoría: ");
                String categoria = leerLinea();

                salida.print("Impacto (BAJO, MEDIO, ALTO): ");
                String impacto = leerLinea();

                salida.print("Urgencia (BAJA, MEDIA, ALTA): ");
                String urgencia = leerLinea();

                Incidencia incidencia = controlador.registrarIncidencia(
                                titulo,
                                descripcion,
                                categoria,
                                impacto,
                                urgencia);

                salida.println("Incidencia registrada correctamente.");
                salida.println("Identificador: " + incidencia.getId());
                salida.println(
                                "Prioridad calculada: "
                                                + incidencia.getPrioridad());
        }

        private void listarIncidencias() {
                salida.println();
                salida.println("LISTADO DE INCIDENCIAS");

                formateadorIncidencias.mostrarIncidencias(
                                controlador.listarTodas(),
                                salida);
        }

        private void finalizarAplicacion() {
                ejecutando = false;
                salida.println("Aplicación finalizada correctamente.");
        }

        private String leerLinea() {
                try {
                        String valor = entrada.readLine();

                        if (valor == null) {
                                ejecutando = false;
                                return "12";
                        }

                        return valor.trim();

                } catch (IOException excepcion) {
                        throw new IllegalStateException(
                                        "No fue posible leer la entrada de consola.",
                                        excepcion);
                }
        }

        private void buscarIncidenciaPorIdentificador() {
                salida.println();
                salida.println("BUSCAR INCIDENCIA");

                salida.print("Identificador: ");
                String identificador = leerLinea();

                controlador.buscarPorIdentificador(identificador)
                                .ifPresentOrElse(
                                                incidencia -> formateadorIncidencias.mostrarIncidencia(
                                                                incidencia,
                                                                salida),
                                                () -> salida.println(
                                                                "No se encontró una incidencia "
                                                                                + "con ese identificador."));
        }

        private void filtrarIncidenciasPorEstado() {
                salida.println();
                salida.println("FILTRAR POR ESTADO");

                salida.println(
                                "Estados disponibles: REGISTRADA, LISTA, "
                                                + "EN_DESARROLLO, EN_VALIDACION, FINALIZADA");

                salida.print("Estado: ");
                String estado = leerLinea();

                formateadorIncidencias.mostrarIncidencias(
                                controlador.filtrarPorEstado(estado),
                                salida);
        }

        private void filtrarIncidenciasPorPrioridad() {
                salida.println();
                salida.println("FILTRAR POR PRIORIDAD");

                salida.println(
                                "Prioridades disponibles: NORMAL, ALTA, CRITICA");

                salida.print("Prioridad: ");
                String prioridad = leerLinea();

                formateadorIncidencias.mostrarIncidencias(
                                controlador.filtrarPorPrioridad(prioridad),
                                salida);
        }

        private void mostrarIncidenciasAbiertas() {
                salida.println();
                salida.println("INCIDENCIAS ABIERTAS");

                formateadorIncidencias.mostrarIncidencias(
                                controlador.listarAbiertas(),
                                salida);
        }

        private void mostrarIncidenciasFinalizadas() {
                salida.println();
                salida.println("INCIDENCIAS FINALIZADAS");

                formateadorIncidencias.mostrarIncidencias(
                                controlador.listarFinalizadas(),
                                salida);
        }

        private void avanzarEstadoIncidencia() {
                salida.println();
                salida.println("AVANZAR ESTADO DE INCIDENCIA");

                salida.print("Identificador: ");
                String identificador = leerLinea();

                salida.println(
                                "Estados permitidos para avance: "
                                                + "LISTA, EN_DESARROLLO, EN_VALIDACION");

                salida.print("Nuevo estado: ");
                String nuevoEstado = leerLinea();

                Incidencia incidencia = controlador.avanzarEstado(
                                identificador,
                                nuevoEstado);

                salida.println(
                                "Estado actualizado correctamente.");

                salida.println(
                                "Nuevo estado: "
                                                + incidencia.getEstado());
        }

        private void finalizarIncidencia() {
                salida.println();
                salida.println("FINALIZAR INCIDENCIA");

                salida.print("Identificador: ");
                String identificador = leerLinea();

                salida.print("Descripción de la solución: ");
                String descripcionSolucion = leerLinea();

                Incidencia incidencia = controlador.finalizarIncidencia(
                                identificador,
                                descripcionSolucion);

                salida.println(
                                "Incidencia finalizada correctamente.");

                salida.println(
                                "Fecha de cierre: "
                                                + incidencia.getFechaCierre());
        }

        private void gestionarExpedite() {
                salida.println();
                salida.println("GESTIÓN EXPEDITE");
                salida.println();
                salida.println("1. Marcar incidencia como EXPEDITE");
                salida.println("2. Mostrar incidencias EXPEDITE");
                salida.println("3. Regresar al menú principal");
                salida.print("Seleccione una opción: ");

                String opcion = leerLinea();

                switch (opcion) {
                        case "1" -> marcarIncidenciaComoExpedite();
                        case "2" -> mostrarIncidenciasExpedite();
                        case "3" -> salida.println(
                                        "Regresando al menú principal.");
                        default -> salida.println(
                                        "Opción EXPEDITE inválida.");
                }
        }

        private void marcarIncidenciaComoExpedite() {
                salida.println();
                salida.println("MARCAR INCIDENCIA COMO EXPEDITE");

                salida.print("Identificador: ");
                String identificador = leerLinea();

                Incidencia incidencia = controlador.marcarComoExpedite(
                                identificador);

                salida.println(
                                "Incidencia marcada como EXPEDITE correctamente.");

                salida.println(
                                "Identificador: "
                                                + incidencia.getId());

                salida.println(
                                "Prioridad: "
                                                + incidencia.getPrioridad());
        }

        private void mostrarIncidenciasExpedite() {
                salida.println();
                salida.println("INCIDENCIAS EXPEDITE");

                formateadorIncidencias.mostrarIncidencias(
                                controlador.listarExpedite(),
                                salida);
        }

        private void mostrarMetricas() {
                salida.println();
                salida.println("MÉTRICAS DE INCIDENCIAS");
                salida.println();

                salida.println(
                                "Total de incidencias: "
                                                + controlador.obtenerCantidadTotal());

                salida.println(
                                "Incidencias abiertas: "
                                                + controlador.obtenerCantidadAbiertas());

                salida.println(
                                "Incidencias finalizadas: "
                                                + controlador.obtenerCantidadFinalizadas());

                salida.println();
                salida.println("Cantidad por prioridad:");

                controlador.obtenerCantidadPorPrioridad()
                                .forEach(
                                                (prioridad, cantidad) -> salida.println(
                                                                "- "
                                                                                + prioridad
                                                                                + ": "
                                                                                + cantidad));

                salida.println();

                controlador.calcularLeadTimePromedio()
                                .ifPresentOrElse(
                                                this::mostrarLeadTime,
                                                () -> salida.println(
                                                                "Lead time promedio: "
                                                                                + "No disponible, no existen "
                                                                                + "incidencias finalizadas."));

                mostrarThroughputTotalRegistrado();
        }

        private void mostrarLeadTime(Duration duracion) {
                long horas = duracion.toHours();
                long minutos = duracion.toMinutesPart();
                long segundos = duracion.toSecondsPart();

                salida.println(
                                "Lead time promedio: "
                                                + horas
                                                + " horas, "
                                                + minutos
                                                + " minutos y "
                                                + segundos
                                                + " segundos.");
        }

        private void mostrarThroughputTotalRegistrado() {
                List<Incidencia> incidencias = controlador.listarTodas();

                if (incidencias.isEmpty()) {
                        salida.println(
                                        "Throughput del periodo registrado: 0");
                        return;
                }

                LocalDateTime fechaInicio = incidencias.stream()
                                .map(Incidencia::getFechaCreacion)
                                .min(LocalDateTime::compareTo)
                                .orElse(LocalDateTime.now());

                LocalDateTime fechaFin = LocalDateTime.now();

                long throughput = controlador.calcularThroughput(
                                fechaInicio,
                                fechaFin);

                salida.println(
                                "Throughput del periodo registrado: "
                                                + throughput);
        }

}