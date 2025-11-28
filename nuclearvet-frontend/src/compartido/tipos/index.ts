// Tipos base de la aplicación
export interface UsuarioAutenticado {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  rol: string; // 'ADMIN' | 'VETERINARIO' | 'RECEPCIONISTA' | 'CLIENTE'
  token: string;
}

export const RolUsuario = {
  ADMIN: 'ADMIN',
  VETERINARIO: 'VETERINARIO',
  RECEPCIONISTA: 'RECEPCIONISTA',
  CLIENTE: 'CLIENTE'
} as const;

export type TipoRol = typeof RolUsuario[keyof typeof RolUsuario];

export interface RespuestaAPI<T> {
  datos: T;
  mensaje?: string;
  exito: boolean;
}

export interface ErrorAPI {
  mensaje: string;
  codigo: string;
  detalles?: any;
}

export interface PaginacionParams {
  pagina: number;
  tamanio: number;
}

export interface RespuestaPaginada<T> {
  contenido: T[];
  totalElementos: number;
  totalPaginas: number;
  paginaActual: number;
  tamanio: number;
}
