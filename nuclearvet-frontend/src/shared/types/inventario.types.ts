// Tipos para el módulo de Inventario

export type CategoriaProducto = 'MEDICAMENTO' | 'VACUNA' | 'ALIMENTO' | 'ACCESORIO' | 'INSTRUMENTAL' | 'OTROS';

export type EstadoStock = 'NORMAL' | 'BAJO' | 'CRITICO' | 'AGOTADO';

export type TipoMovimiento = 'ENTRADA' | 'SALIDA' | 'AJUSTE' | 'VENCIMIENTO';

export interface Producto {
  id: number;
  nombre: string;
  codigo: string;
  categoria: CategoriaProducto;
  descripcion?: string;
  precio: number;
  stockActual: number;
  stockMinimo: number;
  stockMaximo: number;
  unidadMedida: string;
  proveedor?: string;
  lote?: string;
  fechaVencimiento?: string;
  ubicacion?: string;
  activo: boolean;
  fechaRegistro: string;
}

export interface ProductoFormData {
  nombre: string;
  codigo: string;
  categoria: CategoriaProducto;
  descripcion?: string;
  precio: number;
  stockActual: number;
  stockMinimo: number;
  stockMaximo: number;
  unidadMedida: string;
  proveedor?: string;
  lote?: string;
  fechaVencimiento?: string;
  ubicacion?: string;
}

export interface MovimientoInventario {
  id: number;
  productoId: number;
  productoNombre: string;
  tipo: TipoMovimiento;
  cantidad: number;
  precioUnitario: number;
  motivo: string;
  usuarioId: number;
  usuarioNombre: string;
  fecha: string;
  observaciones?: string;
}

export interface ProductoFilters {
  busqueda?: string;
  categoria?: CategoriaProducto;
  estadoStock?: EstadoStock;
  activo?: boolean;
}

export interface InventarioResumen {
  totalProductos: number;
  stockBajo: number;
  stockCritico: number;
  valorTotal: number;
  proximosVencer: number;
}
