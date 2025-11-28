import api from '../../../shared/services/api';
import type { Cita, CitaFormData, CitaFilters, CitaResumen } from '../../../shared/types/cita.types';

export const citaService = {
  // RF3.1: Listar citas
  getAll: async (filters?: CitaFilters): Promise<Cita[]> => {
    const params = new URLSearchParams();
    if (filters?.fecha) params.append('fecha', filters.fecha);
    if (filters?.estado) params.append('estado', filters.estado);
    if (filters?.veterinarioId) params.append('veterinarioId', filters.veterinarioId.toString());
    if (filters?.pacienteId) params.append('pacienteId', filters.pacienteId.toString());
    if (filters?.tipo) params.append('tipo', filters.tipo);
    
    const response = await api.get<Cita[]>(`/citas?${params.toString()}`);
    return response.data;
  },

  // RF3.2: Obtener cita por ID
  getById: async (id: number): Promise<Cita> => {
    const response = await api.get<Cita>(`/citas/${id}`);
    return response.data;
  },

  // RF3.3: Crear cita
  create: async (data: CitaFormData): Promise<Cita> => {
    const response = await api.post<Cita>('/citas', data);
    return response.data;
  },

  // RF3.4: Actualizar cita
  update: async (id: number, data: Partial<CitaFormData>): Promise<Cita> => {
    const response = await api.put<Cita>(`/citas/${id}`, data);
    return response.data;
  },

  // RF3.5: Cancelar cita
  cancel: async (id: number, motivo: string): Promise<void> => {
    await api.patch(`/citas/${id}/cancelar`, { motivo });
  },

  // Confirmar cita
  confirm: async (id: number): Promise<Cita> => {
    const response = await api.patch<Cita>(`/citas/${id}/confirmar`);
    return response.data;
  },

  // Obtener resumen de citas
  getResumen: async (): Promise<CitaResumen> => {
    const response = await api.get<CitaResumen>('/citas/resumen');
    return response.data;
  },

  // Obtener citas del día
  getHoy: async (): Promise<Cita[]> => {
    const hoy = new Date().toISOString().split('T')[0];
    return citaService.getAll({ fecha: hoy });
  },

  // Obtener disponibilidad de horarios
  getDisponibilidad: async (fecha: string, veterinarioId: number) => {
    const response = await api.get(`/citas/disponibilidad?fecha=${fecha}&veterinarioId=${veterinarioId}`);
    return response.data;
  },
};
