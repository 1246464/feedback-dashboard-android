# 📋 GUIA ATUALIZAÇÃO SISTEMA - MAIO 2026

## 🎯 Resumo Executivo das Mudanças

Este documento descreve todas as mudanças arquiteturais implementadas em maio de 2026 que **afetam diretamente como o sistema funciona**.

---

## 1️⃣ NOVO SISTEMA: PLANTÃO + HORÁRIO

### ❌ ANTIGO (Descontinuado)
```
Campo "Turno" único:
- MANHA
- NOITE  
- 5X2
```

### ✅ NOVO (Desde Maio 2026)
```
DOIS campos complementares:

1. PLANTÃO (A ou B):
   - Define QUAL SEMANA O FUNCIONÁRIO TRABALHA
   - Plantão A: Segunda, Quarta, Sexta (dias {1,3,5})
   - Plantão B: Terça, Quinta, Sábado (dias {2,4,6})
   - Só aplicável para turnos 12x36 (não se aplica a 5x2)

2. HORÁRIO (Especifica o intervalo de trabalho):
   - MANHA: 06:00-18:00
   - NOITE: 18:00-06:00
   - 5X2: 07:00-17:00 (segunda a sexta, dias alternados)
```

### 📊 Exemplos Prático:

**Exemplo 1 - Cozinheiro 12x36:**
```
Nome: João da Silva
Cargo: COZINHEIRO
Setor: COZINHA
Horário: MANHA (06:00-18:00)
Plantão: A (trabalha seg/qua/sex)

→ João trabalha: SEG 06:00-18:00, QUA 06:00-18:00, SEX 06:00-18:00
→ Nos outros dias (ter/qui/sab) ele descansa
```

**Exemplo 2 - Auxiliar Noturno:**
```
Nome: Maria Santos
Cargo: AUXILIAR
Setor: COPA
Horário: NOITE (18:00-06:00)
Plantão: B (trabalha ter/qui/sab)

→ Maria trabalha: TER 18:00-06:00, QUI 18:00-06:00, SAB 18:00-06:00
→ Nos outros dias (seg/qua/sex) ela descansa
```

**Exemplo 3 - Estoquista 5x2:**
```
Nome: Pedro Costa
Cargo: ESTOQUISTA
Setor: ESTOQUE
Horário: 5X2 (07:00-17:00)
Plantão: N/A (não se aplica)

→ Pedro trabalha dias alternados de segunda a sexta
→ Sempre das 07:00 até as 17:00
→ Descansa nos fins de semana
```

---

## 2️⃣ NOVOS SETORES HOSPITAIS

### Setores Expandidos de 6 para 10 opções:

```
✅ COZINHA (Cozinha Central)
✅ COPA (Copeiragem/Refeitório)
✅ ESTOQUE (Gestão de Suprimentos)
✅ MATERNIDADE (Setor de Maternidade)
✅ SAMU (SAMU - Atendimento de Emergência)
✅ Zoonoses (Vigilância em Saúde)
✅ HEMADE (Hemato-hemodiálise)
✅ GCM (Guarda Civil Metropolitana)
✅ Autarquia (Administração)
✅ PS Central (Pronto-Socorro Central)
```

---

## 3️⃣ CAMPO CARGO AGORA É DIGITÁVEL

### ❌ ANTIGO
```
Spinner (dropdown) com opções pré-definidas
```

### ✅ NOVO
```
EditText (campo de texto digitável)
- Sugestão: "Ex: Cozinheiro, Auxiliar, Enfermeira..."
- Aceita qualquer texto (mais flexível)
- Melhor para hospitais com cargos personalizados
```

---

## 4️⃣ NOVO SISTEMA DE FILTRO DE CARDÁPIOS

### O que mudou:

**CardapiosViewModel** agora:
- Busca `horario` + `plantao` do usuário
- Chama `FiltroVisibilidadeCardapio.filtrarCardapios(lista, horario, plantao)`
- Retorna apenas cardápios visíveis para esse user

### Novas Regras de Filtro:

```
1. Usuário MANHA (06:00-18:00):
   ✅ Vê: Cardápio TARDE de TODOS os dias
   ❌ Não vê: Cardápio NOITE

2. Usuário NOITE (18:00-06:00):
   ✅ Vê: Cardápio NOITE de TODOS os dias
   ❌ Não vê: Cardápio TARDE

3. Usuário 5X2 (07:00-17:00, seg-sex):
   ✅ Vê: Cardápio TARDE APENAS dos dias que trabalha (seg-sex)
   ❌ Não vê: Cardápio NOITE (nunca)
   ❌ Não vê: Cardápios dos fins de semana

4. Usuário Plantão A:
   - Se horário=MANHA: vê TARDE de seg/qua/sex
   - Se horário=NOITE: vê NOITE de seg/qua/sex
   (Plantão B trabalha ter/qui/sab)
```

### Classes Afetadas:

```
✅ CardapiosViewModel.java
   - Novo método: carregarCardapiosParaUsuario()
   - Observa: cardapiosVisiveis (LiveData)

✅ FiltroVisibilidadeCardapio.java
   - Novo método: podeVerCardapio(horario, turnoCardapio, data, diasTrabalho)
   - Novo método: obterDiasTrabalho(horario, plantao)
   - Novo método: getDiasPlantaoA() → {1,3,5}
   - Novo método: getDiasPlantaoB() → {2,4,6}

✅ CardapioTurnoRepository.java
   - Cache sincronizado com Room
```

---

