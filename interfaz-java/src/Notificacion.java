/**
 * Clase para representar una notificacion en la base de datos
 */
public class Notificacion {
    private int id;
    private String tipo;
    private String razon;
    private double angulo;
    private boolean emailEnviado;
    private String destinatario;
    private String timestamp;
    
    public Notificacion(int id, String tipo, String razon, double angulo, boolean emailEnviado, String destinatario, String timestamp) {
        this.id = id;
        this.tipo = tipo;
        this.razon = razon;
        this.angulo = angulo;
        this.emailEnviado = emailEnviado;
        this.destinatario = destinatario;
        this.timestamp = timestamp;
    }
    
    // Getters
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getRazon() { return razon; }
    public double getAngulo() { return angulo; }
    public boolean isEmailEnviado() { return emailEnviado; }
    public String getDestinatario() { return destinatario; }
    public String getTimestamp() { return timestamp; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setRazon(String razon) { this.razon = razon; }
    public void setAngulo(double angulo) { this.angulo = angulo; }
    public void setEmailEnviado(boolean emailEnviado) { this.emailEnviado = emailEnviado; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return String.format("Notificacion [ID: %d, Tipo: %s, Razon: %s, Angulo: %.1f°, Email: %s, Destinatario: %s, Fecha: %s]",
                id, tipo, razon, angulo, emailEnviado ? "Enviado" : "No enviado", destinatario, timestamp);
    }
} 