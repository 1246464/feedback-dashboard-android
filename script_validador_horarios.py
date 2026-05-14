#!/usr/bin/env python3
"""
Script de Teste: Validador de Horários
Simula a lógica do ValidadorHorario.java em Python
Útil para testar dados de importação antes de enviar ao Firebase
"""

import sys
from typing import Optional, List, Tuple

# ===== CONSTANTES =====
HORARIO_5X2 = "07:00-17:00"
HORARIO_MANHA = "06:00-18:00"
HORARIO_NOITE = "18:00-06:00"

HORARIOS_VALIDOS = [HORARIO_5X2, HORARIO_MANHA, HORARIO_NOITE]

MAPEAMENTO_TURNO_HORARIO = {
    "5X2": HORARIO_5X2,
    "MANHA": HORARIO_MANHA,
    "NOITE": HORARIO_NOITE,
}

MAPEAMENTO_HORARIO_TURNO = {
    HORARIO_5X2: "5X2",
    HORARIO_MANHA: "MANHA",
    HORARIO_NOITE: "NOITE",
}

DESCRICOES = {
    HORARIO_5X2: "Regime 5x2 (7h às 17h)",
    HORARIO_MANHA: "Turno Manhã (6h às 18h)",
    HORARIO_NOITE: "Turno Noite (18h às 6h)",
}

DURACOES = {
    HORARIO_5X2: 10,
    HORARIO_MANHA: 12,
    HORARIO_NOITE: 12,
}


# ===== FUNÇÕES PRINCIPAIS =====

def is_valido(horario: Optional[str]) -> bool:
    """Verifica se um horário é um dos 3 padrão"""
    if not horario or not isinstance(horario, str):
        return False
    return horario.strip() in HORARIOS_VALIDOS


def get_turno_para_horario(horario: Optional[str]) -> Optional[str]:
    """Retorna o turno correspondente ao horário"""
    if not horario:
        return None
    return MAPEAMENTO_HORARIO_TURNO.get(horario.strip())


def get_horario_para_turno(turno: Optional[str]) -> Optional[str]:
    """Retorna o horário correspondente ao turno"""
    if not turno:
        return None
    return MAPEAMENTO_TURNO_HORARIO.get(turno.strip().upper())


def get_duracao(horario: Optional[str]) -> int:
    """Retorna duração em horas"""
    if not horario or not is_valido(horario):
        return -1
    return DURACOES.get(horario.strip(), -1)


def get_descricao(horario: Optional[str]) -> Optional[str]:
    """Retorna descrição legível"""
    if not horario or not is_valido(horario):
        return None
    return DESCRICOES.get(horario.strip())


def is_par_valido(turno: Optional[str], horario: Optional[str]) -> bool:
    """Valida correspondência turno-horário"""
    if not is_valido(horario):
        return False
    turno_esperado = get_turno_para_horario(horario)
    return turno_esperado and turno_esperado.upper() == (turno.strip().upper() if turno else None)


# ===== FUNÇÕES DE TESTE =====

def teste_horario(horario: str, esperado_valido: bool = True) -> Tuple[bool, str]:
    """Testa um horário único"""
    resultado = is_valido(horario)
    status = "✅" if resultado == esperado_valido else "❌"
    
    if resultado:
        descricao = get_descricao(horario)
        turno = get_turno_para_horario(horario)
        duracao = get_duracao(horario)
        return (resultado == esperado_valido, 
                f"{status} '{horario}' → {turno} ({duracao}h) - {descricao}")
    else:
        return (resultado == esperado_valido,
                f"{status} '{horario}' → INVÁLIDO")


