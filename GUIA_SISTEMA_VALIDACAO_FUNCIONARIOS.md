# 📋 Guia de Implementação: Sistema de Validação de Funcionários e Cardápio por Turno

## 🎯 Objetivo

Melhorar a segurança e validação de funcionários no app, garantindo que apenas funcionários reais e verificados possam se registrar, e que cada um veja apenas o cardápio do seu turno específico.

---

## 🔧 Melhorias Implementadas

### 1. **Novo Modelo: Funcionario.java**
- **Campos**: CPF, Nome, Email, Telefone, Cargo, Turno, Setor, Horário, Status Ativo, Data de Admissão
- **Localização**: `app/src/main/java/com/example/visualizadorapp/model/Funcionario.java`
- **Função**: Armazenar dados de funcionários validados pela instituição

### 2. **Novo Modelo: CardapioTurno.java**
- **Campos**: Data, Turno (MANHA, TARDE, NOITE), Cardápio completo, Calorias, Proteínas, Informações adicionais
- **Localização**: `app/src/main/java/com/example/visualizadorapp/model/CardapioTurno.java`
- **Função**: Relacionar cardápios a turnos específicos

### 3. **Modelo Atualizado: Usuario.java**
- **Novos campos**: 
  - `cpf` - CPF do funcionário (11 dígitos)
  - `telefone` - Telefone de contato (11 dígitos com DDD)
  - `turno` - Turno específico (MANHA, TARDE, NOITE, 5X2)
  - `validado` - Boolean indicando se foi validado contra base de funcionários
  - `dataRegistro` - Data de criação da conta

### 4. **Validadores Criados**

#### **ValidadorCPF.java**
- Validação de CPF com cálculo de dígitos verificadores
- Métodos:
  - `isValido(cpf)` - Valida se CPF é válido
  - `removerFormatacao(cpf)` - Remove formatação
  - `formatar(cpf)` - Formata como XXX.XXX.XXX-XX

