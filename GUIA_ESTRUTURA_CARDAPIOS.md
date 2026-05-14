# 🍽️ Guia de Cardápios por Turno

## 📋 Estrutura de Cardápios

O sistema de cardápios funciona da seguinte forma:

### ☕ Café da Manhã (Madrugada/Manhã)
- **Status**: **PADRÃO** - Não é gerenciado no sistema
- **Motivo**: Café da manhã é o mesmo todos os dias
- **O que serve**: Café, pão, bolo, queijo, presunto
- **Ação**: Nenhuma - é automático

### 🍲 Almoço (Turno TARDE)
- **Turno**: `TARDE`  
- **Horário**: 11:00 - 14:00
- **Funções**: Gerenciado no CardapioTurno
- **Componentes**:
  - Prato Principal
  - Guarnição
  - Acompanhamento
  - Salada
  - Sobremesa

### 🌙 Jantar (Turno NOITE)
- **Turno**: `NOITE`
- **Horário**: 17:00 - 22:00
- **Funções**: Gerenciado no CardapioTurno
- **Componentes**:
  - Prato Principal
  - Guarnição
  - Acompanhamento
  - Salada
  - Sobremesa

---

## 👥 Turnos de Trabalho do Funcionário vs. Cardápios Disponíveis

### Funcionário que trabalha de MADRUGADA/MANHÃ
```
Turno de trabalho: MANHA (ex: 06:00-14:00)
     ↓
Café da manhã: ☕ PADRÃO (automático)
     ↓
Almoço: 🍲 Vê o cardápio do TARDE
     ↓
Jantar: 🌙 Vê o cardápio da NOITE (se termina depois das 17h)
```

### Funcionário que trabalha à TARDE
```
Turno de trabalho: TARDE (ex: 14:00-22:00)
     ↓
Café da manhã: ☕ PADRÃO (se chegar cedo)
     ↓
Almoço: 🍲 Vê o cardápio do TARDE
     ↓
Jantar: 🌙 Vê o cardápio da NOITE
```

### Funcionário que trabalha à NOITE
```
Turno de trabalho: NOITE (ex: 22:00-06:00)
     ↓
Café da manhã: ☕ PADRÃO (se terminar depois das 06h)
     ↓
Almoço: 🍲 Vê o cardápio do TARDE (se iniciar antes)
     ↓
Jantar: 🌙 Vê o cardápio da NOITE
```

### Funcionário que trabalha 5x2
```
Turno de trabalho: 5X2 (ex: 08:00-17:00)
     ↓
Café da manhã: ☕ PADRÃO (automático)
     ↓
Almoço: 🍲 Vê o cardápio do TARDE
     ↓
Jantar: 🌙 Pode ver, mas não está na jornada de trabalho
```

---

## 🔧 Como Gerenciar Cardápios

### No Firebase Console

Estrutura esperada:

```json
{
  "cardapios_turno": {
    "2024-01-15_TARDE": {
      "id": "2024-01-15_TARDE",
      "data": "2024-01-15",
      "turno": "TARDE",
      "pratoPrincipal": "Carne com batata",
      "guarnicao": "Feijão",
      "acompanhamento": "Arroz",
      "salada": "Alface com tomate",
      "sobremesa": "Sorvete"
    },
    "2024-01-15_NOITE": {
      "id": "2024-01-15_NOITE",
      "data": "2024-01-15",
      "turno": "NOITE",
      "pratoPrincipal": "Frango assado",
      "guarnicao": "Batata doce",
      "acompanhamento": "Legumes",
      "salada": "Rúcula",
      "sobremesa": "Melancia"
    }
  }
}
```

### Opções de Turno para Cardápio
- ✅ `TARDE` - Almoço
- ✅ `NOITE` - Jantar
- ❌ `MANHA` - NÃO USE (café da manhã é padrão)

---

## 📱 Exibição no App

### Quando usuário abre CardapioSemanalActivity

```
Turno do Usuário: TARDE (trabalha 14:00-22:00)
                 ↓
App mostra:
    Almoço (TARDE) ← Cardápio para seu turno
    Jantar (NOITE) ← Pode ver também

---

Turno do Usuário: NOITE (trabalha 22:00-06:00)
                 ↓
App mostra:
    Almoço (TARDE) ← Pode ver
    Jantar (NOITE) ← Cardápio principal
```

---

## 💡 Exemplos de Cardápios

### Segunda-feira

#### Almoço (TARDE)
```
Prato: Bife com cebola
Guarnição: Batata palha
Acompanhamento: Arroz integral
Salada: Repolho roxo
Sobremesa: Fruta da época
Calorias: 2400 kcal
```

