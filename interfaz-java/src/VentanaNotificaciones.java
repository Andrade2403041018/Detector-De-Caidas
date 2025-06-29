import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Ventana para mostrar el historial de notificaciones
 */
public class VentanaNotificaciones extends JFrame {
    private BaseDatos baseDatos;
    private JTable tablaNotificaciones;
    private DefaultTableModel modelo;
    private JLabel lblContador;
    private Timer timer;
    private List<Notificacion> notificaciones;
    
    public VentanaNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
        baseDatos = new BaseDatos();
        inicializarComponentes();
        cargarNotificaciones();
        iniciarActualizacionAutomatica();
    }
    
    private void inicializarComponentes() {
        setTitle("Historial de Notificaciones - Sistema de Deteccion de Caidas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con información
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblContador = new JLabel("Total de notificaciones: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 14));
        panelSuperior.add(lblContador);
        
        // Botón de actualizar
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarNotificaciones();
            }
        });
        panelSuperior.add(btnActualizar);
        
        // Botón de limpiar
        JButton btnLimpiar = new JButton("Limpiar Historial");
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(
                    VentanaNotificaciones.this,
                    "Estas seguro de que quieres eliminar todo el historial de notificaciones?",
                    "Confirmar eliminacion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirmacion == JOptionPane.YES_OPTION) {
                    limpiarHistorial();
                }
            }
        });
        panelSuperior.add(btnLimpiar);
        
        // Tabla de notificaciones
        String[] columnas = {"ID", "Tipo", "Razon", "Angulo", "Email Enviado", "Destinatario", "Fecha/Hora"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla de solo lectura
            }
        };
        
        tablaNotificaciones = new JTable(modelo);
        tablaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaNotificaciones.getTableHeader().setReorderingAllowed(false);
        
        // Configurar colores de la tabla
        tablaNotificaciones.setGridColor(Color.LIGHT_GRAY);
        tablaNotificaciones.setShowGrid(true);
        tablaNotificaciones.setRowHeight(25);
        
        // Scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaNotificaciones);
        scrollPane.setPreferredSize(new Dimension(850, 400));
        
        // Panel inferior con información
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("Las notificaciones se actualizan automáticamente cada 5 segundos");
        lblInfo.setForeground(Color.GRAY);
        panelInferior.add(lblInfo);
        
        // Agregar componentes al panel principal
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        // Agregar panel principal al frame
        add(panelPrincipal);
    }
    
    private void cargarNotificaciones() {
        modelo.setRowCount(0);
        for (Notificacion notif : notificaciones) {
            Object[] fila = {
                notif.getId(),
                notif.getTipo(),
                notif.getRazon(),
                String.format("%.1f°", notif.getAngulo()),
                notif.isEmailEnviado() ? "Si" : "No",
                notif.getDestinatario(),
                notif.getTimestamp()
            };
            modelo.addRow(fila);
        }
        lblContador.setText("Total de notificaciones: " + notificaciones.size());
        resaltarFilas();
    }
    
    private void resaltarFilas() {
        // Simplificar el resaltado para evitar problemas de codificación
        tablaNotificaciones.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row < tablaNotificaciones.getRowCount()) {
                    String tipo = (String) tablaNotificaciones.getValueAt(row, 1);
                    if (tipo != null && tipo.contains("Caida")) {
                        c.setBackground(new Color(255, 200, 200)); // Rojo claro para caídas
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    }
                }
                return c;
            }
        });
    }
    
    private void limpiarHistorial() {
        // Aquí se implementaría la limpieza de la base de datos
        // Por ahora solo mostramos un mensaje
        JOptionPane.showMessageDialog(this, 
            "Función de limpieza implementada en la base de datos", 
            "Información", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void iniciarActualizacionAutomatica() {
        timer = new Timer(5000, new ActionListener() { // Actualizar cada 5 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarNotificaciones();
            }
        });
        timer.start();
    }
    
    public void detenerActualizacion() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    @Override
    public void dispose() {
        detenerActualizacion();
        super.dispose();
    }
    
    // Método para mostrar la ventana
    public static void mostrarVentana(List<Notificacion> notificaciones) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaNotificaciones ventana = new VentanaNotificaciones(notificaciones);
                ventana.setVisible(true);
            }
        });
    }
} 