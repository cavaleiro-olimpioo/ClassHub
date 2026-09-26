import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import Icon from '../components/Icon.jsx';
import ThemeToggle from '../components/ThemeToggle.jsx';
import { Button, Field, Input } from '../components/ui.jsx';
import { useToast } from '../components/ToastProvider.jsx';
import { dashboardPathFor, login as doLogin } from '../lib/session.js';

const HIGHLIGHTS = [
  'Notas, boletins e médias consolidated por bimestre',
  'Frequência e justificativa de faltas em tempo real',
  'Grade de horários, calendário e comunicados da escola',
  'Achados e perdidos e ocorrências do dia a dia'
];

export default function Login() {
  const navigate = useNavigate();
  const toast = useToast();
  const [searchParams] = useSearchParams();

  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Mensagem de sessao expirada vinda do 401 (apenas uma vez)
  useEffect(() => {
    if (searchParams.get('expired') === 'true') {
      toast.warning('Sua sessão expirou. Faça login novamente.');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();

    if (!email.trim() || !senha) {
      toast.warning('Por favor, preencha todos os campos.');
      return;
    }

    setSubmitting(true);
    try {
      const session = await doLogin(email.trim(), senha);
      toast.success('Login realizado com sucesso!');
      navigate(dashboardPathFor(session.perfil), { replace: true });
    } catch (error) {
      toast.error(error.message || 'Não foi possível realizar o login.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth auth--split">
      <ThemeToggle className="auth__theme" />
      <div className="auth__aside">
        <h2>
          Bem-vindo ao
          <br />
          ClassHub
        </h2>
        <p>
          Gestão escolar completa para rede de ensino: secretaria, professores e alunos no mesmo lugar, com dados
          sempre atualizados.
        </p>
        {HIGHLIGHTS.map((item) => (
          <div className="bullet" key={item}>
            <Icon name="check" size={18} />
            <span>{item}</span>
          </div>
        ))}
      </div>

      <div className="auth__card">
        <div className="auth__brand">
          <span className="sidebar-brand__mark" style={{ background: 'linear-gradient(135deg,#4f7cff,#2f4bd8)' }}>
            <Icon name="school" size={19} strokeWidth={2} />
          </span>
          <span>
            <span className="auth__brand-text">ClassHub</span>
            <br />
            <span className="auth__brand-sub">Sistema de Gestão Escolar</span>
          </span>
        </div>

        <h1 className="auth__title">Acesse sua conta</h1>
        <p className="auth__sub">Entre com suas credenciais institucionais para continuar.</p>

        <form onSubmit={handleSubmit} className="stack" style={{ gap: 14 }}>
          <Field label="E-mail" required htmlFor="email">
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="seu.email@escola.com"
              autoComplete="email"
              autoFocus
              required
            />
          </Field>

          <Field label="Senha" required htmlFor="senha">
            <Input
              id="senha"
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="••••••••"
              autoComplete="current-password"
              required
            />
          </Field>

          <Button type="submit" block size="lg" loading={submitting} icon="logout2">
            {submitting ? 'Autenticando...' : 'Entrar no Sistema'}
          </Button>
        </form>

        <div className="auth__foot">
          <Link className="link" to="/recuperar-senha">
            Esqueceu sua senha?
          </Link>
        </div>

      </div>
    </div>
  );
}
