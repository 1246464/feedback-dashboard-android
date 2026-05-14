# 📥 Guia de Importação: Dados de Funcionários para Firebase

## 🎯 Objetivo

Explicar como importar dados de funcionários (CPF, Email, Telefone, Cargo, Turno, Setor) para o Firebase Realtime Database, que será usado para validação durante o cadastro de usuários.

---

## 📋 Formato de Dados Necessários

### Estrutura JSON do Funcionário

```json
{
  "cpf": "12345678901",
  "nome": "João Silva",
  "email": "joao.silva@example.com",
  "telefone": "11987654321",
  "cargo": "COZINHEIRO",
  "turno": "MANHA",
  "setor": "Cozinha",
  "horario": "06:00-14:00",
  "ativo": true,
  "dataAdmissao": 1704067200000
}
```

### Campo por Campo

| Campo | Tipo | Descrição | Exemplo |
|-------|------|-----------|---------|
| **cpf** | String | CPF sem formatação (11 dígitos) | `12345678901` |
| **nome** | String | Nome completo | `João Silva` |
| **email** | String | Email corporativo | `joao.silva@example.com` |
| **telefone** | String | Telefone com DDD (11 dígitos) | `11987654321` |
| **cargo** | String | Cargo enum como string | `COZINHEIRO`, `TECNICA`, `AUXILIAR`, etc |
| **turno** | String | Turno de trabalho do funcionário | `MANHA`, `TARDE`, `NOITE`, `5X2` |
| **setor** | String | Setor da instituição | `Cozinha`, `Hemade`, `GCM`, etc |
| **horario** | String | Horário de trabalho | `06:00-14:00`, `14:00-22:00`, `22:00-06:00` |
| **ativo** | Boolean | Se funcionário está ativo | `true` ou `false` |
| **dataAdmissao** | Number | Timestamp Unix em milissegundos | `1704067200000` |

---

## 🔧 Opção 1: Importação via Firebase Console (Mais Fácil)

### Passo 1: Preparar Dados em JSON

Salvar os dados em um arquivo chamado `funcionarios.json`:

```json
{
  "12345678901": {
    "cpf": "12345678901",
    "nome": "João Silva",
    "email": "joao.silva@example.com",
    "telefone": "11987654321",
    "cargo": "COZINHEIRO",
    "turno": "MANHA",
    "setor": "Cozinha",
    "horario": "06:00-14:00",
    "ativo": true,
    "dataAdmissao": 1704067200000
  },
  "98765432101": {
    "cpf": "98765432101",
    "nome": "Maria Santos",
    "email": "maria.santos@example.com",
    "telefone": "11987654322",
    "cargo": "TECNICA",
    "turno": "TARDE",
    "setor": "Cozinha",
    "horario": "14:00-22:00",
    "ativo": true,
    "dataAdmissao": 1704153600000
  },
  "11111111111": {
    "cpf": "11111111111",
    "nome": "Pedro Costa",
    "email": "pedro.costa@example.com",
    "telefone": "11987654323",
    "cargo": "AUXILIAR",
    "turno": "NOITE",
    "setor": "Cozinha",
    "horario": "22:00-06:00",
    "ativo": true,
    "dataAdmissao": 1704240000000
  }
}
```

### Passo 2: Acessar Firebase Console

