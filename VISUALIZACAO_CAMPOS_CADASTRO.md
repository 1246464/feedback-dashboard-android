# 📋 Visualização dos Campos do Formulário de Cadastro

## 🔍 Status Atual
**Layout em uso:** `activity_cadastro.xml` (Formulário Simples)  
**Arquivo Java:** `CadastroActivity.java`

---

## 1️⃣ FORMULÁRIO SIMPLES (Atual - activity_cadastro.xml)

### ✅ Campos Visíveis:

```
┌─────────────────────────────────┐
│     CRIE SUA CONTA              │
│ Preencha os dados abaixo        │
├─────────────────────────────────┤
│                                 │
│ [  Nome Completo         ]      │
│                                 │
│ [  E-mail                ]      │
│                                 │
│ [  Senha                 ]      │
│                                 │
│ [  Seu Cargo (ex: Enf)   ]      │
│                                 │
│ Selecione seu Setor:            │
│ [  ▼ Dropdown Setores   ]       │
│                                 │
│ Seu Plantão (12x36):            │
│ [  ▼ Dropdown Plantão   ]       │
│                                 │
│ Preferências Alimentares:       │
│ [  ▼ Dropdown Preferência ]     │
│                                 │
│ [  FINALIZAR CADASTRO   ]       │
│                                 │
└─────────────────────────────────┘
```

### 📊 Campos do Formulário Simples:

| Campo | ID | Tipo | Status |
|-------|-----|------|---------|
| Nome Completo | `edtNomeCadastro` | EditText | ✅ Visível |
| E-mail | `edtEmailCadastro` | EditText | ✅ Visível |
| Senha | `edtSenhaCadastro` | EditText (password) | ✅ Visível |
| Seu Cargo | `edtCargoCadastro` | EditText | ✅ Visível |
| Setor | `spinner_setores` | Spinner | ✅ Visível |
| Plantão (12x36) | `spinner_plantao` | Spinner | ✅ Visível |
| Preferência Alimentar | `spinnerPreferencia` | Spinner | ✅ Visível |

---

## 2️⃣ FORMULÁRIO MELHORADO (Disponível - activity_cadastro_melhorado.xml)

### ⚠️ Campos NÃO Visíveis no Formulário Simples:

O formulário melhorado adicionou estes campos que **NÃO APARECEM** no atual:

```
┌─────────────────────────────────────┐
│   CRIAR CONTA                       │
├─────────────────────────────────────┤
│                                     │
│ SEÇÃO 1: BUSCAR FUNCIONÁRIO         │
│ [  Email                    ]       │
│ [  BUSCAR DADOS FUNCIONÁRIO ]       │
│                                     │
│ SEÇÃO 2: DADOS DO FUNCIONÁRIO       │
│ (Aparece após buscar)               │
│                                     │
│ Nome: (mostrado)                    │
│ Cargo: (mostrado)                   │
│ Turno: (mostrado)                   │
│ Setor: (mostrado)                   │
│                                     │
│ [  CPF (verificado)       ] (desab) │
│ [  Telefone para Contato  ]         │
│ [  Preferência Alimentar  ]         │
│                                     │
│ SEÇÃO 3: CRIAR SENHA                │
│ (Aparece após dados)                │
│                                     │
│ [  Senha (min 6 carac)    ]         │
│ [  Confirmar Senha        ]         │
│                                     │
│ [  FINALIZAR CADASTRO     ]         │
│                                     │
└─────────────────────────────────────┘
```

### 📊 Campos Adicionais no Formulário Melhorado:

| Campo | ID | Tipo | Status |
|-------|-----|------|---------|
| **CPF** | `edtCPF` | EditText | ❌ **NOVO** (desabilitado) |
| **Telefone** | `edtTelefone` | EditText | ❌ **NOVO** |
| **Confirmar Senha** | `edtConfirmaSenha` | EditText | ❌ **NOVO** |
| Dados Funcionário | `containerDadosFuncionario` | LinearLayout | ❌ **NOVO** (oculto até buscar) |
| Seção Senha | `containerSenha` | LinearLayout | ❌ **NOVO** (oculto até dados) |

---

## 🔄 Comparação Lado a Lado

### Formulário Simples vs Melhorado

| Aspecto | Simples | Melhorado |
|---------|---------|-----------|
| **Entrada** | Manual | Busca por email |
| **Validação CPF** | ❌ Não | ✅ Sim |
| **Validação Telefone** | ❌ Não | ✅ Sim |
| **Confirma Senha** | ❌ Não | ✅ Sim |
| **Dados pre-preenchidos** | ❌ Não | ✅ Sim (após busca) |
| **Fluxo** | Linear | Em 3 seções |

---

## 🎯 Recomendação

### Se você quer VER TODOS OS CAMPOS:

**Opção 1:** Usar o formulário melhorado  
```java
setContentView(R.layout.activity_cadastro_melhorado);
```

**Opção 2:** Adicionar os campos faltantes ao formulário simples

### Campos que Não Estão Aparecendo no Formulário Simples:
- ❌ CPF
- ❌ Telefone para Contato  
- ❌ Confirmação de Senha

---

## 📁 Arquivos Relacionados

- **Layout Simples:** `app/src/main/res/layout/activity_cadastro.xml`
- **Layout Melhorado:** `app/src/main/res/layout/activity_cadastro_melhorado.xml`
- **Código Java:** `app/src/main/java/com/example/visualizadorapp/CadastroActivity.java`

---

## 💡 Como Você Pode Ajustar

Se quer manter o layout simples mas ADICIONAR os campos faltantes:

1. Abra `activity_cadastro.xml`
2. Adicione os campos que faltam (CPF, Telefone, Confirmação de Senha)
3. Atualize `CadastroActivity.java` para capturar esses novos campos

Quer que eu faça isso para você? 😊
