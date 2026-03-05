# ⚠️ IMPORTANTE - USE O APP PARA GESTÃO!

> **A partir da versão 2.0, NÃO configure funcionários manualmente no Firebase!**
> 
> ✅ **Use o sistema administrativo do app:**
> - Consulte: **GUIA_GESTAO_FUNCIONARIOS.md**
> - Acesse no app: Painel Admin → "👥 Gerenciar Funcionários"
> - Crie, edite e delete funcionários diretamente pela interface
>
> 📖 **Este documento serve apenas como referência técnica da estrutura de dados**

---

# 🏥 GUIA DE CONFIGURAÇÃO DE FUNCIONÁRIOS

## 📊 Estrutura Organizacional

### Plantão A (12x36)
- 1 Técnica em Nutrição
- 1 Cozinheiro
- 2 Auxiliares de Cozinha
- 1 Copeira
- 1 Meio Oficial (10:00-22:00)
- 1 Copeiro Noturno (19:00-07:00)

### Plantão B (12x36)
- 1 Técnica em Nutrição
- 1 Cozinheiro
- 2 Auxiliares de Cozinha
- 1 Copeira
- 1 Meio Oficial (10:00-22:00)
- 1 Copeiro Noturno (19:00-07:00)

### Regime 5x2 (Segunda a Sexta)
- 1 Líder de Cozinha
- 1 Estoquista
- 1 Meio Oficial
- 1 Copeira

### Gerência
- 1 Gerente (acesso total)

---

## 🔐 Cargos e Acessos

| Cargo | Tela Principal | Funcionalidades |
|-------|---------------|-----------------|
| **Gerente** | AdminActivity | ✅ Tudo (acesso completo) |
| **Técnica** | DashboardTecnicaActivity | ✅ Pré-preparo, Ingredientes, Passagem Turno, Dashboard |
| **Líder de Cozinha** | DashboardLiderActivity | ✅ Igual Técnica + Reservas + Visão Semanal |
| **Cozinheiro** | DashboardCozinhaActivity | ✅ Tarefas, Cardápio, Passagem Turno |
| **Auxiliar** | DashboardCozinhaActivity | ✅ Tarefas, Cardápio |
| **Meio Oficial** | DashboardCozinhaActivity | ✅ Tarefas, Cardápio, Passagem Turno |
| **Estoquista** | DashboardEstoqueActivity | ✅ Ingredientes, Lista Compras, Visão Semanal |
| **Copeira/Copeiro** | DashboardCopeiraActivity | ✅ Cardápio, Reservas, Visão Semanal |
| **Usuário Comum** | MainActivity | ✅ Votar, Avaliar, Sugerir, Reservar |

---

## 📝 Como Cadastrar Funcionários no Firebase

### 1. Acesse Firebase Console
- `https://console.firebase.google.com`
- Selecione o projeto `insights-cardapio`
- Vá em **Realtime Database**

### 2. Estrutura de Dados

Navegue até: `usuarios/{userId}/`

Adicione os seguintes campos para cada funcionário:

```json
{
  "usuarios": {
    "{userId}": {
      "nome": "Maria Silva",
      "email": "maria@hospital.com",
      "cargo": "Técnica em Nutrição",
      "plantao": "A",
      "horario": "07:00 - 19:00",
      "setor": "Cozinha",
      "tipo": "tecnica"
    }
  }
}
```

### 3. Valores Permitidos por Campo

#### Campo: `cargo` (OBRIGATÓRIO)
```
- "Gerente"
- "Técnica em Nutrição"
- "Líder de Cozinha"
- "Cozinheiro"
- "Auxiliar de Cozinha"
- "Meio Oficial (Plantão)"
- "Meio Oficial (5x2)"
- "Estoquista"
- "Copeira"
- "Copeira (5x2)"
- "Copeiro Noturno"
- "Usuário" (padrão para quem só come)
```

#### Campo: `plantao` (Para regime 12x36)
```
- "A"
- "B"
```

#### Campo: `horario` (Opcional)
```
Exemplos:
- "07:00 - 19:00"
- "19:00 - 07:00"
- "10:00 - 22:00"
```

#### Campo: `tipo` (Compatibilidade com sistema antigo)
```
- "admin" (para Gerente)
- "tecnica"
- "lider"
- "cozinha"
- "estoque"
- "servico"
- "comum"
```

---

## 🎯 Exemplos Práticos