#### **ValidadorTelefone.java**
- Validação de telefone brasileiro (11 dígitos)
- Métodos:
  - `isValido(telefone)` - Valida telefone
  - `isCelular(telefone)` - Verifica se é celular
  - `isFixo(telefone)` - Verifica se é fixo
  - `formatar(telefone)` - Formata como (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX
  - `extrairDDD(telefone)` - Extrai DDD

### 5. **DAOs Criados**

#### **FuncionarioDao.java**
Operações no banco local:
- `buscarPorCPF(cpf)` - Busca funcionário por CPF
- `buscarPorEmail(email)` - Busca funcionário por email
- `buscarPorTelefone(telefone)` - Busca funcionário por telefone
- `buscarPorTurno(turno)` - Busca todos de um turno
- `contarPorEmail(email)` - Verifica se email existe
- `contarPorCPF(cpf)` - Verifica se CPF existe
- `buscarTodosAtivos()` - Retorna funcionários ativos
- `buscarTurnosUnicos()` - Lista turno únicos

#### **CardapioTurnoDao.java**
Operações no banco local:
- `buscarPorDataETurno(data, turno)` - Cardápio específico
- `buscarPorTurno(turno)` - Todos cardápios de um turno
- `buscarPorPeriodoETurno(inicio, fim, turno)` - Período + turno
- `buscarFavoritosPorTurno(turno)` - Favoritos de um turno
- `atualizarFavorito(id, isFavorito)` - Marca/desmarcar favorito

### 6. **Repositórios Criados**

#### **FuncionarioRepository.java**
- Sincroniza funcionários do Firebase com banco local
- Busca funcionários localmente
- Valida existência de email e CPF
- Métodos principais:
  - `buscarPorEmail(email)` - Busca rápida
  - `buscarPorCPF(cpf)` - Busca rápida
  - `emailJaExiste(email)` - Verificação booleana
  - `cpfJaExiste(cpf)` - Verificação booleana
  - `sincronizarComFirebase()` - Sincroniza dados

#### **CardapioTurnoRepository.java**
- Sincroniza cardápios do Firebase com banco local
- Busca cardápios por turno do usuário
- Métodos principais:
  - `buscarPorDataETurno(data, turno)` - Cardápio específico
  - `buscarPorTurnoAsync(turno)` - LiveData dos cardápios
  - `buscarProximosAsync(data)` - Próximas refeições
  - `buscarFavoritosPorTurnoAsync(turno)` - Favoritos do turno

### 7. **Nova Atividade de Cadastro: CadastroMelhoradoActivity.java**

**Fluxo do Cadastro:**

```
1. Usuário insere EMAIL
   ↓
2. Sistema busca funcionário com este email
   ↓
   ├─ Se NÃO encontrado → Mensagem de erro
   │
   └─ Se encontrado → Mostra dados:
      • Nome
      • Cargo
      • Turno
      • Setor
   ↓
3. Sistema preenche CPF automaticamente (não editável)
   ↓
4. Usuário insere TELEFONE
   (Validação: 11 dígitos)
   ↓
5. Usuário seleciona PREFERÊNCIA ALIMENTAR
   ↓
6. Usuário insere SENHA
   (Validação: mínimo 6 caracteres)
   ↓
7. Usuário confirma SENHA
   ↓
8. Sistema valida:
   - CPF com módulo 11
   - Telefone com 11 dígitos
   - Senhas iguais
   - CPF corresponde ao do funcionário
   ↓
9. Se tudo OK → Cria conta no Firebase Auth
   Salva dados no Realtime Database com:
   - uid, nome, email, cpf, telefone
   - cargo, turno, setor, horario
   - preferencia, validado=true, dataRegistro
```

---

## 📱 Interface de Cadastro Melhorada

### Layout: `activity_cadastro_melhorado.xml`

**3 Seções:**

1. **Verificar Funcionário**
   - Campo de email
   - Botão "Buscar Dados do Funcionário"

2. **Dados do Funcionário** (Aparece após busca bem-sucedida)
   - Cards com: Nome, Cargo, Turno, Setor
   - CPF (preenchido automaticamente, não editável)
   - Telefone (com mascaramento)
   - Spinner de Preferência Alimentar

3. **Criar Senha**
   - Campo de Senha
   - Campo de Confirmação
   - Botão "Finalizar Cadastro"

---

## 🔄 Fluxo de Sincronização

### Firebase → App Local

```
Firebase Database:
├── funcionarios/
│   ├── {cpf1}: Funcionario Object
│   ├── {cpf2}: Funcionario Object
│   └── ...
│
├── cardapios_turno/
│   ├── 2024-01-15_MANHA: CardapioTurno
│   ├── 2024-01-15_TARDE: CardapioTurno
│   ├── 2024-01-15_NOITE: CardapioTurno
│   └── ...
│
└── usuarios/
    ├── {uid1}: Usuario (com cpf, telefone, turno, validado)
    ├── {uid2}: Usuario
    └── ...

         ↓ (Sincronização via Repository)

Room Database Local:
├── funcionarios table
├── cardapio_turno table
├── cardapios table (original, mantido para compatibilidade)
└── ...
```

---

## 🍽️ Sistema de Cardápio por Turno

### Antes (Sistema Antigo)
- Cardápio único para todos
- Não diferencia por turno
- Modelo: `Cardapio` (apenas data)

### Depois (Novo Sistema)
- Cardápio específico por turno
- Usuário vê apenas o do seu turno
- Modelo: `CardapioTurno` (data + turno)

### Implementação na Interface

**CardapioSemanalActivity (Sugestão de Melhoria):**

```java
// Ao carregar cardápios, filtrar pelo turno do usuário
String turnoUsuario = usuarioLogado.getTurno(); // "MANHA", "TARDE", etc

cardapioRepository.buscarPorTurnoAsync(turnoUsuario).observe(this, cardapios -> {
    // Exibir apenas cardápios deste turno
    atualizarUI(cardapios);
});
```

---

## 🔐 Segurança Implementada

| Validação | Método |
|-----------|--------|
| **Email único** | Firebase Auth (nativo) + Banco local |
| **CPF único** | Verificado contra base de funcionários |
| **CPF válido** | Algoritmo módulo 11 |
| **Telefone válido** | Validação de 11 dígitos + DDD |
| **Funcionário real** | Busca na tabela `funcionarios` |
| **Turno correto** | Vem da base de funcionários |
| **Senha segura** | Mínimo 6 caracteres + Firebase Auth |

---

## 📊 Estrutura do Firebase

### Funcionários (Importado do RH)
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
      "setor": "Cozinha",
      "horario": "6-14",
      "ativo": true,
      "dataAdmissao": 1234567890,
      "ultimaAtualizacao": 1234567890
    }
  }
}
```

### Cardápios por Turno
```json
{
  "cardapios_turno": {
    "2024-01-15_MANHA": {
      "id": "2024-01-15_MANHA",
      "data": "2024-01-15",
      "turno": "MANHA",
      "pratoPrincipal": "Carne com batata",
      "guarnicao": "Feijão",
      "salada": "Alface",
      "sobremesa": "Fruta",
      "calorias": "2500 kcal",
      "proteinas": "45g"
    }
  }
}
```

---

## 🚀 Como Usar

### 1. Importar Funcionários para Firebase

**Opção A: Importação Manual via Console Firebase**
1. Ir a Firebase Console → Realtime Database
2. Importar dados JSON da estrutura de funcionários
3. Guardar em nó `funcionarios`

**Opção B: Admin SDK (Backend)**
```java
// Usar Firebase Admin SDK para importar CSV/Excel
Map<String, Funcionario> funcionarios = lerDadosRH();
database.child("funcionarios").setValue(funcionarios);
```

### 2. Testar Cadastro Melhorado

1. **Pré-requisito**: Funcionários já importados no Firebase
2. **Abrir** `CadastroMelhoradoActivity`
3. **Inserir** email de um funcionário existente
4. **Sistema** busca e mostra dados
5. **Usuário** confirma e insere telefone/senha
6. **Conta criada** com `validado=true`

### 3. Exibir Cardápio por Turno

**Em CardapioSemanalActivity.java:**
```java
// Obter turno do usuário logado
String turnoUsuario = usuarioAtual.getTurno();

