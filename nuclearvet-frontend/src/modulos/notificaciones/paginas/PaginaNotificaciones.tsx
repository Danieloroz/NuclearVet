import { useState } from 'react';
import { Bell, Clock, AlertTriangle, CheckCircle, Calendar, Package, DollarSign } from 'lucide-react';
import type { Notificacion, TipoNotificacion } from '../../../shared/types/notificacion.types';

export default function PaginaNotificaciones() {
  const [tabActiva, setTabActiva] = useState<'todas' | 'sinLeer' | 'importantes' | 'leidas'>('todas');

  // Datos de ejemplo
  const notificacionesEjemplo: Notificacion[] = [
    {
      id: 1,
      tipo: 'CITA' as TipoNotificacion,
      titulo: 'Nueva cita programada',
      mensaje: 'Se ha programado una cita para Max (Golden Retriever) mañana a las 10:00 AM',
      prioridad: 'MEDIA',
      leida: false,
      importante: false,
      usuarioId: 1,
      fecha: '2024-11-28 14:30',
      accionUrl: '/citas',
      accionTexto: 'Ver Agenda'
    },
    {
      id: 2,
      tipo: 'ALERTA' as TipoNotificacion,
      titulo: 'Stock crítico de vacunas',
      mensaje: 'La vacuna antirrábica tiene stock crítico (8 unidades). Se recomienda hacer un pedido urgente.',
      prioridad: 'URGENTE',
      leida: false,
      importante: true,
      usuarioId: 1,
      fecha: '2024-11-28 13:15',
      accionUrl: '/inventario',
      accionTexto: 'Ver Inventario'
    },
    {
      id: 3,
      tipo: 'PAGO' as TipoNotificacion,
      titulo: 'Pago recibido',
      mensaje: 'Se ha registrado el pago de $125.00 por la consulta de Luna',
      prioridad: 'BAJA',
      leida: true,
      importante: false,
      usuarioId: 1,
      fecha: '2024-11-28 11:45',
      accionUrl: '/administracion',
      accionTexto: 'Ver Detalles'
    },
    {
      id: 4,
      tipo: 'RECORDATORIO' as TipoNotificacion,
      titulo: 'Recordatorio de vacunación',
      mensaje: 'Rocky debe recibir su vacuna de refuerzo esta semana',
      prioridad: 'ALTA',
      leida: false,
      importante: true,
      usuarioId: 1,
      fecha: '2024-11-28 09:00',
      accionUrl: '/pacientes',
      accionTexto: 'Ver Paciente'
    },
    {
      id: 5,
      tipo: 'SISTEMA' as TipoNotificacion,
      titulo: 'Backup completado',
      mensaje: 'Se ha completado exitosamente el backup diario de la base de datos',
      prioridad: 'BAJA',
      leida: true,
      importante: false,
      usuarioId: 1,
      fecha: '2024-11-28 06:00'
    }
  ];

  const resumen = {
    sinLeer: 3,
    hoy: 5,
    estaSemana: 12,
    alertas: 2
  };

  const getIconoTipo = (tipo: TipoNotificacion) => {
    switch (tipo) {
      case 'CITA':
        return <Calendar className="w-5 h-5" />;
      case 'PAGO':
        return <DollarSign className="w-5 h-5" />;
      case 'ALERTA':
        return <AlertTriangle className="w-5 h-5" />;
      case 'RECORDATORIO':
        return <Clock className="w-5 h-5" />;
      case 'SISTEMA':
        return <CheckCircle className="w-5 h-5" />;
      default:
        return <Bell className="w-5 h-5" />;
    }
  };

  const getPrioridadColor = (prioridad: string) => {
    switch (prioridad) {
      case 'URGENTE':
        return 'bg-peligro-100 text-peligro-600';
      case 'ALTA':
        return 'bg-advertencia-100 text-advertencia-600';
      case 'MEDIA':
        return 'bg-primario-100 text-primario-600';
      default:
        return 'bg-gris-100 text-gris-600';
    }
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gris-900">Notificaciones</h1>
        <p className="text-gris-600 mt-1">Centro de notificaciones y recordatorios</p>
      </div>

      {/* Tarjetas de resumen */}
      <div className="grid grid-cols-4 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Sin Leer</p>
              <p className="text-3xl font-bold text-peligro-600">{resumen.sinLeer}</p>
            </div>
            <div className="w-12 h-12 bg-peligro-100 rounded-xl flex items-center justify-center">
              <Bell className="w-6 h-6 text-peligro-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Hoy</p>
              <p className="text-3xl font-bold text-primario-600">{resumen.hoy}</p>
            </div>
            <div className="w-12 h-12 bg-primario-100 rounded-xl flex items-center justify-center">
              <Clock className="w-6 h-6 text-primario-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Esta Semana</p>
              <p className="text-3xl font-bold text-gris-900">{resumen.estaSemana}</p>
            </div>
            <div className="w-12 h-12 bg-gris-100 rounded-xl flex items-center justify-center">
              <Calendar className="w-6 h-6 text-gris-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Alertas</p>
              <p className="text-3xl font-bold text-advertencia-600">{resumen.alertas}</p>
            </div>
            <div className="w-12 h-12 bg-advertencia-100 rounded-xl flex items-center justify-center">
              <AlertTriangle className="w-6 h-6 text-advertencia-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Layout principal */}
      <div className="grid grid-cols-12 gap-6">
        {/* Centro de Notificaciones */}
        <div className="col-span-8 bg-white rounded-lg shadow-sm border border-gris-200">
          {/* Tabs */}
          <div className="border-b border-gris-200">
            <div className="flex gap-6 px-6">
              <button
                onClick={() => setTabActiva('todas')}
                className={`py-4 border-b-2 font-medium transition-colors ${
                  tabActiva === 'todas'
                    ? 'border-primario-600 text-primario-600'
                    : 'border-transparent text-gris-600 hover:text-gris-900'
                }`}
              >
                Todas
              </button>
              <button
                onClick={() => setTabActiva('sinLeer')}
                className={`py-4 border-b-2 font-medium transition-colors ${
                  tabActiva === 'sinLeer'
                    ? 'border-primario-600 text-primario-600'
                    : 'border-transparent text-gris-600 hover:text-gris-900'
                }`}
              >
                Sin Leer ({resumen.sinLeer})
              </button>
              <button
                onClick={() => setTabActiva('importantes')}
                className={`py-4 border-b-2 font-medium transition-colors ${
                  tabActiva === 'importantes'
                    ? 'border-primario-600 text-primario-600'
                    : 'border-transparent text-gris-600 hover:text-gris-900'
                }`}
              >
                Importantes
              </button>
              <button
                onClick={() => setTabActiva('leidas')}
                className={`py-4 border-b-2 font-medium transition-colors ${
                  tabActiva === 'leidas'
                    ? 'border-primario-600 text-primario-600'
                    : 'border-transparent text-gris-600 hover:text-gris-900'
                }`}
              >
                Leídas
              </button>
            </div>
          </div>

          {/* Lista de notificaciones */}
          <div className="divide-y divide-gris-200">
            {notificacionesEjemplo.map((notif) => (
              <div
                key={notif.id}
                className={`p-4 hover:bg-gris-50 transition-colors cursor-pointer ${
                  !notif.leida ? 'bg-primario-50/30' : ''
                }`}
              >
                <div className="flex items-start gap-4">
                  <div className={`p-3 rounded-xl ${getPrioridadColor(notif.prioridad)}`}>
                    {getIconoTipo(notif.tipo)}
                  </div>

                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-semibold text-gris-900">{notif.titulo}</h3>
                      {!notif.leida && (
                        <span className="w-2 h-2 rounded-full bg-primario-600"></span>
                      )}
                      {notif.importante && (
                        <span className="px-2 py-0.5 rounded text-xs font-medium bg-peligro-100 text-peligro-700">
                          Importante
                        </span>
                      )}
                    </div>
                    <p className="text-sm text-gris-700 mb-2">{notif.mensaje}</p>
                    <div className="flex items-center gap-4">
                      <span className="text-xs text-gris-500">{notif.fecha}</span>
                      {notif.accionUrl && (
                        <button className="text-xs text-primario-600 hover:text-primario-700 font-medium">
                          {notif.accionTexto}
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Recordatorios Automáticos */}
        <div className="col-span-4 space-y-6">
          <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
            <h3 className="font-semibold text-gris-900 mb-4">Recordatorios Automáticos</h3>
            
            <div className="space-y-4">
              <div className="p-3 bg-primario-50 rounded-lg">
                <div className="flex items-center gap-2 mb-2">
                  <Calendar className="w-4 h-4 text-primario-600" />
                  <p className="text-sm font-medium text-primario-900">Citas del Día</p>
                </div>
                <p className="text-xs text-primario-700">6 citas programadas para hoy</p>
              </div>

              <div className="p-3 bg-advertencia-50 rounded-lg">
                <div className="flex items-center gap-2 mb-2">
                  <Clock className="w-4 h-4 text-advertencia-600" />
                  <p className="text-sm font-medium text-advertencia-900">Vacunas Pendientes</p>
                </div>
                <p className="text-xs text-advertencia-700">3 pacientes necesitan vacunación</p>
              </div>

              <div className="p-3 bg-peligro-50 rounded-lg">
                <div className="flex items-center gap-2 mb-2">
                  <Package className="w-4 h-4 text-peligro-600" />
                  <p className="text-sm font-medium text-peligro-900">Alertas de Inventario</p>
                </div>
                <p className="text-xs text-peligro-700">2 productos con stock crítico</p>
              </div>
            </div>

            <button className="w-full mt-4 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors text-sm">
              Configurar Recordatorios
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
