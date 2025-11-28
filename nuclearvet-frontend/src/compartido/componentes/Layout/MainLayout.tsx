import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

export const MainLayout = () => {
  return (
    <div className="min-h-screen bg-gris-50">
      <Sidebar />
      
      {/* Contenido principal */}
      <main className="ml-60 p-8">
        <Outlet />
      </main>
    </div>
  );
};
