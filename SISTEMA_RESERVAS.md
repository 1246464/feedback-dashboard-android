# 🍽️ Sistema de Reservas - Documentação Completa

## 📋 Visão Geral

O sistema de reservas permite que usuários comuns reservem suas refeições do dia, e o administrador acompanhe todas as reservas, incluindo **a escolha específica de cada usuário** (ovo, peixe, etc.).

---

## ✨ Funcionalidades Implementadas

### 1. **Para Usuários Comuns**

#### Como Reservar:
1. Acesse o app e faça login
2. Na tela principal (MainActivity), clique em "🍽️ Reservar Meu Prato"
3. Veja o cardápio do dia
4. Adicione observações (opcional): "Sem cebola", "Pouco sal", etc.
5. Clique em "Fazer Reserva"

#### O que é Salvo:
- ✅ **Nome do usuário**
- ✅ **Setor** (ex: Produção, RH, etc.)
- ✅ **Escolha do prato** (ovo, peixe, etc.) - **COPIADO DO VOTO**
- ✅ **Observações** (preferências especiais)
- ✅ **Data e hora da reserva**
- ✅ **Status** (ATIVA, CANCELADA, UTILIZADA)

#### Regras:
- ⏰ Horário limite: **18:00** (após isso, não pode mais reservar)
- 🔒 **Apenas 1 reserva ativa por dia** por usuário
- 📝 A **escolha é obtida automaticamente** do voto do usuário

---

### 2. **Para Administradores**

#### Como Visualizar Reservas:
1. Acesse o **Painel Administrativo**
2. Clique em "🍽️ Reservas do Dia"
3. Veja a lista completa com:
   - Nome do usuário
   - **Escolha do prato** (ex: "Ovo", "Peixe")
   - Setor
   - Observações
   - Status

#### Resumo Executivo:
No topo da tela, você verá:
```
📋 Reservas do Dia
Reservas do dia: 03/03/2026
12 reservas ativas

📊 Resumo das escolhas:
• Ovo: 8
• Peixe: 3
• Não votou: 1
```

#### Ações Disponíveis:
Para cada reserva, o admin pode:
- ✅ **Marcar como Utilizada** (quando a pessoa retirou o prato)
- ❌ **Cancelar reserva** (se necessário)

---

## 🔄 Fluxo Completo do Sistema

### Cenário Típico:

```
MANHÃ (até 10:00)
└── Usuário vota no cardápio
    └── Escolhe: "Ovo" 
    └── Salvo no Firebase: /escolhas/{data}/{userId}/escolha = "Ovo"

TARDE (até 18:00)
└── Usuário faz reserva
    └── Sistema BUSCA automaticamente a escolha "Ovo"
    └── Salva no Room Database com: escolhaPrato = "Ovo"

ADMIN (qualquer hora)
└── Abre "Reservas do Dia"
    └── Vê: "João Silva - Ovo - Setor: Produção"
    └── Sabe exatamente: 8 pessoas querem ovo, 3 querem peixe
```

---

## 🎯 Integração entre Voto e Reserva

### Como Funciona:

1. **Usuário vota** na tela principal (MainActivity):
   - Escolhe entre: Ovo, Peixe, etc.
   - Salvo em: `Firebase → /escolhas/{data}/{userId}/escolha`

2. **Usuário reserva** (ReservaActivity):
   - Sistema **busca automaticamente** no Firebase
   - Copia a escolha do voto para a reserva
   - Salva no Room Database local

3. **Admin visualiza** (ListaReservasActivity):
   - Lê do Room Database
   - Mostra a escolha de cada pessoa
   - **Consolida totais** (quantos querem cada opção)

---

## 📊 Estrutura de Dados

### Modelo Reserva (Room Database):

```java
@Entity(tableName = "reservas")
public class Reserva {
    int id;
    String userId;
    String data;              // "2026-03-03"
    String nomeUsuario;       // "João Silva"
    String emailUsuario;
    String escolhaPrato;      // "Ovo" ← NOVO!
    String setor;             // "Produção" ← NOVO!
    String observacao;        // "Sem cebola"
    String statusReserva;     // "ATIVA", "CANCELADA", "UTILIZADA"
    long timestampReserva;
}
```

### Firebase - Escolhas (/escolhas):

```
escolhas/
  └── 2026-03-03/
      └── {userId}/
          ├── escolha: "Ovo"
          ├── setor: "Produção"
          └── timestamp: 1234567890
```

---

## 🖥️ Telas do Sistema

### 1. MainActivity (Usuário)
```
┌─────────────────────────────────┐
│  🍽️ Cardápio do Dia             │
│  Data: 03/03/2026                │
│                                  │
│  🍗 Prato: Frango Assado         │
│  🍖 Guarnição: Arroz             │
│  🍚 Acompanhamento: Feijão       │
│  🥗 Salada: Verde                │
│  🍰 Sobremesa: Pudim             │
│                                  │
│  [💡 Sugerir Prato]              │
│  [🍽️ Reservar Meu Prato] ← AQUI │
└─────────────────────────────────┘
```

### 2. ReservaActivity (Usuário)
```
┌─────────────────────────────────┐
│  🍽️ Reservar Prato               │
│                                  │
│  Cardápio de Hoje:               │
│  🍗 Prato: Frango Assado         │
│  🍖 Guarnição: Arroz             │
│  ...                             │
│                                  │
│  Observações:                    │
│  [_____________]                 │
│                                  │
│  [Fazer Reserva]                 │
│                                  │
│  Minha reserva ativa:            │
│  Status: ATIVA                   │
│  Observação: Sem cebola          │
│  [Cancelar Reserva]              │
└─────────────────────────────────┘
```

