// Tipos para el módulo de autenticación

export interface CredencialesLogin {
  email: string;
  contraseña: string;
}

export interface DatosRegistro {
  nombre: string;
  apellido: string;
  email: string;
  contraseña: string;
  telefono: string;
  direccion?: string;
}

export interface RecuperacionContraseña {
  email: string;
}

export interface RestablecerContraseña {
  token: string;
  nuevaContraseña: string;
  confirmarContraseña: string;
}

export interface RespuestaLogin {
  token: string;
  tipo: string;
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  rol: string;
}
