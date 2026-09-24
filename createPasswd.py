"""
Script provisório pra gerar hashes bcrypt de senhas de teste.
Uso: python3 gerar_senhas.py
"""

import bcrypt

# Adicione aqui: (identificador, senha em texto puro)
senhas = [
    ("aluno@classhub.local", "123456"),
    ("giovanni.medeiros@aluno.cps.sp.gov.br", "123456"),
    ("guilherme.olimpio@aluno.cps.sp.gov.br", "123456"),
]

for identificador, senha in senhas:
    hash_bytes = bcrypt.hashpw(senha.encode(), bcrypt.gensalt())
    hash_str = hash_bytes.decode()
    print(f"{identificador} | senha: {senha} -> {hash_str}")