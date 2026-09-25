import { Navigate, useLocation } from 'react-router-dom';
import { getSession, isTokenExpired } from '../lib/session.js';

/**
 * Guarda de rotas (substitui `Auth.initRouteGuard()`).
 * - Sem sessao -> /login
 * - Token expirado -> limpa e volta ao login
 * - Perfil diferente do exigido -> dashboard do proprio perfil
 */
export default function RequireAuth({ children, perfil }) {
  const location = useLocation();
  const session = getSession();

  if (!session) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (isTokenExpired(session.token)) {
    try {
      sessionStorage.clear();
    } catch {
      /* ignore */
    }
    return <Navigate to="/login?expired=true" replace />;
  }

  if (perfil && session.perfil !== perfil) {
    return <Navigate to={`/${session.perfil.toLowerCase()}`} replace />;
  }

  return children;
}
