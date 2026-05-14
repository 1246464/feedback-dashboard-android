# 🔔 CONFIGURAÇÃO DE NOTIFICAÇÕES PUSH - Firebase Cloud Messaging (FCM)

## ✅ O QUE JÁ ESTÁ CONFIGURADO NO APP

- ✅ Serviço `MyFirebaseMessagingService` criado
- ✅ Registrado no `AndroidManifest.xml`
- ✅ Permissão `POST_NOTIFICATIONS` adicionada
- ✅ Canais de notificação criados
- ✅ `google-services.json` presente

---

## 🔧 O QUE VOCÊ PRECISA FAZER NO FIREBASE

### 📍 Passo 1: Acessar Firebase Cloud Messaging

1. Acesse: https://console.firebase.google.com
2. Selecione seu projeto: **insights-cardapio**
3. Menu esquerdo → **Cloud Messaging** (ou **Messaging**)

---

### 📍 Passo 2: Habilitar Firebase Cloud Messaging

1. Se ainda não estiver habilitado, clique em **"Habilitar Cloud Messaging"**
2. Se pedirr permissão de cobrança, aceite (é grátis para uso moderado)
3. Aguarde ativação (geralmente é imediato)

---

### 📍 Passo 3: Enviar uma Notificação de Teste

#### **Opção A: Pelo Console (Teste Rápido)**

1. Em **Cloud Messaging**, clique em **"Criar primeira campanha"** ou **"Nova campanha"**
2. Escolha: **"Notificações"** (não Campanhas com segmentação)
3. Preencha:
   - **Título:** "Teste de Notificação"
   - **Texto do corpo:** "Se vir isso, está funcionando! 🎉"
4. **Público-alvo:** 
   - Clique em **"Selecionar público"**
   - Escolha: **Android**
   - Selecione seu app: **com.example.visualizadorapp**
5. **Agendamento:** Deixe como "Agora"
6. Clique em **"Revisar"** → **"Publicar"**

#### **Resultado Esperado:**
Se o app estiver aberto no celular, você verá a notificação aparecer na parte superior da tela ou na bandeja de notificações.

---

### 📍 Passo 4: Ativar para Uso Programático (Importante!)

Se você quer enviar notificações **do seu código** (não apenas do console), precisa:

#### 🔑 Obter a Chave do Servidor (Server Key):

1. Firebase Console → Seu projeto → **Engrenagem (Configurações)** → **Configurações do projeto**
2. Aba: **Contas de serviço**
3. Clique em **"Python"** (ou sua linguagem, mas vamos usar a opção REST)
4. Clique em **"Gerar nova chave privada"** ou copie o **ID do projeto**

---

## 🎯 COMO O APP FUNCIONA COM NOTIFICAÇÕES

### 📱 Fluxo Atual:

1. **Usuário faz uma ação** (por exemplo: Reserva um prato)
   ```
   ReservaActivity.java → confirmarReserva()
   ```

2. **Código envia notificação local** (já funciona!)
   ```java
   NotificationHelper.notificarNovaReserva(context, nome, turno, prato)
   ```

3. **Código também poderia enviar notificação PUSH** (Firebase Cloud Messaging)
   ```
   Sua Function/Backend → Firebase Cloud Messaging → App
   ```

---

## 📨 TIPOS DE NOTIFICAÇÕES SUPORTADAS

O app reconhece automaticamente esses tipos de mensagens:

| Tipo | Ícone | Exemplo | Abre Activity |
|------|-------|---------|---------------|
| `novo_cardapio` | 📋 | "Novo cardápio publicado" | MainActivity |
| `reserva` | 🍽️ | "Lembrete: sua reserva de hoje" | MainActivity |
| `mudanca_cardapio` | ⚠️ | "Cardápio de hoje foi alterado" | DashboardPreparoActivity |
| `tarefa_pendente` | ✅ | "Você tem 3 tarefas pendentes" | DashboardPreparoActivity |
| `ingrediente_faltando` | 📦 | "Tomate está faltando" | GestaoIngredientesActivity |
| `passagem_turno` | 💬 | "Nova mensagem de passagem de turno" | PassagemTurnoActivity |

