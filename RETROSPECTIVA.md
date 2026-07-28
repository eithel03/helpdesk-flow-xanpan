# Retrospectiva del proyecto HelpDesk Flow

## 1. ¿Qué aportó Kanban al trabajo de la pareja?

Kanban ayudó a organizar el trabajo de manera visual y a mantener claridad sobre qué actividades estaban pendientes, preparadas, en desarrollo, en validación o terminadas. El tablero permitió dividir el proyecto en historias de usuario, tareas técnicas, documentación y actividades de cierre. También facilitó identificar quién era responsable de cada tarjeta y evitar que ambos integrantes trabajaran sin coordinación sobre la misma actividad. El uso del tablero hizo visible el avance real del proyecto y permitió relacionar cada cambio con una rama, commits, pruebas y Pull Requests.

## 2. ¿Qué dificultad generó el límite WIP?

La principal dificultad del límite WIP fue que obligó a no iniciar varias tareas al mismo tiempo. En algunos momentos existía interés por avanzar en otra tarjeta mientras una actividad seguía en validación. Esto exigió respetar la capacidad definida y planificar mejor el orden del trabajo. Sin embargo, esta restricción también resultó positiva porque redujo tareas abandonadas y promovió terminar, revisar e integrar un cambio antes de acumular trabajo incompleto.

## 3. ¿Qué errores fueron detectados mediante TDD?

El desarrollo dirigido por pruebas permitió detectar errores relacionados con validaciones, cálculo de prioridad, transiciones de estado y cierre de incidencias. Las pruebas comprobaron que el título no estuviera vacío, que la descripción cumpliera la longitud mínima y que la prioridad se asignara correctamente según impacto y urgencia. También ayudaron a impedir saltos o retrocesos de estado, evitar el cierre sin una solución y controlar la regla que permite solo una incidencia EXPEDITE activa en desarrollo o validación. Las pruebas de SQLite verificaron duplicados, consultas, filtros y conservación de datos.

## 4. ¿Qué parte del código fue refactorizada?

Se refactorizó la presentación de incidencias en consola. Antes, `ControladorConsola` coordinaba operaciones y además contenía la lógica de impresión. Esta responsabilidad se extrajo a `FormateadorIncidenciasConsola`. La clase `MenuConsola` pasó a utilizar el nuevo formateador, mientras que el controlador conservó sus métodos públicos mediante delegación para no romper compatibilidad. La refactorización fue protegida con pruebas de caracterización y la suite aumentó de 139 a 142 pruebas sin fallos.

## 5. ¿Cómo afectó el cambio de requerimiento?

El cambio obligatorio de EXPEDITE obligó a extender el diseño sin reescribir las historias anteriores. Fue necesario agregar una política y un servicio específicos, además de pruebas que verificaran que solo las incidencias críticas pudieran marcarse como EXPEDITE y que únicamente una pudiera permanecer activa en `EN_DESARROLLO` o `EN_VALIDACION`. Este cambio mostró la importancia de tener responsabilidades separadas y pruebas de regresión.

## 6. ¿En qué ayudó la IA?

La IA ayudó a revisar requisitos, proponer estructuras de clases, formular criterios de aceptación, analizar errores de Maven, preparar comandos Git y orientar la creación de pruebas. También sirvió para revisar documentación, organizar el README final y proponer una refactorización de bajo riesgo. Cada sugerencia fue verificada mediante ejecución local, revisión del código, pruebas automatizadas y GitHub Actions.

## 7. ¿En qué se equivocó o fue insuficiente la IA?

La IA fue insuficiente cuando asumió comandos o configuraciones que no funcionaban exactamente en el entorno de Windows y PowerShell. Por ejemplo, la ejecución con Maven presentó dificultades y fue necesario usar un classpath explícito para incluir el controlador JDBC de SQLite. También fue necesario revisar manualmente cambios de codificación, textos con escapes Unicode y archivos que no debían incluirse en los commits. Esto confirmó que sus respuestas no podían aceptarse sin validación humana.

## 8. ¿Qué cambiaríamos en una siguiente versión?

En una siguiente versión comenzaríamos antes la documentación de evidencia RED, GREEN y REFACTOR. También definiríamos desde el inicio una forma más sencilla de ejecutar la aplicación con todas sus dependencias, por ejemplo mediante un plugin de Maven o un JAR ejecutable. Mantendríamos commits más pequeños durante algunas fases y prepararíamos con anticipación el guion de demostración. Además, mejoraríamos el manejo de codificación de caracteres y ampliaríamos las pruebas de integración de la interfaz de consola.