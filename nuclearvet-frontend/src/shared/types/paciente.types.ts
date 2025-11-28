// Tipos para el módulo de Pacientes

export type Especie = 'PERRO' | 'GATO' | 'AVE' | 'ROEDOR' | 'REPTIL' | 'OTRO';

export type EstadoPaciente = 'ACTIVO' | 'TRATAMIENTO' | 'INACTIVO' | 'FALLECIDO';

export interface Paciente {
  id: number;
  nombre: string;
  especie: Especie;
  raza: string;
  edad: number;
  peso: number;
  color: string;
  sexo: 'MACHO' | 'HEMBRA';
  propietarioId: number;
  propietarioNombre: string;
  propietarioTelefono: string;
  propietarioEmail: string;
  estado: EstadoPaciente;
  observaciones?: string;
  fotoUrl?: string;
  fechaRegistro: string;
  ultimaVisita?: string;
  numeroHistoriaClinica: string;
}

export interface PacienteFormData {
  nombre: string;
  especie: Especie;
  raza: string;
  edad: number;
  peso: number;
  color: string;
  sexo: 'MACHO' | 'HEMBRA';
  propietarioId: number;
  estado: EstadoPaciente;
  observaciones?: string;
  fotoUrl?: string;
}

export interface PacienteFilters {
  busqueda?: string;
  especie?: Especie;
  estado?: EstadoPaciente;
  propietarioId?: number;
}