---

## 🧪 TESTE COMPLETO

### 1️⃣ Instalar APK no Celular
```bash
# Conectar seu celular via USB e executar:
adb install -r app/build/outputs/apk/release/app-release.apk
```

### 2️⃣ Fazer Login
1. Abra o app
2. Faça login com sua conta

### 3️⃣ Enviar Notificação de Teste (Console)
1. Firebase Console → Cloud Messaging
2. Clique em **"Nova campanha"** → **"Notificações"**
3. Preencha título e corpo
4. Selecione: **Android** → **com.example.visualizadorapp**
5. Publica
6. Verifique se a notificação aparece no celular

### ✅ Resultado:
- ✅ Notificação aparece na bandeja
- ✅ Clique abre a Activity correspondente
- ✅ Vibração e som funcionam

---

## 💡 INTEGRAÇÃO COM SEU CÓDIGO

Se você quiser que o app **envie notificações automaticamente** (quando usuário reserva, etc):

### Opção 1: Cloud Functions (Recomendado)
```javascript
// Firebase Cloud Function que se ativa quando reserva é criada
exports.notificarCozinha = functions.database
  .ref('reservas/{data}/{reservaId}')
  .onCreate(async (snapshot, context) => {
    const reserva = snapshot.val();
    
    // Envia notificação push para cozinheiros
    await admin.messaging().send({
      notification: {
        title: "🍽️ Nova Reserva!",
        body: `${reserva.nomeUsuario} reservou ${reserva.escolhaPrato}`
      },
      data: {
        tipo: "reserva",
        reservaId: context.params.reservaId
      },
      topic: "cozinha-" + reserva.turno // Envia para tópico
    });
  });
```

### Opção 2: Backend Próprio
Se tem um servidor Node.js/Python, use a biblioteca do Firebase Admin SDK:

**Node.js:**
```javascript
const admin = require('firebase-admin');

admin.messaging().send({
  notification: {
    title: "Novo Cardápio",
    body: "Cardápio de segunda foi publicado"
  },
  data: {
    tipo: "novo_cardapio"
  },
  topic: "todos"
});
```

---

## 🔍 VERIFICAR SE ESTÁ FUNCIONANDO

### No Celular:

1. **Abra o app**
2. Vá para: **Painel Admin** (ou dashboard)
3. Procure por mensagens de teste do Firebase
4. Verifique a bandeja de notificações

### No Logcat (Android Studio):

```bash
./gradlew installDebug
adb logcat | grep "FCM\|Messaging"
```

Você deve ver algo como:
```
D/FCM: Mensagem recebida: titulo=... body=...
D/MyFirebaseMessagingService: Notificação enviada
```

---

## ❌ PROBLEMAS COMUNS

### ❌ "Notificação não aparece"

**Causa 1:** App não tem permissão
- **Solução:** Vá em Configurações → Seu App → Notificações → Ativar

**Causa 2:** Google Play Services desatualizado
- **Solução:** Abra Play Store → Pesquise "Google Play Services" → Atualizar

**Causa 3:** FCM não está habilitado
- **Solução:** Verifique no Firebase Console se Cloud Messaging está ativado

### ❌ "Permissão negada ao enviar"

**Causa:** Chave de servidor incorreta
- **Solução:** Gere nova chave no Firebase Console

---

## 📋 CHECKLIST FINAL

Antes de considerar pronto:

- [ ] Firebase Cloud Messaging está habilitado
- [ ] Consegue enviar notificação de teste via console
- [ ] Notificação aparece no celular
- [ ] Clicando abre a Activity correta
- [ ] App está em desenvolvimento? Ative modo debug
- [ ] App está em produção? Desative testes com Device Token

---

## 📞 RESUMO RÁPIDO

**Para começar AGORA:**
1. Firebase Console → Cloud Messaging
2. Clique em **"Nova campanha"**
3. Escolha **"Notificações"**
4. Preencha título e corpo
5. Selecione seu app (Android)
6. Clique **"Publicar"**
7. Verifique notificação no celular ✅

---

**Versão:** 1.0  
**Última atualização:** Maio 2026
