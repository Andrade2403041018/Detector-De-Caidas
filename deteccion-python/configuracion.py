# Configuración del Sistema de Detección de Caídas
# ================================================

# Configuración de la cámara
CAMARA_ID = 0  # 0 = cámara web principal

# Configuración de detección
UMBRAL_ANGULO = 45  # Grados para detectar caída
CONTADOR_CONFIRMACION = 5  # Frames consecutivos para confirmar caída
INTERVALO_ALERTA = 5  # Segundos entre alertas

# Configuración de email (Gmail)
EMAIL_CONFIG = {
    "remitente": "tu_email@gmail.com",  # Cambiar por tu email
    "password": "tu_password_app",      # Contraseña de aplicación
    "destinatario": "cuidador@email.com", # Email del cuidador
    "smtp_server": "smtp.gmail.com",
    "smtp_port": 587,
    "intervalo_minimo": 60  # Segundos entre emails
}

# Configuración de logs
LOG_CONFIG = {
    "archivo_log": "../datos/eventos.log",
    "formato_timestamp": "%Y-%m-%d %H:%M:%S"
}

# Configuración de interfaz
INTERFAZ_CONFIG = {
    "titulo_ventana": "Detector de Caidas - Sistema PEF",
    "mostrar_angulo": True,
    "mostrar_contador": True,
    "mostrar_umbral": True,
    "mostrar_estado_email": True
}

# Configuración de colores (BGR)
COLORES = {
    "verde": (0, 255, 0),      # Contorno detectado
    "azul": (255, 0, 0),       # Rectángulo de ángulo
    "rojo": (0, 0, 255),       # Alerta de caída
    "amarillo": (0, 255, 255), # Inclinación detectada
    "blanco": (255, 255, 255), # Texto normal
    "verde_claro": (0, 255, 0), # Email activo
    "rojo_claro": (0, 0, 255)   # Email inactivo
}

# Configuración de filtros
FILTROS = {
    "area_minima_contorno": 5000,  # Área mínima para considerar contorno válido
    "filtro_ruido": 300           # Área mínima para dibujar contorno
}

# Mensajes del sistema
MENSAJES = {
    "inicio": "🎥 Iniciando detector de caídas completo...",
    "alerta": "🚨 ALERTA DE CAÍDA DETECTADA",
    "email_enviado": "✅ Email enviado exitosamente",
    "email_error": "❌ Error al enviar email",
    "email_no_config": "📧 Notificaciones por email no configuradas",
    "salida": "👋 Detector detenido"
}

# Controles del teclado
CONTROLES = {
    "salir": "q",
    "reiniciar": "r", 
    "aumentar_umbral": ["+", "="],
    "disminuir_umbral": "-",
    "configurar_email": "e"
}

def mostrar_configuracion():
    """Muestra la configuración actual del sistema"""
    print("⚙️ CONFIGURACIÓN DEL SISTEMA")
    print("=" * 40)
    print(f"📐 Umbral de ángulo: {UMBRAL_ANGULO}°")
    print(f"📊 Contador de confirmación: {CONTADOR_CONFIRMACION}")
    print(f"⏰ Intervalo entre alertas: {INTERVALO_ALERTA}s")
    print(f"📧 Email remitente: {EMAIL_CONFIG['remitente']}")
    print(f"📨 Email destinatario: {EMAIL_CONFIG['destinatario']}")
    print("=" * 40)

def validar_configuracion():
    """Valida que la configuración sea correcta"""
    errores = []
    
    if UMBRAL_ANGULO < 10 or UMBRAL_ANGULO > 90:
        errores.append("Umbral de ángulo debe estar entre 10 y 90 grados")
    
    if CONTADOR_CONFIRMACION < 1:
        errores.append("Contador de confirmación debe ser mayor a 0")
    
    if INTERVALO_ALERTA < 1:
        errores.append("Intervalo de alerta debe ser mayor a 0")
    
    if EMAIL_CONFIG["remitente"] == "tu_email@gmail.com":
        errores.append("Debes configurar tu email de Gmail")
    
    if EMAIL_CONFIG["password"] == "tu_password_app":
        errores.append("Debes configurar tu contraseña de aplicación")
    
    if EMAIL_CONFIG["destinatario"] == "cuidador@email.com":
        errores.append("Debes configurar el email del destinatario")
    
    return errores

if __name__ == "__main__":
    mostrar_configuracion()
    errores = validar_configuracion()
    
    if errores:
        print("\n❌ Errores en la configuración:")
        for error in errores:
            print(f"  - {error}")
        print("\n💡 Edita este archivo para corregir los errores")
    else:
        print("\n✅ Configuración válida") 