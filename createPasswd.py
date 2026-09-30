"""
Script provisório pra gerar hashes bcrypt de senhas de teste.
Uso: python3 gerar_senhas.py

Para cada par (identificador, senha em texto puro) definido na lista
`senhas`, o script gera o hash bcrypt correspondente e imprime no console
a linha "identificador | senha: <senha> -> <hash>", útil para popular
manualmente a base de dados de demonstração com usuários de teste.
"""

import bcrypt

# Adicione aqui: (identificador, senha em texto puro)
senhas = [
    ("robson.gomes@professor.cps.sp.gov.br", "MelhorProfessorEtec123"),
    ("guilherme.olimpio@aluno.cps.sp.gov.br", "killall123"),
    ("giovanni.medeiros@aluno.cps.sp.gov.br", "giovanniNigga123"),
    ("thiago.silva102@aluno.cps.sp.gov.br", "Junho282011"),
    ("nicaeli.cardoso@aluno.cps.sp.gov.br", "123batatinhafrita")
]

for identificador, senha in senhas:
    # Gera um salt aleatório e calcula o hash bcrypt da senha em texto puro.
    hash_bytes = bcrypt.hashpw(senha.encode(), bcrypt.gensalt())
    # Decodifica o hash (bytes) para string, pronto para ser copiado ao banco.
    hash_str = hash_bytes.decode()
    print(f"{identificador} | senha: {senha} -> {hash_str}")
