# 🔥 CONFIGURAÇÃO DO FIREBASE - Sistema de Gestão de Usuários

## ✅ O QUE JÁ DEVE ESTAR CONFIGURADO

Se você já usa o app (login funciona), então você já tem:
- ✅ Firebase Authentication habilitado
- ✅ Realtime Database criado
- ✅ Conexão do app com o Firebase

---

## 🔐 REGRAS DE SEGURANÇA NECESSÁRIAS

### ⚠️ IMPORTANTE: Verifique suas regras!

Para o sistema de gestão de usuários funcionar corretamente, você precisa configurar as **Regras de Segurança** do Realtime Database.

### 📍 Onde configurar:

1. Acesse: https://console.firebase.google.com
2. Selecione seu projeto: **insights-cardapio**
3. Menu lateral → **Realtime Database**
4. Aba: **Regras** (Rules)

---

## 🎯 OPÇÃO 1: Regras Simples (Desenvolvimento/Teste)

**Use apenas para testes ou se TODOS os usuários são confiáveis**

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

✅ **Vantagens:**
- Funciona imediatamente
- Qualquer usuário autenticado pode ler/escrever tudo

⚠️ **Desvantagens:**
- **INSEGURO!** Qualquer funcionário pode deletar dados de outros
- Usuários comuns podem se promover a gerentes
- Não recomendado para produção

---

## 🛡️ OPÇÃO 2: Regras Seguras (Recomendado para Produção)

**Protege o sistema - apenas gerentes podem gerenciar funcionários**

```json
{
  "rules": {
    "usuarios": {
      "$uid": {
        ".read": "auth != null && (auth.uid === $uid || root.child('usuarios').child(auth.uid).child('cargo').val() === 'GERENTE')",
        ".write": "auth != null && root.child('usuarios').child(auth.uid).child('cargo').val() === 'GERENTE'"
      }
    },
    "cardapio": {
      ".read": "auth != null",
      "$date": {
        ".write": "auth != null && root.child('usuarios').child(auth.uid).child('cargo').val() === 'GERENTE'"
      }
    },
    "votos": {
      ".read": "auth != null",
      "$date": {
        "$uid": {
          ".write": "auth != null && auth.uid === $uid"
        }
      }
    },
    "comentarios": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "cardapioSemanal": {
      ".read": "auth != null",
      ".write": "auth != null && (root.child('usuarios').child(auth.uid).child('cargo').val() === 'GERENTE' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'TECNICA' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'LIDER_COZINHA')"
    },
    "tarefasPreparo": {
      ".read": "auth != null",
      ".write": "auth != null && (root.child('usuarios').child(auth.uid).child('cargo').val() === 'GERENTE' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'TECNICA' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'LIDER_COZINHA')"
    },
    "passagemTurno": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "ingredientes": {
      ".read": "auth != null",
      ".write": "auth != null && (root.child('usuarios').child(auth.uid).child('cargo').val() === 'GERENTE' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'TECNICA' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'LIDER_COZINHA' || root.child('usuarios').child(auth.uid).child('cargo').val() === 'ESTOQUISTA')"
    },
    "reservas": {
      ".read": "auth != null",
      "$date": {
        "$uid": {
          ".write": "auth != null && auth.uid === $uid"
        }
      }
    }
  }
}
```

✅ **Vantagens:**
- **SEGURO!** Apenas gerentes podem criar/editar/deletar funcionários
- Usuários só podem ver seu próprio perfil
- Gerentes podem ver todos os perfis
- Outras áreas protegidas por cargo específico

⚠️ **Problema Potencial:**
- Se você não tem nenhum gerente criado ainda, pode ficar "preso"
- **Solução:** Use a Opção 1 temporariamente para criar o primeiro gerente, depois ative a Opção 2

---

## 🚀 PASSO A PASSO RECOMENDADO

### 1️⃣ Se você ainda NÃO tem nenhum funcionário:

1. Configure as **Regras Simples (Opção 1)** no Firebase
2. Faça login no app com sua conta
3. Vá em: Painel Admin → Gerenciar Funcionários
4. Crie a primeira conta de GERENTE (pode ser você mesmo)
5. Teste que consegue criar/editar/deletar funcionários
6. ✅ Depois que confirmar que funciona, **mude para as Regras Seguras (Opção 2)**

### 2️⃣ Se você já tem funcionários cadastrados:

1. Verifique no Firebase Console → Realtime Database → Data
2. Procure por: `usuarios/{algum_uid}/cargo`
3. Se já existe alguém com `cargo: "GERENTE"`:
   - ✅ Vá direto para **Regras Seguras (Opção 2)**
