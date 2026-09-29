import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import Icon from '../components/Icon.jsx';
import ThemeToggle from '../components/ThemeToggle.jsx';
import { Button, Field, Input } from '../components/ui.jsx';
import { useToast } from '../components/ToastProvider.jsx';
import { dashboardPathFor, login as doLogin } from '../lib/session.js';

const HIGHLIGHTS = [
  '67 Notas, boletins da resenha e médias do Floyd por bimestre',
  'Frequência do amostradinho e faltas em tempo real: lá ele!',
  'Grade de horários com Kirk e Jennifer, bora bill e comunicados eitxha',
  'Achados e perdidos da resenha, eitcha e ocorrências do dia a dia 67'
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
      toast.warning('Eitcha! Sua sessão expirou no PhonkHub 67. Faça login novamente, amostradinho.');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();

    if (!email.trim() || !senha) {
      toast.warning('Eitxha! Por favor, preencha todos os campos da resenha.');
      return;
    }

    setSubmitting(true);
    try {
      const session = await doLogin(email.trim(), senha);
      toast.success('Login realizado com sucesso! Bora bill na resenha do PhonkHub 67!');
      navigate(dashboardPathFor(session.perfil), { replace: true });
    } catch (error) {
      toast.error(error.message || 'Lá ele! Não foi possível realizar o login no 67.');
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
          PhonkHub 67
        </h2>
        <p>
          Gestão escolar completa da resenha 67: Bora Bill, Amostradinho, Jennifer, Kirk e Floyd no mesmo lugar! Eitxha, lá ele!
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
            <span className="auth__brand-text">PhonkHub</span>
            <br />
            <span className="auth__brand-sub">Sistema de Gestão Escolar e Resenha 67</span>
          </span>
        </div>

        <h1 className="auth__title">Acesse sua conta na Resenha 67</h1>
        <p className="auth__sub">Entre com suas credenciais institucionais do PhonkHub para resenhar (lá ele, bora bill!).</p>

        <form onSubmit={handleSubmit} className="stack" style={{ gap: 14 }}>
          <Field label="E-mail da Resenha" required htmlFor="email">
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="jennifer.floyd67@phonkhub.local"
              autoComplete="email"
              autoFocus
              required
            />
          </Field>

          <Field label="Senha do Amostradinho" required htmlFor="senha">
            <Input
              id="senha"
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="•••••••• (senha da resenha)"
              autoComplete="current-password"
              required
            />
          </Field>

          <Button type="submit" block size="lg" loading={submitting} icon="logout2">
            {submitting ? 'Autenticando no 67...' : 'Entrar no PhonkHub (Bora Bill!)'}
          </Button>
        </form>

        <div className="auth__foot">
          <Link className="link" to="/recuperar-senha">
            Esqueceu sua senha? Lá ele!
          </Link>
        </div>

      </div>
    </div>
  );
}
