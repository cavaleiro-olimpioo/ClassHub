import { createContext, useContext, useState } from 'react';
const AuthContext = createContext();
export function AuthProvider({ children }) { const [usuario, setUsuario] = useState(null); const login = ({ usuario: nome, tipo }) => setUsuario({ nome: nome || (tipo === 'aluno' ? 'Ana Clara' : 'Mariana Costa'), tipo, avatar: tipo === 'aluno' ? 'AC' : 'MC' }); const logout = () => setUsuario(null); return <AuthContext.Provider value={{ usuario, login, logout }}>{children}</AuthContext.Provider>; }
export const useAuth = () => useContext(AuthContext);