### Exemplo 1: Técnica do Plantão A
```json
{
  "nome": "Ana Paula Santos",
  "email": "ana.paula@hospital.com",
  "cargo": "Técnica em Nutrição",
  "plantao": "A",
  "horario": "07:00 - 19:00",
  "setor": "Nutrição",
  "tipo": "tecnica"
}
```

**Resultado**: Ao fazer login, Ana verá:
- ✅ Gestão de Pré-Preparo
- ✅ Passagem de Turno
- ✅ Ingredientes
- ✅ Dashboard de Pré-Preparo
- ✅ Histórico de Mudanças

---

### Exemplo 2: Cozinheiro do Plantão B
```json
{
  "nome": "Carlos Mendes",
  "email": "carlos@hospital.com",
  "cargo": "Cozinheiro",
  "plantao": "B",
  "horario": "19:00 - 07:00",
  "setor": "Cozinha",
  "tipo": "cozinha"
}
```

**Resultado**: Carlos verá:
- ✅ Cardápio do Dia
- ✅ Minhas Tarefas de Pré-Preparo
- ✅ Passagem de Turno
- ✅ Ver Ingredientes

---

### Exemplo 3: Auxiliar do Plantão A
```json
{
  "nome": "João Silva",
  "email": "joao@hospital.com",
  "cargo": "Auxiliar de Cozinha",
  "plantao": "A",
  "horario": "07:00 - 19:00",
  "setor": "Cozinha",
  "tipo": "cozinha"
}
```

**Resultado**: João verá:
- ✅ Cardápio do Dia
- ✅ Minhas Tarefas de Pré-Preparo
- ✅ Passagem de Turno
- ✅ Ver Ingredientes

---

### Exemplo 4: Copeira Plantão A
```json
{
  "nome": "Juliana Costa",
  "email": "juliana@hospital.com",
  "cargo": "Copeira",
  "plantao": "A",
  "horario": "07:00 - 19:00",
  "setor": "Serviços Gerais",
  "tipo": "servico"
}
```

**Resultado**: Juliana verá:
- ✅ Cardápio do Dia
- ✅ Reservas do Dia (para distribuição)
- ✅ Visão Semanal

---

### Exemplo 5: Copeiro Noturno Plantão B
```json
{
  "nome": "Pedro Oliveira",
  "email": "pedro@hospital.com",
  "cargo": "Copeiro Noturno",
  "plantao": "B",
  "horario": "19:00 - 07:00",
  "setor": "Serviços Gerais",
  "tipo": "servico"
}
```

**Resultado**: Pedro verá:
- ✅ Cardápio do Dia
- ✅ Reservas do Dia
- ✅ Visão Semanal

---

### Exemplo 6: Meio Oficial Plantão (10h-22h)
```json
{
  "nome": "Roberto Lima",
  "email": "roberto@hospital.com",
  "cargo": "Meio Oficial (Plantão)",
  "plantao": "A",
  "horario": "10:00 - 22:00",
  "setor": "Cozinha",
  "tipo": "cozinha"
}
```

**Resultado**: Roberto verá:
- ✅ Cardápio do Dia
- ✅ Minhas Tarefas
- ✅ Passagem de Turno

---

### Exemplo 7: Líder de Cozinha (5x2)
```json
{
  "nome": "Fernanda Rocha",
  "email": "fernanda@hospital.com",
  "cargo": "Líder de Cozinha",
  "horario": "08:00 - 17:00",
  "setor": "Cozinha",
  "tipo": "lider"
}
```

**Resultado**: Fernanda verá:
- ✅ Cardápio
- ✅ Gestão de Pré-Preparo
- ✅ Passagem de Turno
- ✅ Ingredientes
- ✅ Dashboard
- ✅ Histórico
- ✅ Reservas

---

### Exemplo 8: Estoquista (5x2)
```json
{
  "nome": "Marcos Andrade",
  "email": "marcos@hospital.com",
  "cargo": "Estoquista",
  "horario": "08:00 - 17:00",
  "setor": "Estoque",
  "tipo": "estoque"
}
```

**Resultado**: Marcos verá:
- ✅ Cardápio
- ✅ Gestão de Ingredientes
- ✅ Lista de Compras Semanal
- ✅ Visão Semanal

---

### Exemplo 9: Meio Oficial 5x2
```json
{
  "nome": "Paula Martins",
  "email": "paula@hospital.com",
  "cargo": "Meio Oficial (5x2)",
  "horario": "08:00 - 17:00",
  "setor": "Cozinha",
  "tipo": "cozinha"
}
```

