import { lazy, Suspense } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import AppShell from './components/AppShell.jsx';
import RequireAuth from './components/RequireAuth.jsx';
import { Loading } from './components/ui.jsx';
import { ToastProvider } from './components/ToastProvider.jsx';
import { setUnauthorizedHandler } from './lib/api.js';
import { getSession } from './lib/session.js';

import Login from './pages/Login.jsx';
import RecuperarSenha from './pages/RecuperarSenha.jsx';

// Code-splitting por area para manter o bundle inicial enxuto
const AdminDashboard = lazy(() => import('./pages/admin/Dashboard.jsx'));
const AdminAlunos = lazy(() => import('./pages/admin/Alunos.jsx'));
const AdminProfessores = lazy(() => import('./pages/admin/Professores.jsx'));
const AdminSeries = lazy(() => import('./pages/admin/Series.jsx'));
const AdminTurmas = lazy(() => import('./pages/admin/Turmas.jsx'));
const AdminDisciplinas = lazy(() => import('./pages/admin/Disciplinas.jsx'));
const AdminHorarios = lazy(() => import('./pages/admin/Horarios.jsx'));
const AdminCalendario = lazy(() => import('./pages/admin/Calendario.jsx'));
const AdminBimestre = lazy(() => import('./pages/admin/Bimestre.jsx'));
const AdminAchadosPerdidos = lazy(() => import('./pages/admin/AchadosPerdidos.jsx'));

const ProfessorDashboard = lazy(() => import('./pages/professor/Dashboard.jsx'));
const ProfessorChamada = lazy(() => import('./pages/professor/Chamada.jsx'));
const ProfessorNotas = lazy(() => import('./pages/professor/Notas.jsx'));
const ProfessorOcorrencias = lazy(() => import('./pages/professor/Ocorrencias.jsx'));
const ProfessorAlunos = lazy(() => import('./pages/professor/Alunos.jsx'));
const ProfessorHorarios = lazy(() => import('./pages/professor/Horarios.jsx'));

const AlunoDashboard = lazy(() => import('./pages/aluno/Dashboard.jsx'));
const AlunoNotas = lazy(() => import('./pages/aluno/Notas.jsx'));
const AlunoFaltas = lazy(() => import('./pages/aluno/Faltas.jsx'));
const AlunoBoletim = lazy(() => import('./pages/aluno/Boletim.jsx'));
const AlunoHorarios = lazy(() => import('./pages/aluno/Horarios.jsx'));
const AlunoOcorrencias = lazy(() => import('./pages/aluno/Ocorrencias.jsx'));
const AlunoCalendario = lazy(() => import('./pages/aluno/Calendario.jsx'));
const AlunoAchadosPerdidos = lazy(() => import('./pages/aluno/AchadosPerdidos.jsx'));

/** Raiz: /admin, /professor ou /aluno conforme o perfil logado. */
function HomeRedirect() {
  const session = getSession();
  if (!session) return <Navigate to="/login" replace />;
  return <Navigate to={`/${session.perfil.toLowerCase()}`} replace />;
}

function NotFound() {
  return (
    <div className="empty" style={{ paddingTop: 80 }}>
      <span className="empty__icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 8v5M12 16h.01" />
        </svg>
      </span>
      <div className="empty__title">Página não encontrada</div>
      <p className="empty__sub">O endereço acessado não existe ou foi movido.</p>
    </div>
  );
}

export default function App() {
  // 401 -> limpa a sessao e volta ao login (substitui o window.location do front estatico)
  setUnauthorizedHandler(() => {
    if (!window.location.pathname.startsWith('/login')) {
      window.location.assign('/login?expired=true');
    }
  });

  return (
    <BrowserRouter>
      <ToastProvider>
        <Suspense fallback={<Loading message="Carregando módulo..." />}>
          <Routes>
            {/* Publicas */}
            <Route path="/login" element={<Login />} />
            <Route path="/recuperar-senha" element={<RecuperarSenha />} />
            <Route path="/" element={<HomeRedirect />} />

            {/* Admin */}
            <Route element={<RequireAuth perfil="ADMIN"><AppShell /></RequireAuth>}>
              <Route path="/admin" element={<AdminDashboard />} />
              <Route path="/admin/alunos" element={<AdminAlunos />} />
              <Route path="/admin/professores" element={<AdminProfessores />} />
              <Route path="/admin/series" element={<AdminSeries />} />
              <Route path="/admin/turmas" element={<AdminTurmas />} />
              <Route path="/admin/disciplinas" element={<AdminDisciplinas />} />
              <Route path="/admin/horarios" element={<AdminHorarios />} />
              <Route path="/admin/calendario" element={<AdminCalendario />} />
              <Route path="/admin/bimestre" element={<AdminBimestre />} />
              <Route path="/admin/achados-perdidos" element={<AdminAchadosPerdidos />} />
              <Route path="/admin/*" element={<NotFound />} />
            </Route>

            {/* Professor */}
            <Route element={<RequireAuth perfil="PROFESSOR"><AppShell /></RequireAuth>}>
              <Route path="/professor" element={<ProfessorDashboard />} />
              <Route path="/professor/chamada" element={<ProfessorChamada />} />
              <Route path="/professor/notas" element={<ProfessorNotas />} />
              <Route path="/professor/ocorrencias" element={<ProfessorOcorrencias />} />
              <Route path="/professor/alunos" element={<ProfessorAlunos />} />
              <Route path="/professor/horarios" element={<ProfessorHorarios />} />
              <Route path="/professor/*" element={<NotFound />} />
            </Route>

            {/* Aluno */}
            <Route element={<RequireAuth perfil="ALUNO"><AppShell /></RequireAuth>}>
              <Route path="/aluno" element={<AlunoDashboard />} />
              <Route path="/aluno/notas" element={<AlunoNotas />} />
              <Route path="/aluno/faltas" element={<AlunoFaltas />} />
              <Route path="/aluno/boletim" element={<AlunoBoletim />} />
              <Route path="/aluno/horarios" element={<AlunoHorarios />} />
              <Route path="/aluno/ocorrencias" element={<AlunoOcorrencias />} />
              <Route path="/aluno/calendario" element={<AlunoCalendario />} />
              <Route path="/aluno/achados-perdidos" element={<AlunoAchadosPerdidos />} />
              <Route path="/aluno/*" element={<NotFound />} />
            </Route>

            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </ToastProvider>
    </BrowserRouter>
  );
}