### 3. ListaReservasActivity (Admin)
```
┌─────────────────────────────────┐
│  📋 Reservas do Dia              │
│  Reservas do dia: 03/03/2026     │
│  12 reservas ativas              │
│                                  │
│  📊 Resumo das escolhas:         │
│  • Ovo: 8                        │
│  • Peixe: 3                      │
│  • Não votou: 1                  │
│                                  │
│  ────────────────────────        │
│                                  │
│  👤 João Silva                   │
│  🍽️ Escolha: Ovo ← MOSTRA AQUI! │
│  🏢 Setor: Produção              │
│  📌 Status: ATIVA                │
│  💬 Obs: Sem cebola              │
│  [✓ Utilizada] [✗ Cancelar]     │
│                                  │
│  ────────────────────────        │
│                                  │
│  👤 Maria Santos                 │
│  🍽️ Escolha: Peixe              │
│  🏢 Setor: RH                    │
│  📌 Status: ATIVA                │
│  [✓ Utilizada] [✗ Cancelar]     │
│                                  │
│  [← Voltar]                      │
└─────────────────────────────────┘
```

---

## 💡 Casos de Uso

### Caso 1: Preparação para o Almoço
**Cenário**: Cozinha precisa saber quantos ovos preparar

**Solução**:
1. Admin abre "🍽️ Reservas do Dia"
2. Vê o resumo: "• Ovo: 8"
3. Prepara **8 porções de ovo**
4. Evita desperdício!

---

### Caso 2: Usuário com Restrição
**Cenário**: João não pode comer cebola

**Solução**:
1. João vota em "Ovo"
2. João reserva e adiciona: "Sem cebola"
3. Admin vê: "João Silva - Ovo - Obs: Sem cebola"
4. Cozinha prepara sem cebola

---

### Caso 3: Controle de Retirada
**Cenário**: Verificar quem já retirou

**Solução**:
1. Admin marca "✓ Utilizada" quando a pessoa retira
2. Contador atualiza automaticamente
3. Sabe quantas pessoas ainda vão buscar

---

## 🔧 Modificações Técnicas Realizadas

### Arquivos Modificados:

1. **Reserva.java** - Adicionados campos:
   - `String escolhaPrato`
   - `String setor`

2. **AppDatabase.java** - Versão atualizada:
   - `version = 2` → `version = 3`

3. **ReservaActivity.java** - Busca automática:
   - Carrega setor do Firebase
   - Carrega escolha do voto do Firebase
   - Salva na reserva

### Arquivos Criados:

4. **ListaReservasActivity.java** - Tela admin
5. **activity_lista_reservas.xml** - Layout principal
6. **item_reserva.xml** - Layout de cada reserva

### Arquivos Atualizados:

7. **activity_admin.xml** - Adicionado botão "Reservas do Dia"
8. **AdminActivity.java** - Listener para abrir tela
9. **AndroidManifest.xml** - Registrada nova activity

---

## 📱 Como Testar

### Teste Completo:

```
1. Login como usuário comum
2. Votar no cardápio (escolher "Ovo")
3. Clicar em "Reservar Meu Prato"
4. Adicionar observação: "Pouco sal"
5. Confirmar reserva

6. Logout
7. Login como admin
8. Clicar em "Reservas do Dia"
9. VERIFICAR: 
   ✓ Nome do usuário aparece
   ✓ Escolha "Ovo" está visível ← IMPORTANTE!
   ✓ Observação "Pouco sal" aparece
   ✓ Resumo mostra "• Ovo: 1"

10. Marcar como "Utilizada"
11. Status muda para "UTILIZADA"
```

---

## ⚠️ Regras e Validações

### Horário:
- ✅ Reservas permitidas: **até 18:00**
- ❌ Após 18:00: Botão desabilitado

### Duplicação:
- ✅ 1 reserva ativa por usuário/dia
- ❌ Não pode fazer 2 reservas no mesmo dia

### Escolha:
- ✅ Se votou: Escolha é copiada automaticamente
- ⚠️ Se não votou: Salva como "Não votou"

### Status:
- **ATIVA**: Reserva confirmada, aguardando retirada
- **UTILIZADA**: Pessoa já retirou o prato
- **CANCELADA**: Reserva cancelada (pelo usuário ou admin)

---

## 🎨 Códigos de Cores

| Status | Cor | Significado |
|--------|-----|-------------|
| ATIVA | 🟢 Verde | Aguardando retirada |
| UTILIZADA | 🔵 Azul | Já retirou |
| CANCELADA | 🔴 Vermelho | Cancelada |

---

## 🚀 Benefícios do Sistema

✅ **Menos Desperdício**: Sabe exatamente quantos pratos preparar
✅ **Atendimento Personalizado**: Vê observações de cada pessoa
✅ **Controle Total**: Rastreia quem reservou e quem retirou
✅ **Planejamento**: Resumo agregado das escolhas
✅ **Transparência**: Admin vê todas as informações

---

## 📊 Métricas Disponíveis

O sistema fornece:
- **Total de reservas ativas** do dia
- **Contagem por escolha** (X pessoas querem ovo, Y querem peixe)
- **Status individual** de cada reserva
- **Observações especiais** de cada usuário

---

## 🔐 Segurança

- Cada usuário vê apenas suas próprias reservas
- Admin vê todas as reservas
- Validação de horário (18:00)
- Prevenção de duplicatas

---

## 🎓 Conclusão

O sistema agora está **100% integrado**:

1. ✅ Usuário vota → Sistema salva escolha
2. ✅ Usuário reserva → Sistema copia escolha automaticamente
3. ✅ Admin visualiza → Vê exatamente quem quer o quê

**Resultado**: Produção eficiente, menos desperdício, clientes satisfeitos!

---

**Última Atualização**: 2024  
**Status**: ✅ Implementação Completa
