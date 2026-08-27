import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard, Users, GraduationCap, BookOpen, ClipboardCheck,
  FileText, Clock, Megaphone, Calendar, Bell, UserCheck, AlertTriangle,
  Search, Settings, User, LogOut, ChevronLeft, ChevronRight, X, Database,
} from 'lucide-react';
import { cn } from '../../utils';
import { useAuth } from '../../contexts/AuthContext';
import type { Permission } from '../../types';

interface NavItem {
  label: string;
  path: string;
  icon: React.ElementType;
  permission?: Permission;
  roles?: string[];
}

interface NavSection {
  title: string;
  items: NavItem[];
}

const navSections: NavSection[] = [
  {
    title: 'Principal',
    items: [{ label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard }],
  },
  {
    title: 'Acadêmico',
    items: [
      { label: 'Alunos', path: '/alunos', icon: Users, permission: 'view_students' },
      { label: 'Turmas', path: '/turmas', icon: GraduationCap, permission: 'view_classes' },
      { label: 'Notas', path: '/notas', icon: BookOpen, permission: 'manage_grades', roles: ['admin', 'professor'] },
      { label: 'Frequência', path: '/frequencia', icon: ClipboardCheck, permission: 'manage_attendance', roles: ['admin', 'professor'] },
      { label: 'Boletim', path: '/boletim', icon: FileText, permission: 'view_report_card' },
      { label: 'Horários', path: '/horarios', icon: Clock },
    ],
  },
  {
    title: 'Comunicação',
    items: [
      { label: 'Mural de Avisos', path: '/avisos', icon: Megaphone },
      { label: 'Calendário', path: '/calendario', icon: Calendar },
      { label: 'Notificações', path: '/notificacoes', icon: Bell },
    ],
  },
  {
    title: 'Gestão',
    items: [
      { label: 'Professores', path: '/professores', icon: UserCheck, permission: 'view_teachers' },
      { label: 'Ocorrências', path: '/ocorrencias', icon: AlertTriangle, roles: ['admin', 'professor'] },
      { label: 'Achados e Perdidos', path: '/achados-perdidos', icon: Search },
      { label: 'Cadastros', path: '/cadastros', icon: Database, permission: 'manage_registrations' },
    ],
  },
  {
    title: 'Sistema',
    items: [
      { label: 'Configurações', path: '/configuracoes', icon: Settings, permission: 'view_settings' },
      { label: 'Perfil', path: '/perfil', icon: User },
    ],
  },
];

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
  mobileOpen: boolean;
  onMobileClose: () => void;
}

export function Sidebar({ collapsed, onToggle, mobileOpen, onMobileClose }: SidebarProps) {
  const { user, logout, hasPermission } = useAuth();
  const navigate = useNavigate();

  const filterItems = (items: NavItem[]) =>
    items.filter(item => {
      if (item.roles && user && !item.roles.includes(user.role)) return false;
      if (item.permission && !hasPermission(item.permission)) return false;
      return true;
    });

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const content = (
    <div className="flex h-full flex-col">
      <div className="flex h-16 items-center justify-between border-b border-gray-200 px-4 dark:border-gray-700">
        {!collapsed && (
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary-600 text-white font-bold text-sm">CH</div>
            <span className="text-lg font-bold text-gray-900 dark:text-white">ClassHub</span>
          </div>
        )}
        {collapsed && (
          <div className="mx-auto flex h-8 w-8 items-center justify-center rounded-lg bg-primary-600 text-white font-bold text-sm">CH</div>
        )}
        <button onClick={onMobileClose} className="lg:hidden rounded-lg p-1 hover:bg-gray-100 dark:hover:bg-gray-700">
          <X className="h-5 w-5" />
        </button>
      </div>

      <nav className="flex-1 overflow-y-auto px-3 py-4">
        {navSections.map(section => {
          const items = filterItems(section.items);
          if (items.length === 0) return null;
          return (
            <div key={section.title} className="mb-4">
              {!collapsed && (
                <p className="mb-2 px-3 text-xs font-semibold uppercase tracking-wider text-gray-400">{section.title}</p>
              )}
              {items.map(item => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  onClick={onMobileClose}
                  className={({ isActive }) => cn(
                    'mb-0.5 flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                    isActive
                      ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300'
                      : 'text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-700/50',
                    collapsed && 'justify-center px-2'
                  )}
                  title={collapsed ? item.label : undefined}
                >
                  <item.icon className="h-5 w-5 shrink-0" />
                  {!collapsed && <span>{item.label}</span>}
                </NavLink>
              ))}
            </div>
          );
        })}
      </nav>

      <div className="border-t border-gray-200 p-3 dark:border-gray-700">
        <button
          onClick={handleLogout}
          className={cn(
            'flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-accent-600 hover:bg-accent-50 dark:hover:bg-accent-900/20',
            collapsed && 'justify-center px-2'
          )}
        >
          <LogOut className="h-5 w-5" />
          {!collapsed && <span>Sair</span>}
        </button>
      </div>
    </div>
  );

  return (
    <>
      {mobileOpen && <div className="fixed inset-0 z-40 bg-black/50 lg:hidden" onClick={onMobileClose} />}
      <aside className={cn(
        'fixed inset-y-0 left-0 z-50 flex flex-col border-r border-gray-200 bg-white transition-all duration-300 dark:border-gray-700 dark:bg-gray-900',
        collapsed ? 'w-[72px]' : 'w-64',
        mobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
      )}>
        {content}
        <button
          onClick={onToggle}
          className="absolute -right-3 top-20 hidden h-6 w-6 items-center justify-center rounded-full border border-gray-200 bg-white shadow-sm lg:flex dark:border-gray-600 dark:bg-gray-800"
        >
          {collapsed ? <ChevronRight className="h-3 w-3" /> : <ChevronLeft className="h-3 w-3" />}
        </button>
      </aside>
    </>
  );
}
