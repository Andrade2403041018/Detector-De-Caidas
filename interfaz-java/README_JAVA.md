# Interfaz Java - Sistema de Detección de Caídas

## Fase 3: Interfaz de Monitoreo y Gestión

Esta carpeta contiene la interfaz Java del Sistema Inteligente de Detección de Caídas y Emergencias para Personas Mayores.

## 📁 Estructura del Proyecto

```
interfaz-java/
├── src/
│   ├── PanelMonitoreo.java      # Ventana principal del sistema
│   ├── Configuracion.java       # Gestión de configuración
│   ├── BaseDatos.java           # Manejo de base de datos SQLite
│   ├── Evento.java              # Clase para representar eventos
│   ├── VentanaHistorial.java    # Ventana de historial de eventos
│   └── VentanaConfiguracion.java # Ventana de configuración
├── bin/                         # Archivos compilados (.class)
├── lib/                         # Librerías externas
└── README_JAVA.md              # Este archivo
```

## 🚀 Requisitos

- **Java 17 LTS** o superior
- **SQLite JDBC Driver** (incluido automáticamente)
- **Sistema operativo**: Windows, macOS, Linux

## 🔧 Compilación

### Opción 1: Compilación Manual

```bash
# Navegar al directorio del proyecto
cd interfaz-java

# Crear directorio bin si no existe
mkdir -p bin

# Compilar todos los archivos Java
javac -d bin src/*.java
```

### Opción 2: Script de Compilación

```bash
# En Windows
compile.bat

# En Linux/macOS
./compile.sh
```

## 🎯 Ejecución

### Opción 1: Ejecución Directa

```bash
# Desde el directorio interfaz-java
java -cp bin PanelMonitoreo
```

### Opción 2: Script de Ejecución

```bash
# En Windows
run.bat

# En Linux/macOS
./run.sh
```

## 🖥️ Funcionalidades de la Interfaz

### Panel Principal de Monitoreo

- **Estado del Sistema**: Muestra si el sistema está activo o inactivo
- **Información en Tiempo Real**: Última detección, contador de eventos
- **Logs en Vivo**: Área de texto con logs del sistema
- **Controles Principales**: Iniciar/Detener sistema, configuración, historial

### Ventana de Configuración

- **Configuración de Detección**:
  - Umbral de ángulo (10° - 90°)
  - Contador de confirmación (1-20)
  - Intervalo de alerta (1-60 segundos)

- **Configuración de Email**:
  - Email remitente
  - Contraseña de aplicación
  - Email destinatario
  - Servidor SMTP y puerto

- **Configuración de Base de Datos**:
  - URL de conexión
  - Usuario y contraseña

### Ventana de Historial

- **Tabla de Eventos**: Muestra todos los eventos registrados
- **Información Detallada**: ID, tipo, ángulo, fecha/hora
- **Exportación CSV**: Exportar historial a archivo CSV
- **Actualización en Tiempo Real**: Recargar datos desde la base de datos

## 🗄️ Base de Datos

### Estructura de la Tabla

```sql
CREATE TABLE eventos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo TEXT NOT NULL,
    angulo REAL,
    timestamp TEXT NOT NULL
);
```

### Archivo de Base de Datos

- **Ubicación**: `caidas.db` (en el directorio raíz)
- **Tipo**: SQLite
- **Acceso**: Automático desde la aplicación

## ⚙️ Configuración

### Archivo de Configuración

- **Ubicación**: `config.properties`
- **Formato**: Propiedades Java estándar
- **Persistencia**: Se guarda automáticamente

### Configuración por Defecto

```properties
# Detección
umbral.angulo=45
contador.confirmacion=5
intervalo.alerta=5

# Email
email.remitente=
email.password=
email.destinatario=
smtp.server=smtp.gmail.com
smtp.port=587

# Base de datos
db.url=jdbc:sqlite:caidas.db
db.usuario=
db.password=
```

## 🔗 Integración con Python

### Comunicación

La interfaz Java está diseñada para trabajar en conjunto con el sistema Python:

1. **Detección**: El sistema Python detecta caídas y registra eventos
2. **Base de Datos**: Ambos sistemas comparten la misma base SQLite
3. **Notificaciones**: La interfaz Java puede enviar notificaciones de prueba

### Flujo de Trabajo

1. **Iniciar Sistema Python**: Ejecutar el detector de caídas
2. **Abrir Interfaz Java**: Ejecutar `PanelMonitoreo`
3. **Monitorear**: Ver eventos en tiempo real
4. **Configurar**: Ajustar parámetros según necesidades
5. **Exportar**: Generar reportes CSV del historial

## 🛠️ Desarrollo

### Agregar Nuevas Funcionalidades

1. **Crear nueva clase Java** en `src/`
2. **Compilar** con `javac -d bin src/*.java`
3. **Probar** ejecutando la aplicación
4. **Documentar** cambios en este README

### Estructura de Clases

- **PanelMonitoreo**: Ventana principal y control del sistema
- **Configuracion**: Gestión de parámetros del sistema
- **BaseDatos**: Operaciones de base de datos SQLite
- **Evento**: Modelo de datos para eventos de caídas
- **VentanaHistorial**: Interfaz para ver historial
- **VentanaConfiguracion**: Interfaz para configurar sistema

## 🐛 Solución de Problemas

### Error de Compilación

```bash
# Verificar versión de Java
java -version

# Limpiar archivos compilados
rm -rf bin/*

# Recompilar
javac -d bin src/*.java
```

### Error de Base de Datos

```bash
# Verificar archivo de base de datos
ls -la caidas.db

# Eliminar y recrear base de datos
rm caidas.db
java -cp bin PanelMonitoreo
```

### Error de Configuración

```bash
# Verificar archivo de configuración
cat config.properties

# Eliminar y recrear configuración
rm config.properties
java -cp bin PanelMonitoreo
```

## 📊 Características Técnicas

- **Interfaz**: Swing (Java GUI)
- **Base de Datos**: SQLite con JDBC
- **Configuración**: Properties file
- **Logs**: Área de texto en tiempo real
- **Exportación**: Formato CSV
- **Compatibilidad**: Multiplataforma

## 🎯 Próximos Pasos

1. **Integración Completa**: Conectar con sistema Python en tiempo real
2. **Notificaciones Push**: Implementar notificaciones push
3. **Reportes Avanzados**: Gráficos y estadísticas
4. **Interfaz Web**: Versión web de la interfaz
5. **Móvil**: Aplicación móvil para monitoreo

## 📞 Soporte

Para problemas o preguntas sobre la interfaz Java:

1. Revisar este README
2. Verificar logs de la aplicación
3. Comprobar configuración del sistema
4. Verificar conectividad de base de datos

---

**Versión**: 1.0  
**Fecha**: 2024  
**Autor**: Sistema de Detección de Caídas 