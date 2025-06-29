import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class NotificadorEmailJava {
    public static boolean enviar(String remitente, String password, String destinatario, String smtpServer, int smtpPort, String asunto, String cuerpo) {
        try {
            // API Key y Secret de Mailjet
            String apiKey = "a03f1fad3ccad9587ad11501d9fae5ed";
            String apiSecret = "aaa47e54d5a04a241bb5506ed6c25d82";
            String fromEmail = "detectorcaidas9@gmail.com";

            // Escapar comillas y saltos de línea
            String safeAsunto = asunto.replace("\"", "\\\"").replace("\n", "\\n");
            String safeCuerpo = cuerpo.replace("\"", "\\\"").replace("\n", "\\n");

            String json = "{"
                + "\"Messages\":[{"
                + "\"From\":{\"Email\":\"" + fromEmail + "\",\"Name\":\"Sistema Caidas\"},"
                + "\"To\":[{\"Email\":\"" + destinatario + "\",\"Name\":\"Destinatario\"}],"
                + "\"Subject\":\"" + safeAsunto + "\"," 
                + "\"TextPart\":\"" + safeCuerpo + "\"," 
                + "\"HTMLPart\":\"<h3>" + safeCuerpo + "</h3>\""
                + "}]"
                + "}";

            URL url = new URL("https://api.mailjet.com/v3.1/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            // Autenticación básica con API Key y Secret
            String auth = apiKey + ":" + apiSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes("UTF-8"));
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Leer respuesta de error si la hay
            if (responseCode != 200) {
                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = errorStream.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    System.out.println("Respuesta de error Mailjet: " + result.toString("UTF-8"));
                }
                System.out.println("Error al enviar correo. Revisa la consola para más detalles.");
                return false;
            } else {
                System.out.println("¡Correo enviado correctamente!");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al enviar email por API Mailjet: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 