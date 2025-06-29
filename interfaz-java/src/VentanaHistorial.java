import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * Ventana para mostrar el historial de eventos de caídas
 */
public class VentanaHistorial extends JFrame {
    
    private JTable tablaEventos;
    private DefaultTableModel modelo;
    private List<Evento> eventos;
    private JLabel labelTotal;
    private JButton botonExportar;
    private JButton botonActualizar;
    
    public VentanaHistorial(List<Evento> eventos) {
        this.eventos = eventos;
        inicializarInterfaz();
        cargarDatos();
    }
    
    private void inicializarInterfaz() {
        setTitle("Historial de Eventos - Sistema de Deteccion de Caidas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Informacion
        crearPanelInformacion();
        
        // Panel central - Tabla
        crearPanelTabla();
        
        // Panel inferior - Controles
        crearPanelControles();
        
        add(panelPrincipal);
    }
    
    private void crearPanelInformacion() {
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Informacion"));
        
        labelTotal = new JLabel("Total de eventos: " + eventos.size());
        labelTotal.setFont(new Font("Arial", Font.BOLD, 14));
        
        panelInfo.add(labelTotal);
        
        add(panelInfo, BorderLayout.NORTH);
    }
    
    private void crearPanelTabla() {
        // Crear modelo de tabla
        String[] columnas = {"ID", "Tipo", "Angulo", "Fecha/Hora"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer tabla de solo lectura
            }
        };
        
        tablaEventos = new JTable(modelo);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.getTableHeader().setReorderingAllowed(false);
        
        // Configurar colores
        tablaEventos.setGridColor(Color.LIGHT_GRAY);
        tablaEventos.setShowGrid(true);
        tablaEventos.setRowHeight(25);
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Eventos Registrados"));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void crearPanelControles() {
        JPanel panelControles = new JPanel(new FlowLayout());
        panelControles.setBorder(BorderFactory.createTitledBorder("Controles"));
        
        // Botón Actualizar
        botonActualizar = new JButton("Actualizar");
        botonActualizar.setBackground(new Color(60, 60, 60));
        botonActualizar.setForeground(Color.BLACK);
        botonActualizar.setFont(botonActualizar.getFont().deriveFont(Font.BOLD));
        botonActualizar.setBorderPainted(false);
        botonActualizar.setFocusPainted(false);
        botonActualizar.setOpaque(true);
        botonActualizar.addActionListener(e -> actualizarDatos());
        
        // Botón Exportar
        botonExportar = new JButton("Exportar CSV");
        botonExportar.setBackground(new Color(60, 60, 60));
        botonExportar.setForeground(Color.BLACK);
        botonExportar.setFont(botonExportar.getFont().deriveFont(Font.BOLD));
        botonExportar.setBorderPainted(false);
        botonExportar.setFocusPainted(false);
        botonExportar.setOpaque(true);
        botonExportar.addActionListener(e -> exportarCSV());
        
        // Botón Cerrar
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.setBackground(new Color(60, 60, 60));
        botonCerrar.setForeground(Color.BLACK);
        botonCerrar.setFont(botonCerrar.getFont().deriveFont(Font.BOLD));
        botonCerrar.setBorderPainted(false);
        botonCerrar.setFocusPainted(false);
        botonCerrar.setOpaque(true);
        botonCerrar.addActionListener(e -> dispose());
        
        panelControles.add(botonActualizar);
        panelControles.add(botonExportar);
        panelControles.add(botonCerrar);
        
        add(panelControles, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        // Limpiar tabla
        modelo.setRowCount(0);
        
        // Agregar eventos
        for (Evento evento : eventos) {
            Object[] fila = {
                evento.getId(),
                evento.getTipo(),
                String.format("%.1f°", evento.getAngulo()),
                evento.getTimestamp()
            };
            modelo.addRow(fila);
        }
        
        // Actualizar contador
        labelTotal.setText("Total de eventos: " + eventos.size());
        
        // Mensaje si no hay eventos
        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay eventos registrados en el historial.", 
                "Sin eventos", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void actualizarDatos() {
        // Recargar datos desde la base de datos
        BaseDatos baseDatos = new BaseDatos();
        eventos = baseDatos.obtenerEventos();
        cargarDatos();
        
        JOptionPane.showMessageDialog(this, 
            "Datos actualizados correctamente.", 
            "Actualizacion", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar historial como CSV");
        fileChooser.setSelectedFile(new File("historial_caidas.csv"));
        
        int resultado = fileChooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            guardarCSV(archivo);
        }
    }
    
    private void guardarCSV(File archivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            // Encabezados
            writer.println("ID,Tipo,Angulo,Timestamp");
            
            // Datos
            for (Evento evento : eventos) {
                writer.println(evento.getFormatoCSV());
            }
            
            JOptionPane.showMessageDialog(this, 
                "Archivo CSV exportado correctamente:\n" + archivo.getAbsolutePath(), 
                "Exportacion exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al exportar archivo: " + e.getMessage(), 
                "Error de exportacion", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 