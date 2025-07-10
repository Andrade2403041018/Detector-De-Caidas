import javax.swing.*;
import java.awt.*;

/**
 * Ventana de configuracion del sistema de deteccion de caidas
 */
public class VentanaConfiguracion extends JFrame {
    
    private Configuracion config;
    private PanelMonitoreo panelPrincipal;
    
    // Componentes de deteccion
    private JSpinner spinnerUmbral;
    private JSpinner spinnerConfirmacion;
    private JSpinner spinnerIntervalo;
    
    // Componentes de email
    private JTextField campoEmailRemitente;
    private JPasswordField campoEmailPassword;
    private JTextField campoEmailDestinatario;
    private JTextField campoSmtpServer;
    private JSpinner spinnerSmtpPort;
    
    // Componentes de base de datos
    private JTextField campoDbUrl;
    private JTextField campoDbUsuario;
    private JPasswordField campoDbPassword;
    
    public VentanaConfiguracion(Configuracion config, PanelMonitoreo panelPrincipal) {
        this.config = config;
        this.panelPrincipal = panelPrincipal;
        inicializarInterfaz();
        cargarConfiguracion();
    }
    
    private void inicializarInterfaz() {
        setTitle("Configuracion - Sistema de Deteccion de Caidas");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de umbral
        JPanel panelUmbral = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelUmbral.add(new JLabel("Umbral de angulo (grados): "));
        spinnerUmbral = new JSpinner(new SpinnerNumberModel(45, 10, 90, 5));
        panelUmbral.add(spinnerUmbral);
        panel.add(panelUmbral);
        
        // Panel de email destinatario
        JPanel panelEmail = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEmail.add(new JLabel("Email destinatario: "));
        campoEmailDestinatario = new JTextField(20);
        panelEmail.add(campoEmailDestinatario);
        panel.add(panelEmail);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardarConfiguracion());
        JButton botonRestaurar = new JButton("Restaurar");
        botonRestaurar.addActionListener(e -> cargarConfiguracion());
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(e -> dispose());
        panelBotones.add(botonGuardar);
        panelBotones.add(botonRestaurar);
        panelBotones.add(botonCerrar);
        panel.add(Box.createVerticalStrut(20));
        panel.add(panelBotones);
        
        add(panel);
    }
    
    private void cargarConfiguracion() {
        spinnerUmbral.setValue(config.getUmbralAngulo());
        campoEmailDestinatario.setText(config.getEmailDestinatario());
    }
    
    private void guardarConfiguracion() {
        config.setUmbralAngulo((Integer) spinnerUmbral.getValue());
        config.setEmailDestinatario(campoEmailDestinatario.getText());
        config.guardarConfiguracion();
        if (panelPrincipal != null) {
            panelPrincipal.actualizarConfiguracion();
        }
        JOptionPane.showMessageDialog(this, "Configuracion guardada correctamente.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
} 