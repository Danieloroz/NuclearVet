import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Stethoscope,
  Calendar,
  Package,
  Users,
  Bell,
  Settings,
  LogOut,
} from 'lucide-react';

interface MenuItem {
  nombre: string;
  ruta: string;
  icono: React.ReactNode;
  roles?: string[];
}

const menuItems: MenuItem[] = [
  {
    nombre: 'Dashboard',
    ruta: '/dashboard',
    icono: <LayoutDashboard size={20} />,
  },
  {
    nombre: 'Pacientes',
    ruta: '/pacientes',
    icono: <Stethoscope size={20} />,
  },
  {
    nombre: 'Citas',
    ruta: '/citas',
    icono: <Calendar size={20} />,
  },
  {
    nombre: 'Inventario',
    ruta: '/inventario',
    icono: <Package size={20} />,
  },
  {
    nombre: 'Usuarios',
    ruta: '/usuarios',
    icono: <Users size={20} />,
    roles: ['ADMIN', 'VETERINARIO'],
  },
  {
    nombre: 'Notificaciones',
    ruta: '/notificaciones',
    icono: <Bell size={20} />,
  },
  {
    nombre: 'Administración',
    ruta: '/administracion',
    icono: <Settings size={20} />,
    roles: ['ADMIN', 'VETERINARIO'],
  },
];

export const Sidebar = () => {
  const location = useLocation();
  
  // Mock del usuario actual - después vendrá del store de autenticación
  const usuarioActual = {
    nombre: 'María González',
    rol: 'Veterinaria Principal',
  };

  const handleCerrarSesion = () => {
    // TODO: Implementar lógica de cierre de sesión
    console.log('Cerrando sesión...');
  };

  return (
    <aside className="w-60 bg-white border-r border-gris-200 flex flex-col h-screen fixed left-0 top-0">
      {/* Logo y título */}
      <div className="p-6 border-b border-gris-200">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-primario-600 rounded-xl flex items-center justify-center">
            <Stethoscope className="text-white" size={24} />
          </div>
          <div>
            <h1 className="font-semibold text-gris-900">VetCare Pro</h1>
            <p className="text-xs text-gris-500">Gestión Integral</p>
          </div>
        </div>
      </div>

      {/* Navegación principal */}
      <nav className="flex-1 px-3 py-4 overflow-y-auto">
        <ul className="space-y-1">
          {menuItems.map((item) => {
            const estaActivo = location.pathname === item.ruta;
            
            return (
              <li key={item.ruta}>
                <Link
                  to={item.ruta}
                  className={`
                    flex items-center gap-3 px-4 py-3 rounded-lg
                    transition-colors duration-150 ease-in-out
                    ${
                      estaActivo
                        ? 'bg-primario-50 text-primario-600 font-medium'
                        : 'text-gris-700 hover:bg-gris-50'
                    }
                  `}
                >
                  <span className={estaActivo ? 'text-primario-600' : 'text-gris-400'}>
                    {item.icono}
                  </span>
                  <span>{item.nombre}</span>
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* Usuario actual y cerrar sesión */}
      <div className="p-4 border-t border-gris-200">
        <div className="mb-3">
          <p className="text-sm font-medium text-gris-900">{usuarioActual.nombre}</p>
          <p className="text-xs text-gris-500">{usuarioActual.rol}</p>
        </div>
        
        <button
          onClick={handleCerrarSesion}
          className="
            w-full flex items-center justify-center gap-2
            px-4 py-2.5 rounded-lg
            text-peligro-600 hover:bg-peligro-50
            transition-colors duration-150
            font-medium text-sm
          "
        >
          <LogOut size={18} />
          <span>Cerrar Sesión</span>
        </button>
      </div>
    </aside>
  );
};
