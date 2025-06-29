import os
os.environ['ULTRALYTICS_LOGGING'] = 'False'
from ultralytics.utils import LOGGER
LOGGER.setLevel("ERROR")

import cv2
import numpy as np
from ultralytics import YOLO
from collections import deque
from datetime import datetime
import time
from notificador_email import NotificadorEmail
from base_datos_python import BaseDatosPython

class DetectorPosturasYOLO:
    def __init__(self):
        # Configuración
        self.POSE_MODEL = 'yolov8n-pose.pt'
        self.HISTORIA_ANGULOS = 10
        self.UMBRAL_CAIDA_ANGULO = 45
        self.UMBRAL_CAIDA_ANGULO_SUP = 135
        self.UMBRAL_MOV_BRUSCO = 30
        self.model = YOLO(self.POSE_MODEL)
        self.cap = cv2.VideoCapture(0)
        self.historial_angulos = deque(maxlen=self.HISTORIA_ANGULOS)
        self.ultima_postura = None
        self.ultima_alerta = 0
        self.keypoint_names = [
            "Nariz", "Ojo Izq", "Ojo Der", "Oreja Izq", "Oreja Der",
            "Hombro Izq", "Hombro Der", "Codo Izq", "Codo Der",
            "Muñeca Izq", "Muñeca Der", "Cadera Izq", "Cadera Der",
            "Rodilla Izq", "Rodilla Der", "Tobillo Izq", "Tobillo Der"
        ]

        # Sistema de notificaciones
        self.notificador = NotificadorEmail()
        self.notificaciones_habilitadas = False
        
        # Base de datos
        self.base_datos = BaseDatosPython()
        self.destinatario_actual = "cuidador@email.com"

        # Ignorar frames iniciales para evitar falsas alarmas
        self.frames_ignorados = 0
        self.FRAMES_IGNORAR = 10

        # Verificar cámara
        if not self.cap.isOpened():
            print("❌ Error: No se pudo abrir la cámara")
            exit()

    def configurar_notificaciones(self, email, password, destinatario):
        """Configura el sistema de notificaciones por email"""
        self.notificador.configurar_credenciales(email, password, destinatario)
        self.notificaciones_habilitadas = True
        print("✅ Notificaciones por email configuradas")

    def calcular_angulo_vertical(self, p1, p2):
        """Calcula el ángulo entre dos puntos respecto a la vertical"""
        dx = p2[0] - p1[0]
        dy = p2[1] - p1[1]
        angulo = np.degrees(np.arctan2(dx, dy))
        angulo = abs(angulo)
        return angulo

    def clasificar_postura(self, keypoints):
        """Clasifica la postura según los puntos clave principales"""
        try:
            l_shoulder = keypoints[5][:2]
            r_shoulder = keypoints[6][:2]
            l_hip = keypoints[11][:2]
            r_hip = keypoints[12][:2]
            centro_hombros = ((l_shoulder[0] + r_shoulder[0]) / 2, (l_shoulder[1] + r_shoulder[1]) / 2)
            centro_caderas = ((l_hip[0] + r_hip[0]) / 2, (l_hip[1] + r_hip[1]) / 2)
            angulo = self.calcular_angulo_vertical(centro_hombros, centro_caderas)
            if angulo < 30:
                return "Acostado (boca arriba/abajo)", angulo
            elif angulo < 60:
                return "Acostado de lado", angulo
            elif angulo < 120:
                return "De pie", angulo
            elif angulo < 150:
                return "Sentado", angulo
            else:
                return "Acostado (boca arriba/abajo)", angulo
        except Exception:
            return "Desconocida", 0

    def detectar_caida(self, angulo):
        """Detecta caída por ángulo"""
        return (angulo <= self.UMBRAL_CAIDA_ANGULO) or (angulo >= self.UMBRAL_CAIDA_ANGULO_SUP)

    def detectar_movimiento_brusco(self):
        """Detecta movimiento brusco por cambio de ángulo"""
        if len(self.historial_angulos) < 2:
            return False
        cambio = abs(self.historial_angulos[-1] - self.historial_angulos[-2])
        return cambio > self.UMBRAL_MOV_BRUSCO

    def detectar_caida_por_movimiento_brusco(self):
        """Detecta caída por movimiento brusco que resulte en ángulo bajo"""
        if len(self.historial_angulos) < 2:
            return False
        # Movimiento brusco + ángulo actual ≤ 45°
        cambio = abs(self.historial_angulos[-1] - self.historial_angulos[-2])
        angulo_actual = self.historial_angulos[-1]
        return (cambio > self.UMBRAL_MOV_BRUSCO) and (angulo_actual <= self.UMBRAL_CAIDA_ANGULO)

    def log_evento(self, evento, angulo):
        """Guarda el evento en un log"""
        with open("eventos_yolo.log", "a", encoding="utf-8") as f:
            f.write(f"{datetime.now()} - {evento} (Ángulo: {angulo:.1f}°)\n")

    def analizar_partes(self, frame, keypoints):
        h, w = frame.shape[:2]
        for i, (x, y) in enumerate(keypoints):
            if x > 0 and y > 0:
                cv2.circle(frame, (int(x), int(y)), 6, (0, 0, 255), -1)
                cv2.putText(frame, self.keypoint_names[i], (int(x)+5, int(y)-5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,255,0), 1)
        # Ejemplo: alerta si la cabeza (nariz) está cerca del suelo (parte baja de la imagen)
        cabeza = keypoints[0]
        if cabeza[1] > h * 0.85:
            cv2.putText(frame, "ALERTA: Cabeza cerca del suelo!", (10, h-20), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255), 2)

    def generar_alerta(self, angulo_detectado):
        """Genera una alerta de caída completa"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"\n🚨 ALERTA DE CAÍDA DETECTADA - {timestamp}")
        print(f"📐 Ángulo detectado: {angulo_detectado:.1f}°")
        print("🔊 Reproduciendo sonido de alerta...")
        
        # Enviar notificación por email si está configurado
        if self.notificaciones_habilitadas:
            print("📧 Enviando notificación por email...")
            if self.notificador.enviar_alerta_caida(angulo_detectado):
                print("✅ Email enviado exitosamente")
            else:
                print("❌ Error al enviar email")
        else:
            print("📧 Notificaciones por email no configuradas")
        
        print("📱 Enviando mensaje al cuidador...")
        
        # Guardar en archivo de log
        self.log_evento("Caida detectada", angulo_detectado)

    def detectar_caida_avanzada(self, keypoints, angulo, frame):
        # Solo permitir detección avanzada si el ángulo está en el rango de caída
        if not (angulo <= self.UMBRAL_CAIDA_ANGULO or angulo >= self.UMBRAL_CAIDA_ANGULO_SUP):
            return False
        h, w = frame.shape[:2]
        cabeza = keypoints[0]
        cadera_izq = keypoints[11]
        cadera_der = keypoints[12]
        tobillo_izq = keypoints[15]
        tobillo_der = keypoints[16]
        hombro_izq = keypoints[5]
        hombro_der = keypoints[6]
        # 1. Cabeza cerca del suelo
        if cabeza[1] > h * 0.85:
            cv2.putText(frame, "CAIDA: Cabeza cerca del suelo", (10, h-60), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255), 2)
            return True
        # 2. Caderas cerca del suelo
        if (cadera_izq[1] > h * 0.85) or (cadera_der[1] > h * 0.85):
            cv2.putText(frame, "CAIDA: Caderas cerca del suelo", (10, h-40), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255), 2)
            return True
        # 3. Eje hombros-caderas horizontal (ángulo bajo o alto)
        if (angulo <= self.UMBRAL_CAIDA_ANGULO) or (angulo >= self.UMBRAL_CAIDA_ANGULO_SUP):
            cv2.putText(frame, "CAIDA: Eje cuerpo horizontal", (10, h-20), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255), 2)
            return True
        # 4. Colapso del cuerpo (hombros y tobillos muy cerca verticalmente)
        centro_hombros = ((hombro_izq[0] + hombro_der[0]) / 2, (hombro_izq[1] + hombro_der[1]) / 2)
        centro_tobillos = ((tobillo_izq[0] + tobillo_der[0]) / 2, (tobillo_izq[1] + tobillo_der[1]) / 2)
        distancia_vertical = abs(centro_hombros[1] - centro_tobillos[1])
        if distancia_vertical < h * 0.2:
            cv2.putText(frame, "CAIDA: Cuerpo colapsado", (10, h-80), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255), 2)
            return True
        return False

    def ejecutar(self):
        """Ejecuta el detector en tiempo real"""
        print("🚀 Iniciando detector de caídas completo...")
        print(f"📐 Umbral de ángulo: 0°-45° o 135°-180° (caída)")
        print("📋 Presiona 'q' para salir")
        print("📋 Presiona 'r' para reiniciar")
        print("📋 Presiona '+' o '-' para ajustar el umbral (no afecta el rango de caída)")
        print("📋 Presiona 'e' para configurar email")
        print("=" * 50)
        while True:
            ret, frame = self.cap.read()
            if not ret:
                print("❌ Error al capturar video")
                break

            results = self.model(frame)
            annotated_frame = results[0].plot()

            # Procesar solo la primera persona detectada
            if results[0].keypoints is not None and len(results[0].keypoints.xy) > 0:
                keypoints = results[0].keypoints.xy[0].cpu().numpy()
                confs = results[0].keypoints.conf[0].cpu().numpy() if hasattr(results[0].keypoints, 'conf') else np.ones(len(keypoints))
                partes_faltantes = []
                # Índices de partes críticas
                partes_criticas = {
                    'cabeza': 0,
                    'hombro_izq': 5,
                    'hombro_der': 6,
                    'cadera_izq': 11,
                    'cadera_der': 12,
                    'tobillo_izq': 15,
                    'tobillo_der': 16
                }
                for nombre, idx in partes_criticas.items():
                    if confs[idx] < 0.5:
                        partes_faltantes.append(self.keypoint_names[idx])
                if partes_faltantes:
                    cv2.putText(annotated_frame, f"Advertencia: No se detecta: {', '.join(partes_faltantes)}", (10, 200), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0,140,255), 2)
                # Solo analizar si hay datos suficientes
                datos_suficientes = all(confs[idx] >= 0.5 for idx in partes_criticas.values())
                self.analizar_partes(annotated_frame, keypoints)
                postura, angulo = self.clasificar_postura(keypoints) if datos_suficientes else ("Desconocida", 0)

                # Si la postura es desconocida, el ángulo es 0.0 o faltan keypoints críticos, ignorar este frame
                if not datos_suficientes or postura == "Desconocida" or angulo == 0.0:
                    self.ultima_postura = postura
                    continue

                self.historial_angulos.append(angulo)

                # Mostrar postura y ángulo
                cv2.putText(annotated_frame, f"Postura: {postura}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,255,0), 2)
                cv2.putText(annotated_frame, f"Angulo: {angulo:.1f}°", (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,255,0), 2)

                # Ignorar los primeros frames tras iniciar o reiniciar
                if self.frames_ignorados < self.FRAMES_IGNORAR:
                    self.frames_ignorados += 1
                    self.ultima_postura = postura
                    continue

                # Solo analizar si hay historial suficiente
                if datos_suficientes and len(self.historial_angulos) >= 2:
                    caida_detectada = False
                    razon_caida = ""

                    # SOLO si el ángulo es <=45° o >=135° se permite cualquier alerta
                    if (angulo <= self.UMBRAL_CAIDA_ANGULO or angulo >= self.UMBRAL_CAIDA_ANGULO_SUP):
                        # Detección avanzada (cabeza/caderas cerca del suelo, colapso, eje horizontal)
                        if self.detectar_caida_avanzada(keypoints, angulo, annotated_frame):
                            caida_detectada = True
                            razon_caida = "Caída: Postura horizontal o partes cerca del suelo"
                        # O movimiento brusco + ángulo bajo
                        elif self.detectar_caida_por_movimiento_brusco():
                            caida_detectada = True
                            razon_caida = "Movimiento brusco + ángulo bajo"
                            cv2.putText(annotated_frame, "CAIDA: Movimiento brusco + ángulo bajo", (10, annotated_frame.shape[0]-100), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255), 2)
                    else:
                        # Si el ángulo NO está en el rango, NUNCA se permite alerta
                        caida_detectada = False
                        razon_caida = ""

                    # Detección de movimiento brusco normal (sin caída)
                    if not caida_detectada and self.detectar_movimiento_brusco():
                        cv2.putText(annotated_frame, "MOVIMIENTO BRUSCO!", (10, 140), cv2.FONT_HERSHEY_SIMPLEX, 1, (0,255,255), 3)
                        if self.ultima_postura != "Brusco" or (datetime.now().timestamp() - self.ultima_alerta > 5):
                            self.log_evento("Movimiento brusco", angulo)
                            self.ultima_alerta = datetime.now().timestamp()
                        self.ultima_postura = "Brusco"

                    # Mostrar alerta de caída si se detectó
                    if caida_detectada and angulo != 0.0 and postura != "Desconocida":
                        cv2.putText(annotated_frame, "CAIDA DETECTADA!", (10, 100), cv2.FONT_HERSHEY_SIMPLEX, 1, (0,0,255), 3)
                        cv2.putText(annotated_frame, f"Razon: {razon_caida}", (10, 130), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0,0,255), 2)
                        if self.ultima_postura != "Caida" or (datetime.now().timestamp() - self.ultima_alerta > 5):
                            # Enviar notificación por email
                            email_enviado = False
                            if self.notificaciones_habilitadas:
                                email_enviado = self.notificador.enviar_alerta_caida(angulo)
                            # Guardar en base de datos
                            self.base_datos.guardar_notificacion(
                                tipo="Caída detectada",
                                razon=razon_caida,
                                angulo=angulo,
                                email_enviado=email_enviado,
                                destinatario=self.destinatario_actual
                            )
                            # Guardar evento en base de datos
                            self.base_datos.guardar_evento("Caída detectada", angulo)
                            self.ultima_alerta = datetime.now().timestamp()
                        self.ultima_postura = "Caida"
                    else:
                        self.ultima_postura = postura
                else:
                    self.ultima_postura = postura

            cv2.imshow('YOLOv8 Postura y Caidas', annotated_frame)
            key = cv2.waitKey(1) & 0xFF
            if key == ord('q'):
                break
            elif key == ord('r'):
                print("🔄 Reiniciando detector...")
                self.historial_angulos.clear()
                self.ultima_postura = None
                self.frames_ignorados = 0
            elif key == ord('+') or key == ord('='):
                self.UMBRAL_CAIDA_ANGULO += 5
                print(f"📐 Umbral aumentado a: {self.UMBRAL_CAIDA_ANGULO}° (no afecta el rango de caída)")
            elif key == ord('-'):
                self.UMBRAL_CAIDA_ANGULO = max(10, self.UMBRAL_CAIDA_ANGULO - 5)
                print(f"📐 Umbral disminuido a: {self.UMBRAL_CAIDA_ANGULO}° (no afecta el rango de caída)")
            elif key == ord('e'):
                self.configurar_email_interactivo()
        self.cap.release()
        cv2.destroyAllWindows()
        print("👋 Detector detenido")

    def configurar_email_interactivo(self):
        """Configura el email de forma interactiva"""
        print("\n📧 CONFIGURACIÓN DE EMAIL")
        print("=" * 30)
        
        try:
            email = input("Tu email de Gmail: ")
            password = input("🔑 Tu contraseña de aplicación: ")
            destinatario = input("📨 Email del destinatario: ")
            
            self.configurar_notificaciones(email, password, destinatario)
            
        except KeyboardInterrupt:
            print("\n❌ Configuración cancelada")

if __name__ == "__main__":
    detector = DetectorPosturasYOLO()
    detector.ejecutar() 