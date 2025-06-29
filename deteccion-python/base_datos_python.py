import sqlite3
import os
from datetime import datetime

class BaseDatosPython:
    def __init__(self, db_path="../interfaz-java/caidas.db"):
        """Inicializa la conexión a la base de datos SQLite"""
        self.db_path = db_path
        self.conexion = None
        self.conectar()
        self.crear_tabla_notificaciones()
        self.crear_tabla_eventos()
    
    def conectar(self):
        """Conecta a la base de datos SQLite"""
        try:
            # Verificar si el archivo existe
            if not os.path.exists(self.db_path):
                print(f"⚠️ Base de datos no encontrada en: {self.db_path}")
                print("📁 Creando nueva base de datos...")
            
            self.conexion = sqlite3.connect(self.db_path)
            print(f"✅ Conectado a base de datos: {self.db_path}")
        except Exception as e:
            print(f"❌ Error al conectar a la base de datos: {e}")
            self.conexion = None
    
    def crear_tabla_notificaciones(self):
        """Crea la tabla de notificaciones si no existe"""
        if not self.conexion:
            return
        
        try:
            cursor = self.conexion.cursor()
            sql = """
            CREATE TABLE IF NOT EXISTS notificaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                razon TEXT,
                angulo REAL,
                email_enviado BOOLEAN DEFAULT 0,
                destinatario TEXT,
                timestamp TEXT NOT NULL
            )
            """
            cursor.execute(sql)
            self.conexion.commit()
            print("✅ Tabla de notificaciones creada/verificada")
        except Exception as e:
            print(f"❌ Error al crear tabla de notificaciones: {e}")
    
    def guardar_notificacion(self, tipo, razon, angulo, email_enviado, destinatario):
        """Guarda una notificación en la base de datos"""
        if not self.conexion:
            print("❌ No hay conexión a la base de datos")
            return False
        
        try:
            cursor = self.conexion.cursor()
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            
            sql = """
            INSERT INTO notificaciones (tipo, razon, angulo, email_enviado, destinatario, timestamp)
            VALUES (?, ?, ?, ?, ?, ?)
            """
            cursor.execute(sql, (tipo, razon, angulo, email_enviado, destinatario, timestamp))
            self.conexion.commit()
            
            print(f"✅ Notificación guardada en BD: {tipo} - {razon}")
            return True
        except Exception as e:
            print(f"❌ Error al guardar notificación: {e}")
            return False
    
    def obtener_notificaciones(self):
        """Obtiene todas las notificaciones de la base de datos"""
        if not self.conexion:
            return []
        
        try:
            cursor = self.conexion.cursor()
            sql = "SELECT * FROM notificaciones ORDER BY id DESC"
            cursor.execute(sql)
            return cursor.fetchall()
        except Exception as e:
            print(f"❌ Error al obtener notificaciones: {e}")
            return []
    
    def obtener_contador_notificaciones(self):
        """Obtiene el número total de notificaciones"""
        if not self.conexion:
            return 0
        
        try:
            cursor = self.conexion.cursor()
            sql = "SELECT COUNT(*) FROM notificaciones"
            cursor.execute(sql)
            return cursor.fetchone()[0]
        except Exception as e:
            print(f"❌ Error al contar notificaciones: {e}")
            return 0
    
    def cerrar(self):
        """Cierra la conexión a la base de datos"""
        if self.conexion:
            self.conexion.close()
            print("🔒 Conexión a base de datos cerrada")

    def guardar_evento(self, tipo, angulo):
        if not self.conexion:
            print("❌ No hay conexión a la base de datos")
            return False
        try:
            cursor = self.conexion.cursor()
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            sql = """
            INSERT INTO eventos (tipo, angulo, timestamp)
            VALUES (?, ?, ?)
            """
            cursor.execute(sql, (tipo, angulo, timestamp))
            self.conexion.commit()
            print(f"✅ Evento guardado en BD: {tipo} - {angulo}")
            return True
        except Exception as e:
            print(f"❌ Error al guardar evento: {e}")
            return False

    def crear_tabla_eventos(self):
        """Crea la tabla de eventos si no existe"""
        if not self.conexion:
            return
        try:
            cursor = self.conexion.cursor()
            sql = """
            CREATE TABLE IF NOT EXISTS eventos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                angulo REAL,
                timestamp TEXT NOT NULL
            )
            """
            cursor.execute(sql)
            self.conexion.commit()
            print("✅ Tabla de eventos creada/verificada")
        except Exception as e:
            print(f"❌ Error al crear tabla de eventos: {e}")

# Función de prueba
def probar_base_datos():
    """Función para probar la conexión a la base de datos"""
    print("🧪 PRUEBA DE CONEXIÓN A BASE DE DATOS")
    print("=" * 50)
    
    bd = BaseDatosPython()
    
    if bd.conexion:
        print("✅ Conexión exitosa")
        
        # Probar guardar notificación
        print("\n📝 Probando guardar notificación...")
        exito = bd.guardar_notificacion(
            tipo="Caída detectada",
            razon="Movimiento brusco + ángulo bajo",
            angulo=25.5,
            email_enviado=True,
            destinatario="cuidador@email.com"
        )
        
        if exito:
            print("✅ Notificación guardada correctamente")
        
        # Mostrar contador
        contador = bd.obtener_contador_notificaciones()
        print(f"📊 Total de notificaciones: {contador}")
        
        bd.cerrar()
    else:
        print("❌ No se pudo conectar a la base de datos")

if __name__ == "__main__":
    probar_base_datos() 