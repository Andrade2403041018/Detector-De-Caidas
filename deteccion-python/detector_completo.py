import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # Solo errores críticos de TensorFlow

import absl.logging
absl.logging.set_verbosity(absl.logging.ERROR)

import cv2
import mediapipe as mp
import math
from collections import deque
from datetime import datetime
from notificador_email import NotificadorEmail
from base_datos_python import BaseDatosPython

mp_pose = mp.solutions.pose
pose = mp_pose.Pose()
mp_dibujo = mp.solutions.drawing_utils

class DetectorPosturasMediaPipe:
    def __init__(self):
        self.cap = None
        for idx in [0, 1, 2]:
            cap = cv2.VideoCapture(idx)
            cap.set(cv2.CAP_PROP_FRAME_WIDTH, 800)
            cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 600)
            if cap.isOpened():
                print(f"\u2705 Cámara abierta con índice {idx} (resolución 800x600)")
                self.cap = cap
                break
            else:
                cap.release()
        if self.cap is None or not self.cap.isOpened():
            print("\u274c Error: No se pudo abrir ninguna cámara (índices 0, 1, 2)")
            exit()
        self.historial_angulos = deque(maxlen=10)
        self.ultima_postura = None
        self.ultima_alerta = 0
        self.notificador = NotificadorEmail()
        self.notificaciones_habilitadas = False
        self.base_datos = BaseDatosPython()
        self.destinatario_actual = "cuidador@email.com"
        self.frames_ignorados = 0
        self.FRAMES_IGNORAR = 10

    def configurar_notificaciones(self, email, password, destinatario):
        self.notificador.configurar_credenciales(email, password, destinatario)
        self.notificaciones_habilitadas = True
        print("✅ Notificaciones por email configuradas")

    def calcular_angulo(self, p1, p2):
        x1, y1 = p1
        x2, y2 = p2
        dx = x2 - x1
        dy = y2 - y1
        angulo_rad = math.atan2(dx, dy)
        angulo_deg = abs(math.degrees(angulo_rad))
        return angulo_deg

    def detectar_caida(self, angulo):
        return angulo >= 60

    def ejecutar(self):
        print("🚀 Iniciando detector de caídas con MediaPipe...")
        print("📋 Presiona 'q' para salir")
        frame_count = 0
        while True:
            ret, frame = self.cap.read()
            if not ret:
                print("❌ Error al capturar video")
                break
            frame_count += 1
            if frame_count % 3 != 0:
                cv2.imshow('Detección de Caídas - MediaPipe', frame)
                if cv2.getWindowProperty('Detección de Caídas - MediaPipe', cv2.WND_PROP_VISIBLE) < 1:
                    print("\n❌ Ventana cerrada manualmente. Terminando el programa...")
                    break
                key = cv2.waitKey(1) & 0xFF
                if key == ord('q'):
                    break
                continue
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            resultado = pose.process(frame_rgb)
            if resultado.pose_landmarks:
                mp_dibujo.draw_landmarks(frame, resultado.pose_landmarks, mp_pose.POSE_CONNECTIONS)
                landmarks = resultado.pose_landmarks.landmark
                hombro_izq = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x,
                              landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
                cadera_izq = [landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].x,
                              landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].y]
                hombro_der = [landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].x,
                              landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].y]
                cadera_der = [landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].x,
                              landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].y]
                angulo_izq = self.calcular_angulo(hombro_izq, cadera_izq)
                angulo_der = self.calcular_angulo(hombro_der, cadera_der)
                angulo = min(angulo_izq, angulo_der)
                self.historial_angulos.append(angulo)
                if self.frames_ignorados < self.FRAMES_IGNORAR:
                    self.frames_ignorados += 1
                    self.ultima_postura = "Ignorado"
                    continue
                if self.detectar_caida(angulo):
                    cv2.putText(frame, f'¡CAIDA DETECTADA! Angulo: {int(angulo)}°',
                                (10, 40), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 3)
                    print("¡Alerta! Caída detectada!")
                    if self.ultima_postura != "Caida" or (datetime.now().timestamp() - self.ultima_alerta > 5):
                        email_enviado = False
                        if self.notificaciones_habilitadas:
                            email_enviado = self.notificador.enviar_alerta_caida(angulo)
                        self.base_datos.guardar_notificacion(
                            tipo="Caída detectada",
                            razon="Ángulo mayor o igual a 60°",
                            angulo=angulo,
                            email_enviado=email_enviado,
                            destinatario=self.destinatario_actual
                        )
                        self.base_datos.guardar_evento("Caída detectada", angulo)
                        self.ultima_alerta = datetime.now().timestamp()
                    self.ultima_postura = "Caida"
                else:
                    cv2.putText(frame, f'Normal. Angulo: {int(angulo)}°',
                                (10, 40), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
                    self.ultima_postura = "Normal"
            cv2.imshow('Detección de Caídas - MediaPipe', frame)
            if cv2.getWindowProperty('Deteccion de Caidas - MediaPipe', cv2.WND_PROP_VISIBLE) < 1:
                print("\n❌ Ventana cerrada manualmente. Terminando el programa...")
                break
            key = cv2.waitKey(1) & 0xFF
            if key == ord('q'):
                break
        self.cap.release()
        cv2.destroyAllWindows()
        print("👋 Detector detenido")

if __name__ == "__main__":
    detector = DetectorPosturasMediaPipe()
    detector.ejecutar() 