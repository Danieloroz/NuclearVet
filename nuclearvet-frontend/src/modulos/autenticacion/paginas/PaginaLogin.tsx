import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Stethoscope } from 'lucide-react';

export const PaginaLogin = () => {
  const navigate = useNavigate();
  const [credenciales, setCredenciales] = useState({
    email: '',
    contrasena: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Implementar lógica de autenticación con el backend
    console.log('Iniciando sesión...', credenciales);
    // Temporal: navegar al dashboard
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-gris-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo y título */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-primario-600 rounded-2xl mb-4">
            <Stethoscope className="text-white" size={40} />
          </div>
          <h1 className="text-2xl font-semibold text-gris-900 mb-1">VetCare Pro</h1>
          <p className="text-gris-600">Sistema de Gestión Veterinaria</p>
        </div>

        {/* Formulario de login */}
        <div className="bg-white rounded-2xl shadow-sm border border-gris-200 p-8">
          <div className="mb-6">
            <h2 className="text-xl font-semibold text-gris-900">Iniciar Sesión</h2>
            <p className="text-sm text-gris-600 mt-1">
              Ingrese sus credenciales para acceder al sistema
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Correo electrónico */}
            <div>
              <label 
                htmlFor="email" 
                className="block text-sm font-medium text-gris-900 mb-2"
              >
                Correo Electrónico
              </label>
              <input
                id="email"
                type="email"
                placeholder="usuario@ejemplo.com"
                value={credenciales.email}
                onChange={(e) => setCredenciales({ ...credenciales, email: e.target.value })}
                className="
                  w-full px-4 py-3 rounded-lg
                  bg-gris-50 border border-gris-200
                  text-gris-900 placeholder-gris-400
                  focus:outline-none focus:ring-2 focus:ring-primario-500 focus:border-transparent
                  transition-all
                "
                required
              />
            </div>

            {/* Contraseña */}
            <div>
              <label 
                htmlFor="contrasena" 
                className="block text-sm font-medium text-gris-900 mb-2"
              >
                Contraseña
              </label>
              <input
                id="contrasena"
                type="password"
                placeholder="Ingrese su contraseña"
                value={credenciales.contrasena}
                onChange={(e) => setCredenciales({ ...credenciales, contrasena: e.target.value })}
                className="
                  w-full px-4 py-3 rounded-lg
                  bg-gris-50 border border-gris-200
                  text-gris-900 placeholder-gris-400
                  focus:outline-none focus:ring-2 focus:ring-primario-500 focus:border-transparent
                  transition-all
                "
                required
              />
            </div>

            {/* Olvidó contraseña */}
            <div className="text-right">
              <button
                type="button"
                className="text-sm text-primario-600 hover:text-primario-700 font-medium"
              >
                ¿Olvidó su contraseña?
              </button>
            </div>

            {/* Botón de login */}
            <button
              type="submit"
              className="
                w-full py-3.5 rounded-lg
                bg-primario-600 hover:bg-primario-700
                text-white font-medium
                transition-colors duration-150
                focus:outline-none focus:ring-2 focus:ring-primario-500 focus:ring-offset-2
              "
            >
              Ingresar al Sistema
            </button>
          </form>
        </div>

        {/* Mensaje de seguridad */}
        <p className="text-center text-sm text-gris-500 mt-6">
          Sistema seguro con encriptación de datos
        </p>

        {/* Botón de ayuda flotante */}
        <button
          className="
            fixed bottom-6 right-6
            w-12 h-12 rounded-full
            bg-gris-800 hover:bg-gris-900
            text-white
            shadow-lg hover:shadow-xl
            transition-all
            flex items-center justify-center
          "
          aria-label="Ayuda"
        >
          ?
        </button>
      </div>
    </div>
  );
};
