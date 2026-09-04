export const disciplinas = ['Português', 'Matemática', 'Ciências', 'História', 'Geografia', 'Inglês', 'Artes', 'Educação Física'];
export const professores = [
  { id: 1, nome: 'Mariana Costa', disciplina: 'Português', avatar: 'MC' }, { id: 2, nome: 'Rafael Lima', disciplina: 'Matemática', avatar: 'RL' },
  { id: 3, nome: 'Bianca Souza', disciplina: 'Ciências', avatar: 'BS' }, { id: 4, nome: 'João Mendes', disciplina: 'História', avatar: 'JM' }, { id: 5, nome: 'Carla Freitas', disciplina: 'Inglês', avatar: 'CF' }
];
export const turmas = Array.from({ length: 9 }, (_, i) => ({ id: i + 1, nome: `${i + 1}º ano ${i % 2 ? 'B' : 'A'}`, alunos: i === 0 ? 25 : 28 + (i % 4), professor: professores[i % 5].nome, sala: `Sala ${101 + i}` }));
const nomes = ['Ana Clara', 'Bruno Henrique', 'Caio Martins', 'Duda Almeida', 'Enzo Rocha', 'Fernanda Lima', 'Gabriel Santos', 'Helena Costa', 'Igor Nunes', 'Júlia Freire', 'Kaique Silva', 'Laura Mendes', 'Miguel Alves', 'Nina Reis', 'Otávio Barros', 'Pietra Ramos', 'Ravi Teixeira', 'Sofia Paiva', 'Theo Moura', 'Valentina Dias', 'Yasmin Lopes', 'Zeca Oliveira', 'Alice Campos', 'Bernardo Melo', 'Cecília Cruz', 'Daniel Ribeiro', 'Eloá Torres', 'Felipe Andrade', 'Giovana Moraes', 'Heitor Cardoso', 'Isabela Rocha', 'Lucas Almeida', 'Manuela Lima', 'Noah Martins', 'Olívia Santos', 'Pedro Costa', 'Raquel Freitas', 'Samuel Nunes', 'Tainá Silva', 'Vitor Reis'];
export const alunos = nomes.map((nome, i) => ({ id: i + 1, nome, turmaId: Math.floor(i / 5) + 1, turma: turmas[Math.floor(i / 5)].nome, faltas: (i * 3) % 22, responsavel: i % 2 ? 'Maria ' + nome.split(' ')[0] : 'Carlos ' + nome.split(' ')[0], contato: '(11) 9 8' + String(1000000 + i).slice(1), avatar: nome.split(' ').map(n => n[0]).join('') }));
export const notas = disciplinas.map((disciplina, d) => ({ disciplina, notas: [7.8 - (d % 3) * .4, 8.2 - (d % 4) * .35, 7.5 + (d % 2) * .6, 8.4 - (d % 3) * .25], faltas: d % 4 }));
export const avisosIniciais = [
  { id: 1, titulo: 'Reunião de responsáveis', conteudo: 'Encontro geral na próxima quinta-feira, às 19h.', data: '28 ago', autor: 'Coordenação', prioridade: 'urgente', fixado: true },
  { id: 2, titulo: 'Feira de Ciências 2026', conteudo: 'As inscrições de projetos seguem abertas até 10 de setembro.', data: '27 ago', autor: 'Prof. Bianca', prioridade: 'normal', fixado: true },
  { id: 3, titulo: 'Semana da Pátria', conteudo: 'Confira a programação especial preparada para as turmas.', data: '25 ago', autor: 'Direção', prioridade: 'normal', fixado: false },
  { id: 4, titulo: 'Atualização da biblioteca', conteudo: 'A biblioteca estará fechada nesta sexta para inventário.', data: '22 ago', autor: 'Biblioteca', prioridade: 'normal', fixado: false }
];
export const ocorrenciasIniciais = [
  { id: 1, aluno: 'Bruno Henrique', data: '26/08/2026', tipo: 'Advertência', descricao: 'Conversa recorrente durante a aula.' },
  { id: 2, aluno: 'Duda Almeida', data: '22/08/2026', tipo: 'Elogio', descricao: 'Excelente participação no projeto de leitura.' },
  { id: 3, aluno: 'Enzo Rocha', data: '18/08/2026', tipo: 'Outro', descricao: 'Atendimento à família registrado.' }
];
export const eventos = [{dia:3,tipo:'feriado',titulo:'Dia da Independência'},{dia:9,tipo:'prova',titulo:'Avaliação de Matemática'},{dia:12,tipo:'evento',titulo:'Feira de Ciências'},{dia:16,tipo:'reuniao',titulo:'Conselho de classe'},{dia:19,tipo:'prova',titulo:'Avaliação de Português'},{dia:23,tipo:'evento',titulo:'Gincana escolar'},{dia:25,tipo:'reuniao',titulo:'Reunião de responsáveis'}];
export const itensIniciais = [{id:1,nome:'Garrafa azul',categoria:'materiais',local:'Pátio',data:'27/08',icone:'💧',entregue:false},{id:2,nome:'Casaco de moletom',categoria:'roupas',local:'Biblioteca',data:'25/08',icone:'🧥',entregue:false},{id:3,nome:'Estojo verde',categoria:'materiais',local:'Sala 104',data:'23/08',icone:'✏️',entregue:false},{id:4,nome:'Fone de ouvido',categoria:'eletrônicos',local:'Quadra',data:'20/08',icone:'🎧',entregue:false}];
export const horario = ['Português','Matemática','Ciências','História','Geografia','Inglês'].map((x,i)=>[x,disciplinas[(i+1)%8],disciplinas[(i+2)%8],disciplinas[(i+3)%8],disciplinas[(i+4)%8]]);
