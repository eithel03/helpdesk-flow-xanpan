# Refactorización: extracción del formateo de incidencias de consola

## Problema encontrado

`ControladorConsola` mezclaba responsabilidades de coordinación de casos de uso
con presentación en consola. La clase registraba incidencias, convertía entradas,
buscaba, filtraba, avanzaba estados, finalizaba incidencias, consultaba EXPEDITE,
consultaba métricas y, además, imprimía el detalle de cada incidencia en un
`PrintStream`.

## Evidencia del problema

- `ControladorConsola` tenía métodos públicos de presentación:
  `mostrarIncidencias` y `mostrarIncidencia`.
- También tenía un método privado `imprimirIncidencia` con el formato completo
  de salida: ID, título, categoría, impacto, urgencia, prioridad, estado,
  indicador EXPEDITE y fecha de creación.
- `MenuConsola` dependía de esos métodos del controlador únicamente para mostrar
  resultados, aunque el controlador ya tenía otras responsabilidades de
  coordinación.

## Clases o métodos afectados

- `ControladorConsola`
  - `mostrarIncidencias`.
  - `mostrarIncidencia`.
  - `imprimirIncidencia` fue extraído.
- `MenuConsola`
  - Listado general.
  - Búsqueda por identificador.
  - Filtros por estado y prioridad.
  - Listado de incidencias abiertas, finalizadas y EXPEDITE.
- Nueva clase `FormateadorIncidenciasConsola`.

## Cambio realizado

Se creó `FormateadorIncidenciasConsola` para concentrar el formato de salida de
incidencias en consola. `MenuConsola` ahora utiliza ese formateador directamente
para mostrar listas y detalles.

`ControladorConsola` conserva sus métodos públicos de presentación, pero delega
en el formateador para mantener la compatibilidad con el comportamiento previo.

No se modificaron reglas de dominio, cálculo de prioridad, flujo de estados,
EXPEDITE, persistencia SQLite ni el esquema de la base de datos.

## Razón técnica

La presentación de incidencias pertenece a la capa de consola y no al controlador
que coordina operaciones sobre el repositorio y los servicios.

Extraer esta responsabilidad mejora la cohesión, reduce la mezcla de
responsabilidades y deja el formato de salida aislado para facilitar su
mantenimiento y sus pruebas.

## Pruebas que protegieron el cambio

- `MenuConsolaTest`: caracteriza el comportamiento visible de la consola al
  listar, buscar, filtrar y mostrar incidencias abiertas, finalizadas, EXPEDITE
  y métricas.
- `FormateadorIncidenciasConsolaTest`: caracteriza el formato actual de una
  incidencia individual y el mensaje mostrado cuando una lista está vacía.
- `ControladorConsolaTest`: caracteriza que el método público conservado
  `mostrarIncidencia` mantiene el formato actual por compatibilidad.
- Suite completa ejecutada con `mvn clean test`.

## Resultado antes

- Comando: `mvn clean test`.
- Tests run: 139.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Resultado: `BUILD SUCCESS`.

## Resultado después

### Pruebas específicas

- Comando:
  `mvn "-Dtest=MenuConsolaTest,FormateadorIncidenciasConsolaTest,ControladorConsolaTest" test`.
- Tests run: 24.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Resultado: `BUILD SUCCESS`.

### Suite completa

- Comando: `mvn clean test`.
- Tests run: 142.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Resultado: `BUILD SUCCESS`.

## Riesgos considerados

- Cambio accidental en los mensajes visibles de la consola:
  mitigado mediante `MenuConsolaTest` y las pruebas de caracterización del
  formateador.
- Ruptura de métodos públicos existentes en `ControladorConsola`:
  mitigada conservando esos métodos y delegando en
  `FormateadorIncidenciasConsola`.
- Cambio funcional en las reglas de negocio:
  evitado porque no se modificaron `Incidencia`, `CalculadorPrioridad`,
  `ServicioExpedite`, `PoliticaExpedite`, los repositorios ni las métricas.
- Cambio en SQLite:
  evitado porque no se modificaron `RepositorioIncidenciasSQLite`,
  `ConexionSQLite`, `InicializadorBaseDatos` ni el esquema de la base de datos.

## Confirmación de comportamiento

La refactorización no agrega funcionalidades nuevas ni modifica el comportamiento
funcional existente.

El formato de las incidencias fue trasladado a una clase dedicada, pero las
salidas esperadas, las reglas de negocio, la persistencia SQLite, EXPEDITE y las
métricas continúan protegidas por la suite automatizada.

La suite completa pasó de 139 a 142 pruebas, con 0 fallos, 0 errores y 0 pruebas
omitidas.