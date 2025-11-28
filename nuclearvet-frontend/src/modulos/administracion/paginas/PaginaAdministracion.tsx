import { useState } from 'react';
import { DollarSign, TrendingUp, FileText, CreditCard, Plus, Filter, Download } from 'lucide-react';

type EstadoPago = 'PAGADO' | 'PENDIENTE' | 'VENCIDO';
type MetodoPago = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' | 'CHEQUE';

interface Pago {
  id: number;
  fecha: string;
  paciente: string;
  propietario: string;
  servicio: string;
  monto: number;
  metodoPago: MetodoPago;
  estado: EstadoPago;
  facturaNumero?: string;
}

export default function PaginaAdministracion() {
  const [tabActiva, setTabActiva] = useState<'facturacion' | 'reportes' | 'configuracion'>('facturacion');
  const [filtroEstado, setFiltroEstado] = useState<EstadoPago | 'TODOS'>('TODOS');

  // Datos de ejemplo
  const pagosEjemplo: Pago[] = [
    {
      id: 1,
      fecha: '2024-11-28',
      paciente: 'Max',
      propietario: 'Juan Pérez',
      servicio: 'Consulta + Vacunación',
      monto: 125.00,
      metodoPago: 'TARJETA',
      estado: 'PAGADO',
      facturaNumero: 'FACT-001'
    },
    {
      id: 2,
      fecha: '2024-11-28',
      paciente: 'Luna',
      propietario: 'María García',
      servicio: 'Tratamiento infección',
      monto: 250.00,
      metodoPago: 'EFECTIVO',
      estado: 'PAGADO',
      facturaNumero: 'FACT-002'
    },
    {
      id: 3,
      fecha: '2024-11-27',
      paciente: 'Rocky',
      propietario: 'Carlos López',
      servicio: 'Chequeo general',
      monto: 85.00,
      metodoPago: 'TRANSFERENCIA',
      estado: 'PENDIENTE'
    },
    {
      id: 4,
      fecha: '2024-11-26',
      paciente: 'Mimi',
      propietario: 'Ana Martínez',
      servicio: 'Vacuna antirrábica',
      monto: 45.00,
      metodoPago: 'EFECTIVO',
      estado: 'PAGADO',
      facturaNumero: 'FACT-003'
    },
    {
      id: 5,
      fecha: '2024-11-25',
      paciente: 'Toby',
      propietario: 'Luis Hernández',
      servicio: 'Cirugía esterilización',
      monto: 450.00,
      metodoPago: 'TARJETA',
      estado: 'PAGADO',
      facturaNumero: 'FACT-004'
    }
  ];

  const resumenFinanciero = {
    ingresosHoy: 375.00,
    ingresosMes: 12850.00,
    pagosPendientes: 85.00,
    facturasMes: 45
  };

  const getEstadoBadgeColor = (estado: EstadoPago) => {
    switch (estado) {
      case 'PAGADO':
        return 'bg-exito-100 text-exito-700';
      case 'PENDIENTE':
        return 'bg-advertencia-100 text-advertencia-700';
      case 'VENCIDO':
        return 'bg-peligro-100 text-peligro-700';
      default:
        return 'bg-gris-100 text-gris-700';
    }
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gris-900">Administración</h1>
        <p className="text-gris-600 mt-1">Facturación, pagos y reportes financieros</p>
      </div>

      {/* Tarjetas de resumen */}
      <div className="grid grid-cols-4 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Ingresos Hoy</p>
              <p className="text-3xl font-bold text-exito-600">${resumenFinanciero.ingresosHoy.toFixed(2)}</p>
            </div>
            <div className="w-12 h-12 bg-exito-100 rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-exito-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Este Mes</p>
              <p className="text-3xl font-bold text-primario-600">${resumenFinanciero.ingresosMes.toLocaleString()}</p>
            </div>
            <div className="w-12 h-12 bg-primario-100 rounded-xl flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-primario-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Pagos Pendientes</p>
              <p className="text-3xl font-bold text-advertencia-600">${resumenFinanciero.pagosPendientes.toFixed(2)}</p>
            </div>
            <div className="w-12 h-12 bg-advertencia-100 rounded-xl flex items-center justify-center">
              <CreditCard className="w-6 h-6 text-advertencia-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Facturas</p>
              <p className="text-3xl font-bold text-gris-900">{resumenFinanciero.facturasMes}</p>
            </div>
            <div className="w-12 h-12 bg-gris-100 rounded-xl flex items-center justify-center">
              <FileText className="w-6 h-6 text-gris-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-sm border border-gris-200">
        <div className="border-b border-gris-200">
          <div className="flex gap-6 px-6">
            <button
              onClick={() => setTabActiva('facturacion')}
              className={`py-4 border-b-2 font-medium transition-colors ${
                tabActiva === 'facturacion'
                  ? 'border-primario-600 text-primario-600'
                  : 'border-transparent text-gris-600 hover:text-gris-900'
              }`}
            >
              Facturación
            </button>
            <button
              onClick={() => setTabActiva('reportes')}
              className={`py-4 border-b-2 font-medium transition-colors ${
                tabActiva === 'reportes'
                  ? 'border-primario-600 text-primario-600'
                  : 'border-transparent text-gris-600 hover:text-gris-900'
              }`}
            >
              Reportes
            </button>
            <button
              onClick={() => setTabActiva('configuracion')}
              className={`py-4 border-b-2 font-medium transition-colors ${
                tabActiva === 'configuracion'
                  ? 'border-primario-600 text-primario-600'
                  : 'border-transparent text-gris-600 hover:text-gris-900'
              }`}
            >
              Configuración
            </button>
          </div>
        </div>

        {/* Contenido tab Facturación */}
        {tabActiva === 'facturacion' && (
          <div className="p-6">
            {/* Barra de acciones */}
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-4">
                <div className="flex items-center gap-2">
                  <Filter className="w-5 h-5 text-gris-400" />
                  <select
                    value={filtroEstado}
                    onChange={(e) => setFiltroEstado(e.target.value as EstadoPago | 'TODOS')}
                    className="px-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
                  >
                    <option value="TODOS">Todos los estados</option>
                    <option value="PAGADO">Pagado</option>
                    <option value="PENDIENTE">Pendiente</option>
                    <option value="VENCIDO">Vencido</option>
                  </select>
                </div>
              </div>

              <div className="flex gap-2">
                <button className="flex items-center gap-2 px-4 py-2 bg-exito-600 text-white rounded-lg hover:bg-exito-700 transition-colors">
                  <Download className="w-5 h-5" />
                  Exportar
                </button>
                <button className="flex items-center gap-2 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                  <Plus className="w-5 h-5" />
                  Nuevo Pago
                </button>
              </div>
            </div>

            {/* Tabla de pagos */}
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gris-50 border-b border-gris-200">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Fecha</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Paciente</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Propietario</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Servicio</th>
                    <th className="px-4 py-3 text-right text-sm font-semibold text-gris-900">Monto</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Método</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Estado</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gris-200">
                  {pagosEjemplo.map((pago) => (
                    <tr key={pago.id} className="hover:bg-gris-50 transition-colors">
                      <td className="px-4 py-4">
                        <p className="text-sm text-gris-900">
                          {new Date(pago.fecha).toLocaleDateString('es-ES')}
                        </p>
                      </td>
                      <td className="px-4 py-4">
                        <p className="font-medium text-gris-900">{pago.paciente}</p>
                      </td>
                      <td className="px-4 py-4">
                        <p className="text-sm text-gris-900">{pago.propietario}</p>
                      </td>
                      <td className="px-4 py-4">
                        <p className="text-sm text-gris-700">{pago.servicio}</p>
                      </td>
                      <td className="px-4 py-4 text-right">
                        <p className="font-semibold text-gris-900">${pago.monto.toFixed(2)}</p>
                      </td>
                      <td className="px-4 py-4 text-center">
                        <span className="px-2 py-1 rounded-full text-xs font-medium bg-primario-100 text-primario-700">
                          {pago.metodoPago}
                        </span>
                      </td>
                      <td className="px-4 py-4 text-center">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getEstadoBadgeColor(pago.estado)}`}>
                          {pago.estado}
                        </span>
                      </td>
                      <td className="px-4 py-4">
                        <div className="flex items-center justify-center gap-2">
                          {pago.facturaNumero ? (
                            <button className="px-3 py-1 text-sm bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                              Ver Factura
                            </button>
                          ) : (
                            <button className="px-3 py-1 text-sm bg-exito-600 text-white rounded-lg hover:bg-exito-700 transition-colors">
                              Generar Factura
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Contenido tab Reportes */}
        {tabActiva === 'reportes' && (
          <div className="p-6">
            <p className="text-center text-gris-600 py-12">
              Reportes financieros y estadísticas (próximamente)
            </p>
          </div>
        )}

        {/* Contenido tab Configuración */}
        {tabActiva === 'configuracion' && (
          <div className="p-6">
            <p className="text-center text-gris-600 py-12">
              Configuración de facturación y métodos de pago (próximamente)
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
