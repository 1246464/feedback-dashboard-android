# 🚀 COMECE AQUI - Filtro de Visibilidade de Cardápios

## 📦 O Que Você Recebeu

**Um sistema completo que impede usuários de ver cardápios que não são do seu turno.**

```
MANHA (06:00-18:00)         NOITE (18:00-06:00)         5X2 (07:00-17:00)
├─ ✅ Vê: TARDE             ├─ ✅ Vê: NOITE              ├─ ✅ Vê: TARDE
└─ ❌ Não vê: NOITE         └─ ❌ Não vê: TARDE         ├─ ❌ Não vê: NOITE
                                                         ├─ ❌ Não vê: fins de semana
                                                         └─ ❌ Não vê: >7 dias
```

---

## ⚡ Quick Start (5 minutos)

### Passo 1: Entender o Conceito
👉 Abra: **[RESUMO_FILTRO_CARDAPIOS.md](RESUMO_FILTRO_CARDAPIOS.md)**
- Leia a seção "3 Regras Simples" (1 minuto)
- Veja os exemplos "Como Usar" (2 minutos)

### Passo 2: Copiar Código
👉 Abra: **[INTEGRACAO_FILTRO_CARDAPIOS.md](INTEGRACAO_FILTRO_CARDAPIOS.md)**
- Procure seu Activity (MainActivity, DashboardCozinha, etc)
- Copie o snippet do código
- Cole no seu Android Studio

### Passo 3: Compilar e Testar
- Compile o projeto (Ctrl+B)
- Se tiver erro, vá para "Troubleshooting" abaixo
- Teste com um usuário MANHA - não deve ver NOITE

**Pronto! ✅**

---

## 📂 Arquivos Criados

### Classes Java

#### ✨ FiltroVisibilidadeCardapio.java
```
Localização: app/src/main/java/.../utils/FiltroVisibilidadeCardapio.java
Tamanho: ~350 linhas
Função: Verificar se usuário pode ver cardápio
Testes: 20+ testes unitários (vejo abaixo)
```

**Método principal:**
```java
boolean podeVer = FiltroVisibilidadeCardapio.podeVerCardapio(
    turnoUsuario,        // "MANHA", "NOITE" ou "5X2"
    horarioUsuario,      // "06:00-18:00", etc
    turnoCardapio,       // "TARDE" ou "NOITE"
    dataCardapio,        // "2026-05-15"
    diasTrabalho         // null para MANHA/NOITE, int[] para 5X2
);
```

#### ✨ CardapiosViewModel.java
```
Localização: app/src/main/java/.../viewmodel/CardapiosViewModel.java
Tamanho: ~250 linhas
Função: Integração com UI (LiveData)
```

**Como usar:**
```java
CardapiosViewModel vm = new ViewModelProvider(this).get(CardapiosViewModel.class);
vm.getCardapiosVisiveis().observe(this, cardapios -> {
    adapter.setData(cardapios);  // RecyclerView atualiza automático
});
vm.carregarCardapiosParaUsuario();
```

### Testes

#### ✅ FiltroVisibilidadeCardapioTest.java
```
Localização: app/src/test/java/.../utils/FiltroVisibilidadeCardapioTest.java
Tamanho: ~350 linhas
Testes: 20+ casos

Rodar com:
Clique direito em FiltroVisibilidadeCardapioTest → Run
```

### Documentação

| Arquivo | Tempo | Propósito |
|---------|-------|----------|
| **RESUMO_FILTRO_CARDAPIOS.md** | 5 min | Quick start rápido |
| **GUIA_FILTRO_VISIBILIDADE_CARDAPIOS.md** | 15 min | Tudo explicado em detalhes |
| **INTEGRACAO_FILTRO_CARDAPIOS.md** | 5 min | Copiar snippets prontos |
| **ENTREGA_FILTRO_VISIBILIDADE_CARDAPIOS.md** | 10 min | Sumário visual e próximos passos |

---

## 🎯 Próximas Ações

### Se você ainda NÃO integrou em nenhuma Activity:

```
1. Abra INTEGRACAO_FILTRO_CARDAPIOS.md
2. Procure sua Activity (MainActivity, etc)
3. Copie o snippet de código
4. Cole no Android Studio
5. Compile
6. Pronto!
```

### Se você JÁ integrou em algumas Activities:

```
1. Integre nos demais (lista em INTEGRACAO_FILTRO_CARDAPIOS.md)
2. Teste cada uma com usuários de turno diferente
3. Se tudo passar, você terminou!
```

### Se você quer ENTENDER MELHOR:

```
1. Leia GUIA_FILTRO_VISIBILIDADE_CARDAPIOS.md (15 min)
2. Veja os testes em FiltroVisibilidadeCardapioTest.java
3. Entenda a regra do seu turno específico
```

---

## 🧪 Checklist de Integração

Para cada Activity que você integrar:

- [ ] Importou `CardapiosViewModel`?
- [ ] Importou `FiltroVisibilidadeCardapio`?
- [ ] Declarou `private CardapiosViewModel cardapiosViewModel;`?
- [ ] Inicializou no `onCreate()`?
- [ ] Observou `getCardapiosVisiveis()`?
- [ ] Chamou `carregarCardapiosParaUsuario()`?
- [ ] Compilou sem erros?
- [ ] Testou no emulador?

**Depois de completar ✅ todos acima**, você terminou a integração!

---

## ❌ Troubleshooting

