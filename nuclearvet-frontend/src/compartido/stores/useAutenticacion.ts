import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { UsuarioAutenticado } from '../tipos';

interface EstadoAutenticacion {
  usuario: UsuarioAutenticado | null;
  estaAutenticado: boolean;
  iniciarSesion: (usuario: UsuarioAutenticado) => void;
  cerrarSesion: () => void;
  actualizarUsuario: (usuario: Partial<UsuarioAutenticado>) => void;
}

export const useAutenticacion = create<EstadoAutenticacion>()(
  persist(
    (set) => ({
      usuario: null,
      estaAutenticado: false,

      iniciarSesion: (usuario) => {
        localStorage.setItem('token', usuario.token);
        set({ usuario, estaAutenticado: true });
      },

      cerrarSesion: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
        set({ usuario: null, estaAutenticado: false });
      },

      actualizarUsuario: (datosActualizados) =>
        set((state) => ({
          usuario: state.usuario
            ? { ...state.usuario, ...datosActualizados }
            : null,
        })),
    }),
    {
      name: 'autenticacion-storage',
    }
  )
);
