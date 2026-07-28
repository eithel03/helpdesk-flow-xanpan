# HelpDesk Flow

Sistema de gestión de incidencias técnicas desarrollado como parte de la tarea programativa del curso **ITI-822 Metodologías Ágiles de Desarrollo de Software**.

## Descripción del sistema

HelpDesk Flow es una aplicación de consola para registrar, priorizar, consultar, atender, validar y finalizar incidencias técnicas.

El sistema permite:

- Registrar incidencias con título, descripción, categoría, impacto y urgencia.
- Calcular automáticamente la prioridad.
- Gestionar el flujo de estados de una incidencia.
- Buscar y filtrar incidencias.
- Consultar incidencias abiertas y finalizadas.
- Generar métricas básicas.
- Marcar incidencias críticas como EXPEDITE.
- Conservar la información mediante una base de datos SQLite.
- Ejecutar las funcionalidades desde una interfaz de consola.

El flujo principal de una incidencia es:

```text
REGISTRADA
    ↓
LISTA
    ↓
EN_DESARROLLO
    ↓
EN_VALIDACION
    ↓
FINALIZADA
```

El proyecto utiliza Xanpan, combinando Kanban para administrar el flujo de trabajo con prácticas de Extreme Programming, como desarrollo dirigido por pruebas, programación en pareja, integración continua y refactorización.

La inteligencia artificial se utilizó como herramienta de asistencia técnica. Las interacciones relevantes fueron revisadas, verificadas y documentadas por los integrantes.

## Integrantes

- Eithel Herrera Rojas
- Luis Diego Chavala González

## Información académica

- **Universidad:** Universidad Técnica Nacional
- **Sede:** San Carlos
- **Curso:** ITI-822 Metodologías Ágiles de Desarrollo de Software
- **Docente:** Andrés Joseph Jiménez Leandro
- **Modalidad:** Pareja de dos estudiantes

## Tecnologías utilizadas

- Java 17
- Maven
- JUnit 5
- SQLite
- JDBC
- Git
- GitHub
- GitHub Actions
- GitHub Projects

## Requisitos de ejecución

Para compilar, probar y ejecutar el proyecto se requiere:

- JDK 17 o una versión superior.
- Apache Maven 3.9 o una versión compatible.
- Git, para clonar y administrar el repositorio.
- Una terminal como PowerShell, Git Bash o la terminal integrada de Visual Studio Code.

Para verificar las instalaciones:

```bash
java -version
mvn -version
git --version
```

Java debe mostrar una versión 17 o superior.

## Obtener el proyecto

Clonar el repositorio:

```bash
git clone https://github.com/eithel03/helpdesk-flow-xanpan.git
```

Entrar en la carpeta:

```bash
cd helpdesk-flow-xanpan
```

## Compilación

Para limpiar y compilar el proyecto:

```bash
mvn clean compile
```

Resultado esperado:

```text
BUILD SUCCESS
```

Para generar el paquete y ejecutar también las pruebas:

```bash
mvn clean package
```

El resultado generado se almacena en la carpeta:

```text
target/
```

## Ejecución de pruebas

Para ejecutar toda la suite automatizada:

```bash
mvn clean test
```

Resultado validado:

```text
Tests run: 142
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Las pruebas cubren, entre otros aspectos:

- Registro y validación de incidencias.
- Cálculo de prioridad.
- Transiciones válidas e inválidas.
- Cierre con descripción de solución.
- Consultas y filtros.
- Métricas.
- Persistencia SQLite.
- Reglas EXPEDITE.
- Interfaz de consola.
- Formateo de incidencias.

## Ejecución de la aplicación

Primero se debe compilar el proyecto:

```bash
mvn clean package
```

### Windows PowerShell

Ejecutar:

```powershell
java -cp "target/classes;$env:USERPROFILE\.m2\repository\org\xerial\sqlite-jdbc\3.53.2.0\sqlite-jdbc-3.53.2.0.jar" com.helpdeskflow.HelpDeskFlowApplication
```

### Git Bash

Ejecutar:

```bash
java -cp "target/classes:$HOME/.m2/repository/org/xerial/sqlite-jdbc/3.53.2.0/sqlite-jdbc-3.53.2.0.jar" com.helpdeskflow.HelpDeskFlowApplication
```

Al iniciar, se muestra el menú principal:

```text
HELPDESK FLOW

