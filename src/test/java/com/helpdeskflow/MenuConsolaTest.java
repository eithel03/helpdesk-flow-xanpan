package com.helpdeskflow;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class MenuConsolaTest {

        @Test
        void debeMostrarMenuPrincipalYSalirDeFormaControlada() {
                StringReader entrada = new StringReader("12\n");
                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                entrada,
                                new PrintStream(salidaCapturada));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("HELPDESK FLOW"));
                assertTrue(salida.contains("1. Registrar incidencia"));
                assertTrue(salida.contains("12. Salir"));
                assertTrue(salida.contains("Aplicación finalizada"));
        }

        @Test
        void debeInformarOpcionInvalidaYContinuarHastaSalir() {
                StringReader entrada = new StringReader(
                                "99\n12\n");

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                entrada,
                                new PrintStream(salidaCapturada));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("Opción inválida"));
                assertTrue(salida.contains("Aplicación finalizada"));
        }

        @Test
        void debeRegistrarUnaIncidenciaValidaDesdeElMenu() {
                String entradaUsuario = String.join(
                                "\n",
                                "1",
                                "Servidor sin conexión",
                                "El servidor principal no responde solicitudes.",
                                "Servidores",
                                "ALTO",
                                "ALTA",
                                "12") + "\n";

                StringReader entrada = new StringReader(entradaUsuario);

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                ControladorConsola controlador = new ControladorConsola(repositorio);

                MenuConsola menu = new MenuConsola(
                                entrada,
                                new PrintStream(salidaCapturada),
                                controlador);

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "Incidencia registrada correctamente."));

                assertTrue(
                                salida.contains(
                                                "Prioridad calculada: CRITICA"));

                assertTrue(repositorio.listarTodas().size() == 1);
        }

        @Test
        void debeInformarErrorAlRegistrarDatosInvalidosYContinuar() {
                String entradaUsuario = String.join(
                                "\n",
                                "1",
                                "",
                                "Descripción suficientemente extensa.",
                                "Hardware",
                                "BAJO",
                                "BAJA",
                                "12") + "\n";

                StringReader entrada = new StringReader(entradaUsuario);

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                MenuConsola menu = new MenuConsola(
                                entrada,
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "Error: El título no puede estar vacío."));

                assertTrue(
                                salida.contains(
                                                "Aplicación finalizada correctamente."));

                assertTrue(repositorio.listarTodas().isEmpty());
        }

        @Test
        void debeListarLasIncidenciasRegistradas() {
                String entradaUsuario = String.join(
                                "\n",
                                "1",
                                "Problema de monitor",
                                "El monitor principal no muestra ninguna imagen.",
                                "Hardware",
                                "BAJO",
                                "BAJA",
                                "2",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("LISTADO DE INCIDENCIAS"));
                assertTrue(salida.contains("Problema de monitor"));
                assertTrue(salida.contains("Prioridad: NORMAL"));
                assertTrue(salida.contains("Estado: REGISTRADA"));
        }

        @Test
        void debeBuscarUnaIncidenciaPorIdentificador() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Error de red",
                                "La conexión de red presenta interrupciones.",
                                "Redes",
                                Impacto.MEDIO,
                                Urgencia.MEDIA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "3",
                                incidencia.getId().toString(),
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("BUSCAR INCIDENCIA"));
                assertTrue(salida.contains("Error de red"));
                assertTrue(
                                salida.contains(incidencia.getId().toString()));
        }

        @Test
        void debeInformarCuandoElIdentificadorNoExiste() {
                String entradaUsuario = String.join(
                                "\n",
                                "3",
                                UUID.randomUUID().toString(),
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(
                                                new RepositorioIncidenciasMemoria()));

                menu.ejecutar();

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "No se encontró una incidencia"));
        }

        @Test
        void debeInformarIdentificadorConFormatoInvalido() {
                String entradaUsuario = String.join(
                                "\n",
                                "3",
                                "identificador-invalido",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(
                                                new RepositorioIncidenciasMemoria()));

                menu.ejecutar();

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "El identificador ingresado no tiene un formato válido."));
        }

        @Test
        void debeFiltrarIncidenciasPorEstado() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Teclado dañado",
                                "El teclado dejó de responder completamente.",
                                "Hardware",
                                Impacto.BAJO,
                                Urgencia.BAJA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "4",
                                "REGISTRADA",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("FILTRAR POR ESTADO"));
                assertTrue(salida.contains("Teclado dañado"));
        }

        @Test
        void debeFiltrarIncidenciasPorPrioridad() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Servidor detenido",
                                "El servidor no responde desde la madrugada.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "5",
                                "CRITICA",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("FILTRAR POR PRIORIDAD"));
                assertTrue(salida.contains("Servidor detenido"));
                assertTrue(salida.contains("Prioridad: CRITICA"));
        }

        @Test
        void debeMostrarIncidenciasAbiertas() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Impresora sin papel",
                                "La impresora principal no tiene papel disponible.",
                                "Impresoras",
                                Impacto.BAJO,
                                Urgencia.BAJA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "6",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(salida.contains("INCIDENCIAS ABIERTAS"));
                assertTrue(salida.contains("Impresora sin papel"));
        }

        @Test
        void debeMostrarMensajeCuandoNoHayFinalizadas() {
                String entradaUsuario = String.join(
                                "\n",
                                "7",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(
                                                new RepositorioIncidenciasMemoria()));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "No existen incidencias para mostrar."));
        }

        @Test
        void debeAvanzarUnaIncidenciaAlEstadoSiguiente() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Problema de impresora",
                                "La impresora no responde al enviar documentos.",
                                "Hardware",
                                Impacto.BAJO,
                                Urgencia.BAJA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "8",
                                incidencia.getId().toString(),
                                "LISTA",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                assertEquals(
                                EstadoIncidencia.LISTA,
                                incidencia.getEstado());

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "Estado actualizado correctamente."));
        }

        @Test
        void debeInformarUnaTransicionInvalida() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Problema de impresora",
                                "La impresora no responde al enviar documentos.",
                                "Hardware",
                                Impacto.BAJO,
                                Urgencia.BAJA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "8",
                                incidencia.getId().toString(),
                                "EN_DESARROLLO",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "Transición inválida"));

                assertEquals(
                                EstadoIncidencia.REGISTRADA,
                                incidencia.getEstado());
        }

        @Test
        void debeFinalizarUnaIncidenciaConSolucion() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Problema de servidor",
                                "El servidor presenta una falla de comunicación.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);

                incidencia.avanzarA(EstadoIncidencia.LISTA);
                incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);
                incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "9",
                                incidencia.getId().toString(),
                                "Se restauró el servicio correctamente.",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                assertEquals(
                                EstadoIncidencia.FINALIZADA,
                                incidencia.getEstado());

                assertEquals(
                                "Se restauró el servicio correctamente.",
                                incidencia.getDescripcionSolucion());

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "Incidencia finalizada correctamente."));
        }

        @Test
        void debeRechazarFinalizacionSinSolucion() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Problema de servidor",
                                "El servidor presenta una falla de comunicación.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);

                incidencia.avanzarA(EstadoIncidencia.LISTA);
                incidencia.avanzarA(EstadoIncidencia.EN_DESARROLLO);
                incidencia.avanzarA(EstadoIncidencia.EN_VALIDACION);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "9",
                                incidencia.getId().toString(),
                                "",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                assertEquals(
                                EstadoIncidencia.EN_VALIDACION,
                                incidencia.getEstado());

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "La descripción de solución es obligatoria."));
        }

        @Test
        void debeMarcarUnaIncidenciaCriticaComoExpedite() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Servidor principal detenido",
                                "El servidor principal no responde solicitudes.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "10",
                                "1",
                                incidencia.getId().toString(),
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                assertTrue(incidencia.esExpedite());

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "Incidencia marcada como EXPEDITE correctamente."));
        }

        @Test
        void debeRechazarExpediteEnIncidenciaNoCritica() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Problema de monitor",
                                "El monitor principal no muestra ninguna imagen.",
                                "Hardware",
                                Impacto.BAJO,
                                Urgencia.BAJA);

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "10",
                                "1",
                                incidencia.getId().toString(),
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                assertFalse(incidencia.esExpedite());

                assertTrue(
                                salidaCapturada.toString().contains(
                                                "Solo una incidencia crítica puede marcarse como EXPEDITE."));
        }

        @Test
        void debeMostrarLasIncidenciasExpedite() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Servidor principal detenido",
                                "El servidor principal no responde solicitudes.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);

                incidencia.marcarComoExpedite();
                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "10",
                                "2",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "INCIDENCIAS EXPEDITE"));

                assertTrue(
                                salida.contains(
                                                "Servidor principal detenido"));

                assertTrue(
                                salida.contains(
                                                "EXPEDITE: Sí"));
        }

        @Test
        void debeMostrarMetricasBasicas() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                repositorio.guardar(
                                new Incidencia(
                                                "Problema de monitor",
                                                "El monitor principal no muestra imagen.",
                                                "Hardware",
                                                Impacto.BAJO,
                                                Urgencia.BAJA));

                repositorio.guardar(
                                new Incidencia(
                                                "Servidor detenido",
                                                "El servidor principal no responde solicitudes.",
                                                "Servidores",
                                                Impacto.ALTO,
                                                Urgencia.ALTA));

                String entradaUsuario = String.join(
                                "\n",
                                "11",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "MÉTRICAS DE INCIDENCIAS"));

                assertTrue(
                                salida.contains(
                                                "Total de incidencias: 2"));

                assertTrue(
                                salida.contains(
                                                "Incidencias abiertas: 2"));

                assertTrue(
                                salida.contains(
                                                "Incidencias finalizadas: 0"));

                assertTrue(
                                salida.contains(
                                                "- NORMAL: 1"));

                assertTrue(
                                salida.contains(
                                                "- CRITICA: 1"));

                assertTrue(
                                salida.contains(
                                                "Throughput del periodo registrado: 0"));
        }

        @Test
        void debeMostrarThroughputYLeadTimeConIncidenciaFinalizada() {
                RepositorioIncidencias repositorio = new RepositorioIncidenciasMemoria();

                Incidencia incidencia = new Incidencia(
                                "Servidor detenido",
                                "El servidor principal no responde solicitudes.",
                                "Servidores",
                                Impacto.ALTO,
                                Urgencia.ALTA);

                incidencia.avanzarA(
                                EstadoIncidencia.LISTA);

                incidencia.avanzarA(
                                EstadoIncidencia.EN_DESARROLLO);

                incidencia.avanzarA(
                                EstadoIncidencia.EN_VALIDACION);

                incidencia.finalizar(
                                "Se restauró correctamente el servicio.");

                repositorio.guardar(incidencia);

                String entradaUsuario = String.join(
                                "\n",
                                "11",
                                "12") + "\n";

                ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();

                MenuConsola menu = new MenuConsola(
                                new StringReader(entradaUsuario),
                                new PrintStream(salidaCapturada),
                                new ControladorConsola(repositorio));

                menu.ejecutar();

                String salida = salidaCapturada.toString();

                assertTrue(
                                salida.contains(
                                                "Incidencias finalizadas: 1"));

                assertTrue(
                                salida.contains(
                                                "Lead time promedio:"));

                assertTrue(
                                salida.contains(
                                                "Throughput del periodo registrado: 1"));
        }

}