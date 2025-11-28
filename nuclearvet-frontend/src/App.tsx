import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { MainLayout } from './compartido/componentes/Layout/MainLayout';
import { PaginaLogin } from './modulos/autenticacion/paginas/PaginaLogin';
import { Dashboard } from './modulos/dashboard/paginas/Dashboard';
import PaginaPacientes from './modulos/pacientes/paginas/PaginaPacientes';
import PaginaCitas from './modulos/citas/paginas/PaginaCitas';
import PaginaInventario from './modulos/inventario/paginas/PaginaInventario';
import PaginaUsuarios from './modulos/usuarios/paginas/PaginaUsuarios';
import PaginaNotificaciones from './modulos/notificaciones/paginas/PaginaNotificaciones';
import PaginaAdministracion from './modulos/administracion/paginas/PaginaAdministracion';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Ruta pública - Login */}
        <Route path="/login" element={<PaginaLogin />} />
        
        {/* Rutas protegidas con layout */}
        <Route element={<MainLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/pacientes" element={<PaginaPacientes />} />
          <Route path="/citas" element={<PaginaCitas />} />
          <Route path="/inventario" element={<PaginaInventario />} />
          <Route path="/usuarios" element={<PaginaUsuarios />} />
          <Route path="/notificaciones" element={<PaginaNotificaciones />} />
          <Route path="/administracion" element={<PaginaAdministracion />} />
        </Route>

        {/* Ruta por defecto */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