4. Se NÃO existe gerente:
   - Use **Regras Simples (Opção 1)** temporariamente
   - Crie um gerente
   - Depois mude para **Regras Seguras (Opção 2)**

---

## 🧪 COMO TESTAR SE ESTÁ FUNCIONANDO

### ✅ Teste 1: Criar Funcionário
1. Login como **GERENTE**
2. Painel Admin → Gerenciar Funcionários → + Novo Funcionário
3. Preencha os dados e clique em "Criar"
4. **Esperado:** Mensagem "Funcionário criado com sucesso!"
5. **Se der erro:** Suas regras estão bloqueando a escrita

### ✅ Teste 2: Listar Funcionários
1. Login como **GERENTE**
2. Painel Admin → Gerenciar Funcionários
3. **Esperado:** Ver a lista de todos os funcionários
4. **Se der erro:** Suas regras estão bloqueando a leitura

### ✅ Teste 3: Editar Funcionário
1. Login como **GERENTE**
2. Gerenciar Funcionários → Escolha um funcionário → ✏️ Editar
3. Mude o cargo e clique em "Salvar"
4. **Esperado:** Mensagem "Funcionário atualizado!"
5. **Se der erro:** Suas regras estão bloqueando a atualização

### ✅ Teste 4: Deletar Funcionário
1. Login como **GERENTE**
2. Gerenciar Funcionários → Escolha um funcionário → 🗑️ Deletar → Confirmar
3. **Esperado:** Funcionário some da lista
4. **Se der erro:** Suas regras estão bloqueando a exclusão

---

## ❌ ERROS COMUNS

### Erro: "Erro ao carregar usuários: Permission denied"
**Causa:** Regras impedem leitura do nó "usuarios"  
**Solução:** 
- Se você é gerente: Verifique se seu campo `cargo` está correto no Firebase
- Se não é gerente: Use Regras Simples temporariamente

### Erro: "Erro ao criar conta: Permission denied"
**Causa:** Regras impedem escrita em "usuarios/{uid}"  
**Solução:** Use Regras Simples ou verifique se você é GERENTE

### Erro: "Email já existe"
**Causa:** Já existe uma conta com esse email no Firebase Authentication  
**Solução:** 
- Use outro email, OU
- Delete a conta antiga no Firebase Console → Authentication

---

## 🔍 COMO VERIFICAR SUAS REGRAS ATUAIS

1. Firebase Console: https://console.firebase.google.com
2. Selecione: **insights-cardapio**
3. Menu: **Realtime Database** → Aba **Regras**
4. Você verá algo tipo:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

**Se tiver `.read: true` e `.write: true`:**
- ✅ Tudo funciona
- ⚠️ Mas está 100% aberto (inseguro)

**Se tiver `.read: "auth != null"` e `.write: "auth != null"`:**
- ✅ Qualquer usuário autenticado pode tudo
- ⚠️ Moderadamente seguro (ok para uso interno controlado)

**Se tiver regras complexas com `cargo === 'GERENTE'`:**
- ✅ Máxima segurança
- ⚠️ Precisa ter ao menos 1 gerente criado

---

## 📋 CHECKLIST FINAL

Antes de usar em produção, verifique:

- [ ] Firebase Authentication está habilitado
- [ ] Realtime Database foi criado
- [ ] URL do banco está correta no código (já está: `insights-cardapio-default-rtdb.firebaseio.com`)
- [ ] Regras de segurança estão configuradas (Opção 1 ou 2)
- [ ] Pelo menos 1 conta de GERENTE foi criada
- [ ] Testou criar/editar/deletar funcionários
- [ ] Funcionários criados conseguem fazer login
- [ ] Cada cargo redireciona para o dashboard correto

---

## 🆘 AINDA COM PROBLEMAS?

### Opção Rápida: Liberar Tudo (Temporário!)

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

⚠️ **ATENÇÃO:** Isso deixa seu banco COMPLETAMENTE ABERTO!
- Use APENAS para testes locais
- NUNCA em produção
- Qualquer pessoa com a URL do banco pode ler/escrever tudo

---

## 📞 RESUMO RÁPIDO

**Para começar a usar AGORA:**
1. Firebase Console → Realtime Database → Regras
2. Cole as **Regras Simples (Opção 1)**
3. Clique em "Publicar"
4. Abra o app → Login → Gerenciar Funcionários
5. Crie funcionários à vontade
6. ✅ Pronto! Está funcionando

**Para deixar seguro depois:**
1. Certifique-se que tem ao menos 1 GERENTE criado
2. Volte nas Regras do Firebase
3. Cole as **Regras Seguras (Opção 2)**
4. Clique em "Publicar"
5. ✅ Agora está protegido!

---

**Versão:** 2.0  
**Última atualização:** Março 2026
