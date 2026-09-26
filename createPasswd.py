"""
Script provisório pra gerar hashes bcrypt de senhas de teste.
Uso: python3 gerar_senhas.py
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
    hash_bytes = bcrypt.hashpw(senha.encode(), bcrypt.gensalt())
    hash_str = hash_bytes.decode()
    print(f"{identificador} | senha: {senha} -> {hash_str}")