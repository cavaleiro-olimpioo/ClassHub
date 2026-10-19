import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Field, Input } from '../components/ui.jsx';
import { useToast } from '../components/ToastProvider.jsx';
import { api } from '../lib/api.js';
import { getSession } from '../lib/session.js';
import Icon from '../components/Icon.jsx';
import ThemeToggle from '../components/ThemeToggle.jsx';

/**
 * Página para o usuário alterar a própria senha.
 * Utiliza o endpoint `/auth/alterar-senha` já implementado no backend.
 * O e‑mail é pré‑preenchido com o da sessão (pode ser editado).
 */
export default function AlterarSenha() {
  const toast = useToast();
  const navigate = useNavigate();
  const session = getSession();

  const [email, setEmail] = useState(session?.email ?? '');
  const [newPassword, setNewPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (!email.trim()) {
      toast.warning('Informe o e‑mail da conta.');
      return;
    }
    if (!newPassword) {
      toast.warning('Informe a nova senha.');
      return;
    }
    setSubmitting(true);
    try {
      const payload = { email: email.trim(), senha: newPassword };
      const response = await api.post('/auth/alterar-senha', payload);
      setDone(true);
      toast.success(response?.mensagem || 'Senha alterada com sucesso.', 6000);
      // opcional: redirecionar para tela inicial
      setTimeout(() => navigate('/'), 2000);
    } catch (err) {
      toast.error(err.message || 'Falha ao alterar a senha.');
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
            <span className="auth__brand-sub">Alterar senha</span>
          </span>
        </div>
        <h1 className="auth__title">Alterar senha</h1>
        {done ? (
          <div className="alert alert--success">
            <Icon name="check" size={18} />
            <div>
              <div className="alert__title">Senha alterada</div>
              Você pode fazer login com a nova senha.
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="stack" style={{ gap: 14 }}>
            <Field label="E‑mail" required htmlFor="email">
              <Input
                id="email"
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="seu.email@escola.com"
                autoComplete="email"
                required
              />
            </Field>
            <Field label="Nova senha" required htmlFor="senha">
              <Input
                id="senha"
                type="password"
                value={newPassword}
                onChange={e => setNewPassword(e.target.value)}
                placeholder="********"
                required
              />
            </Field>
            <Button type="submit" block size="lg" loading={submitting} icon="lock">
              Alterar senha
            </Button>
          </form>
        )}
        <div className="auth__foot">
          <Button type="button" variant="link" onClick={() => navigate(-1)} style={{ marginTop: 10 }}>
            ← Voltar
          </Button>
        </div>
      </div>
    </div>
  );
}