// Buscar cardápios apenas deste turno
cardapioTurnoRepository.buscarPorTurnoAsync(turnoUsuario)
    .observe(this, cardapios -> {
        // Exibir no RecyclerView/ListView
        adapter.submitList(cardapios);
    });
```

---

## 📝 Próximos Passos Recomendados

1. **Criar interface de gerenciamento** de funcionários (Admin)
   - Adicionar novos funcionários
   - Atualizar dados
   - Desativar funcionários

2. **Criar interface de gerenciamento** de cardápios por turno
   - Criar/editar cardápios para cada turno
   - Copiar cardápios entre dias

3. **Notificações** quando cardápio muda para o turno do usuário

4. **Relatórios** de cardápios/turnos

5. **Sincronização offline** melhorada com Firebase

6. **Auditoria** - Log de quem acessou qual cardápio

---

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| Funcionário não encontrado | Verificar se email está correto na base; Sincronizar Firebase |
| CPF inválido | Verificar se CPF tem 11 dígitos; Validar com algoritmo módulo 11 |
| Telefone inválido | Deve ter 11 dígitos com DDD válido (11-99) |
| Cardápio não aparece | Verificar se turno está correto; Sincronizar CardapioTurno |
| Erro ao sincronizar | Verificar conexão com Firebase; Logs em Logcat |

---

## 📄 Arquivos Criados/Modificados

### ✅ Arquivos Criados
- `Funcionario.java` - Modelo de funcionário
- `CardapioTurno.java` - Modelo de cardápio por turno
- `ValidadorCPF.java` - Validador de CPF
- `ValidadorTelefone.java` - Validador de telefone
- `FuncionarioDao.java` - DAO para funcionários
- `CardapioTurnoDao.java` - DAO para cardápios por turno
- `FuncionarioRepository.java` - Repositório de funcionários
- `CardapioTurnoRepository.java` - Repositório de cardápios
- `CadastroMelhoradoActivity.java` - Nova atividade de cadastro
- `activity_cadastro_melhorado.xml` - Layout da nova atividade
- `edit_text_background.xml` - Drawable para EditText
- `rounded_background.xml` - Drawable para containers

### ✏️ Arquivos Modificados
- `Usuario.java` - Adicionados campos: cpf, telefone, turno, validado, dataRegistro
- `AppDatabase.java` - Adicionados DAOs e versão do banco atualizada (v5)
- `colors.xml` - Adicionadas cores: teal_700, light_blue_background, light_gray

---

## 📞 Suporte

Para dúvidas sobre a implementação:
1. Verificar logs no Logcat
2. Revisar comentários no código
3. Consultar documentação do Firebase
4. Validar estrutura do JSON no Firebase Console

---

**Versão**: 1.0  
**Data**: Janeiro 2024  
**Status**: Implementação Completa
