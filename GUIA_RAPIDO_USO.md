# 🚀 Guia Rápido - Sistema de Pré-Preparo Integrado

## 📱 Novidades no App

### 🆕 4 Novos Botões no Admin

Ao abrir o **AdminActivity**, você verá:

1. **🔪 Gestão de Pré-Preparo** (já existia)
2. **📋 Passagem de Turno** (já existia)
3. **📦 Gestão de Ingredientes** ← NOVO
4. **📊 Dashboard de Pré-Preparo** ← NOVO

---

## ⚡ O Que Mudou ao Salvar Cardápio

### ANTES:
```
Admin salva cardápio → Pronto ✓
```

### AGORA:
```
Admin salva cardápio →
  ├─ 📝 Detecta mudanças
  ├─ 💾 Registra no histórico
  ├─ ✅ Cria 6 tarefas automaticamente
  ├─ 💬 Gera passagem de turno
  └─ 🔔 Envia notificações → Equipe é alertada!
```

---

## 📊 Dashboard (Tela Principal)

**Como acessar:**
Admin → **📊 Dashboard de Pré-Preparo**

**O que você vê:**

### Card 1: Tarefas de Preparo
- Pendentes: **5**
- Concluídas: **2**
- Botão: **Ver Tarefas →**

### Card 2: Ingredientes
- OK: **12**
- Faltando: **3** ⚠️
- Botão: **Gerenciar →**

### Card 3: Passagens de Turno
- Novas: **2** 
- Total: **5**
- Botão: **Ver Mensagens →**

### Card 4: Mudanças no Cardápio
- Mudanças hoje: **3**
- Botão: **Ver Histórico →**

---

## 📦 Gestão de Ingredientes

### Adicionar Ingrediente:
1. Abrir **Gestão de Ingredientes**
2. Clicar no **botão flutuante (+)** no canto inferior direito
3. Preencher:
   - Nome (ex: Frango)
   - Quantidade (ex: 5kg)
   - Observação (opcional)
4. **Adicionar**

### Mudar Status:
Cada ingrediente tem 3 botões rápidos:
- **✓ OK** → Marca como Disponível (verde)
- **⚠ Parcial** → Marca como Parcial (laranja)
- **✗ Falta** → Marca como Faltando (vermelho)

### Filtros:
- Todos
- ✓ Disponíveis
- ✗ Faltando
- ⚠ Parciais

### Navegação:
- **◀ Anterior** / **Próximo ▶** → Navegar entre datas
- Clique no card → Editar ingrediente

---

## 📝 Histórico de Mudanças

**Como acessar:**
Dashboard → **Ver Histórico →**

**O que mostra:**
- 🍗 Item alterado (Prato Principal, Guarnição, etc.)
- Valor anterior: Frango assado
- **Valor novo: Carne moída** (em destaque)
- Data/hora: 02/03/2026 • 14:30
- Por: admin
- Status: ✓ Turnos notificados

**Filtros:**
- Última Semana
- Último Mês

---

## 🔔 Notificações

O sistema agora envia notificações automáticas:

### Tipos:
1. **⚠️ Mudança no Cardápio** (Urgente)
   - Quando: Admin altera cardápio
   - Ação: Abre Dashboard

2. **📦 Ingrediente Faltando** (Urgente)
   - Quando: Ingrediente marcado como faltando
   - Ação: Abre Gestão de Ingredientes

3. **💬 Nova Passagem de Turno**
   - Quando: Novo recado criado
   - Ação: Abre Passagens de Turno

4. **🚨 Tarefas Urgentes**
   - Quando: Tarefas de alta prioridade pendentes
   - Ação: Abre Dashboard

---

## ✨ Tarefas Geradas Automaticamente

Quando você salva um cardápio, **6 tarefas são criadas**:

| # | Descrição | Turno | Prioridade |
|---|-----------|-------|------------|
| 1 | Pré-preparar: [Prato Principal] | Manhã | ⭐⭐⭐⭐⭐ |
| 2 | Preparar guarnição: [Guarnição] | Manhã | ⭐⭐⭐⭐ |
| 3 | Preparar acompanhamento: [Acomp] | Manhã | ⭐⭐⭐ |
| 4 | Higienizar e preparar salada: [Salada] | Tarde | ⭐⭐⭐⭐ |
| 5 | Preparar sobremesa: [Sobremesa] | Manhã | ⭐⭐ |
| 6 | Verificar todos os ingredientes | Tarde | ⭐⭐⭐⭐⭐ |

