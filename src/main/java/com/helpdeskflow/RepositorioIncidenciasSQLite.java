package com.helpdeskflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RepositorioIncidenciasSQLite
        implements RepositorioIncidencias {

    private static final String INSERTAR = """
            INSERT INTO incidencias (
                id,
                titulo,
                descripcion,
                categoria,
                impacto,
                urgencia,
                prioridad,
                estado,
                fecha_creacion,
                fecha_cierre,
                descripcion_solucion,
                expedite
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String BUSCAR_POR_ID = """
            SELECT *
            FROM incidencias
            WHERE id = ?
            """;

    private static final String LISTAR_TODAS = """
            SELECT *
            FROM incidencias
            ORDER BY orden
            """;

    private static final String LISTAR_ABIERTAS = """
            SELECT *
            FROM incidencias
            WHERE estado <> 'FINALIZADA'
            ORDER BY orden
            """;

    private static final String LISTAR_FINALIZADAS = """
            SELECT *
            FROM incidencias
            WHERE estado = 'FINALIZADA'
            ORDER BY orden
            """;

    private static final String FILTRAR_POR_ESTADO = """
            SELECT *
            FROM incidencias
            WHERE estado = ?
            ORDER BY orden
            """;

    private static final String FILTRAR_POR_PRIORIDAD = """
            SELECT *
            FROM incidencias
            WHERE prioridad = ?
            ORDER BY orden
            """;

    private final ConexionSQLite conexionSQLite;

    public RepositorioIncidenciasSQLite(Path archivoBaseDatos) {
        prepararDirectorio(archivoBaseDatos);

        this.conexionSQLite = new ConexionSQLite(archivoBaseDatos);
        new InicializadorBaseDatos(conexionSQLite).inicializar();
    }

    private static void prepararDirectorio(Path archivoBaseDatos) {
        if (archivoBaseDatos == null) {
            throw new IllegalArgumentException(
                    "La ruta de la base de datos es obligatoria.");
        }

        Path rutaAbsoluta = archivoBaseDatos.toAbsolutePath().normalize();
        Path directorio = rutaAbsoluta.getParent();

        if (directorio == null) {
            return;
        }

        try {
            Files.createDirectories(directorio);
        } catch (IOException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible crear el directorio de la base de datos.",
                    excepcion);
        }
    }

    @Override
    public void guardar(Incidencia incidencia) {
        validarIncidencia(incidencia);

        try (Connection conexion = conexionSQLite.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {

            sentencia.setString(1, incidencia.getId().toString());
            sentencia.setString(2, incidencia.getTitulo());
            sentencia.setString(3, incidencia.getDescripcion());
            sentencia.setString(4, incidencia.getCategoria());
            sentencia.setString(5, incidencia.getImpacto().name());
            sentencia.setString(6, incidencia.getUrgencia().name());
            sentencia.setString(7, incidencia.getPrioridad().name());
            sentencia.setString(8, incidencia.getEstado().name());
            sentencia.setString(
                    9,
                    incidencia.getFechaCreacion().toString());

            if (incidencia.getFechaCierre() == null) {
                sentencia.setNull(10, java.sql.Types.VARCHAR);
            } else {
                sentencia.setString(
                        10,
                        incidencia.getFechaCierre().toString());
            }

            if (incidencia.getDescripcionSolucion() == null) {
                sentencia.setNull(11, java.sql.Types.VARCHAR);
            } else {
                sentencia.setString(
                        11,
                        incidencia.getDescripcionSolucion());
            }
            sentencia.setInt(
                    12,
                    incidencia.esExpedite() ? 1 : 0);

            sentencia.executeUpdate();

        } catch (SQLException excepcion) {
            if (esViolacionDeRestriccion(excepcion)) {
                throw new IllegalArgumentException(
                        "Ya existe una incidencia con el identificador indicado.",
                        excepcion);
            }

            throw new ExcepcionPersistencia(
                    "No fue posible guardar la incidencia.",
                    excepcion);
        }
    }

    @Override
    public Optional<Incidencia> buscarPorId(UUID id) {
        validarId(id);

        try (Connection conexion = conexionSQLite.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(BUSCAR_POR_ID)) {

            sentencia.setString(1, id.toString());

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapearIncidencia(resultado));
            }

        } catch (SQLException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible buscar la incidencia.",
                    excepcion);
        }
    }

    @Override
    public List<Incidencia> listarTodas() {
        return ejecutarConsultaLista(LISTAR_TODAS);
    }

    @Override
    public List<Incidencia> listarAbiertas() {
        return ejecutarConsultaLista(LISTAR_ABIERTAS);
    }

    @Override
    public List<Incidencia> listarFinalizadas() {
        return ejecutarConsultaLista(LISTAR_FINALIZADAS);
    }

    @Override
    public List<Incidencia> filtrarPorEstado(
            EstadoIncidencia estado) {

        validarEstado(estado);

        return ejecutarConsultaConParametro(
                FILTRAR_POR_ESTADO,
                estado.name());
    }

    @Override
    public List<Incidencia> filtrarPorPrioridad(
            Prioridad prioridad) {

        validarPrioridad(prioridad);

        return ejecutarConsultaConParametro(
                FILTRAR_POR_PRIORIDAD,
                prioridad.name());
    }

    private List<Incidencia> ejecutarConsultaLista(String sql) {
        List<Incidencia> incidencias = new java.util.ArrayList<>();

        try (Connection conexion = conexionSQLite.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                incidencias.add(mapearIncidencia(resultado));
            }

            return List.copyOf(incidencias);

        } catch (SQLException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible consultar las incidencias.",
                    excepcion);
        }
    }

    private List<Incidencia> ejecutarConsultaConParametro(
            String sql,
            String parametro) {

        List<Incidencia> incidencias = new java.util.ArrayList<>();

        try (Connection conexion = conexionSQLite.abrir();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, parametro);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    incidencias.add(mapearIncidencia(resultado));
                }
            }

            return List.copyOf(incidencias);

        } catch (SQLException excepcion) {
            throw new ExcepcionPersistencia(
                    "No fue posible filtrar las incidencias.",
                    excepcion);
        }
    }

    private Incidencia mapearIncidencia(ResultSet resultado)
            throws SQLException {

        String fechaCierreTexto = resultado.getString("fecha_cierre");

        String solucion = resultado.getString("descripcion_solucion");

        return new Incidencia(
                UUID.fromString(resultado.getString("id")),
                resultado.getString("titulo"),
                resultado.getString("descripcion"),
                resultado.getString("categoria"),
                convertirEnum(
                        Impacto.class,
                        resultado.getString("impacto"),
                        "impacto"),
                convertirEnum(
                        Urgencia.class,
                        resultado.getString("urgencia"),
                        "urgencia"),
                convertirEnum(
                        Prioridad.class,
                        resultado.getString("prioridad"),
                        "prioridad"),
                convertirEnum(
                        EstadoIncidencia.class,
                        resultado.getString("estado"),
                        "estado"),
                LocalDateTime.parse(
                        resultado.getString("fecha_creacion")),
                fechaCierreTexto == null
                        ? null
                        : LocalDateTime.parse(fechaCierreTexto),
                solucion,
                resultado.getInt("expedite") == 1);
    }

    private <E extends Enum<E>> E convertirEnum(
            Class<E> tipo,
            String valor,
            String campo) {

        try {
            return Enum.valueOf(tipo, valor);
        } catch (IllegalArgumentException | NullPointerException excepcion) {
            throw new ExcepcionPersistencia(
                    "El valor almacenado para "
                            + campo
                            + " no es válido.",
                    excepcion);
        }
    }

    private static void validarIncidencia(
            Incidencia incidencia) {

        if (incidencia == null) {
            throw new IllegalArgumentException(
                    "La incidencia es obligatoria.");
        }
    }

    private static void validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador es obligatorio.");
        }
    }

    private static void validarEstado(EstadoIncidencia estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio.");
        }
    }

    private static void validarPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException(
                    "La prioridad es obligatoria.");
        }
    }

    private static boolean esViolacionDeRestriccion(
            SQLException excepcion) {

        return excepcion.getErrorCode() == 19;
    }

}