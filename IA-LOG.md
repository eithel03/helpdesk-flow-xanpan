# Bitácora de uso de inteligencia artificial

## Propósito

Este archivo registra interacciones relevantes con herramientas de inteligencia artificial utilizadas durante el desarrollo de HelpDesk Flow. Cada resultado fue revisado antes de incorporarse al proyecto.

| Fecha | Herramienta | Objetivo | Resultado usado | Verificación | Cambios humanos |
|---|---|---|---|---|---|
| 2026-07-24 | ChatGPT / Codex | Preparar la configuración inicial del proyecto con Java, Maven y JUnit 5. | Se utilizó una propuesta de estructura con `pom.xml`, clase principal y prueba inicial compatible con Java 17. | Se ejecutaron `mvn clean compile` y `mvn test`. También se revisó manualmente la versión de Java y Maven instalada. | Se ajustó la compilación para usar `release 17`, aunque el equipo tenía Java 22 instalado. También se corrigió un problema de codificación BOM detectado en un archivo Java. |
| 2026-07-27 | ChatGPT / Codex | Implementar y validar la persistencia de incidencias con SQLite. | Se utilizó la recomendación de mantener la interfaz `RepositorioIncidencias` y crear una implementación `RepositorioIncidenciasSQLite`, conservando también el repositorio en memoria. | Se ejecutaron las pruebas de integración de SQLite y la suite completa. Además, se cerró y volvió a abrir la aplicación para comprobar que las incidencias permanecieran guardadas. | Se adaptó la solución al diseño existente, agregó el archivo `.db` al `.gitignore` y verificó manualmente la persistencia, los filtros y las métricas después de reiniciar la aplicación. |
| 2026-07-27 | ChatGPT | Encontrar una forma funcional de ejecutar la aplicación de consola con la dependencia SQLite. | La sugerencia inicial de ejecutar la aplicación con Maven no funcionó correctamente en PowerShell. Se utilizó finalmente un comando `java -cp` con el controlador JDBC de SQLite incluido explícitamente en el classpath. | Se ejecutó la aplicación y se probaron registro, búsqueda, filtros, estados, EXPEDITE, métricas, persistencia y salida controlada. | Se rechazó la ejecución inicial mediante `mvn exec:java` porque presentó problemas de interpretación de parámetros en PowerShell. Se sustituyó por un comando compatible con el entorno real de Windows. |
| 2026-07-28 | ChatGPT / Codex | Seleccionar y aplicar una refactorización segura sin modificar el comportamiento funcional. | Se utilizó la propuesta de extraer el formateo de incidencias desde `ControladorConsola` hacia `FormateadorIncidenciasConsola`. | Antes del cambio había 139 pruebas en verde. Después se ejecutó `mvn clean test` y se obtuvieron 142 pruebas, 0 fallos, 0 errores y `BUILD SUCCESS`. También se realizó una prueba manual de la consola. | Se limitó el alcance a una sola refactorización de bajo riesgo, agregó pruebas de caracterización y documentó el cambio en `REFACTORIZACION.md`. Se rechazó refactorizar el mapeo SQLite porque implicaba mayor riesgo sobre la persistencia. |
| 2026-07-28 | ChatGPT | Completar la documentación final del proyecto. | Se utilizó una propuesta para actualizar `README.md` y estructurar `RETROSPECTIVA.md` e `IA-LOG.md` según los requisitos de la tarea. | Se comparó el contenido con la rúbrica, se verificaron comandos reales, enlace al tablero, cantidad de pruebas y estado de GitHub Actions. | Se corrigió nombres, ajustó la redacción a hechos reales, eliminó textos provisionales y evitó incluir archivos de planificación que no correspondían al entregable. |

## Respuesta de IA modificada por nosotros

La propuesta inicial para ejecutar la aplicación mediante Maven fue modificada porque no funcionó de forma confiable en PowerShell. Se reemplazó esa recomendación por un comando `java -cp` que incluye explícitamente el controlador JDBC de SQLite.

## Sugerencia de IA rechazada

Se rechazó realizar una refactorización adicional sobre el mapeo de `ResultSet` a `Incidencia` dentro de la persistencia SQLite. Aunque era una oportunidad válida, se consideró de mayor riesgo porque podía afectar la reconstrucción de datos y la persistencia ya validada.

## Razón técnica del rechazo

La refactorización de SQLite habría involucrado código relacionado con fechas, enumeraciones, campos opcionales y reconstrucción completa de incidencias. Como la persistencia ya estaba cubierta por pruebas y funcionaba correctamente, se prefirió una mejora de menor riesgo en la capa de presentación de consola.

## Forma de verificación

Los resultados utilizados fueron verificados mediante:

- Revisión manual del código.
- Ejecución de `mvn clean compile`.
- Ejecución de `mvn clean test`.
- Pruebas específicas con JUnit 5.
- Pruebas manuales de la interfaz de consola.
- Verificación de persistencia después de cerrar y volver a ejecutar la aplicación.
- Revisión de Pull Requests por el otro integrante.
- Confirmación de GitHub Actions en verde.
