import { useState } from 'react';
import { Search, Plus, Package, AlertTriangle, TrendingDown, DollarSign, Filter } from 'lucide-react';
import type { Producto, CategoriaProducto, EstadoStock } from '../../../shared/types/inventario.types';

export default function PaginaInventario() {
  const [busqueda, setBusqueda] = useState('');
  const [filtroCategoria, setFiltroCategoria] = useState<CategoriaProducto | 'TODOS'>('TODOS');
  const [tabActiva, setTabActiva] = useState<'inventario' | 'movimientos' | 'alertas'>('inventario');

  // Datos de ejemplo
  const productosEjemplo: Producto[] = [
    {
      id: 1,
      nombre: 'Amoxicilina 500mg',
      codigo: 'MED-001',
      categoria: 'MEDICAMENTO' as CategoriaProducto,
      descripcion: 'Antibiótico de amplio espectro',
      precio: 25.50,
      stockActual: 50,
      stockMinimo: 20,
      stockMaximo: 100,
      unidadMedida: 'Tabletas',
      proveedor: 'FarmaVet',
      lote: 'LOT-2024-001',
      fechaVencimiento: '2025-06-30',
      ubicacion: 'Estante A1',
      activo: true,
      fechaRegistro: '2024-01-15'
    },
    {
      id: 2,
      nombre: 'Vacuna Antirrábica',
      codigo: 'VAC-001',
      categoria: 'VACUNA' as CategoriaProducto,
      precio: 45.00,
      stockActual: 8,
      stockMinimo: 15,
      stockMaximo: 50,
      unidadMedida: 'Dosis',
      proveedor: 'BioVet',
      lote: 'LOT-2024-VAC',
      fechaVencimiento: '2025-03-15',
      ubicacion: 'Refrigerador 1',
      activo: true,
      fechaRegistro: '2024-02-20'
    },
    {
      id: 3,
      nombre: 'Alimento Premium Perros',
      codigo: 'ALI-001',
      categoria: 'ALIMENTO' as CategoriaProducto,
      precio: 85.00,
      stockActual: 3,
      stockMinimo: 10,
      stockMaximo: 30,
      unidadMedida: 'Kg',
      proveedor: 'NutriPet',
      ubicacion: 'Bodega B',
      activo: true,
      fechaRegistro: '2024-03-10'
    },
    {
      id: 4,
      nombre: 'Jeringa 10ml',
      codigo: 'INS-001',
      categoria: 'INSTRUMENTAL' as CategoriaProducto,
      precio: 1.50,
      stockActual: 150,
      stockMinimo: 50,
      stockMaximo: 200,
      unidadMedida: 'Unidades',
      proveedor: 'MediSupply',
      ubicacion: 'Estante C3',
      activo: true,
      fechaRegistro: '2024-01-05'
    },
    {
      id: 5,
      nombre: 'Collar Isabelino M',
      codigo: 'ACC-001',
      categoria: 'ACCESORIO' as CategoriaProducto,
      precio: 12.00,
      stockActual: 25,
      stockMinimo: 10,
      stockMaximo: 40,
      unidadMedida: 'Unidades',
      proveedor: 'PetSupplies',
      ubicacion: 'Estante D2',
      activo: true,
      fechaRegistro: '2024-02-01'
    }
  ];

  const resumenInventario = {
    totalProductos: 248,
    stockBajo: 5,
    stockCritico: 3,
    valorTotal: 125750.00
  };

  const getEstadoStock = (producto: Producto): EstadoStock => {
    const porcentaje = (producto.stockActual / producto.stockMinimo) * 100;
    if (producto.stockActual === 0) return 'AGOTADO';
    if (porcentaje <= 50) return 'CRITICO';
    if (porcentaje <= 100) return 'BAJO';
    return 'NORMAL';
  };

  const getEstadoBadgeColor = (estado: EstadoStock) => {
    switch (estado) {
      case 'NORMAL':
        return 'bg-exito-100 text-exito-700';
      case 'BAJO':
        return 'bg-advertencia-100 text-advertencia-700';
      case 'CRITICO':
        return 'bg-peligro-100 text-peligro-700';
      case 'AGOTADO':
        return 'bg-gris-100 text-gris-700';
      default:
        return 'bg-gris-100 text-gris-700';
    }
  };

  const getCategoriaColor = (categoria: CategoriaProducto) => {
    switch (categoria) {
      case 'MEDICAMENTO':
        return 'bg-primario-100 text-primario-700';
      case 'VACUNA':
        return 'bg-exito-100 text-exito-700';
      case 'ALIMENTO':
        return 'bg-advertencia-100 text-advertencia-700';
      case 'INSTRUMENTAL':
        return 'bg-gris-100 text-gris-700';
      case 'ACCESORIO':
        return 'bg-peligro-100 text-peligro-700';
      default:
        return 'bg-gris-100 text-gris-700';
    }
  };

  return (
    <div className="p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gris-900">Inventario</h1>
        <p className="text-gris-600 mt-1">Control de productos, medicamentos y suministros</p>
      </div>

      {/* Tarjetas de resumen */}
      <div className="grid grid-cols-4 gap-6 mb-6">
        {/* Total Productos */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Total Productos</p>
              <p className="text-3xl font-bold text-gris-900">{resumenInventario.totalProductos}</p>
            </div>
            <div className="w-12 h-12 bg-primario-100 rounded-xl flex items-center justify-center">
              <Package className="w-6 h-6 text-primario-600" />
            </div>
          </div>
        </div>

        {/* Stock Bajo */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Stock Bajo</p>
              <p className="text-3xl font-bold text-advertencia-600">{resumenInventario.stockBajo}</p>
            </div>
            <div className="w-12 h-12 bg-advertencia-100 rounded-xl flex items-center justify-center">
              <TrendingDown className="w-6 h-6 text-advertencia-600" />
            </div>
          </div>
        </div>

        {/* Stock Crítico */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Stock Crítico</p>
              <p className="text-3xl font-bold text-peligro-600">{resumenInventario.stockCritico}</p>
            </div>
            <div className="w-12 h-12 bg-peligro-100 rounded-xl flex items-center justify-center">
              <AlertTriangle className="w-6 h-6 text-peligro-600" />
            </div>
          </div>
        </div>

        {/* Valor Total */}
        <div className="bg-white rounded-lg shadow-sm border border-gris-200 p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gris-600 mb-1">Valor Total</p>
              <p className="text-3xl font-bold text-exito-600">${resumenInventario.valorTotal.toLocaleString()}</p>
            </div>
            <div className="w-12 h-12 bg-exito-100 rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-exito-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-sm border border-gris-200 mb-6">
        <div className="border-b border-gris-200">
          <div className="flex gap-6 px-6">
            <button
              onClick={() => setTabActiva('inventario')}
              className={`py-4 border-b-2 font-medium transition-colors ${
                tabActiva === 'inventario'
                  ? 'border-primario-600 text-primario-600'
                  : 'border-transparent text-gris-600 hover:text-gris-900'
              }`}
            >
              Inventario
            </button>
            <button
              onClick={() => setTabActiva('movimientos')}
              className={`py-4 border-b-2 font-medium transition-colors ${
                tabActiva === 'movimientos'
                  ? 'border-primario-600 text-primario-600'
                  : 'border-transparent text-gris-600 hover:text-gris-900'
              }`}
            >
              Movimientos
            </button>
            <button
              onClick={() => setTabActiva('alertas')}
              className={`py-4 border-b-2 font-medium transition-colors ${
                tabActiva === 'alertas'
                  ? 'border-primario-600 text-primario-600'
                  : 'border-transparent text-gris-600 hover:text-gris-900'
              }`}
            >
              Alertas
            </button>
          </div>
        </div>

        {/* Contenido del tab Inventario */}
        {tabActiva === 'inventario' && (
          <div className="p-6">
            {/* Barra de búsqueda y filtros */}
            <div className="flex items-center gap-4 mb-6">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gris-400" />
                <input
                  type="text"
                  placeholder="Buscar por nombre, código o categoría..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
                />
              </div>

              <div className="flex items-center gap-2">
                <Filter className="w-5 h-5 text-gris-400" />
                <select
                  value={filtroCategoria}
                  onChange={(e) => setFiltroCategoria(e.target.value as CategoriaProducto | 'TODOS')}
                  className="px-4 py-2 border border-gris-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primario-500"
                >
                  <option value="TODOS">Todas las categorías</option>
                  <option value="MEDICAMENTO">Medicamentos</option>
                  <option value="VACUNA">Vacunas</option>
                  <option value="ALIMENTO">Alimentos</option>
                  <option value="INSTRUMENTAL">Instrumental</option>
                  <option value="ACCESORIO">Accesorios</option>
                  <option value="OTROS">Otros</option>
                </select>
              </div>

              <button className="flex items-center gap-2 px-4 py-2 bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                <Plus className="w-5 h-5" />
                Nuevo Producto
              </button>

              <button className="flex items-center gap-2 px-4 py-2 bg-exito-600 text-white rounded-lg hover:bg-exito-700 transition-colors">
                <Package className="w-5 h-5" />
                Registrar Entrada
              </button>
            </div>

            {/* Tabla de productos */}
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gris-50 border-b border-gris-200">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Producto</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gris-900">Categoría</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Stock Actual</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Stock Mínimo</th>
                    <th className="px-4 py-3 text-right text-sm font-semibold text-gris-900">Precio</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Vencimiento</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Estado</th>
                    <th className="px-4 py-3 text-center text-sm font-semibold text-gris-900">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gris-200">
                  {productosEjemplo.map((producto) => {
                    const estadoStock = getEstadoStock(producto);
                    return (
                      <tr key={producto.id} className="hover:bg-gris-50 transition-colors">
                        <td className="px-4 py-4">
                          <div>
                            <p className="font-medium text-gris-900">{producto.nombre}</p>
                            <p className="text-sm text-gris-600">{producto.codigo}</p>
                          </div>
                        </td>
                        <td className="px-4 py-4">
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${getCategoriaColor(producto.categoria)}`}>
                            {producto.categoria}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-center">
                          <span className="font-semibold text-gris-900">
                            {producto.stockActual} {producto.unidadMedida}
                          </span>
                        </td>
                        <td className="px-4 py-4 text-center text-gris-600">
                          {producto.stockMinimo}
                        </td>
                        <td className="px-4 py-4 text-right font-medium text-gris-900">
                          ${producto.precio.toFixed(2)}
                        </td>
                        <td className="px-4 py-4 text-center text-sm text-gris-600">
                          {producto.fechaVencimiento 
                            ? new Date(producto.fechaVencimiento).toLocaleDateString('es-ES')
                            : '-'
                          }
                        </td>
                        <td className="px-4 py-4 text-center">
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${getEstadoBadgeColor(estadoStock)}`}>
                            {estadoStock}
                          </span>
                        </td>
                        <td className="px-4 py-4">
                          <div className="flex items-center justify-center gap-2">
                            <button className="px-3 py-1 text-sm bg-primario-600 text-white rounded-lg hover:bg-primario-700 transition-colors">
                              Editar
                            </button>
                            <button className="px-3 py-1 text-sm bg-gris-200 text-gris-700 rounded-lg hover:bg-gris-300 transition-colors">
                              Ver
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Contenido del tab Movimientos */}
        {tabActiva === 'movimientos' && (
          <div className="p-6">
            <p className="text-center text-gris-600 py-12">
              Historial de movimientos de inventario (próximamente)
            </p>
          </div>
        )}

        {/* Contenido del tab Alertas */}
        {tabActiva === 'alertas' && (
          <div className="p-6">
            <p className="text-center text-gris-600 py-12">
              Alertas de stock bajo y productos próximos a vencer (próximamente)
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
