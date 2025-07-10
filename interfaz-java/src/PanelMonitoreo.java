import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.time.Duration;

/**
 * Panel de Monitoreo para Sistema de Deteccion de Caidas
 * Fase 3: Interfaz Java con Base de Datos
 */
public class PanelMonitoreo extends JFrame {
    
    // Componentes de la interfaz
    private JPanel panelPrincipal;
    private JTextArea areaLogs;
    private JLabel labelEstado;
    private JLabel labelUltimaDeteccion;
    private JLabel labelContadorEventos;
    private JButton botonIniciar;
    private JButton botonDetener;
    private JButton botonConfigurar;
    private JButton botonVerLogs;
    private JButton botonEnviarNotificacion;
    private JButton botonVerNotificaciones;
    
    // Base de datos
    private BaseDatos baseDatos;
    
    // Estado del sistema
    private boolean sistemaActivo = false;
    private int contadorEventos = 0;
    private String ultimaDeteccion = "Ninguna";
    
    // Configuración
    private Configuracion config;
    
    private int ultimoIdEvento = -1;
    private Timer timerMonitoreo;
    private int ultimoIdEventoNotificado = -1; // Nueva variable para controlar notificaciones
    
    private LocalDateTime horaInicioSistema;
    
    public PanelMonitoreo() {
        config = new Configuracion();
        baseDatos = new BaseDatos();
        horaInicioSistema = LocalDateTime.now();
        inicializarInterfaz();
        cargarDatos();
    }
    
    private void inicializarInterfaz() {
        setTitle("Sistema de Deteccion de Caidas - Panel de Monitoreo");
        setSize(800, 850); // Más alto para dar más espacio abajo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con BoxLayout vertical
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Estado del sistema
        crearPanelEstado();
        panelPrincipal.add(Box.createVerticalStrut(10));
        
        // Panel central - Logs en tiempo real
        crearPanelLogs();
        panelPrincipal.add(Box.createVerticalStrut(10));
        
        // Panel inferior - Controles
        crearPanelControles();
        
        setContentPane(panelPrincipal);
    }
    
    private void crearPanelEstado() {
        JPanel panelEstado = new JPanel();
        panelEstado.setLayout(new GridLayout(2, 3, 10, 5));
        panelEstado.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        
        // Estado actual
        labelEstado = new JLabel("SISTEMA INACTIVO");
        labelEstado.setForeground(Color.RED);
        labelEstado.setFont(new Font("Arial", Font.BOLD, 16));
        labelEstado.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ultima deteccion
        labelUltimaDeteccion = new JLabel("Ultima deteccion: " + ultimaDeteccion);
        labelUltimaDeteccion.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Contador de eventos
        labelContadorEventos = new JLabel("Eventos totales: " + contadorEventos);
        labelContadorEventos.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Configuracion
        JLabel labelConfig = new JLabel("Umbral: " + config.getUmbralAngulo() + "°");
        labelConfig.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Email configurado
        JLabel labelEmail = new JLabel("Email: " + (config.getEmailDestinatario().isEmpty() ? "No configurado" : "Configurado"));
        labelEmail.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Base de datos
        JLabel labelDB = new JLabel("Base de datos: " + (baseDatos.isConectado() ? "Conectada" : "Desconectada"));
        labelDB.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelEstado.add(labelEstado);
        panelEstado.add(labelUltimaDeteccion);
        panelEstado.add(labelContadorEventos);
        panelEstado.add(labelConfig);
        panelEstado.add(labelEmail);
        panelEstado.add(labelDB);
        
        panelPrincipal.add(panelEstado);
    }
    
    private void crearPanelLogs() {
        JPanel panelLogs = new JPanel();
        panelLogs.setLayout(new BorderLayout());
        panelLogs.setBorder(BorderFactory.createTitledBorder("Logs en Tiempo Real"));
        
        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        // Intentar Consolas, luego Arial, luego Monospaced
        Font fuenteLogs = null;
        try {
            fuenteLogs = new Font("Consolas", Font.PLAIN, 16);
            if (!fuenteLogs.getFamily().equals("Consolas")) {
                fuenteLogs = new Font("Arial", Font.PLAIN, 16);
            }
        } catch (Exception e) {
            fuenteLogs = new Font("Monospaced", Font.PLAIN, 16);
        }
        areaLogs.setFont(fuenteLogs);
        areaLogs.setBackground(Color.BLACK);
        areaLogs.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(areaLogs);
        scrollPane.setPreferredSize(new Dimension(750, 350));
        panelLogs.add(scrollPane, BorderLayout.CENTER);
        panelLogs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        panelPrincipal.add(panelLogs);
    }
    
    private void crearPanelControles() {
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new GridLayout(1, 6, 10, 0)); // 1 fila, 6 columnas, espacio horizontal de 10
        panelControles.setBorder(BorderFactory.createTitledBorder("Controles"));
        panelControles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Más alto
        
        // Boton Ver Notificaciones
        botonVerNotificaciones = new JButton("Notificaciones");
        botonVerNotificaciones.setBackground(new Color(60, 60, 60));
        botonVerNotificaciones.setForeground(Color.BLACK);
        botonVerNotificaciones.setFont(botonVerNotificaciones.getFont().deriveFont(Font.BOLD, 12f));
        botonVerNotificaciones.addActionListener(e -> mostrarNotificaciones());
        
