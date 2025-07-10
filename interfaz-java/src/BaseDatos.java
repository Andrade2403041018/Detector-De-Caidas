import java.sql.*;
import java.util.*;

/**
 * Clase para manejar la base de datos SQLite de eventos de caidas
 */
public class BaseDatos {
    private Connection conexion;
    private boolean conectado = false;
    private final String DB_URL = "jdbc:sqlite:caidas.db";

    public BaseDatos() {
        conectar();
        crearTablaSiNoExiste();
    }

    private void conectar() {
        try {
            conexion = DriverManager.getConnection(DB_URL);
            conectado = true;
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            conectado = false;
        }
    }

    private void crearTablaSiNoExiste() {
        if (!conectado) return;
        
        // Tabla de eventos
        String sqlEventos = "CREATE TABLE IF NOT EXISTS eventos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo TEXT NOT NULL," +
                "angulo REAL," +
                "timestamp TEXT NOT NULL" +
                ")";
        
        // Tabla de notificaciones
        String sqlNotificaciones = "CREATE TABLE IF NOT EXISTS notificaciones (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo TEXT NOT NULL," +
                "razon TEXT," +
                "angulo REAL," +
                "email_enviado BOOLEAN DEFAULT 0," +
                "destinatario TEXT," +
                "timestamp TEXT NOT NULL" +
                ")";
        
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sqlEventos);
            stmt.execute(sqlNotificaciones);
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    public void guardarEvento(String tipo, double angulo, String timestamp) {
        if (!conectado) return;
        String sql = "INSERT INTO eventos (tipo, angulo, timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            pstmt.setDouble(2, angulo);
            pstmt.setString(3, timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar evento: " + e.getMessage());
        }
    }

    public List<Evento> obtenerEventos() {
        List<Evento> eventos = new ArrayList<>();
        if (!conectado) return eventos;
        String sql = "SELECT * FROM eventos ORDER BY id DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                double angulo = rs.getDouble("angulo");
                String timestamp = rs.getString("timestamp");
                eventos.add(new Evento(id, tipo, angulo, timestamp));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener eventos: " + e.getMessage());
        }
        return eventos;
    }

    public Evento obtenerUltimoEvento() {
        if (!conectado) return null;
        String sql = "SELECT * FROM eventos ORDER BY id DESC LIMIT 1";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                double angulo = rs.getDouble("angulo");
                String timestamp = rs.getString("timestamp");
                return new Evento(id, tipo, angulo, timestamp);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ultimo evento: " + e.getMessage());
        }
        return null;
    }

    public int obtenerContadorEventos() {
        if (!conectado) return 0;
        String sql = "SELECT COUNT(*) AS total FROM eventos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar eventos: " + e.getMessage());
        }
        return 0;
    }

    // Métodos para manejar notificaciones
    public void guardarNotificacion(String tipo, String razon, double angulo, boolean emailEnviado, String destinatario, String timestamp) {
        if (!conectado) return;
        String sql = "INSERT INTO notificaciones (tipo, razon, angulo, email_enviado, destinatario, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            pstmt.setString(2, razon);
            pstmt.setDouble(3, angulo);
            pstmt.setBoolean(4, emailEnviado);
            pstmt.setString(5, destinatario);
            pstmt.setString(6, timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar notificacion: " + e.getMessage());
        }
    }
    
    public List<Notificacion> obtenerNotificaciones() {
        List<Notificacion> notificaciones = new ArrayList<>();
        if (!conectado) return notificaciones;
        String sql = "SELECT * FROM notificaciones ORDER BY id DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                String razon = rs.getString("razon");
                double angulo = rs.getDouble("angulo");
                boolean emailEnviado = rs.getBoolean("email_enviado");
                String destinatario = rs.getString("destinatario");
                String timestamp = rs.getString("timestamp");
                notificaciones.add(new Notificacion(id, tipo, razon, angulo, emailEnviado, destinatario, timestamp));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener notificaciones: " + e.getMessage());
        }
        return notificaciones;
    }
    
    public int obtenerContadorNotificaciones() {
        if (!conectado) return 0;
        String sql = "SELECT COUNT(*) AS total FROM notificaciones";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar notificaciones: " + e.getMessage());
        }
        return 0;
    }
} 