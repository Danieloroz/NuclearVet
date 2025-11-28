import { Calendar, Users, AlertCircle, Package, TrendingUp } from 'lucide-react';

interface Metrica {
  titulo: string;
  valor: string | number;
  subtexto: string;
  icono: React.ReactNode;
  color: string;
  tendencia?: string;
}

interface Cita {
  hora: string;
  paciente: string;
  propietario: string;
  servicio: string;
  estado: 'Confirmada' | 'Pendiente' | 'Urgente';
}

export const Dashboard = () => {
  const metricas: Metrica[] = [
    {
      titulo: 'Citas Hoy',
      valor: 12,
      subtexto: '+3 vs ayer',
      icono: <Calendar size={24} />,
      color: 'primario',
      tendencia: 'up',
    },
    {
      titulo: 'Pacientes Activos',
      valor: 248,
      subtexto: '+18 este mes',
      icono: <Users size={24} />,
      color: 'exito',
      tendencia: 'up',
    },
    {
      titulo: 'Alertas Pendientes',
      valor: 5,
      subtexto: '2 urgentes',
      icono: <AlertCircle size={24} />,
      color: 'advertencia',
    },
    {
      titulo: 'Stock Crítico',
      valor: 3,
      subtexto: 'Reabastecer',
      icono: <Package size={24} />,
      color: 'peligro',
    },
  ];

  const citasHoy: Cita[] = [
    {
      hora: '09:00',
      paciente: 'Max',
      propietario: 'Carlos Ruiz',
      servicio: 'Consulta General',
      estado: 'Confirmada',
    },
    {
      hora: '09:30',
      paciente: 'Luna',
      propietario: 'Ana Martínez',
      servicio: 'Vacunación',
      estado: 'Confirmada',
    },
    {
      hora: '10:00',
      paciente: 'Rocky',
      propietario: 'Pedro López',
      servicio: 'Cirugía',
      estado: 'Urgente',
    },
    {
      hora: '11:00',
      paciente: 'Mimi',
      propietario: 'Laura Torres',
      servicio: 'Control',
      estado: 'Pendiente',
    },
  ];

  const actividadReciente = [
    {
      tipo: 'Nueva historia clínica',
      descripcion: 'Max - Consulta completada',
      tiempo: 'hace 5 min',
    },
    {
      tipo: 'Medicamento dispensado',
      descripcion: 'Antibiótico - Luna',
      tiempo: 'hace 23 min',
    },
    {
      tipo: 'Cita agendada',
      descripcion: 'Rocky - Cirugía programada',
      tiempo: 'hace 1 h',
    },
    {
      tipo: 'Pago registrado',
      descripcion: 'Consulta - $45.00',
      tiempo: 'hace 2 h',
    },
  ];

  const getEstadoColor = (estado: Cita['estado']) => {
    switch (estado) {
      case 'Confirmada':
        return 'bg-exito-100 text-exito-700';
      case 'Pendiente':
        return 'bg-advertencia-100 text-advertencia-700';
      case 'Urgente':
        return 'bg-peligro-100 text-peligro-700';
      default:
        return 'bg-gris-100 text-gris-700';
    }
  };

  const getIconColor = (color: string) => {
    const colores = {
      primario: 'text-primario-600',
      exito: 'text-exito-600',
      advertencia: 'text-advertencia-600',
      peligro: 'text-peligro-600',
    };
    return colores[color as keyof typeof colores] || 'text-gris-600';
  };

  return (
    <div className="space-y-6">
      {/* Encabezado */}
      <div>
        <h1 className="text-2xl font-semibold text-gris-900">Panel de Control</h1>
        <p className="text-gris-600 mt-1">Resumen general del sistema veterinario</p>
      </div>

      {/* Métricas principales */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {metricas.map((metrica) => (
          <div
            key={metrica.titulo}
            className="bg-white rounded-xl border border-gris-200 p-6 hover:shadow-md transition-shadow"
          >
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <p className="text-sm text-gris-600 mb-2">{metrica.titulo}</p>
                <p className="text-3xl font-semibold text-gris-900 mb-1">
                  {metrica.valor}
                </p>
                <p className="text-xs text-gris-500">{metrica.subtexto}</p>
              </div>
              <div className={`p-3 rounded-lg bg-${metrica.color}-50`}>
                <span className={getIconColor(metrica.color)}>{metrica.icono}</span>
              </div>
            </div>
            {metrica.tendencia && (
              <div className="mt-3 flex items-center gap-1">
                <TrendingUp size={14} className="text-exito-600" />
                <span className="text-xs text-exito-600">Tendencia positiva</span>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Contenido principal */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Citas de hoy */}
        <div className="lg:col-span-2 bg-white rounded-xl border border-gris-200 p-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-2">
              <Calendar className="text-primario-600" size={20} />
              <h2 className="text-lg font-semibold text-gris-900">Citas de Hoy</h2>
            </div>
            <button className="text-primario-600 hover:text-primario-700 text-sm font-medium">
              Ver Agenda Completa
            </button>
          </div>

          <div className="space-y-4">
            {citasHoy.map((cita, index) => (
              <div
                key={index}
                className="flex items-center gap-4 p-4 rounded-lg border border-gris-100 hover:border-gris-200 transition-colors"
              >
                <div className="text-center min-w-[60px]">
                  <p className="text-lg font-semibold text-gris-900">{cita.hora}</p>
                </div>
                
                <div className="flex-1">
                  <p className="font-medium text-gris-900">{cita.paciente}</p>
                  <p className="text-sm text-gris-600">
                    {cita.propietario} - {cita.servicio}
                  </p>
                </div>

                <span
                  className={`
                    px-3 py-1 rounded-full text-xs font-medium
                    ${getEstadoColor(cita.estado)}
                  `}
                >
                  {cita.estado}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Actividad reciente */}
        <div className="bg-white rounded-xl border border-gris-200 p-6">
          <h2 className="text-lg font-semibold text-gris-900 mb-6">
            Actividad Reciente
          </h2>

          <div className="space-y-4">
            {actividadReciente.map((actividad, index) => (
              <div key={index} className="flex gap-3">
                <div className="w-2 h-2 rounded-full bg-primario-600 mt-2"></div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-gris-900">
                    {actividad.tipo}
                  </p>
                  <p className="text-sm text-gris-600">{actividad.descripcion}</p>
                  <p className="text-xs text-gris-500 mt-1">{actividad.tiempo}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Ocupación diaria */}
      <div className="bg-white rounded-xl border border-gris-200 p-6">
        <h2 className="text-lg font-semibold text-gris-900 mb-4">
          Ocupación Diaria
        </h2>
        <div className="h-48 flex items-end justify-around gap-2">
          {/* Placeholder para gráfico - se implementaría con una librería de gráficos */}
          {[65, 80, 45, 90, 70, 85, 60].map((altura, index) => (
            <div key={index} className="flex-1 flex flex-col items-center gap-2">
              <div
                className="w-full bg-primario-100 rounded-t-lg transition-all hover:bg-primario-200"
                style={{ height: `${altura}%` }}
              ></div>
              <span className="text-xs text-gris-500">
                {['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'][index]}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
