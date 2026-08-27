import { useState, useMemo } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Menu, Search, Bell, Sun, Moon, ChevronRight } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';
import { useData } from '../../contexts/DataContext';
import { Avatar } from '../Avatar/Avatar';
import { Dropdown, DropdownItem } from '../Dropdown/Dropdown';
import { Badge } from '../Badge/Badge';
import { formatDateTime } from '../../utils';

const routeLabels: Record<string, string> = {
  dashboard: 'Dashboard', alunos: 'Alunos', turmas: 'Turmas', notas: 'Notas',
  frequencia: 'Frequência', boletim: 'Boletim', horarios: 'Horários',
  avisos: 'Mural de Avisos', calendario: 'Calendário', notificacoes: 'Notificações',
  professores: 'Professores', ocorrencias: 'Ocorrências', 'achados-perdidos': 'Achados e Perdidos',
  cadastros: 'Cadastros', configuracoes: 'Configurações', perfil: 'Perfil',
  novo: 'Novo', editar: 'Editar',
};

const roleLabels: Record<string, string> = {
  admin: 'Administrador', professor: 'Professor', aluno: 'Aluno',
};

interface HeaderProps {
  onMenuClick: () => void;
  sidebarCollapsed: boolean;
}

export function Header({ onMenuClick, sidebarCollapsed }: HeaderProps) {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const { students, teachers, classes, announcements, events, notifications, setNotifications } = useData();
  const location = useLocation();
  const navigate = useNavigate();
  const [search, setSearch] = useState('');

  const breadcrumbs = useMemo(() => {
    const parts = location.pathname.split('/').filter(Boolean);
    return parts.map((part, i) => ({
      label: routeLabels[part] || part,
      path: '/' + parts.slice(0, i + 1).join('/'),
    }));
  }, [location.pathname]);

  const searchResults = useMemo(() => {
    if (!search || search.length < 2) return [];
    const q = search.toLowerCase();
    const results: { type: string; label: string; path: string }[] = [];
    students.filter(s => s.name.toLowerCase().includes(q)).slice(0, 3).forEach(s => results.push({ type: 'Aluno', label: s.name, path: `/alunos/${s.id}` }));
    teachers.filter(t => t.name.toLowerCase().includes(q)).slice(0, 3).forEach(t => results.push({ type: 'Professor', label: t.name, path: `/professores/${t.id}` }));
    classes.filter(c => c.name.toLowerCase().includes(q)).slice(0, 3).forEach(c => results.push({ type: 'Turma', label: c.name, path: `/turmas/${c.id}` }));
    announcements.filter(a => a.title.toLowerCase().includes(q)).slice(0, 2).forEach(a => results.push({ type: 'Aviso', label: a.title, path: '/avisos' }));
    events.filter(e => e.title.toLowerCase().includes(q)).slice(0, 2).forEach(e => results.push({ type: 'Evento', label: e.title, path: '/calendario' }));
    return results;
  }, [search, students, teachers, classes, announcements, events]);

  const unreadCount = notifications.filter(n => !n.read).length;

  const markAllRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, read: true })));
  };

  return (
    <header className={`sticky top-0 z-30 flex h-16 items-center justify-between border-b border-gray-200 bg-white/80 px-4 backdrop-blur-md dark:border-gray-700 dark:bg-gray-900/80 lg:px-6 ${sidebarCollapsed ? 'lg:pl-20' : 'lg:pl-68'}`}>
      <div className="flex items-center gap-4">
        <button onClick={onMenuClick} className="rounded-lg p-2 hover:bg-gray-100 lg:hidden dark:hover:bg-gray-800">
          <Menu className="h-5 w-5" />
        </button>
        <nav className="hidden items-center gap-1 text-sm sm:flex">
          {breadcrumbs.map((crumb, i) => (
            <span key={crumb.path} className="flex items-center gap-1">
              {i > 0 && <ChevronRight className="h-3.5 w-3.5 text-gray-400" />}
              <button
                onClick={() => navigate(crumb.path)}
                className={`${i === breadcrumbs.length - 1 ? 'font-medium text-gray-900 dark:text-gray-100' : 'text-gray-500 hover:text-gray-700 dark:text-gray-400'}`}
              >
                {crumb.label}
              </button>
            </span>
          ))}
        </nav>
      </div>

      <div className="flex items-center gap-2">
        <div className="relative hidden md:block">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Buscar alunos, turmas..."
            className="w-64 rounded-lg border border-gray-200 bg-gray-50 py-2 pl-9 pr-3 text-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100"
          />
          {searchResults.length > 0 && (
            <div className="absolute right-0 top-full mt-1 w-80 rounded-xl border border-gray-200 bg-white py-2 shadow-lg dark:border-gray-700 dark:bg-gray-800">
              {searchResults.map((r, i) => (
                <button
                  key={i}
                  onClick={() => { navigate(r.path); setSearch(''); }}
                  className="flex w-full items-center gap-3 px-4 py-2 text-left text-sm hover:bg-gray-50 dark:hover:bg-gray-700"
                >
                  <Badge color="blue">{r.type}</Badge>
                  <span className="text-gray-700 dark:text-gray-300">{r.label}</span>
                </button>
              ))}
            </div>
          )}
        </div>

        <button onClick={toggleTheme} className="rounded-lg p-2 text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800">
          {theme === 'light' ? <Moon className="h-5 w-5" /> : <Sun className="h-5 w-5" />}
        </button>

        <Dropdown
          trigger={
            <button className="relative rounded-lg p-2 text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800">
              <Bell className="h-5 w-5" />
              {unreadCount > 0 && (
                <span className="absolute -right-0.5 -top-0.5 flex h-4 w-4 items-center justify-center rounded-full bg-accent-500 text-[10px] font-bold text-white">
                  {unreadCount}
                </span>
              )}
            </button>
          }
          className="w-80"
        >
          <div className="border-b border-gray-200 px-4 py-3 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <span className="font-semibold text-gray-900 dark:text-gray-100">Notificações</span>
              <button onClick={markAllRead} className="text-xs text-primary-600 hover:underline">Marcar todas como lidas</button>
            </div>
          </div>
          {notifications.slice(0, 6).map(n => (
            <div key={n.id} className={`px-4 py-3 ${!n.read ? 'bg-primary-50/50 dark:bg-primary-900/10' : ''}`}>
              <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{n.title}</p>
              <p className="text-xs text-gray-500">{n.message}</p>
              <p className="mt-1 text-xs text-gray-400">{formatDateTime(n.date)}</p>
            </div>
          ))}
          <DropdownItem onClick={() => navigate('/notificacoes')}>Ver todas</DropdownItem>
        </Dropdown>

        <Dropdown
          trigger={
            <button className="flex items-center gap-2 rounded-lg p-1.5 hover:bg-gray-100 dark:hover:bg-gray-800">
              <Avatar src={user?.avatar} name={user?.name || ''} size="sm" />
              <div className="hidden text-left lg:block">
                <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{user?.name}</p>
                <p className="text-xs text-gray-500">{roleLabels[user?.role || '']}</p>
              </div>
            </button>
          }
        >
          <DropdownItem onClick={() => navigate('/perfil')}>Meu Perfil</DropdownItem>
          <DropdownItem onClick={() => navigate('/configuracoes')}>Configurações</DropdownItem>
          <DropdownItem onClick={() => { logout(); navigate('/login'); }}>Sair</DropdownItem>
        </Dropdown>
      </div>
    </header>
  );
}
