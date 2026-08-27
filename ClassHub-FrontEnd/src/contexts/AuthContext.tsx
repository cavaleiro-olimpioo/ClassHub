import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { User, Permission } from '../types';
import { DEMO_USERS, ROLE_PERMISSIONS } from '../data/mockData';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (matricula: string, password: string, role: string) => boolean;
  logout: () => void;
  hasPermission: (permission: Permission) => boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('classhub_user');
    return saved ? JSON.parse(saved) : null;
  });

  useEffect(() => {
    if (user) localStorage.setItem('classhub_user', JSON.stringify(user));
    else localStorage.removeItem('classhub_user');
  }, [user]);

  const login = (matricula: string, password: string, role: string) => {
    const found = DEMO_USERS.find(
      u => (u.matricula === matricula || u.email === matricula) && u.password === password && u.role === role
    );
    if (!found) return false;
    const { password: _, ...userData } = found;
    setUser({
      ...userData,
      permissions: ROLE_PERMISSIONS[found.role],
    });
    return true;
  };

  const logout = () => setUser(null);

  const hasPermission = (permission: Permission) => {
    return user?.permissions.includes(permission) ?? false;
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, logout, hasPermission }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
