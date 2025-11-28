// Tipos para el módulo de Citas

export type EstadoCita = 'PENDIENTE' | 'CONFIRMADA' | 'EN_CURSO' | 'COMPLETADA' | 'CANCELADA' | 'URGENTE';

export type TipoCita = 'CONSULTA' | 'VACUNACION' | 'CIRUGIA' | 'URGENCIA' | 'REVISION' | 'OTROS';

export interface Cita {
  id: number;
  fecha: string;
  hora: string;
  pacienteId: number;
  pacienteNombre: string;
  pacienteEspecie: string;
  propietarioId: number;
  propietarioNombre: string;
  propietarioTelefono: string;
  veterinarioId: number;
  veterinarioNombre: string;
  tipo: TipoCita;
  motivo: string;
  estado: EstadoCita;
  observaciones?: string;
  duracionEstimada: number; // en minutos
  fechaCreacion: string;
  fechaActualizacion?: string;
}

export interface CitaFormData {
  fecha: string;
  hora: string;
  pacienteId: number;
  veterinarioId: number;
  tipo: TipoCita;
  motivo: string;
  estado: EstadoCita;
  observaciones?: string;
  duracionEstimada: number;
}

export interface CitaFilters {
  fecha?: string;
  estado?: EstadoCita;
  veterinarioId?: number;
  pacienteId?: number;
  tipo?: TipoCita;
}

export interface CitaResumen {
  total: number;
  hoy: number;
  confirmadas: number;
  pendientes: number;
  urgentes: number;
}
