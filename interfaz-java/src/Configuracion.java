import java.io.*;
import java.util.Properties;

/**
 * Clase para manejar la configuración del sistema
 */
public class Configuracion {
    private Properties propiedades;
    private String archivoConfig = "config.properties";
    
    public Configuracion() {
        propiedades = new Properties();
        cargarConfiguracion();
    }
    
    private void cargarConfiguracion() {
        try {
            File archivo = new File(archivoConfig);
            if (archivo.exists()) {
                FileInputStream fis = new FileInputStream(archivo);
                propiedades.load(fis);
                fis.close();
            } else {
                // Crear configuración por defecto
                crearConfiguracionPorDefecto();
            }
        } catch (IOException e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            crearConfiguracionPorDefecto();
        }
    }
    
    private void crearConfiguracionPorDefecto() {
        propiedades.setProperty("umbral_angulo", "45");
        propiedades.setProperty("email_remitente", "");
        propiedades.setProperty("email_password", "");
        propiedades.setProperty("email_destinatario", "");
        propiedades.setProperty("api_key_mailjet", "");
        propiedades.setProperty("api_secret_mailjet", "");
        guardarConfiguracion();
    }
    
    public void guardarConfiguracion() {
        try {
            FileOutputStream fos = new FileOutputStream(archivoConfig);
            propiedades.store(fos, "Configuración del Sistema de Detección de Caídas");
            fos.close();
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
        }
    }
    
    // Getters
    public int getUmbralAngulo() {
        return Integer.parseInt(propiedades.getProperty("umbral_angulo", "45"));
    }
    
    public String getEmailRemitente() {
        return propiedades.getProperty("email_remitente", "");
    }
    
    public String getEmailPassword() {
        return propiedades.getProperty("email_password", "");
    }
    
    public String getEmailDestinatario() {
        return propiedades.getProperty("email_destinatario", "");
    }
    
    public String getApiKeyMailjet() {
        return propiedades.getProperty("api_key_mailjet", "");
    }
    
    public String getApiSecretMailjet() {
        return propiedades.getProperty("api_secret_mailjet", "");
    }
    
    // Setters
    public void setUmbralAngulo(int umbral) {
        propiedades.setProperty("umbral_angulo", String.valueOf(umbral));
    }
    
    public void setEmailRemitente(String email) {
        propiedades.setProperty("email_remitente", email);
    }
    
    public void setEmailPassword(String password) {
        propiedades.setProperty("email_password", password);
    }
    
    public void setEmailDestinatario(String email) {
        propiedades.setProperty("email_destinatario", email);
    }
    
    public void setApiKeyMailjet(String apiKey) {
        propiedades.setProperty("api_key_mailjet", apiKey);
    }
    
    public void setApiSecretMailjet(String apiSecret) {
        propiedades.setProperty("api_secret_mailjet", apiSecret);
    }
    
    // Métodos adicionales para compatibilidad
    public String getSmtpServer() {
        return propiedades.getProperty("smtp_server", "smtp.gmail.com");
    }
    
    public int getSmtpPort() {
        return Integer.parseInt(propiedades.getProperty("smtp_port", "587"));
    }
    
    public int getContadorConfirmacion() {
        return Integer.parseInt(propiedades.getProperty("contador_confirmacion", "3"));
    }
    
    public int getIntervaloAlerta() {
        return Integer.parseInt(propiedades.getProperty("intervalo_alerta", "60"));
    }
    
    public String getDbUrl() {
        return propiedades.getProperty("db_url", "jdbc:sqlite:caidas.db");
    }
    
    public String getDbUsuario() {
        return propiedades.getProperty("db_usuario", "");
    }
    
    public String getDbPassword() {
        return propiedades.getProperty("db_password", "");
    }
    
    public void setSmtpServer(String server) {
        propiedades.setProperty("smtp_server", server);
    }
    
    public void setSmtpPort(int port) {
        propiedades.setProperty("smtp_port", String.valueOf(port));
    }
    
    public void setContadorConfirmacion(int contador) {
        propiedades.setProperty("contador_confirmacion", String.valueOf(contador));
    }
    
    public void setIntervaloAlerta(int intervalo) {
        propiedades.setProperty("intervalo_alerta", String.valueOf(intervalo));
    }
    
    public void setDbUrl(String url) {
        propiedades.setProperty("db_url", url);
    }
    
    public void setDbUsuario(String usuario) {
        propiedades.setProperty("db_usuario", usuario);
    }
    
    public void setDbPassword(String password) {
        propiedades.setProperty("db_password", password);
    }
} 