def teste_arquivo_importacao(caminho_arquivo: str) -> List[dict]:
    """Testa um arquivo JSON de importação"""
    import json
    
    erros = []
    funcionarios = []
    
    try:
        with open(caminho_arquivo, 'r', encoding='utf-8') as f:
            dados = json.load(f)
    except Exception as e:
        return [{"erro": f"Falha ao ler arquivo: {e}"}]
    
    # Esperado: {"funcionarios": {...}} ou array direto
    if isinstance(dados, dict) and "funcionarios" in dados:
        funcionarios_lista = dados["funcionarios"]
    elif isinstance(dados, list):
        funcionarios_lista = dados
    else:
        return [{"erro": "Formato não reconhecido"}]
    
    # Se for dict (Firebase style), converter para lista
    if isinstance(funcionarios_lista, dict):
        funcionarios_lista = list(funcionarios_lista.values())
    
    resultados = []
    for i, func in enumerate(funcionarios_lista, 1):
        resultado = {
            "numero": i,
            "cpf": func.get("cpf", "❌ SEM CPF"),
            "nome": func.get("nome", "❌ SEM NOME"),
            "turno": func.get("turno", "❌ SEM TURNO"),
            "horario": func.get("horario", "❌ SEM HORÁRIO"),
        }
        
        turno = func.get("turno", "").strip().upper()
        horario = func.get("horario", "").strip()
        
        # Validar horário
        if not is_valido(horario):
            resultado["status"] = "❌ HORÁRIO INVÁLIDO"
            resultado["erro"] = f"'{horario}' não é um dos 3 padrão"
        # Validar par turno-horário
        elif not is_par_valido(turno, horario):
            resultado["status"] = "❌ TURNO-HORÁRIO DESACORDADOS"
            resultado["erro"] = f"Turno '{turno}' não combina com horário '{horario}'"
            resultado["esperado"] = get_horario_para_turno(turno)
        else:
            resultado["status"] = "✅ VÁLIDO"
            resultado["duracao"] = f"{get_duracao(horario)}h"
        
        resultados.append(resultado)
    
    return resultados


# ===== TESTES INTERATIVOS =====

