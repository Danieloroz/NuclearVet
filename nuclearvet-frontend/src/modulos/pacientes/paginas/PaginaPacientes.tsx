import { useState } from 'react';
import { Search, Plus, Filter, Dog, Cat, Bird, User } from 'lucide-react';
import type { Paciente, Especie, EstadoPaciente } from '../../../shared/types/paciente.types';

export default function PaginaPacientes() {
  const [busqueda, setBusqueda] = useState('');
  const [filtroEspecie, setFiltroEspecie] = useState<Especie | 'TODOS'>('TODOS');
  const [pacienteSeleccionado, setPacienteSeleccionado] = useState<Paciente | null>(null);

  // Datos de ejemplo (después se conectará al backend)
  const pacientesEjemplo: Paciente[] = [
    {
      id: 1,
      nombre: 'Max',
      especie: 'PERRO' as Especie,
      raza: 'Golden Retriever',
      edad: 3,
      peso: 30,
      color: 'Dorado',
      sexo: 'MACHO',
      propietarioId: 1,
      propietarioNombre: 'Juan Pérez',
      propietarioTelefono: '555-0101',
      propietarioEmail: 'juan@email.com',
      estado: 'ACTIVO' as EstadoPaciente,
      fechaRegistro: '2024-01-15',
      ultimaVisita: '2024-11-20',
      numeroHistoriaClinica: 'HC-001'
    },
    {
      id: 2,
      nombre: 'Luna',
      especie: 'GATO' as Especie,
      raza: 'Siamés',
      edad: 2,
      peso: 4.5,
      color: 'Beige',
      sexo: 'HEMBRA',
      propietarioId: 2,
      propietarioNombre: 'María García',
      propietarioTelefono: '555-0102',
      propietarioEmail: 'maria@email.com',
      estado: 'TRATAMIENTO' as EstadoPaciente,
      fechaRegistro: '2024-03-20',
      ultimaVisita: '2024-11-25',
      numeroHistoriaClinica: 'HC-002',
      observaciones: 'En tratamiento por infección respiratoria'
    },
    {
      id: 3,
      nombre: 'Rocky',
      especie: 'PERRO' as Especie,
      raza: 'Bulldog',
      edad: 5,
      peso: 25,
      color: 'Blanco y negro',
      sexo: 'MACHO',
      propietarioId: 3,
      propietarioNombre: 'Carlos López',
      propietarioTelefono: '555-0103',
      propietarioEmail: 'carlos@email.com',
      estado: 'ACTIVO' as EstadoPaciente,
      fechaRegistro: '2023-08-10',
      ultimaVisita: '2024-11-18',
      numeroHistoriaClinica: 'HC-003'
    },
    {
      id: 4,
      nombre: 'Mimi',
      especie: 'GATO' as Especie,
      raza: 'Persa',
      edad: 4,
      peso: 5.2,
      color: 'Gris',
      sexo: 'HEMBRA',
      propietarioId: 4,
      propietarioNombre: 'Ana Martínez',
      propietarioTelefono: '555-0104',
      propietarioEmail: 'ana@email.com',
      estado: 'ACTIVO' as EstadoPaciente,
      fechaRegistro: '2023-11-05',
      ultimaVisita: '2024-11-22',
      numeroHistoriaClinica: 'HC-004'
    }
  ];

  const getEstadoBadgeColor = (estado: EstadoPaciente) => {
    switch (estado) {
      case 'ACTIVO':
        return 'bg-exito-100 text-exito-700';
      case 'TRATAMIENTO':
        return 'bg-advertencia-100 text-advertencia-700';
      case 'INACTIVO':
        return 'bg-gris-100 text-gris-700';
      case 'FALLECIDO':
        return 'bg-peligro-100 text-peligro-700';
      default:
        return 'bg-gris-100 text-gris-700';
    }
  };

  const getEspecieIcon = (especie: Especie) => {
    switch (especie) {
      case 'PERRO':
        return <Dog className="w-4 h-4" />;
      case 'GATO':
        return <Cat className="w-4 h-4" />;
      case 'AVE':
        return <Bird className="w-4 h-4" />;
      default:
        return <User className="w-4 h-4" />;
    }
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gris-900">Pacientes</h1>
        <p className="text-gris-600 mt-1">Gestión de pacientes y mascotas</p>
      </div>

      {/* Barra de búsqueda y acciones */}
      <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-4 mb-6">
        <div className="flex items-center gap-4">
          {/* Búsqueda */}
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gris-400" />
            <input
              type="text"
              placeholder="Buscar por nombre, propietario o historia clínica..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
            />
          </div>

          {/* Filtro por especie */}
          <div className="flex items-center gap-2">
            <Filter className="w-5 h-5 text-gris-400" />
            <select
              value={filtroEspecie}
              onChange={(e) => setFiltroEspecie(e.target.value as Especie | 'TODOS')}
              className="px-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
            >
              <option value="TODOS">Todas las especies</option>
              <option value="PERRO">Perros</option>
              <option value="GATO">Gatos</option>
              <option value="AVE">Aves</option>
              <option value="ROEDOR">Roedores</option>
              <option value="REPTIL">Reptiles</option>
              <option value="OTRO">Otros</option>
            </select>
          </div>

          {/* Botón nuevo paciente */}
          <button className="flex items-center gap-2 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
            <Plus className="w-5 h-5" />
            Nuevo Paciente
          </button>
        </div>
      </div>

      {/* Layout principal: Lista + Detalles */}
      <div className="grid grid-cols-12 gap-6">
        {/* Lista de pacientes */}
        <div className="col-span-7 bg-white rounded-lg shadow-sm border border-gris-200">
          <div className="p-4 border-b border-gris-200">
            <h2 className="text-lg font-semibold text-gris-900">
              Lista de Pacientes ({pacientesEjemplo.length})
            </h2>
          </div>
          
          <div className="divide-y divide-gris-200">
            {pacientesEjemplo.map((paciente) => (
              <div
                key={paciente.id}
                onClick={() => setPacienteSeleccionado(paciente)}
                className={`p-4 cursor-pointer transition-colors hover:bg-gris-50 ${
                  pacienteSeleccionado?.id === paciente.id ? 'bg-primario-50' : ''
                }`}
              >
                <div className="flex items-start gap-4">
                  {/* Avatar/Icono */}
                  <div className="w-12 h-12 bg-primario-100 rounded-full flex items-center justify-center text-primario-600">
                    {getEspecieIcon(paciente.especie)}
                  </div>

                  {/* Información */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between mb-1">
                      <h3 className="text-base font-semibold text-gris-900">
                        {paciente.nombre}
                      </h3>
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${getEstadoBadgeColor(paciente.estado)}`}>
                        {paciente.estado}
                      </span>
                    </div>
                    
                    <p className="text-sm text-gris-600 mb-1">
                      {paciente.raza} • {paciente.edad} {paciente.edad === 1 ? 'año' : 'años'} • {paciente.peso} kg
                    </p>
                    
                    <div className="flex items-center gap-4 text-sm text-gris-500">
                      <span className="flex items-center gap-1">
                        <User className="w-4 h-4" />
                        {paciente.propietarioNombre}
                      </span>
                      {paciente.ultimaVisita && (
                        <span>
                          Última visita: {new Date(paciente.ultimaVisita).toLocaleDateString('es-ES')}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Panel de detalles */}
        <div className="col-span-5">
          {pacienteSeleccionado ? (
            <div className="bg-white rounded-lg shadow-sm border border-gris-200">
              <div className="p-6 border-b border-gris-200">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h2 className="text-2xl font-bold text-gris-900 mb-1">
                      {pacienteSeleccionado.nombre}
                    </h2>
                    <p className="text-sm text-gris-600">
                      {pacienteSeleccionado.numeroHistoriaClinica}
                    </p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-medium ${getEstadoBadgeColor(pacienteSeleccionado.estado)}`}>
                    {pacienteSeleccionado.estado}
                  </span>
                </div>
              </div>

              <div className="p-6 space-y-6">
                {/* Información básica */}
                <div>
                  <h3 className="text-sm font-semibold text-gris-900 mb-3">Información Básica</h3>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <p className="text-gris-600">Especie</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.especie}</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Raza</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.raza}</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Edad</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.edad} {pacienteSeleccionado.edad === 1 ? 'año' : 'años'}</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Peso</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.peso} kg</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Sexo</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.sexo}</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Color</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.color}</p>
                    </div>
                  </div>
                </div>

                {/* Información del propietario */}
                <div>
                  <h3 className="text-sm font-semibold text-gris-900 mb-3">Propietario</h3>
                  <div className="space-y-2 text-sm">
                    <div>
                      <p className="text-gris-600">Nombre</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.propietarioNombre}</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Teléfono</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.propietarioTelefono}</p>
                    </div>
                    <div>
                      <p className="text-gris-600">Email</p>
                      <p className="font-medium text-gris-900">{pacienteSeleccionado.propietarioEmail}</p>
                    </div>
                  </div>
                </div>

                {/* Observaciones */}
                {pacienteSeleccionado.observaciones && (
                  <div>
                    <h3 className="text-sm font-semibold text-gris-900 mb-3">Observaciones</h3>
                    <p className="text-sm text-gris-700 bg-gris-50 p-3 rounded-lg">
                      {pacienteSeleccionado.observaciones}
                    </p>
                  </div>
                )}

                {/* Botones de acción */}
                <div className="flex gap-2 pt-4 border-t border-gris-200">
                  <button className="flex-1 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                    Editar
                  </button>
                  <button className="flex-1 px-4 py-2 bg-gris-200 text-gris-700 rounded-lg hover:bg-gris-300 transition-colors">
                    Historial
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow-sm border border-gris-200 h-full flex items-center justify-center p-8">
              <div className="text-center">
                <div className="w-16 h-16 bg-gris-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <User className="w-8 h-8 text-gris-400" />
                </div>
                <p className="text-gris-600">Selecciona un paciente para ver sus detalles</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
