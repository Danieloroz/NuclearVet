// Tipos de usuario según el backend
export enum UserRole {
  ADMIN = 'ADMIN',
  VETERINARIO = 'VETERINARIO',
  RECEPCIONISTA = 'RECEPCIONISTA',
  CLIENTE = 'CLIENTE'
}

export interface User {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  rol: UserRole;
  telefono?: string;
  activo: boolean;
}

export interface AuthResponse {
  token: string;
  usuario: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  telefono?: string;
  rol: UserRole;
}
