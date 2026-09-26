import { useState } from 'react';
import { Link } from 'react-router-dom';
import Icon from '../components/Icon.jsx';
import ThemeToggle from '../components/ThemeToggle.jsx';
import { Button, Field, Input } from '../components/ui.jsx';
import { useToast } from '../components/ToastProvider.jsx';
import { api } from '../lib/api.js';

export default function RecuperarSenha() {
  const toast = useToast();
  const [email, setEmail] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [sent, setSent] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (!email.trim()) {
      toast.warning('Informe o e-mail cadastrado.');
      return;
    }

    setSubmitting(true);
    try {
      // O backend sempre responde a mesma mensagem (nao revela se o e-mail existe).
      await api.post('/auth/recuperar-senha', { email: email.trim() }).catch(() => null);
      setSent(true);
      toast.success('Se o e-mail estiver cadastrado, as instruções foram enviadas.', 6000);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth">
      <ThemeToggle className="auth__theme" />
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

        <h1 className="auth__title">Recuperar senha</h1>
        <p className="auth__sub">
          Informe o e-mail institucional cadastrado. Você receberá as instruções para redefinir sua senha.
        </p>

        {sent ? (
          <div className="alert alert--success">
            <Icon name="check" size={18} />
            <div>
              <div className="alert__title">Solicitação registrada</div>
              Se o e-mail estiver cadastrado, as instruções de recuperação foram enviadas.
            </div>
          </div>
        ) : (
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
            <Button type="submit" block size="lg" loading={submitting} icon="logout2">
              Enviar instruções
            </Button>
          </form>
        )}

        <div className="auth__foot">
          <Link className="link" to="/login">
            ← Voltar para o login
          </Link>
        </div>
      </div>
    </div>
  );
}