---

### Exemplo 10: Copeira 5x2
```json
{
  "nome": "Lucia Santos",
  "email": "lucia@hospital.com",
  "cargo": "Copeira (5x2)",
  "horario": "08:00 - 17:00",
  "setor": "Serviços Gerais",
  "tipo": "servico"
}
```

---

### Exemplo 11: Gerente
```json
{
  "nome": "Dra. Beatriz Almeida",
  "email": "beatriz@hospital.com",
  "cargo": "Gerente",
  "setor": "Administração",
  "tipo": "admin"
}
```

**Resultado**: Dra. Beatriz verá:
- ✅ **TUDO** (Painel Administrativo Completo)

---

## 🔄 Fluxo de Login

```
1. Usuário faz login (LoginActivity)
   ↓
2. Sistema busca campo "cargo" no Firebase
   ↓
3. RedirecionadorActivity identifica o cargo
   ↓
4. Redireciona para tela específica:
   
   • Gerente → AdminActivity
   • Técnica → DashboardTecnicaActivity
   • Líder → DashboardLiderActivity
   • Cozinha → DashboardCozinhaActivity
   • Estoque → DashboardEstoqueActivity
   • Copeira → DashboardCopeiraActivity
   • Comum → MainActivity
```

---

## ⚙️ Configuração Inicial do Sistema

### Passo 1: Criar Usuários no Firebase Authentication
1. Firebase Console → Authentication → Users
2. Add User
3. Email: funcionario@hospital.com
4. Senha: (definir)

### Passo 2: Adicionar Dados no Realtime Database
1. Firebase Console → Realtime Database
2. Navegar até `usuarios/`
3. Copiar o `userId` do Authentication
4. Criar nó com os dados acima

### Passo 3: Fazer Login no App
1. Abrir app
2. Inserir email/senha
3. Sistema redireciona automaticamente para tela correta!

---

## 🎨 Cores por Cargo (Interface)

| Cargo | Cor Principal |
|-------|---------------|
| Gerente | 🔴 Vermelho |
| Técnica | 💙 Azul Escuro |
| Líder | 💜 Roxo |
| Cozinha | 🟢 Verde |
| Estoque | 🟠 Laranja |
| Copeira | 🌸 Rosa/Lilás |

---

## 📱 Telas Disponíveis por Cargo

### ✅ Gerente (TODOS)
- AdminActivity
- CardapioSemanalActivity
- EstatisticasActivity
- ComentariosActivity
- GestaoPreparoActivity
- PassagemTurnoActivity
- GestaoIngredientesActivity
- DashboardPreparoActivity
- HistoricoMudancasActivity
- ListaReservasActivity
- VisaoSemanalActivity
- ListaComprasActivity

### 👩‍⚕️ Técnica
- DashboardTecnicaActivity (principal)
- MainActivity
- GestaoPreparoActivity
- PassagemTurnoActivity
- GestaoIngredientesActivity
- DashboardPreparoActivity
- HistoricoMudancasActivity

### 👨‍🍳 Líder de Cozinha
- DashboardLiderActivity (principal)
- Tudo da Técnica + ListaReservasActivity + VisaoSemanalActivity

### 🧑‍🍳 Cozinheiro/Auxiliar/Meio Oficial
- DashboardCozinhaActivity (principal)
- MainActivity
- GestaoPreparoActivity
- PassagemTurnoActivity
- GestaoIngredientesActivity (somente leitura)

### 📦 Estoquista
- DashboardEstoqueActivity (principal)
- MainActivity
- GestaoIngredientesActivity
- ListaComprasActivity
- VisaoSemanalActivity

### ☕ Copeira/Copeiro
- DashboardCopeiraActivity (principal)
- MainActivity
- ListaReservasActivity
- VisaoSemanalActivity

---

## 🚨 Troubleshooting

### Usuário vê tela incorreta?
1. Verificar campo `cargo` no Firebase
2. Deve ser exatamente um dos valores permitidos
3. Maiúsculas/minúsculas importam!

### Usuário não consegue acessar alguma função?
1. Verificar se o cargo tem permissão
2. Consultar tabela "Telas Disponíveis por Cargo"

### Como alterar cargo de alguém?
1. Firebase → Realtime Database
2. `usuarios/{userId}/cargo`
3. Alterar valor
4. Usuário precisa fazer logout/login

---

**Última Atualização**: 2024  
**Status**: ✅ Sistema Configurado
