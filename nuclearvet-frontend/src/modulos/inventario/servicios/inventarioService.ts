import api from '../../../shared/services/api';
import type { 
  Producto, 
  ProductoFormData, 
  ProductoFilters, 
  MovimientoInventario,
  InventarioResumen 
} from '../../../shared/types/inventario.types';

export const inventarioService = {
  // RF4.1: Listar productos
  getAll: async (filters?: ProductoFilters): Promise<Producto[]> => {
    const params = new URLSearchParams();
    if (filters?.busqueda) params.append('busqueda', filters.busqueda);
    if (filters?.categoria) params.append('categoria', filters.categoria);
    if (filters?.estadoStock) params.append('estadoStock', filters.estadoStock);
    if (filters?.activo !== undefined) params.append('activo', filters.activo.toString());
    
    const response = await api.get<Producto[]>(`/inventario/productos?${params.toString()}`);
    return response.data;
  },

  // RF4.2: Obtener producto por ID
  getById: async (id: number): Promise<Producto> => {
    const response = await api.get<Producto>(`/inventario/productos/${id}`);
    return response.data;
  },

  // RF4.3: Crear producto
  create: async (data: ProductoFormData): Promise<Producto> => {
    const response = await api.post<Producto>('/inventario/productos', data);
    return response.data;
  },

  // RF4.4: Actualizar producto
  update: async (id: number, data: Partial<ProductoFormData>): Promise<Producto> => {
    const response = await api.put<Producto>(`/inventario/productos/${id}`, data);
    return response.data;
  },

  // RF4.5: Eliminar producto
  delete: async (id: number): Promise<void> => {
    await api.delete(`/inventario/productos/${id}`);
  },

  // Obtener resumen de inventario
  getResumen: async (): Promise<InventarioResumen> => {
    const response = await api.get<InventarioResumen>('/inventario/resumen');
    return response.data;
  },

  // Registrar entrada de stock
  registrarEntrada: async (productoId: number, cantidad: number, motivo: string) => {
    const response = await api.post(`/inventario/movimientos/entrada`, {
      productoId,
      cantidad,
      motivo
    });
    return response.data;
  },

  // Registrar salida de stock
  registrarSalida: async (productoId: number, cantidad: number, motivo: string) => {
    const response = await api.post(`/inventario/movimientos/salida`, {
      productoId,
      cantidad,
      motivo
    });
    return response.data;
  },

  // Obtener movimientos de un producto
  getMovimientos: async (productoId?: number): Promise<MovimientoInventario[]> => {
    const url = productoId 
      ? `/inventario/movimientos?productoId=${productoId}`
      : '/inventario/movimientos';
    const response = await api.get<MovimientoInventario[]>(url);
    return response.data;
  },

  // Obtener productos con stock bajo
  getStockBajo: async (): Promise<Producto[]> => {
    const response = await api.get<Producto[]>('/inventario/productos/stock-bajo');
    return response.data;
  },

  // Obtener productos próximos a vencer
  getProximosVencer: async (dias: number = 30): Promise<Producto[]> => {
    const response = await api.get<Producto[]>(`/inventario/productos/proximos-vencer?dias=${dias}`);
    return response.data;
  },
};
