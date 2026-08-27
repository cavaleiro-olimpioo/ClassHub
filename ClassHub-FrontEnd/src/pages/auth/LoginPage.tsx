import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { GraduationCap, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from '../../contexts/ToastContext';
import { Input } from '../../components/Input/Input';
import { Select } from '../../components/Select/Select';
import { Button } from '../../components/Button/Button';

export function LoginPage() {
  const [matricula, setMatricula] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('admin');
  const [remember, setRemember] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!matricula || !password) {
      addToast('Preencha todos os campos', 'error');
      return;
    }
    setLoading(true);
    setTimeout(() => {
      const success = login(matricula, password, role);
      setLoading(false);
      if (success) {
        addToast('Login realizado com sucesso!');
        navigate('/dashboard');
      } else {
        addToast('Credenciais inválidas. Use: ADM001, PROF001 ou 2024001 / senha: 123456', 'error');
      }
    }, 800);
  };

  return (
    <div className="flex min-h-screen">
      <div className="hidden w-1/2 bg-primary-600 lg:flex lg:flex-col lg:justify-center lg:px-16">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-white/20">
            <GraduationCap className="h-7 w-7 text-white" />
          </div>
          <span className="text-3xl font-bold text-white">ClassHub</span>
        </div>
        <h1 className="mt-8 text-4xl font-bold leading-tight text-white">
          Sistema de Gestão Escolar
        </h1>
        <p className="mt-4 max-w-md text-lg text-primary-100">
          Plataforma completa para gerenciamento acadêmico do Ensino Fundamental, do 1º ao 9º ano.
        </p>
        <div className="mt-12 grid grid-cols-3 gap-4">
          {['Gestão Acadêmica', 'Comunicação', 'Relatórios'].map(item => (
            <div key={item} className="rounded-xl bg-white/10 p-4 text-center text-sm text-white backdrop-blur">
              {item}
            </div>
          ))}
        </div>
      </div>

      <div className="flex w-full flex-col justify-center px-6 py-12 lg:w-1/2 lg:px-16">
        <div className="mx-auto w-full max-w-md">
          <div className="mb-8 lg:hidden">
            <div className="flex items-center gap-2">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-600 text-white font-bold">CH</div>
              <span className="text-2xl font-bold">ClassHub</span>
            </div>
          </div>

          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Entrar na plataforma</h2>
          <p className="mt-2 text-sm text-gray-500">Acesse sua conta para continuar</p>

          <form onSubmit={handleSubmit} className="mt-8 space-y-5">
            <Input
              label="Matrícula ou E-mail"
              id="matricula"
              value={matricula}
              onChange={e => setMatricula(e.target.value)}
              placeholder="Ex: ADM001 ou email@classhub.edu.br"
            />
            <div className="relative">
              <Input
                label="Senha"
                id="password"
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Digite sua senha"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-9 text-gray-400 hover:text-gray-600"
              >
                {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
              </button>
            </div>
            <Select
              label="Tipo de usuário"
              id="role"
              value={role}
              onChange={e => setRole(e.target.value)}
              options={[
                { value: 'admin', label: 'Administrador' },
                { value: 'professor', label: 'Professor' },
                { value: 'aluno', label: 'Aluno' },
              ]}
            />
            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                <input type="checkbox" checked={remember} onChange={e => setRemember(e.target.checked)} className="rounded border-gray-300" />
                Lembrar-me
              </label>
              <button type="button" className="text-sm text-primary-600 hover:underline" onClick={() => addToast('Funcionalidade simulada. Entre em contato com a secretaria.', 'info')}>
                Esqueci minha senha
              </button>
            </div>
            <Button type="submit" className="w-full" loading={loading}>Entrar</Button>
          </form>

          <div className="mt-8 rounded-lg bg-gray-50 p-4 dark:bg-gray-800">
            <p className="text-xs font-medium text-gray-500 dark:text-gray-400">Contas de demonstração:</p>
            <div className="mt-2 space-y-1 text-xs text-gray-600 dark:text-gray-300">
              <p>Admin: ADM001 / 123456</p>
              <p>Professor: PROF001 / 123456</p>
              <p>Aluno: 2024001 / 123456</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
