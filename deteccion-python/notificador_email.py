import smtplib
import ssl
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import datetime
import os

class NotificadorEmail:
    def __init__(self):
        # Configuración del servidor SMTP (Gmail)
        self.smtp_server = "smtp.gmail.com"
        self.smtp_port = 587
        
        # Configuración del remitente (debes configurar esto)
        self.remitente_email = "tu_email@gmail.com"  # Cambiar por tu email
        self.remitente_password = "tu_password_app"  # Cambiar por tu contraseña de aplicación
        
        # Configuración del destinatario
        self.destinatario_email = "cuidador@email.com"  # Cambiar por el email del cuidador
        
        # Contador para evitar spam
        self.ultimo_envio = None
        self.intervalo_minimo = 60  # Segundos entre emails
        
    def configurar_credenciales(self, email, password, destinatario):
        """Configura las credenciales de email"""
        self.remitente_email = email
        self.remitente_password = password
        self.destinatario_email = destinatario
        print(f"✅ Email configurado: {email} -> {destinatario}")
    
    def crear_mensaje_alerta(self, timestamp, angulo_detectado=None):
        """Crea el mensaje de alerta"""
        asunto = "🚨 ALERTA: Posible caída detectada"
        
        # Crear el cuerpo del mensaje
        cuerpo = f"""
🚨 SISTEMA DE DETECCIÓN DE CAÍDAS - ALERTA

⏰ Fecha y hora: {timestamp}
📍 Ubicación: Sistema de monitoreo
📐 Ángulo detectado: {angulo_detectado}° (si aplica)

⚠️ Se ha detectado una posible caída en el área monitoreada.

🔍 ACCIONES RECOMENDADAS:
1. Verificar inmediatamente la situación
2. Contactar a la persona si es posible
3. Llamar a emergencias si es necesario
4. Revisar el sistema de monitoreo

📞 Números de emergencia:
- Emergencias: 911
- Bomberos: 911
- Policía: 911

📱 Este es un mensaje automático del sistema de detección de caídas.
Por favor, responde a este email para confirmar que has recibido la alerta.

---
Sistema Inteligente de Detección de Caídas
Proyecto PEF - Protección para Personas Mayores
        """
        
        return asunto, cuerpo
    
    def enviar_email(self, asunto, cuerpo):
        """Envía el email de alerta"""
        try:
            # Crear el mensaje
            mensaje = MIMEMultipart()
            mensaje["From"] = self.remitente_email
            mensaje["To"] = self.destinatario_email
            mensaje["Subject"] = asunto
            
            # Agregar el cuerpo del mensaje
            mensaje.attach(MIMEText(cuerpo, "plain", "utf-8"))
            
            # Crear contexto SSL
            contexto = ssl.create_default_context()
            
            # Conectar al servidor SMTP
            with smtplib.SMTP(self.smtp_server, self.smtp_port) as servidor:
                servidor.starttls(context=contexto)
                servidor.login(self.remitente_email, self.remitente_password)
                
                # Enviar el email
                texto = mensaje.as_string()
                servidor.sendmail(self.remitente_email, self.destinatario_email, texto)
                
            print(f"✅ Email enviado exitosamente a {self.destinatario_email}")
            return True
            
        except Exception as e:
            print(f"❌ Error al enviar email: {str(e)}")
            return False
    
    def enviar_alerta_caida(self, angulo_detectado=None):
        """Envía una alerta de caída por email"""
        # Verificar intervalo mínimo
        ahora = datetime.now()
        if (self.ultimo_envio and 
            (ahora - self.ultimo_envio).total_seconds() < self.intervalo_minimo):
            print("⏰ Esperando intervalo mínimo antes de enviar otro email...")
            return False
        
        # Crear y enviar el mensaje
        timestamp = ahora.strftime("%Y-%m-%d %H:%M:%S")
        asunto, cuerpo = self.crear_mensaje_alerta(timestamp, angulo_detectado)
        
        if self.enviar_email(asunto, cuerpo):
            self.ultimo_envio = ahora
            return True
        
        return False
    
    def configurar_gmail(self):
        """Guía para configurar Gmail"""
        print("\n📧 CONFIGURACIÓN DE GMAIL")
        print("=" * 40)
        print("Para usar Gmail, necesitas:")
        print("1. Activar la verificación en 2 pasos")
        print("2. Generar una contraseña de aplicación")
        print("3. Usar esa contraseña en lugar de tu contraseña normal")
        print("\n📋 Pasos:")
        print("1. Ve a https://myaccount.google.com/security")
        print("2. Activa 'Verificación en 2 pasos'")
        print("3. Ve a 'Contraseñas de aplicación'")
        print("4. Genera una nueva contraseña para 'Correo'")
        print("5. Usa esa contraseña en el sistema")
        print("=" * 40)

# Función de prueba
def probar_email():
    """Función para probar el sistema de email"""
    notificador = NotificadorEmail()
    
    print("🧪 PRUEBA DEL SISTEMA DE EMAIL")
    print("=" * 40)
    
    # Solicitar configuración
    email = input("📧 Tu email de Gmail: ")
    password = input("🔑 Tu contraseña de aplicación: ")
    destinatario = input("📨 Email del destinatario: ")
    
    # Configurar
    notificador.configurar_credenciales(email, password, destinatario)
    
    # Enviar prueba
    print("\n📤 Enviando email de prueba...")
    if notificador.enviar_alerta_caida(45):
        print("✅ Prueba exitosa!")
    else:
        print("❌ Error en la prueba")
        notificador.configurar_gmail()

if __name__ == "__main__":
    probar_email() 