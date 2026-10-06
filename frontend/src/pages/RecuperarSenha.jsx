import { useState } from 'react';
import { Link } from 'react-router-dom';
import Icon from '../components/Icon.jsx';
import ThemeToggle from '../components/ThemeToggle.jsx';
import { Button, Field, Input } from '../components/ui.jsx';
import { useToast } from '../components/ToastProvider.jsx';
import { api } from '../lib/api.js';

/**
 * Página de recuperação de senha: permite ao usuário solicitar o envio de
 * instruções de redefinição de senha para o e-mail cadastrado. Por razões
 * de segurança, a API sempre responde com a mesma mensagem de sucesso,
 * independentemente de o e-mail existir ou não.
 *
 * @returns {JSX.Element} a tela de recuperação de senha
 */
export default function RecuperarSenha() {
  const toast = useToast();
  const [email, setEmail] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [sent, setSent] = useState(false);

  /**
   * Envia a solicitação de recuperação de senha para o e-mail informado.
   *
   * @param {import('react').FormEvent} event evento de submit do formulário
   */
  async function handleSubmit(event) {
    event.preventDefault();
    if (!email.trim()) {
      toast.warning('Informe o e-mail cadastrado.');
      return;
    }

    setSubmitting(true);
    try {
      const response = await api.post('/auth/recuperar-senha', { email: email.trim() });
      setSent(true);
      toast.success(response?.mensagem || 'Solicitação registrada.', 6000);
    } catch (err) {
      toast.error(err.message || 'Não foi possível solicitar a recuperação de senha.');
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