## 5️⃣ FORMULÁRIO DE CADASTRO ATUALIZADO

### CadastroMelhoradoActivity.java

**Novos Campos (em ordem de exibição):**

1. Email (Pattern validation)
2. CPF (11 dígitos, módulo 11)
3. Telefone (11 dígitos)
4. **Cargo** (EditText digitável) ← NOVO
5. **Setor** (Spinner 10 opções) ← EXPANDIDO
6. **Plantão** (Spinner A/B) ← NOVO
7. **Horário** (Spinner 3 opções) ← NOVO
8. Preferência (Spinner)
9. Senha
10. Confirmar Senha

**Firebase Save:**
```json
{
  "uid": "user123",
  "email": "joao@hospital.com",
  "cpf": "12345678901",
  "telefone": "11987654321",
  "cargo": "Cozinheiro",
  "setor": "COZINHA",
  "plantao": "A",
  "horario": "06:00-18:00",
  "preferencia": "VEGETARIANO",
  "dataRegistro": "2026-05-14",
  "tipo": "FUNCIONARIO",
  "validado": false
}
```

---

## 6️⃣ DASHBOARDS INTEGRADOS

### 6 Dashboards com CardapiosViewModel:

```
1. DashboardCozinhaActivity
2. DashboardCopeiraActivity
3. DashboardEstoqueActivity
4. DashboardTecnicaActivity
5. DashboardLiderActivity
6. DashboardPreparoActivity

+ ReservaActivity (Reserva de Pratos)
+ HistoricoActivity (Histórico de Cardápios)
```

**Todos agora:**
- ✅ Inicializam `FuncionarioRepository` + `CardapioTurnoRepository`
- ✅ Criam `CardapiosViewModel` via `CardapiosViewModelFactory`
- ✅ Observam `cardapiosVisiveis` (LiveData)
- ✅ Mostram apenas cardápios relevantes ao funcionário

---

## 7️⃣ DASHBOARD DE ESTATÍSTICAS MELHORADO

### EstatisticasActivity.java

**Novos KPIs (4 Cards):**

```
1. Total de Avaliações (Blue)
2. Média de Satisfação (Green)
3. Taxa de Participação (Orange)
4. Cardápio Destaque (Red) - mais votado do dia
```

**Novos Gráficos:**

```
7. Satisfação por Horário
   - Compara MANHA vs NOITE vs 5X2
   - Cores: Verde/Vermelho/Amarelo
   - Tipo: BarChart

8. Comparação Semanal
   - Semana anterior vs Semana atual
   - Cores: Cinza/Azul
   - Tipo: BarChart
```

---

## 8️⃣ BANCO DE DADOS ROOM - VERSÃO 6

### AppDatabase.java

```java
@Database(entities = {...}, version = 6, exportSchema = false)
```

**Alterações:**
- Adicionado campo `plantao` no Funcionario
- Estrutura compatível com novo sistema
- Backward-compatible com dados antigos

**Migração automática:**
- Room destrói e recria banco ao detectar mudança de versão
- Dados antigos são perdidos (aceito para debug)
- Próximas mudanças: implementar Migration strategies

---

## 9️⃣ REPOSITÓRIOS ATUALIZADOS

### FuncionarioRepository
```
- Método: getFuncionarioByUid(String uid) → busca horario+plantao
- Room cache sincronizado
- Fallback para Firebase se não encontrar
```

### CardapioTurnoRepository
```
- Método: getCardapios() → retorna lista completa
- Room cache com sincronização periódica
- Filtra via CardapiosViewModel
```

### ReservaRepository
```
- Método: getReservasDoUsuario() → retorna apenas do user
- Sincroniza com Firebase em tempo real
```

---

## 🔟 VALIDAÇÕES IMPORTANTES

### Validação de Dados Incompletos:

```java
// CardapiosViewModel agora verifica:
if (user == null) → erro "Usuário não autenticado"
if (funcionario == null) → erro "Funcionário não encontrado"
if (horario == null || horario.isEmpty()) → usa fallback MANHA
if (plantao == null || plantao.isEmpty()) → usa fallback PLANTAO_A
```

### Validação de Compatibilidade:

```
❌ Não deixa salvar:
- Plantão sem Horário 12x36
- 5x2 com Plantão selecionado
- Horário vazio

✅ Deixa salvar:
- Qualquer cargo (EditText livre)
- Qualquer setor (lista de 10)
- Qualquer combinação válida
```

---

## 📱 CHECKLIST DE ATUALIZAÇÃO

Ao atualizar o app, verificar:

- [ ] Room database foi limpado (versão 6)
- [ ] CardapiosViewModel está sendo usado em todos dashboards
- [ ] Novo formulário de cadastro é acessível
- [ ] Campos Plantão e Horário aparecem corretamente
- [ ] 10 setores aparecem no dropdown
- [ ] EstatisticasActivity mostra 4 KPIs
- [ ] EstatisticasActivity mostra 2 novos gráficos
- [ ] ReservaActivity não crasheia ao abrir
- [ ] HistoricoActivity não crasheia ao abrir

---

## 🔄 PRÓXIMAS MELHORIAS PLANEJADAS

1. **Migrations Room** - Preservar dados ao atualizar schema
2. **Validação Firebase** - Adicionar regras de segurança
3. **Sincronização Offline** - Cache de cardápios
4. **Notificações** - Alertar mudança de plantão/horário
5. **Relatórios** - Dashboard gerencial completo

---

**Última atualização:** 14 de maio de 2026  
**Status:** ✅ Implementado e Testado