#### Jantar (NOITE)
```
Prato: Sopa de legumes
Guarnição: Pão integral
Acompanhamento: -
Salada: Cenoura ralada
Sobremesa: Iogurte
Calorias: 1200 kcal
```

### Terça-feira

#### Almoço (TARDE)
```
Prato: Frango à parmegiana
Guarnição: Purê de batata
Acompanhamento: Macarrão
Salada: Alface
Sobremesa: Pudim
Calorias: 2600 kcal
```

#### Jantar (NOITE)
```
Prato: Caldo de carne
Guarnição: Biscoito de polvilho
Acompanhamento: -
Salada: Maionese de beterraba
Sobremesa: Maçã assada
Calorias: 1400 kcal
```

---

## 🎯 Fluxo de Visualização

```
Usuário entra no App
        ↓
Abre "Cardápio"
        ↓
Sistema obtém turno do usuário
        ↓
Busca cardápios para:
  - TARDE (Almoço)
  - NOITE (Jantar)
        ↓
Exibe ambos ao usuário
(dependendo do horário, pode estar comendo um ou outro)
```

---

## 📊 Estrutura JSON Mínima

### Sem Café da Manhã

```json
{
  "data": "2024-01-15",
  "turno": "TARDE",  // ou "NOITE"
  "pratoPrincipal": "Bife",
  "guarnicao": "Batata",
  "salada": "Alface",
  "sobremesa": "Fruta"
}
```

---

## ⚠️ Importante

### NÃO CRIE CARDÁPIO PARA MANHA
```javascript
❌ ERRADO:
{
  "id": "2024-01-15_MANHA",
  "turno": "MANHA",  // NÃO USE ISSO
  ...
}

✅ CORRETO:
{
  "id": "2024-01-15_TARDE",
  "turno": "TARDE",  // Almoço
  ...
}
```

---

## 🔄 Atualizar Cardápios

### Admin deveria:

1. Criar/editar cardápio para **TARDE** (almoço do dia)
2. Criar/editar cardápio para **NOITE** (jantar do dia)
3. **NÃO PRECISA** de café da manhã (é padrão)

### Exemplo de Rotina Semanal

```
Cada dia:
├── SEGUNDA
│   ├── TARDE: Bife com batata
│   └── NOITE: Sopa
├── TERÇA
│   ├── TARDE: Frango com arroz
│   └── NOITE: Caldo
├── QUARTA
│   ├── TARDE: Peixe com legumes
│   └── NOITE: Macarrão
├── QUINTA
│   ├── TARDE: Porco com batata doce
│   └── NOITE: Canja
└── SEXTA
    ├── TARDE: Feijoada
    └── NOITE: Arroz com feijão
```

---

## 📝 Checklist para Admin

- [ ] Entender que café da manhã é padrão
- [ ] Criar cardápios apenas para TARDE e NOITE
- [ ] Não usar turno MANHA em CardapioTurno
- [ ] Atualizar cardápios diariamente
- [ ] Verificar se exibem corretamente no app

---

## 🤔 FAQ

**P: Por que não tem café da manhã?**
R: Café da manhã é padrão (café, pão, queijo, presunto) e é o mesmo para todos. Almoço e jantar variam conforme o cardápio.

**P: E se um funcionário trabalhar só de manhã?**
R: Ele almoça no horário de almoço (TARDE) e janta se trabalhar até lá (NOITE). Ambos têm cardápios disponíveis.

**P: Posso ter 3 turnos?**
R: Sim! Mas o sistema gerencia apenas TARDE (almoço) e NOITE (jantar). O café da manhã é automático.

**P: Como atualizar café da manhã se mudar?**
R: Não precisa atualizar no app. Café da manhã é fora do sistema. Atualize no cardápio físico/padrão.

**P: Qual é o cardápio padrão de café da manhã?**
R: Isso fica a cargo da instituição. Pode ser:
- ☕ Café
- 🥐 Pão
- 🧀 Queijo
- 🥓 Presunto
- 🧈 Manteiga
- 🍯 Mel

---

## 📞 Suporte

Dúvida sobre cardápios?

1. Verificar se turno é TARDE ou NOITE (não MANHA)
2. Verificar se data está correta (formato: YYYY-MM-DD)
3. Verificar sincronização Firebase
4. Logs em Logcat: "CardapioTurnoRepository"

---

**Versão**: 1.0  
**Data**: Janeiro 2024  
**Status**: Documentação Completa ✅
