# 🚨 Sistema Inteligente de Detección de Caídas

## 📋 Descripción
Sistema de detección de caídas y emergencias para personas mayores usando visión artificial y sensores. Detecta caídas basándose en el ángulo de inclinación (>45°) y envía notificaciones automáticas por email.

## 🎯 Características
- ✅ Detección de caídas por ángulo de inclinación (>45°)
- ✅ Análisis en tiempo real con cámara web
- ✅ Notificaciones automáticas por email
- ✅ Sistema de logs y registro de eventos
- ✅ Interfaz visual en tiempo real
- ✅ Configuración ajustable
- ✅ Protección contra spam de emails

## 🚀 Instalación y Uso

### Requisitos
- Python 3.13+
- Cámara web
- Cuenta de Gmail (para notificaciones)

### Dependencias Instaladas
```bash
opencv-python==4.8.1.78
numpy==2.3.1
scikit-learn==1.7.0
matplotlib==3.10.3
playsound==1.3.0
Pillow==11.2.1
```

## 📁 Estructura del Proyecto
```
PEF/
├── deteccion-python/
│   ├── detector_completo.py      # 🎯 DETECTOR PRINCIPAL
│   ├── notificador_email.py     # 📧 Sistema de emails
│   ├── configuracion.py         # ⚙️ Configuración
│   ├── detector_angulo_v2.py    # 📐 Detector por ángulo
│   ├── detector_camara.py       # 📷 Detector básico
│   └── detector_simple.py       # 🧪 Simulador
├── interfaz-java/               # ☕ Interfaz Java (próximamente)
├── datos/                       # 📊 Logs y eventos
├── arduino/                     # 🔌 Arduino (opcional)
├── requirements.txt             # 📦 Dependencias
└── README.md                    # 📖 Documentación
```

## 🎯 Uso del Sistema

### 1. Ejecutar el Detector Principal
```bash
cd deteccion-python
python detector_completo.py
```

### 2. Configurar Notificaciones por Email
1. **Activar verificación en 2 pasos** en tu cuenta de Gmail
2. **Generar contraseña de aplicación**:
   - Ve a https://myaccount.google.com/security
   - Activa "Verificación en 2 pasos"
   - Ve a "Contraseñas de aplicación"
   - Genera una nueva para "Correo"
3. **En el detector, presiona 'e'** para configurar email
4. **Ingresa tus credenciales**:
   - Tu email de Gmail
   - Tu contraseña de aplicación
   - Email del destinatario (cuidador)

### 3. Controles del Sistema
- **'q'**: Salir del programa
- **'r'**: Reiniciar detector
- **'+' o '='**: Aumentar umbral de ángulo
- **'-'**: Disminuir umbral de ángulo
- **'e'**: Configurar email

## ⚙️ Configuración

### Umbral de Ángulo
- **Por defecto**: 45°
- **Rango recomendado**: 30° - 60°
- **Ajuste en tiempo real**: Usa '+' y '-'

### Configuración de Email
Edita `configuracion.py` para configuración permanente:
```python
EMAIL_CONFIG = {
    "remitente": "tu_email@gmail.com",
    "password": "tu_password_app",
    "destinatario": "cuidador@email.com"
}
```

## 📊 Monitoreo y Logs

### Información en Pantalla
- **Ángulo actual**: Ángulo de inclinación detectado
- **Contador**: Frames consecutivos de caída
- **Umbral**: Ángulo configurado para detección
- **Estado Email**: ON/OFF según configuración

### Archivos de Log
- **Ubicación**: `datos/eventos.log`
- **Formato**: `YYYY-MM-DD HH:MM:SS - Caída detectada (Ángulo: XX.X°)`

## 📧 Sistema de Notificaciones

### Email de Alerta
El sistema envía emails automáticos con:
- ⏰ Fecha y hora del evento
- 📐 Ángulo detectado
- ⚠️ Acciones recomendadas
- 📞 Números de emergencia
- 📱 Información del sistema

### Protección Anti-Spam
- **Intervalo mínimo**: 60 segundos entre emails
- **Confirmación**: 5 frames consecutivos para activar
- **Logs**: Registro de todos los intentos

## 🔧 Personalización

### Ajustar Sensibilidad
```python
# En configuracion.py
UMBRAL_ANGULO = 45  # Más bajo = más sensible
CONTADOR_CONFIRMACION = 5  # Más alto = menos falsos positivos
```

### Cambiar Colores
```python
COLORES = {
    "verde": (0, 255, 0),      # Contorno detectado
    "rojo": (0, 0, 255),       # Alerta de caída
    # ... más colores
}
```

## 🧪 Pruebas

### Probar Sistema de Email
```bash
python notificador_email.py
```

### Verificar Configuración
```bash
python configuracion.py
```

### Simulador (sin cámara)
```bash
python detector_simple.py
```

## 📈 Próximos Pasos
1. ✅ Fase 1: Detector básico (COMPLETADO)
2. ✅ Fase 2: Detección por ángulo (COMPLETADO)
3. ✅ Fase 3: Notificaciones por email (COMPLETADO)
4. 🔄 Fase 4: Interfaz Java
5. ⏳ Fase 5: Base de datos SQLite
6. ⏳ Fase 6: Integración con WhatsApp/Telegram

## 🛠️ Tecnologías Usadas
- **Python**: Lógica principal
- **OpenCV**: Procesamiento de video y detección
- **NumPy**: Cálculos matemáticos
- **smtplib**: Envío de emails
- **SSL**: Seguridad en comunicaciones

## 📞 Soporte y Troubleshooting

### Problemas Comunes
1. **Cámara no detectada**: Verifica conexión y permisos
2. **Error de email**: Verifica contraseña de aplicación
3. **Falsos positivos**: Aumenta el umbral de ángulo
4. **No detecta caídas**: Disminuye el umbral de ángulo

### Logs de Error
- Revisa la consola para mensajes de error
- Verifica `datos/eventos.log` para eventos
- Comprueba configuración en `configuracion.py`

## 📄 Licencia
Proyecto educativo - Sistema de Detección de Caídas PEF 