**Ver tarefas:**
Dashboard → **Ver Tarefas →** ou Admin → **🔪 Gestão de Pré-Preparo**

---

## 🎯 Fluxo de Trabalho Sugerido

### Para Administrador:
```
1. Planejar cardápio da semana
   └─ Admin → 📅 Planejamento Semanal

2. Publicar cardápio do dia
   └─ Admin → Atualizar Cardápio → ✓ PUBLICAR
   └─ Sistema gera tudo automaticamente!

3. Monitorar execução
   └─ Admin → 📊 Dashboard
   └─ Ver tarefas pendentes
   └─ Verificar ingredientes faltando
```

### Para Equipe de Cozinha:

**Início do Turno:**
```
1. Verificar notificações
2. Abrir Dashboard
3. Ler novas passagens de turno
4. Ver suas tarefas do dia
```

**Durante o Turno:**
```
1. Marcar tarefas como "Em Andamento"
2. Atualizar status de ingredientes
3. Concluir tarefas
```

**Fim do Turno:**
```
1. Marcar todas as tarefas concluídas
2. Criar passagem de turno para próximo turno
3. Reportar problemas/mudanças
```

---

## 🆘 FAQ Rápido

### ❓ Como sei se houve mudança no cardápio?
**R:** Você recebe notificação **⚠️ Mudança no Cardápio** automaticamente

### ❓ E se um ingrediente faltar?
**R:** Marque como "✗ Falta" → Notificação é enviada para todos

### ❓ Como vejo o histórico de mudanças?
**R:** Dashboard → Card "Mudanças no Cardápio" → **Ver Histórico →**

### ❓ Posso editar tarefas geradas automaticamente?
**R:** Sim! Abra **Gestão de Pré-Preparo** → Clique na tarefa → Editar

### ❓ Como adicionar tarefas customizadas?
**R:** **Gestão de Pré-Preparo** → Botão **+** (flutuante)

### ❓ Onde vejo passagens de turnos anteriores?
**R:** Admin → **📋 Passagem de Turno** → Lista completa

---

## 🎨 Cores e Ícones

### Status de Tarefas:
- 🟢 Verde claro = Concluída
- 🟡 Amarelo = Em Andamento
- 🔴 Rosa = Pendente

### Status de Ingredientes:
- 🟢 Verde = Disponível
- 🟠 Laranja = Parcial
- 🔴 Vermelho = Faltando

### Prioridades:
- ⭐⭐⭐⭐⭐ = Urgente (vermelho escuro)
- ⭐⭐⭐⭐ = Alta (laranja)
- ⭐⭐⭐ = Média (amarelo)
- ⭐⭐ = Baixa (verde)

---

## 🔥 Dicas Pro

1. **Atalho Dashboard**: Marque como favorito no launcher
2. **Filtros**: Use filtros para focar no que importa
3. **Notificações**: Não ignore notificações urgentes
4. **Passagem de Turno**: Seja específico nas mensagens
5. **Ingredientes**: Atualize status diariamente pela manhã

---

## ✅ Checklist Diário

**Manhã (Turno 1):**
- [ ] Verificar notificações
- [ ] Checar ingredientes disponíveis
- [ ] Ler passagem do turno anterior
- [ ] Executar tarefas de prioridade 5 e 4

**Tarde (Turno 2):**
- [ ] Ver tarefas pendentes do turno da manhã
- [ ] Preparar saladas (tarefa automática)
- [ ] Verificação geral (tarefa automática)
- [ ] Criar passagem para turno da noite

**Noite (Turno 3):**
- [ ] Limpeza e organização
- [ ] Preparativos para o dia seguinte
- [ ] Criar passagem para turno da manhã

---

**🎉 Aproveite o novo sistema integrado!**

Qualquer dúvida, consulte [IMPLEMENTACAO_COMPLETA.md](IMPLEMENTACAO_COMPLETA.md) para detalhes técnicos.