        botonIniciar = new JButton("Iniciar Sistema");
        botonIniciar.setFont(botonIniciar.getFont().deriveFont(Font.BOLD, 12f));
        botonIniciar.addActionListener(e -> iniciarSistema());
        
        botonDetener = new JButton("Detener Sistema");
        botonDetener.setFont(botonDetener.getFont().deriveFont(Font.BOLD, 12f));
        botonDetener.addActionListener(e -> detenerSistema());
        
        botonConfigurar = new JButton("Configurar");
        botonConfigurar.setFont(botonConfigurar.getFont().deriveFont(Font.BOLD, 12f));
        botonConfigurar.addActionListener(e -> abrirConfiguracion());
        
        botonVerLogs = new JButton("Ver Logs");
        botonVerLogs.setFont(botonVerLogs.getFont().deriveFont(Font.BOLD, 12f));
        botonVerLogs.addActionListener(e -> mostrarHistorial());
        
        botonEnviarNotificacion = new JButton("Enviar Notificacion");
        botonEnviarNotificacion.setFont(botonEnviarNotificacion.getFont().deriveFont(Font.BOLD, 12f));
        botonEnviarNotificacion.addActionListener(e -> enviarNotificacionPrueba());
        
        panelControles.add(botonIniciar);
        panelControles.add(botonDetener);
        panelControles.add(botonConfigurar);
        panelControles.add(botonVerLogs);
        panelControles.add(botonEnviarNotificacion);
        panelControles.add(botonVerNotificaciones);
        panelPrincipal.add(panelControles);
    }
    
    private void iniciarSistema() {
        sistemaActivo = true;
        labelEstado.setText("SISTEMA ACTIVO");
        labelEstado.setForeground(Color.GREEN);
        botonIniciar.setEnabled(false);
        botonDetener.setEnabled(true);
        
        // Resetear contador de eventos notificados al iniciar
        ultimoIdEventoNotificado = ultimoIdEvento;
        
        agregarLog("Sistema iniciado - " + obtenerTimestamp());
        
        // Iniciar monitoreo real de la base de datos
        timerMonitoreo = new Timer();
        timerMonitoreo.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (sistemaActivo) {
                    monitorearEventosReales();
                }
            }
        }, 0, 5000); // Cada 5 segundos
    }
    
    private void detenerSistema() {
        sistemaActivo = false;
        labelEstado.setText("SISTEMA INACTIVO");
        labelEstado.setForeground(Color.RED);
        botonIniciar.setEnabled(true);
        botonDetener.setEnabled(false);
        if (timerMonitoreo != null) {
            timerMonitoreo.cancel();
        }
        agregarLog("Sistema detenido - " + obtenerTimestamp());
    }
    
    private void monitorearEventosReales() {
        Evento ultimoEvento = baseDatos.obtenerUltimoEvento();
        if (ultimoEvento != null && ultimoEvento.getId() != ultimoIdEvento) {
            LocalDateTime fechaEvento = LocalDateTime.parse(ultimoEvento.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime ahora = LocalDateTime.now();
            // Filtrar eventos antiguos, futuros o fuera de rango razonable
            if (fechaEvento.isBefore(horaInicioSistema)) {
                return; // Ignorar eventos antes de iniciar el sistema
            }
            if (fechaEvento.isAfter(ahora)) {
                return; // Ignorar eventos con fecha futura
            }
            if (Duration.between(fechaEvento, ahora).toMinutes() > 2) {
                return; // Ignorar eventos viejos (mas de 2 minutos)
            }
            ultimoIdEvento = ultimoEvento.getId();
            SwingUtilities.invokeLater(() -> {
                labelUltimaDeteccion.setText("Ultima deteccion: " + ultimoEvento.getTimestamp());
                labelContadorEventos.setText("Eventos totales: " + baseDatos.obtenerContadorEventos());
                agregarLog("ALERTA CAIDA DETECTADA - Angulo: " + String.format("%.1f", ultimoEvento.getAngulo()) + "° - " + ultimoEvento.getTimestamp());
                enviarNotificacionAutomatica(ultimoEvento);
            });
        }
    }
    
    private void enviarNotificacionAutomatica(Evento evento) {
        // Verificar si ya se notificó este evento
        if (evento.getId() <= ultimoIdEventoNotificado) {
            return; // Ya se notificó este evento
        }
        
        // Filtrar por hora de inicio
        LocalDateTime fechaEvento = LocalDateTime.parse(evento.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (fechaEvento.isBefore(horaInicioSistema)) {
            return; // Ignorar eventos antiguos
        }
        
        // Refresca el destinatario desde la configuración antes de enviar
        String destinatario = config.getEmailDestinatario();
        String remitente = config.getEmailRemitente();
        String password = config.getEmailPassword();
        String smtpServer = config.getSmtpServer();
        int smtpPort = config.getSmtpPort();
        String asunto = "Alerta de Caida - Sistema de Deteccion";
        String cuerpo = "Se ha detectado una caida real en el sistema a las " + evento.getTimestamp() +
                        "\nTipo: " + evento.getTipo() +
                        "\nAngulo: " + String.format("%.1f", evento.getAngulo()) + "°";
        agregarLog("Enviando notificacion de caida real a: " + destinatario);
        boolean enviado = NotificadorEmailJava.enviar(remitente, password, destinatario, smtpServer, smtpPort, asunto, cuerpo);
        
        // Marcar este evento como notificado
        ultimoIdEventoNotificado = evento.getId();
        
        // Guardar evento en la base de datos
        baseDatos.guardarEvento(evento.getTipo(), evento.getAngulo(), evento.getTimestamp());
        // Guardar notificación en la base de datos
        baseDatos.guardarNotificacion(
            evento.getTipo(),
            "Notificacion automatica enviada", // razon
            evento.getAngulo(),
            enviado,
            destinatario,
            evento.getTimestamp()
        );
        if (enviado) {
            agregarLog("Notificacion enviada correctamente a: " + destinatario + " - " + obtenerTimestamp());
        } else {
            agregarLog("Error al enviar notificacion a: " + destinatario + " - " + obtenerTimestamp());
        }
    }
    
    private void agregarLog(String mensaje) {
        // Solo agregar logs de eventos posteriores a la hora de inicio
        if (mensaje.contains("-") && mensaje.contains(":")) {
            String[] partes = mensaje.split("-");
            if (partes.length > 1) {
                String posibleFecha = partes[partes.length - 1].trim().split(" ")[0];
                try {
                    LocalDateTime fechaEvento = LocalDateTime.parse(posibleFecha, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (fechaEvento.isBefore(horaInicioSistema)) {
                        return; // No mostrar eventos antiguos
                    }
                } catch (Exception e) {
                    // Si no se puede parsear, mostrar igual
                }
            }
        }
        areaLogs.append(mensaje + "\n");
        areaLogs.setCaretPosition(areaLogs.getDocument().getLength());
    }
    
    private void abrirConfiguracion() {
        VentanaConfiguracion ventanaConfig = new VentanaConfiguracion(config, this);
        ventanaConfig.setVisible(true);
    }
    
    private void mostrarHistorial() {
        List<Evento> eventos = baseDatos.obtenerEventos();
        // Filtrar eventos posteriores a la hora de inicio
        List<Evento> eventosFiltrados = new ArrayList<>();
        for (Evento evento : eventos) {
            LocalDateTime fechaEvento = LocalDateTime.parse(evento.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (!fechaEvento.isBefore(horaInicioSistema)) {
                eventosFiltrados.add(evento);
            }
        }
        VentanaHistorial ventanaHistorial = new VentanaHistorial(eventosFiltrados);
        ventanaHistorial.setVisible(true);
    }
    
    private void mostrarNotificaciones() {
        List<Notificacion> notificaciones = baseDatos.obtenerNotificaciones();
        // Filtrar notificaciones posteriores a la hora de inicio
        List<Notificacion> notificacionesFiltradas = new ArrayList<>();
        for (Notificacion notif : notificaciones) {
            LocalDateTime fechaNotif = LocalDateTime.parse(notif.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (!fechaNotif.isBefore(horaInicioSistema)) {
                notificacionesFiltradas.add(notif);
            }
        }
        VentanaNotificaciones.mostrarVentana(notificacionesFiltradas);
    }
    
    private void enviarNotificacionPrueba() {
        Evento ultimoEvento = baseDatos.obtenerUltimoEvento();
        if (ultimoEvento == null) {
            agregarLog("No hay eventos de caida para notificar.");
            JOptionPane.showMessageDialog(this, "No hay eventos de caida para notificar.", "Sin eventos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
            "Desea enviar una notificacion de prueba por email usando el ultimo evento real?",
            "Enviar Notificacion de Prueba",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            enviarNotificacionAutomatica(ultimoEvento);
        }
    }
    
    private void cargarDatos() {
        // Cargar datos desde la base de datos
        contadorEventos = baseDatos.obtenerContadorEventos();
        Evento ultimoEvento = baseDatos.obtenerUltimoEvento();
        
        if (ultimoEvento != null) {
            ultimaDeteccion = ultimoEvento.getTimestamp();
        }
        
        // Actualizar interfaz
        labelContadorEventos.setText("Eventos totales: " + contadorEventos);
        labelUltimaDeteccion.setText("Ultima deteccion: " + ultimaDeteccion);
        
        agregarLog("Datos cargados - " + obtenerTimestamp());
    }
    
    private String obtenerTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public void actualizarConfiguracion() {
        // Recargar configuracion
        config = new Configuracion();
        cargarDatos();
        agregarLog("Configuracion actualizada - " + obtenerTimestamp());
    }
    
    public static void main(String[] args) {
        // Configurar look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al configurar look and feel: " + e.getMessage());
        }
        
        // Ejecutar en EDT
        SwingUtilities.invokeLater(() -> {
            PanelMonitoreo panel = new PanelMonitoreo();
            panel.setVisible(true);
        });
    }
} 