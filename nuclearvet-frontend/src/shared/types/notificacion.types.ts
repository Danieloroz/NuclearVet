// Tipos para el módulo de Notificaciones

export type TipoNotificacion = 'CITA' | 'PAGO' | 'ALERTA' | 'SISTEMA' | 'RECORDATORIO';

export type PrioridadNotificacion = 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE';

export interface Notificacion {
  id: number;
  tipo: TipoNotificacion;
  titulo: string;
  mensaje: string;
  prioridad: PrioridadNotificacion;
  leida: boolean;
  importante: boolean;
  usuarioId: number;
  fecha: string;
  accionUrl?: string;
  accionTexto?: string;
}

export interface NotificacionResumen {
  total: number;
  sinLeer: number;
  hoy: number;
  estaSemana: number;
  alertas: number;
}
