import api from '../../../shared/services/api';
import type { Paciente, PacienteFormData, PacienteFilters } from '../../../shared/types/paciente.types';

export const pacienteService = {
  // RF2.1: Listar pacientes
  getAll: async (filters?: PacienteFilters): Promise<Paciente[]> => {
    const params = new URLSearchParams();
    if (filters?.busqueda) params.append('busqueda', filters.busqueda);
    if (filters?.especie) params.append('especie', filters.especie);
    if (filters?.estado) params.append('estado', filters.estado);
    if (filters?.propietarioId) params.append('propietarioId', filters.propietarioId.toString());
    
    const response = await api.get<Paciente[]>(`/pacientes?${params.toString()}`);
    return response.data;
  },

  // RF2.2: Obtener paciente por ID
  getById: async (id: number): Promise<Paciente> => {
    const response = await api.get<Paciente>(`/pacientes/${id}`);
    return response.data;
  },

  // RF2.3: Crear paciente
  create: async (data: PacienteFormData): Promise<Paciente> => {
    const response = await api.post<Paciente>('/pacientes', data);
    return response.data;
  },

  // RF2.4: Actualizar paciente
  update: async (id: number, data: Partial<PacienteFormData>): Promise<Paciente> => {
    const response = await api.put<Paciente>(`/pacientes/${id}`, data);
    return response.data;
  },

  // RF2.5: Eliminar paciente
  delete: async (id: number): Promise<void> => {
    await api.delete(`/pacientes/${id}`);
  },

  // Obtener historial médico del paciente
  getHistorialMedico: async (id: number) => {
    const response = await api.get(`/pacientes/${id}/historial`);
    return response.data;
  },

  // Buscar pacientes por propietario
  getByPropietario: async (propietarioId: number): Promise<Paciente[]> => {
    const response = await api.get<Paciente[]>(`/pacientes/propietario/${propietarioId}`);
    return response.data;
  },
};
