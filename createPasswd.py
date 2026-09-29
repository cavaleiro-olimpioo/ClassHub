"""
PhonkHub - Gerador de hashes bcrypt da resenha 67!
Coisas como: 67, resenha, la ele, bora bill, eitxha, eitcha, amostradinho, Jennifer, Kirk, Floyd, etc.
Uso: python3 createPasswd.py
"""

import bcrypt

# Adicione aqui: (identificador, senha em texto puro)
senhas = [
    ("professor.kirk.floyd@phonkhub.local", "resenha67"),
    ("jennifer.amostradinha@phonkhub.local", "borabill67"),
    ("laele.resenha@phonkhub.local", "eitcha67"),
    ("robson.gomes@professor.cps.sp.gov.br", "MelhorProfessorEtec123"),
    ("guilherme.olimpio@aluno.cps.sp.gov.br", "killall123")
]

for identificador, senha in senhas:
    hash_bytes = bcrypt.hashpw(senha.encode(), bcrypt.gensalt())
    hash_str = hash_bytes.decode()
    print(f"[{identificador}] senha: {senha} -> {hash_str} (Bora bill!)")