1. Registrar incidencia
2. Listar incidencias
3. Buscar incidencia por identificador
4. Filtrar por estado
5. Filtrar por prioridad
6. Mostrar incidencias abiertas
7. Mostrar incidencias finalizadas
8. Avanzar estado de una incidencia
9. Finalizar incidencia
10. Marcar o gestionar EXPEDITE
11. Mostrar métricas
12. Salir
```

La aplicación crea automáticamente el archivo local:

```text
helpdesk-flow.db
```

Este archivo almacena las incidencias mediante SQLite y permite conservar los datos después de cerrar la aplicación.

## Funcionalidades implementadas

### HU-01: Registrar una incidencia

Permite registrar incidencias con los datos requeridos y valida entradas inválidas.

### HU-02: Calcular automáticamente la prioridad

La prioridad se calcula según el impacto y la urgencia:

| Impacto | Urgencia | Prioridad |
|---|---|---|
| ALTO | ALTA | CRITICA |
| ALTO | MEDIA o BAJA | ALTA |
| MEDIO o BAJO | ALTA | ALTA |
| Otras combinaciones | Otras combinaciones | NORMAL |

### HU-03: Gestionar el flujo de la incidencia

Solo se permiten transiciones consecutivas y no se puede finalizar una incidencia sin registrar una solución.

### HU-04: Consultar y filtrar incidencias

Permite:

- Listar todas las incidencias.
- Buscar por identificador.
- Filtrar por estado.
- Filtrar por prioridad.
- Mostrar incidencias abiertas.
- Mostrar incidencias finalizadas.

### HU-05: Generar métricas básicas

El sistema calcula:

- Total de incidencias.
- Incidencias abiertas.
- Incidencias finalizadas.
- Cantidad por prioridad.
- Throughput.
- Lead time promedio.

### EXPEDITE

Una incidencia crítica puede marcarse como EXPEDITE.

Solo puede existir una incidencia EXPEDITE simultáneamente en los estados:

- `EN_DESARROLLO`
- `EN_VALIDACION`

Cuando la incidencia EXPEDITE activa avanza o finaliza, otra incidencia EXPEDITE puede continuar.

## Decisiones principales de diseño

### Java 17 y Maven

Java 17 se utiliza como versión base del proyecto. Maven administra las dependencias, la compilación y la ejecución de pruebas.

### Arquitectura basada en responsabilidades

El sistema separa las responsabilidades entre:

- Entidades del dominio.
- Reglas de prioridad.
- Reglas de transición.
- Repositorios.
- Servicios de métricas.
- Servicio EXPEDITE.
- Persistencia.
- Interfaz y formateo de consola.

### Patrón Repository

Se definió la interfaz `RepositorioIncidencias` para desacoplar la lógica del sistema del mecanismo de almacenamiento.

Existen dos implementaciones:

- `RepositorioIncidenciasMemoria`, utilizada principalmente en pruebas.
- `RepositorioIncidenciasSQLite`, utilizada para conservar datos localmente.

### Persistencia con SQLite

SQLite fue seleccionado porque:

- No requiere instalar un servidor.
- Almacena la información en un archivo local.
- Se integra con Java mediante JDBC.
- Facilita la demostración y ejecución del proyecto.

La base de datos y la tabla de incidencias se crean automáticamente.

### Uso de enumeraciones

Se utilizan enumeraciones para representar valores controlados como:

- Impacto.
- Urgencia.
- Prioridad.
- Estado.

Esto reduce errores causados por valores escritos incorrectamente.

### Identificadores únicos

Cada incidencia utiliza un identificador UUID generado automáticamente.

### Desarrollo dirigido por pruebas

Las reglas principales se protegieron mediante pruebas automatizadas con JUnit 5.

Se aplicaron ciclos de:

```text
RED → GREEN → REFACTOR
```

### Refactorización del formateo de consola

La presentación de incidencias fue extraída de `ControladorConsola` hacia `FormateadorIncidenciasConsola`.

Esta decisión permitió:

- Separar presentación y coordinación.
- Mejorar la cohesión.
- Reducir responsabilidades del controlador.
- Probar el formato de salida de forma independiente.

La evidencia detallada se encuentra en:

```text
REFACTORIZACION.md
```

## Gestión del trabajo

El desarrollo se administró mediante un tablero Xanpan que combina Kanban para gobernar el flujo y prácticas de XP para mantener la calidad técnica.

### Flujo del tablero

1. Opciones / Backlog
2. Preparado
3. En desarrollo
4. Validación
5. Hecho

### Límites WIP

- Preparado: máximo 3 elementos.
- En desarrollo: máximo 1 elemento por pareja.
- Validación: máximo 1 elemento.
- Hecho: sin límite.

### Políticas del flujo

Una tarjeta puede avanzar cuando cumple las condiciones definidas para cada etapa:

- Descripción y criterios de aceptación completos.
- Capacidad disponible según los límites WIP.
- Responsable definido.
- Código compilado.
- Pruebas ejecutadas.
- Revisión del otro integrante.
- Integración continua en verde.
- Código integrado en `main`.

## Tablero del proyecto

[Acceder al tablero HelpDesk Flow - Xanpan](https://github.com/users/eithel03/projects/2/views/1)

## Integración continua

El proyecto utiliza GitHub Actions para ejecutar automáticamente la integración continua.

El workflow se ejecuta en:

- Cada `push`.
- Cada Pull Request.

El proceso automatizado:

1. Descarga el repositorio.
2. Configura Java 17.
3. Compila el proyecto.
4. Ejecuta la suite completa con Maven.
5. Marca el workflow como fallido si una prueba falla.

Comando principal del workflow:

```bash
mvn clean test
```

Estado actual:

```text
GitHub Actions: configurado y en verde
Pruebas automatizadas: 142
Fallos: 0
Errores: 0
Pruebas omitidas: 0
```

## Estado final del proyecto

HelpDesk Flow cuenta con:

- Cinco historias de usuario implementadas.
- Persistencia local mediante SQLite.
- Interfaz funcional de consola.
- Reglas EXPEDITE.
- Métricas básicas.
- Pruebas automatizadas.
- Integración continua con GitHub Actions.
- Refactorización documentada.
- Gestión del trabajo mediante Xanpan.

El sistema se encuentra funcionalmente implementado y preparado para su validación y demostración técnica.