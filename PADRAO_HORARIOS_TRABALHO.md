# ⏰ Padronização de Horários de Trabalho

## 🎯 Objetivo

Definir os **3 horários fixos** de trabalho que devem ser usados no sistema, sem horas quebradas.

---

## 📋 Horários Padronizados

### 1️⃣ Regime 5x2 (Segunda a Sexta)
```
Horário: 07:00-17:00
Duração: 10 horas
Turno: 5X2
Café da Manhã: ☕ PADRÃO
Almoço: 🍲 TARDE (cardápio)
Jantar: 🌙 Não (fora do expediente)
```

### 2️⃣ Turno Manhã
```
Horário: 06:00-18:00
Duração: 12 horas
Turno: MANHA
Café da Manhã: ☕ PADRÃO
Almoço: 🍲 TARDE (cardápio)
Jantar: 🌙 NOITE (cardápio, se terminar após 17h)
```

### 3️⃣ Turno Noite
```
Horário: 18:00-06:00
Duração: 12 horas
Turno: NOITE
Café da Manhã: ☕ PADRÃO (se terminar após 06h)
Almoço: 🍲 TARDE (cardápio, se iniciar antes das 14h)
Jantar: 🌙 NOITE (cardápio)
```

---

## ✅ Mapeamento Turno → Horário

| Turno | Horário | Tempo | Café | Almoço | Jantar |
|-------|---------|-------|------|--------|--------|
| **5X2** | `07:00-17:00` | 10h | ☕ Padrão | 🍲 Sim | ❌ Não |
| **MANHA** | `06:00-18:00` | 12h | ☕ Padrão | 🍲 Sim | 🌙 Sim |
| **NOITE** | `18:00-06:00` | 12h | ☕ Padrão | 🍲 Talvez | 🌙 Sim |

---

## 📝 Como Importar Dados de Funcionários

### Campo "turno" do Funcionário
```
Opções válidas:
- "MANHA" → deve ter horario="06:00-18:00"
- "NOITE" → deve ter horario="18:00-06:00"
- "5X2" → deve ter horario="07:00-17:00"
- "TARDE" → (raríssimo, funcionário fixo à tarde)
```

### Campo "horario" do Funcionário
```
Valores fixos esperados:
- "07:00-17:00" (para 5x2)
- "06:00-18:00" (para MANHA)
- "18:00-06:00" (para NOITE)

❌ NÃO USE:
- "10-22" (sem os dois pontos)
- "6-18" (sem formatação)
- "7-17" (sem formatação)
- "quebrado" (ex: "06:30-14:30")
```

---

## 📊 Exemplo JSON para Firebase

Ao importar funcionários, use exatamente este formato:

```json
{
  "funcionarios": {
    "12345678901": {
      "cpf": "12345678901",
      "nome": "João Silva",
      "email": "joao@example.com",
      "telefone": "11987654321",
      "cargo": "COZINHEIRO",
      "turno": "MANHA",
      "horario": "06:00-18:00",
      "setor": "Cozinha",
      "ativo": true
    },
    "98765432101": {
      "cpf": "98765432101",
      "nome": "Maria Santos",
      "email": "maria@example.com",
      "telefone": "11987654322",
      "cargo": "LIDER_COZINHA",
      "turno": "5X2",
      "horario": "07:00-17:00",
      "setor": "Cozinha",
      "ativo": true
    },
    "11111111111": {
      "cpf": "11111111111",
      "nome": "Pedro Costa",
      "email": "pedro@example.com",
      "telefone": "11987654323",
      "cargo": "COPEIRO_NOTURNO",
      "turno": "NOITE",
      "horario": "18:00-06:00",
      "setor": "Serviço",
      "ativo": true
    }
  }
}
```

---

## 🔗 Relação com Cardápios

### Sistema de Cardápios

```
Funcionário com turno MANHA (06:00-18:00)
    ↓
Café da Manhã: ☕ PADRÃO (não varia)
Almoço (11:00-14:00): 🍲 Vê cardápio TARDE
Jantar (17:00-22:00): 🌙 Vê cardápio NOITE
    ↓
Pode comer em ambos horários conforme sua jornada

---

Funcionário com turno 5X2 (07:00-17:00)
    ↓
Café da Manhã: ☕ PADRÃO
Almoço (11:00-14:00): 🍲 Vê cardápio TARDE
Jantar (17:00-22:00): 🌙 Fora do expediente (não come na instituição)
    ↓
Come apenas no horário de almoço

---

Funcionário com turno NOITE (18:00-06:00)
    ↓
Café da Manhã: ☕ PADRÃO
Almoço (11:00-14:00): 🍲 Pode ver se chegou cedo
Jantar (17:00-22:00): 🌙 Vê cardápio NOITE
    ↓
Come principalmente no turno da noite
```

---

## 📋 Checklist para Importação

- [ ] Todos os funcionários têm "turno" preenchido (MANHA, NOITE, 5X2)
- [ ] Todos têm "horario" correspondente ao turno:
  - [ ] MANHA → `06:00-18:00`
  - [ ] NOITE → `18:00-06:00`
  - [ ] 5X2 → `07:00-17:00`
- [ ] Formato de hora é sempre `HH:MM-HH:MM` (com dois pontos)
- [ ] Sem horas quebradas (não há 06:30, 14:30, etc.)
- [ ] Todos têm "cargo", "setor", "email", "telefone", "cpf"

---

## ⚠️ Erros Comuns

| Erro | Correto |
|------|---------|
| `horario: "6-18"` | `horario: "06:00-18:00"` |
| `horario: "7:00-17"` | `horario: "07:00-17:00"` |
| `horario: "6:30-18:30"` | `horario: "06:00-18:00"` |
| `turno: "MANHA"`, `horario: "07:00-17:00"` | `turno: "MANHA"`, `horario: "06:00-18:00"` |
| Sem horario | Deve ter `horario` preenchido |

---

## 🧮 Cálculo de Duração

Para validar se o horário está correto:

```
07:00-17:00 = 10 horas (7 à noite - 7 da manhã não, vem de dia)
06:00-18:00 = 12 horas (6h de manhã até 6h de noite)
18:00-06:00 = 12 horas (6h de noite até 6h de manhã do dia seguinte)
```

---

## 📱 Como Exibe no App

### Tela de Cadastro (após buscar email)

```
Nome: João Silva
Cargo: Cozinheiro
Turno: Manhã
Setor: Cozinha

Horário mostrado em tooltip: 06:00-18:00
```

### Tela de Cardápio do Usuário

```
Usuário: João Silva (turno MANHA)
    ↓
Mostra cardápios:
- Almoço (TARDE): Veja refeição para 11:00-14:00
- Jantar (NOITE): Veja refeição para 17:00-22:00
```

---

## 🔄 Atualizar Horário no Firebase

Se um funcionário trocar de turno:

1. Firebase Console
2. Selecionar nó `funcionarios/{cpf}`
3. Editar `turno` (ex: MANHA → NOITE)
4. Editar `horario` correspondente (06:00-18:00 → 18:00-06:00)
5. Funcionário verá mudança no próximo login

---

## 📞 Suporte

Dúvida sobre horários?

1. Verificar tabela Turno → Horário acima
2. Confirmar que está usando exatamente: `HH:MM-HH:MM`
3. Sem horas quebradas
4. Sem minutos variados

---

**Versão**: 1.0
**Data**: Janeiro 2024
**Status**: Padrão Definido ✅
