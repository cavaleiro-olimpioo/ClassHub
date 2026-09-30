import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import { ThemeProvider } from './contexts/ThemeContext.jsx';
import './styles/index.css';

/**
 * Ponto de entrada da aplicação React: monta a árvore de componentes no
 * elemento `#root` do HTML, envolvendo-a em `React.StrictMode` (para
 * detectar problemas em desenvolvimento) e no {@link ThemeProvider}
 * (que disponibiliza o tema claro/escuro para toda a aplicação).
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ThemeProvider>
      <App />
    </ThemeProvider>
  </React.StrictMode>
);
