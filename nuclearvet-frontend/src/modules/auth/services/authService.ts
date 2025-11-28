import api from '../../shared/services/api';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/types/auth.types';

export const authService = {
  // RF1.3: Inicio de sesión
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  // Registro de usuario
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/usuarios', data);
    return response.data;
  },

  // RF1.4: Recuperación de contraseña
  forgotPassword: async (email: string): Promise<void> => {
    await api.post('/auth/forgot-password', { email });
  },

  // Resetear contraseña
  resetPassword: async (token: string, newPassword: string): Promise<void> => {
    await api.post('/auth/reset-password', { token, newPassword });
  },

  // Obtener perfil del usuario actual
  getProfile: async () => {
    const response = await api.get('/usuarios/me');
    return response.data;
  },

  // Actualizar perfil
  updateProfile: async (data: Partial<RegisterRequest>) => {
    const response = await api.put('/usuarios/me', data);
    return response.data;
  },

  // Cambiar contraseña
  changePassword: async (currentPassword: string, newPassword: string): Promise<void> => {
    await api.post('/auth/change-password', { currentPassword, newPassword });
  },
};
