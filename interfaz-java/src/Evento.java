/**
 * Clase para representar un evento de caída en el sistema
 */
public class Evento {
    private int id;
    private String tipo;
    private double angulo;
    private String timestamp;

    public Evento(int id, String tipo, double angulo, String timestamp) {
        this.id = id;
        this.tipo = tipo;
        this.angulo = angulo;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public double getAngulo() {
        return angulo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setAngulo(double angulo) {
        this.angulo = angulo;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Evento #%d: %s (Angulo: %.1f°) - %s", 
                           id, tipo, angulo, timestamp);
    }

    /**
     * Obtiene una representación formateada para mostrar en la interfaz
     */
    public String getFormatoInterfaz() {
        return String.format("ALERTA %s | Angulo: %.1f° | %s", tipo, angulo, timestamp);
    }

    /**
     * Obtiene una representación para exportar a CSV
     */
    public String getFormatoCSV() {
        return String.format("%d,%s,%.1f,%s", id, tipo, angulo, timestamp);
    }
} 