1. Ir para [Firebase Console](https://console.firebase.google.com)
2. Selecionar seu projeto
3. Ir para **Realtime Database**
4. Clicar em **Dados** (aba principal)

### Passo 3: Importar Dados

**Opção A: Usando o menu de importação**
1. Clicar nos **três pontos** (⋮) no canto superior direito
2. Selecionar **"Importar JSON"**
3. Escolher o arquivo `funcionarios.json`
4. Clicar em **"Importar"**

**Opção B: Criar nó e importar**
1. Clicar no **+** para criar novo nó
2. Nomear como `funcionarios`
3. Abrir o nó
4. Usar o menu de importação dentro do nó

### Passo 4: Verificar Estrutura

No Firebase Console, você deverá ver:

```
Realtime Database
├── funcionarios
│   ├── 12345678901
│   │   ├── nome: "João Silva"
│   │   ├── email: "joao.silva@example.com"
│   │   └── ...
│   ├── 98765432101
│   │   ├── nome: "Maria Santos"
│   │   └── ...
│   └── 11111111111
│       └── ...
└── ...
```

---

## 🔨 Opção 2: Usar Admin SDK (Backend/Servidor)

### Pré-requisito

Ter Node.js ou Python instalado

### A. Com Node.js/JavaScript

#### Criar arquivo `import-funcionarios.js`

```javascript
const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

// Inicializar Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json'); // Baixar do Firebase Console

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://seu-projeto.firebaseio.com' // Substituir pela URL do seu projeto
});

const db = admin.database();

// Ler dados do JSON
const funcionariosPath = path.join(__dirname, 'funcionarios.json');
const funcionarios = JSON.parse(fs.readFileSync(funcionariosPath, 'utf8'));

// Importar para Firebase
db.ref('funcionarios').set(funcionarios)
  .then(() => {
    console.log('✅ Funcionários importados com sucesso!');
    process.exit(0);
  })
  .catch((error) => {
    console.error('❌ Erro ao importar:', error);
    process.exit(1);
  });
```

#### Executar importação

```bash
# Instalar Firebase Admin
npm install firebase-admin

# Executar script
node import-funcionarios.js
```

### B. Com Python

#### Criar arquivo `import_funcionarios.py`

```python
import json
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Inicializar Firebase Admin SDK
cred = credentials.Certificate('serviceAccountKey.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://seu-projeto.firebaseio.com'
})

# Ler dados do JSON
with open('funcionarios.json', 'r', encoding='utf-8') as f:
    funcionarios = json.load(f)

# Importar para Firebase
try:
    db.reference('funcionarios').set(funcionarios)
    print('✅ Funcionários importados com sucesso!')
except Exception as e:
    print(f'❌ Erro ao importar: {e}')
```

#### Executar importação

```bash
# Instalar Firebase Admin
pip install firebase-admin

# Executar script
python import_funcionarios.py
```

### Obter Service Account Key

1. No Firebase Console, ir para **Configurações do Projeto**
2. Clicar na aba **Contas de Serviço**
3. Clicar em **Gerar Nova Chave Privada**
4. Salvar como `serviceAccountKey.json`

---

## 📊 Exemplos de Dados de Teste

### Exemplo Completo: 10 Funcionários

```json
{
  "12345678901": {
    "cpf": "12345678901",
    "nome": "João Silva",
    "email": "joao.silva@example.com",
    "telefone": "11987654321",
    "cargo": "COZINHEIRO",
    "turno": "MANHA",
    "setor": "Cozinha",
    "horario": "06:00-14:00",
    "ativo": true,
    "dataAdmissao": 1704067200000
  },
  "98765432101": {
    "cpf": "98765432101",
    "nome": "Maria Santos",
    "email": "maria.santos@example.com",
    "telefone": "11987654322",
    "cargo": "TECNICA",
    "turno": "TARDE",
    "setor": "Cozinha",
    "horario": "14:00-22:00",
    "ativo": true,
    "dataAdmissao": 1704153600000
  },
  "11111111111": {
    "cpf": "11111111111",
    "nome": "Pedro Costa",
    "email": "pedro.costa@example.com",
    "telefone": "11987654323",
    "cargo": "AUXILIAR",
    "turno": "NOITE",
    "setor": "Cozinha",
    "horario": "22:00-06:00",
    "ativo": true,
    "dataAdmissao": 1704240000000
  },
  "22222222222": {
    "cpf": "22222222222",
    "nome": "Ana Silva",
    "email": "ana.silva@example.com",
    "telefone": "11987654324",
    "cargo": "COPEIRA",
    "turno": "MANHA",
    "setor": "Serviço",
    "horario": "06:00-14:00",
    "ativo": true,
    "dataAdmissao": 1704326400000
  },
  "33333333333": {
    "cpf": "33333333333",
    "nome": "Carlos Oliveira",
    "email": "carlos.oliveira@example.com",
    "telefone": "11987654325",
    "cargo": "GERENTE",
    "turno": "5X2",
    "setor": "Administração",
    "horario": "08:00-17:00",
    "ativo": true,
    "dataAdmissao": 1704412800000
  },
  "44444444444": {
    "cpf": "44444444444",
    "nome": "Fernanda Lima",
    "email": "fernanda.lima@example.com",
    "telefone": "11987654326",
    "cargo": "LIDER_COZINHA",
    "turno": "5X2",
    "setor": "Cozinha",
    "horario": "08:00-17:00",
    "ativo": true,
    "dataAdmissao": 1704499200000
  },
  "55555555555": {
    "cpf": "55555555555",
    "nome": "Lucas Martins",
    "email": "lucas.martins@example.com",
    "telefone": "11987654327",
    "cargo": "ESTOQUISTA",
    "turno": "5X2",
    "setor": "Estoque",
    "horario": "08:00-17:00",
    "ativo": true,
    "dataAdmissao": 1704585600000
  },
  "66666666666": {
    "cpf": "66666666666",
    "nome": "Julia Souza",
    "email": "julia.souza@example.com",
    "telefone": "11987654328",
    "cargo": "MEIO_OFICIAL_PLANTAO",
    "turno": "TARDE",
    "setor": "Cozinha",
    "horario": "14:00-22:00",
    "ativo": true,
    "dataAdmissao": 1704672000000
  },
  "77777777777": {
    "cpf": "77777777777",
    "nome": "Bruno Alves",
    "email": "bruno.alves@example.com",
    "telefone": "11987654329",
    "cargo": "COPEIRO_NOTURNO",
    "turno": "NOITE",
    "setor": "Serviço",
    "horario": "22:00-06:00",
    "ativo": true,
    "dataAdmissao": 1704758400000
  },
  "88888888888": {
    "cpf": "88888888888",
    "nome": "Patricia Rocha",
    "email": "patricia.rocha@example.com",
    "telefone": "11987654330",
    "cargo": "MEIO_OFICIAL_5X2",
    "turno": "5X2",
    "setor": "Cozinha",
    "horario": "08:00-17:00",
    "ativo": true,
    "dataAdmissao": 1704844800000
  }
}
```

---

## 🔍 Validar Importação

### No App Android

```java
// Teste rápido: buscar funcionário importado
FuncionarioRepository repo = new FuncionarioRepository(getApplication());
Funcionario func = repo.buscarPorEmail("joao.silva@example.com");

if (func != null) {
    Toast.makeText(this, "✅ Funcionário encontrado: " + func.getNome(), Toast.LENGTH_SHORT).show();
} else {
    Toast.makeText(this, "❌ Funcionário não encontrado", Toast.LENGTH_SHORT).show();
}
```

### No Firebase Console

1. Ir para **Realtime Database**
2. Expandir nó `funcionarios`
3. Verificar se os dados estão presentes

---

## ⚠️ Boas Práticas

### 1. **Backup Antes de Importar**
```bash
# Exportar dados atuais antes de importar novos
# Firebase Console → ⋮ → "Exportar JSON"
```

### 2. **Validar Dados Antes de Importar**
- Verificar se CPF tem 11 dígitos
- Verificar se email é válido
- Verificar se telefone tem 11 dígitos
- Verificar se cargo está na enum

### 3. **Importação Incrementalal**
```javascript
// Ao invés de substituir tudo, adicionar/atualizar
const funcionariosRef = db.ref('funcionarios');
Object.entries(funcionarios).forEach(([cpf, dados]) => {
  funcionariosRef.child(cpf).set(dados);
});
```

### 4. **Regras de Segurança Firebase**
```json
{
  "rules": {
    "funcionarios": {
      ".read": "auth != null",
      ".write": "root.child('admins').child(auth.uid).exists()"
    }
  }
}
```

---

## 🚨 Troubleshooting

| Problema | Solução |
|----------|---------|
| Erro "Permission denied" ao importar | Verificar regras de segurança do Firebase |
| Dados não aparecem no app | Reiniciar app; Limpar cache; Verificar sincronização |
| Email não encontrado no cadastro | Verificar se email no JSON é idêntico ao inserido |
| CPF inválido no teste | Usar CPFs válidos; Validar com módulo 11 |

---

## 📝 Scripts Auxiliares

### Validar CPF em Lote

```python
def validar_cpf(cpf):
    """Valida CPF usando algoritmo módulo 11"""
    cpf_limpo = cpf.replace('.', '').replace('-', '')
    
    if len(cpf_limpo) != 11 or not cpf_limpo.isdigit():
        return False
    
    # Validar primeiro dígito
    soma = sum(int(cpf_limpo[i]) * (10 - i) for i in range(9))
    digito1 = 11 - (soma % 11)
    if digito1 > 9:
        digito1 = 0
    
    if int(cpf_limpo[9]) != digito1:
        return False
    
    # Validar segundo dígito
    soma = sum(int(cpf_limpo[i]) * (11 - i) for i in range(10))
    digito2 = 11 - (soma % 11)
    if digito2 > 9:
        digito2 = 0
    
    return int(cpf_limpo[10]) == digito2

# Teste
print(validar_cpf("12345678901"))  # Implementar com CPF real
```

### Gerar Timestamps Unix

```python
from datetime import datetime

# Data de admissão
data = datetime(2024, 1, 1)
timestamp = int(data.timestamp() * 1000)  # Em milissegundos
print(f"Timestamp: {timestamp}")  # 1704067200000
```

---

## ✅ Checklist de Importação

- [ ] Preparar arquivo JSON com dados dos funcionários
- [ ] Validar CPFs, emails e telefones
- [ ] Backup de dados existentes (se houver)
- [ ] Fazer upload para Firebase Console OU rodar script de importação
- [ ] Verificar estrutura no Firebase Console
- [ ] Testar cadastro de usuário com email importado
- [ ] Testar validação de CPF
- [ ] Verificar se dados sincronizam com app

---

**Próxima Etapa**: [Criar cardápios por turno](GUIA_INTEGRACAO_CARDAPIO_TURNO.md)