def menu_principal():
    """Menu interativo de testes"""
    while True:
        print("\n" + "="*60)
        print("⏰ VALIDADOR DE HORÁRIOS - Menu de Testes")
        print("="*60)
        print("""
1. Validar um horário específico
2. Testar todos os 3 horários padrão
3. Testar horários inválidos (exemplo)
4. Converter Turno → Horário
5. Converter Horário → Turno
6. Testar arquivo de importação JSON
7. Gerar exemplo JSON de importação
8. Sair
        """)
        
        opcao = input("Escolha uma opção (1-8): ").strip()
        
        if opcao == "1":
            horario = input("Digite o horário a validar (ex: 07:00-17:00): ").strip()
            valido, msg = teste_horario(horario, esperado_valido=True)
            print(msg)
        
        elif opcao == "2":
            print("\n✅ Testando os 3 horários padrão:")
            for h in HORARIOS_VALIDOS:
                valido, msg = teste_horario(h)
                print(msg)
        
        elif opcao == "3":
            print("\n❌ Testando horários inválidos (exemplos):")
            invalidos = [
                ("07-17", False),
                ("6-18", False),
                ("10:00-18:00", False),
                ("06:30-18:30", False),
                ("quebrado", False),
            ]
            for h, _ in invalidos:
                valido, msg = teste_horario(h, esperado_valido=False)
                print(msg)
        
        elif opcao == "4":
            turno = input("Digite o turno (5X2, MANHA, NOITE): ").strip().upper()
            horario = get_horario_para_turno(turno)
            if horario:
                print(f"✅ {turno} → {horario}")
            else:
                print(f"❌ Turno '{turno}' não reconhecido")
        
        elif opcao == "5":
            horario = input("Digite o horário (ex: 07:00-17:00): ").strip()
            turno = get_turno_para_horario(horario)
            if turno:
                print(f"✅ {horario} → {turno}")
            else:
                print(f"❌ Horário '{horario}' não reconhecido")
        
        elif opcao == "6":
            arquivo = input("Digite o caminho do arquivo JSON: ").strip()
            print("\nAnalisando arquivo...")
            resultados = teste_arquivo_importacao(arquivo)
            
            if resultados and "erro" in resultados[0]:
                print(f"❌ Erro: {resultados[0]['erro']}")
            else:
                validos = 0
                invalidos = 0
                for r in resultados:
                    status = r.get("status", "")
                    if status.startswith("✅"):
                        validos += 1
                    else:
                        invalidos += 1
                    
                    print(f"\n#{r['numero']} - {r.get('cpf')} - {r.get('nome')}")
                    print(f"   Turno: {r.get('turno')} | Horário: {r.get('horario')}")
                    print(f"   Status: {r.get('status')}")
                    if "erro" in r:
                        print(f"   Erro: {r.get('erro')}")
                    if "esperado" in r:
                        print(f"   Esperado: {r.get('esperado')}")
                    if "duracao" in r:
                        print(f"   Duração: {r.get('duracao')}")
                
                print(f"\n{'='*60}")
                print(f"RESUMO: {validos} válidos, {invalidos} inválidos")
                print(f"{'='*60}")
        
        elif opcao == "7":
            import json
            exemplo = {
                "funcionarios": [
                    {
                        "cpf": "12345678901",
                        "nome": "João Silva",
                        "email": "joao@example.com",
                        "telefone": "11987654321",
                        "cargo": "COZINHEIRO",
                        "turno": "5X2",
                        "horario": "07:00-17:00",
                        "setor": "Cozinha",
                        "ativo": True
                    },
                    {
                        "cpf": "98765432101",
                        "nome": "Maria Santos",
                        "email": "maria@example.com",
                        "telefone": "11987654322",
                        "cargo": "CHEFE",
                        "turno": "MANHA",
                        "horario": "06:00-18:00",
                        "setor": "Cozinha",
                        "ativo": True
                    },
                    {
                        "cpf": "11111111111",
                        "nome": "Pedro Costa",
                        "email": "pedro@example.com",
                        "telefone": "11987654323",
                        "cargo": "COPEIRO",
                        "turno": "NOITE",
                        "horario": "18:00-06:00",
                        "setor": "Serviço",
                        "ativo": True
                    }
                ]
            }
            print("\n" + json.dumps(exemplo, ensure_ascii=False, indent=2))
        
        elif opcao == "8":
            print("Saindo...")
            break
        
        else:
            print("❌ Opção inválida!")


# ===== TESTES AUTOMATIZADOS =====

def executar_testes_automaticos():
    """Executa bateria de testes"""
    print("\n" + "="*60)
    print("🧪 EXECUTANDO TESTES AUTOMATIZADOS")
    print("="*60)
    
    testes = [
        ("07:00-17:00", True, "5X2 - válido"),
        ("06:00-18:00", True, "MANHA - válido"),
        ("18:00-06:00", True, "NOITE - válido"),
        ("10:00-18:00", False, "Horário inválido"),
        ("07-17", False, "Sem formatação"),
        ("06:30-18:30", False, "Horas quebradas"),
    ]
    
    passados = 0
    falhados = 0
    
    for horario, esperado_valido, descricao in testes:
        resultado = is_valido(horario)
        sucesso = resultado == esperado_valido
        
        if sucesso:
            print(f"✅ PASSOU: {descricao} - '{horario}'")
            passados += 1
        else:
            print(f"❌ FALHOU: {descricao} - '{horario}' (esperado {esperado_valido}, obtido {resultado})")
            falhados += 1
    
    print(f"\n{'='*60}")
    print(f"RESULTADO: {passados} passaram, {falhados} falharam")
    print(f"{'='*60}\n")
    
    return falhados == 0


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--test":
        # Modo teste automatizado
        sucesso = executar_testes_automaticos()
        sys.exit(0 if sucesso else 1)
    else:
        # Modo interativo
        menu_principal()
