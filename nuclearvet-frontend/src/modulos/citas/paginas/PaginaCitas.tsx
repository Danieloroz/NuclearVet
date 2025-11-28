import { useState } from 'react';
import { Calendar, Clock, Plus, ChevronLeft, ChevronRight, User, Phone, Stethoscope } from 'lucide-react';
import type { Cita, EstadoCita, TipoCita } from '../../../shared/types/cita.types';

export default function PaginaCitas() {
  const [fechaSeleccionada, setFechaSeleccionada] = useState(new Date());
  const [mostrarModal, setMostrarModal] = useState(false);

  // Datos de ejemplo (después se conectará al backend)
  const citasEjemplo: Cita[] = [
    {
      id: 1,
      fecha: '2024-11-28',
      hora: '09:00',
      pacienteId: 1,
      pacienteNombre: 'Max',
      pacienteEspecie: 'Perro',
      propietarioId: 1,
      propietarioNombre: 'Juan Pérez',
      propietarioTelefono: '555-0101',
      veterinarioId: 1,
      veterinarioNombre: 'Dr. María González',
      tipo: 'CONSULTA' as TipoCita,
      motivo: 'Control de vacunas',
      estado: 'CONFIRMADA' as EstadoCita,
      duracionEstimada: 30,
      fechaCreacion: '2024-11-25'
    },
    {
      id: 2,
      fecha: '2024-11-28',
      hora: '09:30',
      pacienteId: 2,
      pacienteNombre: 'Luna',
      pacienteEspecie: 'Gato',
      propietarioId: 2,
      propietarioNombre: 'María García',
      propietarioTelefono: '555-0102',
      veterinarioId: 1,
      veterinarioNombre: 'Dr. María González',
      tipo: 'REVISION' as TipoCita,
      motivo: 'Revisión post-operatoria',
      estado: 'CONFIRMADA' as EstadoCita,
      duracionEstimada: 20,
      fechaCreacion: '2024-11-25'
    },
    {
      id: 3,
      fecha: '2024-11-28',
      hora: '10:00',
      pacienteId: 3,
      pacienteNombre: 'Rocky',
      pacienteEspecie: 'Perro',
      propietarioId: 3,
      propietarioNombre: 'Carlos López',
      propietarioTelefono: '555-0103',
      veterinarioId: 2,
      veterinarioNombre: 'Dr. Carlos Ruiz',
      tipo: 'URGENCIA' as TipoCita,
      motivo: 'Dolor abdominal agudo',
      estado: 'URGENTE' as EstadoCita,
      duracionEstimada: 45,
      fechaCreacion: '2024-11-28',
      observaciones: 'Atender con prioridad'
    },
    {
      id: 4,
      fecha: '2024-11-28',
      hora: '11:00',
      pacienteId: 4,
      pacienteNombre: 'Mimi',
      pacienteEspecie: 'Gato',
      propietarioId: 4,
      propietarioNombre: 'Ana Martínez',
      propietarioTelefono: '555-0104',
      veterinarioId: 1,
      veterinarioNombre: 'Dr. María González',
      tipo: 'VACUNACION' as TipoCita,
      motivo: 'Vacuna antirrábica anual',
      estado: 'PENDIENTE' as EstadoCita,
      duracionEstimada: 15,
      fechaCreacion: '2024-11-26'
    },
    {
      id: 5,
      fecha: '2024-11-28',
      hora: '14:00',
      pacienteId: 5,
      pacienteNombre: 'Toby',
      pacienteEspecie: 'Perro',
      propietarioId: 5,
      propietarioNombre: 'Luis Hernández',
      propietarioTelefono: '555-0105',
      veterinarioId: 2,
      veterinarioNombre: 'Dr. Carlos Ruiz',
      tipo: 'CIRUGIA' as TipoCita,
      motivo: 'Esterilización',
      estado: 'CONFIRMADA' as EstadoCita,
      duracionEstimada: 90,
      fechaCreacion: '2024-11-20'
    },
    {
      id: 6,
      fecha: '2024-11-28',
      hora: '16:00',
      pacienteId: 6,
      pacienteNombre: 'Kira',
      pacienteEspecie: 'Gato',
      propietarioId: 6,
      propietarioNombre: 'Patricia Silva',
      propietarioTelefono: '555-0106',
      veterinarioId: 1,
      veterinarioNombre: 'Dr. María González',
      tipo: 'CONSULTA' as TipoCita,
      motivo: 'Chequeo general',
      estado: 'PENDIENTE' as EstadoCita,
      duracionEstimada: 30,
      fechaCreacion: '2024-11-27'
    }
  ];

  const resumenCitas = {
    hoy: 12,
    confirmadas: 8,
    pendientes: 3,
    urgentes: 1
  };

  const getEstadoBadgeColor = (estado: EstadoCita) => {
    switch (estado) {
      case 'CONFIRMADA':
        return 'bg-exito-100 text-exito-700 border-exito-200';
      case 'PENDIENTE':
        return 'bg-advertencia-100 text-advertencia-700 border-advertencia-200';
      case 'URGENTE':
        return 'bg-peligro-100 text-peligro-700 border-peligro-200';
      case 'EN_CURSO':
        return 'bg-primario-100 text-primario-700 border-primario-200';
      case 'COMPLETADA':
        return 'bg-gris-100 text-gris-700 border-gris-200';
      case 'CANCELADA':
        return 'bg-gris-100 text-gris-500 border-gris-200';
      default:
        return 'bg-gris-100 text-gris-700 border-gris-200';
    }
  };

  const getEstadoBorderColor = (estado: EstadoCita) => {
    switch (estado) {
      case 'CONFIRMADA':
        return 'border-l-exito-500';
      case 'PENDIENTE':
        return 'border-l-advertencia-500';
      case 'URGENTE':
        return 'border-l-peligro-500';
      case 'EN_CURSO':
        return 'border-l-primario-500';
      default:
        return 'border-l-gris-300';
    }
  };

  const formatearFecha = (fecha: Date) => {
    return fecha.toLocaleDateString('es-ES', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  const cambiarDia = (dias: number) => {
    const nuevaFecha = new Date(fechaSeleccionada);
    nuevaFecha.setDate(nuevaFecha.getDate() + dias);
    setFechaSeleccionada(nuevaFecha);
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gris-900">Agenda de Citas</h1>
        <p className="text-gris-600 mt-1">Gestión y programación de citas veterinarias</p>
      </div>

      {/* Tarjetas de resumen */}
      <div className="grid grid-cols-4 gap-6 mb-6">
        {/* Citas Hoy */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Citas Hoy</p>
              <p className="text-3xl font-bold text-gris-900">{resumenCitas.hoy}</p>
            </div>
            <div className="w-12 h-12 bg-primario-100 rounded-xl flex items-center justify-center">
              <Calendar className="w-6 h-6 text-primario-600" />
            </div>
          </div>
        </div>

        {/* Confirmadas */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Confirmadas</p>
              <p className="text-3xl font-bold text-exito-600">{resumenCitas.confirmadas}</p>
            </div>
            <div className="w-12 h-12 bg-exito-100 rounded-xl flex items-center justify-center">
              <Clock className="w-6 h-6 text-exito-600" />
            </div>
          </div>
        </div>

        {/* Pendientes */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Pendientes</p>
              <p className="text-3xl font-bold text-advertencia-600">{resumenCitas.pendientes}</p>
            </div>
            <div className="w-12 h-12 bg-advertencia-100 rounded-xl flex items-center justify-center">
              <Clock className="w-6 h-6 text-advertencia-600" />
            </div>
          </div>
        </div>

        {/* Urgentes */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Urgentes</p>
              <p className="text-3xl font-bold text-peligro-600">{resumenCitas.urgentes}</p>
            </div>
            <div className="w-12 h-12 bg-peligro-100 rounded-xl flex items-center justify-center">
              <Clock className="w-6 h-6 text-peligro-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Agenda del día */}
      <div className="bg-white rounded-lg shadow-sm border border-gris-200">
        {/* Header de la agenda */}
        <div className="p-6 border-b border-gris-200">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <button
                onClick={() => cambiarDia(-1)}
                className="p-2 hover:bg-gris-100 rounded-lg transition-colors"
              >
                <ChevronLeft className="w-5 h-5 text-gris-600" />
              </button>
              
              <div className="text-center">
                <h2 className="text-xl font-semibold text-gris-900 capitalize">
                  {formatearFecha(fechaSeleccionada)}
                </h2>
                <p className="text-sm text-gris-600">
                  {citasEjemplo.length} citas programadas
                </p>
              </div>

              <button
                onClick={() => cambiarDia(1)}
                className="p-2 hover:bg-gris-100 rounded-lg transition-colors"
              >
                <ChevronRight className="w-5 h-5 text-gris-600" />
              </button>
            </div>

            <button 
              onClick={() => setMostrarModal(true)}
              className="flex items-center gap-2 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors"
            >
              <Plus className="w-5 h-5" />
              Nueva Cita
            </button>
          </div>
        </div>

        {/* Timeline de citas */}
        <div className="p-6">
          <div className="space-y-4">
            {citasEjemplo.map((cita) => (
              <div
                key={cita.id}
                className={`border-l-4 ${getEstadoBorderColor(cita.estado)} bg-gris-50 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer`}
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-4 flex-1">
                    {/* Hora */}
                    <div className="text-center min-w-[60px]">
                      <p className="text-lg font-semibold text-gris-900">{cita.hora}</p>
                      <p className="text-xs text-gris-600">{cita.duracionEstimada} min</p>
                    </div>

                    {/* Información de la cita */}
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <h3 className="text-base font-semibold text-gris-900">
                          {cita.pacienteNombre} - {cita.pacienteEspecie}
                        </h3>
                        <span className={`px-2 py-1 rounded-full text-xs font-medium border ${getEstadoBadgeColor(cita.estado)}`}>
                          {cita.estado}
                        </span>
                        <span className="px-2 py-1 rounded-full text-xs font-medium bg-primario-100 text-primario-700">
                          {cita.tipo}
                        </span>
                      </div>

                      <p className="text-sm text-gris-700 mb-2">
                        <strong>Motivo:</strong> {cita.motivo}
                      </p>

                      <div className="flex items-center gap-4 text-sm text-gris-600">
                        <span className="flex items-center gap-1">
                          <User className="w-4 h-4" />
                          {cita.propietarioNombre}
                        </span>
                        <span className="flex items-center gap-1">
                          <Phone className="w-4 h-4" />
                          {cita.propietarioTelefono}
                        </span>
                        <span className="flex items-center gap-1">
                          <Stethoscope className="w-4 h-4" />
                          {cita.veterinarioNombre}
                        </span>
                      </div>

                      {cita.observaciones && (
                        <p className="text-sm text-peligro-600 mt-2 font-medium">
                          ⚠️ {cita.observaciones}
                        </p>
                      )}
                    </div>
                  </div>

                  {/* Acciones */}
                  <div className="flex gap-2">
                    {cita.estado === 'PENDIENTE' && (
                      <button className="px-3 py-1 text-sm bg-exito-600 text-white rounded-lg hover:bg-exito-700 transition-colors">
                        Confirmar
                      </button>
                    )}
                    {cita.estado === 'CONFIRMADA' && (
                      <button className="px-3 py-1 text-sm bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                        Iniciar
                      </button>
                    )}
                    <button className="px-3 py-1 text-sm bg-gris-200 text-gris-700 rounded-lg hover:bg-gris-300 transition-colors">
                      Editar
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Mensaje si no hay citas */}
          {citasEjemplo.length === 0 && (
            <div className="text-center py-12">
              <Calendar className="w-16 h-16 text-gris-400 mx-auto mb-4" />
              <p className="text-gris-600">No hay citas programadas para este día</p>
              <button
                onClick={() => setMostrarModal(true)}
                className="mt-4 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors"
              >
                Programar Primera Cita
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Modal de nueva cita (placeholder) */}
      {mostrarModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h3 className="text-xl font-bold text-gris-900 mb-4">Nueva Cita</h3>
            <p className="text-gris-600 mb-4">Formulario de nueva cita (próximamente)</p>
            <button
              onClick={() => setMostrarModal(false)}
              className="w-full px-4 py-2 bg-gris-200 text-gris-700 rounded-lg hover:bg-gris-300 transition-colors"
            >
              Cerrar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
