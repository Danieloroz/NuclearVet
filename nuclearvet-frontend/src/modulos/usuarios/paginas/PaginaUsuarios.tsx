import { useState } from 'react';
import { Search, Plus, Users, Shield, UserCog, User as UserIcon } from 'lucide-react';
import type { User, UserRole } from '../../../shared/types/auth.types';

export default function PaginaUsuarios() {
  const [busqueda, setBusqueda] = useState('');
  const [filtroRol, setFiltroRol] = useState<UserRole | 'TODOS'>('TODOS');

  // Datos de ejemplo
  const usuariosEjemplo: (User & { ultimoAcceso: string })[] = [
    {
      id: 1,
      nombre: 'María',
      apellido: 'González',
      email: 'maria.gonzalez@vetcare.com',
      rol: 'VETERINARIO' as UserRole,
      telefono: '555-0201',
      activo: true,
      ultimoAcceso: '2024-11-28 09:30'
    },
    {
      id: 2,
      nombre: 'Carlos',
      apellido: 'Ruiz',
      email: 'carlos.ruiz@vetcare.com',
      rol: 'VETERINARIO' as UserRole,
      telefono: '555-0202',
      activo: true,
      ultimoAcceso: '2024-11-28 08:15'
    },
    {
      id: 3,
      nombre: 'Laura',
      apellido: 'Torres',
      email: 'laura.torres@vetcare.com',
      rol: 'RECEPCIONISTA' as UserRole,
      telefono: '555-0203',
      activo: true,
      ultimoAcceso: '2024-11-28 10:00'
    },
    {
      id: 4,
      nombre: 'Pedro',
      apellido: 'Silva',
      email: 'pedro.silva@vetcare.com',
      rol: 'RECEPCIONISTA' as UserRole,
      telefono: '555-0204',
      activo: true,
      ultimoAcceso: '2024-11-27 18:45'
    },
    {
      id: 5,
      nombre: 'Ana',
      apellido: 'Martínez',
      email: 'admin@vetcare.com',
      rol: 'ADMIN' as UserRole,
      telefono: '555-0205',
      activo: true,
      ultimoAcceso: '2024-11-28 07:00'
    },
    {
      id: 6,
      nombre: 'Roberto',
      apellido: 'Díaz',
      email: 'roberto.diaz@email.com',
      rol: 'CLIENTE' as UserRole,
      telefono: '555-0206',
      activo: true,
      ultimoAcceso: '2024-11-26 14:20'
    }
  ];

  const resumenRoles = {
    veterinarios: 2,
    asistentes: 2,
    administradores: 1,
    clientes: 1
  };

  const getRolBadgeColor = (rol: UserRole) => {
    switch (rol) {
      case 'ADMIN':
        return 'bg-peligro-100 text-peligro-700';
      case 'VETERINARIO':
        return 'bg-primario-100 text-primario-700';
      case 'RECEPCIONISTA':
        return 'bg-exito-100 text-exito-700';
      case 'CLIENTE':
        return 'bg-gris-100 text-gris-700';
      default:
        return 'bg-gris-100 text-gris-700';
    }
  };

  const getRolNombre = (rol: UserRole) => {
    switch (rol) {
      case 'ADMIN':
        return 'Administrador';
      case 'VETERINARIO':
        return 'Veterinario';
      case 'RECEPCIONISTA':
        return 'Recepcionista';
      case 'CLIENTE':
        return 'Cliente';
      default:
        return rol;
    }
  };

  const getIniciales = (nombre: string, apellido: string) => {
    return `${nombre.charAt(0)}${apellido.charAt(0)}`.toUpperCase();
  };

  const getColorAvatar = (index: number) => {
    const colores = [
      'bg-primario-600',
      'bg-exito-600',
      'bg-advertencia-600',
      'bg-peligro-600',
      'bg-gris-600'
    ];
    return colores[index % colores.length];
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gris-900">Usuarios</h1>
        <p className="text-gris-600 mt-1">Gestión de usuarios y permisos del sistema</p>
      </div>

      {/* Tarjetas de resumen por rol */}
      <div className="grid grid-cols-4 gap-6 mb-6">
        {/* Veterinarios */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Veterinarios</p>
              <p className="text-3xl font-bold text-primario-600">{resumenRoles.veterinarios}</p>
            </div>
            <div className="w-12 h-12 bg-primario-100 rounded-xl flex items-center justify-center">
              <UserCog className="w-6 h-6 text-primario-600" />
            </div>
          </div>
        </div>

        {/* Asistentes/Recepcionistas */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Asistentes</p>
              <p className="text-3xl font-bold text-exito-600">{resumenRoles.asistentes}</p>
            </div>
            <div className="w-12 h-12 bg-exito-100 rounded-xl flex items-center justify-center">
              <Users className="w-6 h-6 text-exito-600" />
            </div>
          </div>
        </div>

        {/* Administradores */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Administradores</p>
              <p className="text-3xl font-bold text-peligro-600">{resumenRoles.administradores}</p>
            </div>
            <div className="w-12 h-12 bg-peligro-100 rounded-xl flex items-center justify-center">
              <Shield className="w-6 h-6 text-peligro-600" />
            </div>
          </div>
        </div>

        {/* Clientes */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Clientes</p>
              <p className="text-3xl font-bold text-gris-600">{resumenRoles.clientes}</p>
            </div>
            <div className="w-12 h-12 bg-gris-100 rounded-xl flex items-center justify-center">
              <UserIcon className="w-6 h-6 text-gris-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Tabla de usuarios */}
      <div className="bg-white rounded-lg shadow-sm border border-gris-200">
        {/* Barra de búsqueda y filtros */}
        <div className="p-4 border-b border-gris-200">
          <div className="flex items-center gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gris-400" />
              <input
                type="text"
                placeholder="Buscar por nombre, email o rol..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
              />
            </div>

            <select
              value={filtroRol}
              onChange={(e) => setFiltroRol(e.target.value as UserRole | 'TODOS')}
              className="px-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
            >
              <option value="TODOS">Todos los roles</option>
              <option value="ADMIN">Administradores</option>
              <option value="VETERINARIO">Veterinarios</option>
              <option value="RECEPCIONISTA">Recepcionistas</option>
              <option value="CLIENTE">Clientes</option>
            </select>

            <button className="flex items-center gap-2 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
              <Plus className="w-5 h-5" />
              Nuevo Usuario
            </button>
          </div>
        </div>

        {/* Tabla */}
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gris-50 border-b border-gris-200">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Usuario</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Rol</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Contacto</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Último Acceso</th>
                <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Estado</th>
                <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gris-200">
              {usuariosEjemplo.map((usuario, index) => (
                <tr key={usuario.id} className="hover:bg-gris-50 transition-colors">
                  <td className="px-4 py-4">
                    <div className="flex items-center gap-3">
                      <div className={`w-10 h-10 ${getColorAvatar(index)} rounded-full flex items-center justify-center text-white font-semibold`}>
                        {getIniciales(usuario.nombre, usuario.apellido)}
                      </div>
                      <div>
                        <p className="font-medium text-gris-900">
                          {usuario.nombre} {usuario.apellido}
                        </p>
                        <p className="text-sm text-gris-600">{usuario.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-4">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getRolBadgeColor(usuario.rol)}`}>
                      {getRolNombre(usuario.rol)}
                    </span>
                  </td>
                  <td className="px-4 py-4">
                    <p className="text-sm text-gris-900">{usuario.telefono || '-'}</p>
                  </td>
                  <td className="px-4 py-4">
                    <p className="text-sm text-gris-600">{usuario.ultimoAcceso}</p>
                  </td>
                  <td className="px-4 py-4 text-center">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      usuario.activo 
                        ? 'bg-exito-100 text-exito-700' 
                        : 'bg-gris-100 text-gris-700'
                    }`}>
                      {usuario.activo ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td className="px-4 py-4">
                    <div className="flex items-center justify-center gap-2">
                      <button className="px-3 py-1 text-sm bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                        Editar
                      </button>
                      <button className="px-3 py-1 text-sm bg-gris-200 text-gris-700 rounded-lg hover:bg-gris-300 transition-colors">
                        Permisos
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