### "Classe não encontrada: CardapiosViewModel"
→ Certifique-se que CardapiosViewModel.java está em: 
   `app/src/main/java/com/example/visualizadorapp/viewmodel/`
→ Verifique o import no seu Activity

### "Método podeVerCardapio() não encontrado"
→ Certifique-se que FiltroVisibilidadeCardapio.java está em:
   `app/src/main/java/com/example/visualizadorapp/utils/`
→ Verifique o import no seu Activity

### "Erro: Cannot resolve symbol 'ViewModelProvider'"
→ Certifique-se que tem a dependência no `build.gradle`:
   ```gradle
   implementation "androidx.lifecycle:lifecycle-viewmodel:2.5.1"
   ```

### "NullPointerException no onCreate()"
→ Certifique-se que está chamando:
   ```java
   cardapiosViewModel = new ViewModelProvider(this)
       .get(CardapiosViewModel.class);
   ```

### "RecyclerView não atualiza"
→ Certifique-se que está observando:
   ```java
   vm.getCardapiosVisiveis().observe(this, cardapios -> {
       adapter.setData(cardapios);  // Aqui faz o update
   });
   ```

---

## 📚 Se Quiser Aprender Mais

### Nível 1: Entendimento Rápido (15 min)
1. RESUMO_FILTRO_CARDAPIOS.md
2. INTEGRACAO_FILTRO_CARDAPIOS.md

### Nível 2: Entendimento Completo (30 min)
1. GUIA_FILTRO_VISIBILIDADE_CARDAPIOS.md
2. ENTREGA_FILTRO_VISIBILIDADE_CARDAPIOS.md

### Nível 3: Entendimento Profundo (60 min)
1. Ler FiltroVisibilidadeCardapio.java (código)
2. Ler CardapiosViewModel.java (código)
3. Ler FiltroVisibilidadeCardapioTest.java (testes)

---

## 🎓 O Que Você Vai Conseguir

Após integrar:

✅ Usuário MANHA **não vê mais** cardápio NOITE
✅ Usuário NOITE **não vê mais** cardápio TARDE
✅ Usuário 5X2 **não vê** cardápio de fins de semana
✅ Usuário 5X2 **não vê** cardápio de >7 dias à frente
✅ **Automaticamente** - sem fazer nada manual
✅ **Filtra antes de mostrar** - não precisa remover de lista

---

## 💡 Dicas Pro

### 1️⃣ Use ViewModel, não código direto
```java
// ✅ CORRETO
CardapiosViewModel vm = new ViewModelProvider(this).get(CardapiosViewModel.class);
vm.carregarCardapiosParaUsuario();

// ❌ NÃO RECOMENDADO
List<CardapioTurno> lista = database.getCardapios();
```

### 2️⃣ Sempre observe LiveData, não chame getValue()
```java
// ✅ CORRETO - Atualiza automaticamente
vm.getCardapiosVisiveis().observe(this, cardapios -> {
    adapter.setData(cardapios);
});

// ❌ ERRADO - Só executa uma vez
adapter.setData(vm.getCardapiosVisiveis().getValue());
```

### 3️⃣ Teste com múltiplos usuários
```
- 1 usuario MANHA
- 1 usuario NOITE  
- 1 usuario 5X2
- Teste cada um vendo seu cardápio
```

### 4️⃣ Verifique logs no Logcat
```
Procure por: FiltroVisibilidadeCardapio
Procure por: CardapiosViewModel
```

---

## 🚀 Próximos Passos (Após Integração)

### Curto Prazo
- [ ] Integrar em todas as 7 activities
- [ ] Testar com dados reais
- [ ] Fazer ajustes finais

### Médio Prazo
- [ ] Adicionar mensagens visuais ("Não disponível para seu turno")
- [ ] Adicionar descrição das regras na UI
- [ ] Notificar usuário quando novo cardápio disponível

### Longo Prazo
- [ ] Histórico de cardápios visualizados
- [ ] Favoritos por turno
- [ ] Substituições e ajustes de cardápio

---

## 📞 Precisa de Ajuda?

### Dúvida sobre como usar?
→ Veja RESUMO_FILTRO_CARDAPIOS.md seção "Como Usar"

### Dúvida sobre as regras?
→ Veja GUIA_FILTRO_VISIBILIDADE_CARDAPIOS.md seção "As 3 Regras"

### Dúvida sobre qual arquivo copiar?
→ Veja INTEGRACAO_FILTRO_CARDAPIOS.md procure sua Activity

### Erro ao compilar?
→ Veja a seção "Troubleshooting" acima

### Quero entender o código?
→ Abra FiltroVisibilidadeCardapio.java e veja os comentários

---

## 📊 Status Atual

```
✅ Classe FiltroVisibilidadeCardapio.java      PRONTO
✅ Testes unitários                            PRONTO
✅ ViewModel CardapiosViewModel.java           PRONTO
✅ Documentação completa                       PRONTO
🔄 Integração nas Activities                  VOCÊ FAZ AGORA
```

**Tempo para integrar: ~30 minutos por activity**

---

## 🎯 1️⃣ Seu Próximo Passo

```
1. Abra: INTEGRACAO_FILTRO_CARDAPIOS.md
2. Procure: MainActivity
3. Copie: snippet de código
4. Cole: no seu Android Studio
5. Compile: Ctrl+B
6. Pronto!
```

---

**Versão**: 1.0  
**Data**: Maio 2026  
**Status**: ✅ PRONTO PARA USAR

Boa sorte! 🚀
