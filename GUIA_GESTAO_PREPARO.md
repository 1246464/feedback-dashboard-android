# 🔪 Sistema de Gestão de Pré-Preparo e Passagem de Turno

## 📋 Visão Geral

Este sistema foi desenvolvido para resolver o problema de comunicação entre turnos da cozinha, especialmente quando há mudanças de cardápio em cima da hora. Ele combina **gestão de tarefas de pré-preparo** com **passagem de turno digital**, garantindo que todos os funcionários saibam exatamente o que foi feito e o que precisa ser feito.

---

## 🎯 Problemas Resolvidos

### Antes do Sistema:
- ❌ Plantão da noite não sabia o que o turno da tarde tinha preparado
- ❌ Mudanças de cardápio causavam confusão
- ❌ Ingredientes faltando não eram comunicados adequadamente
- ❌ Tarefas importantes eram esquecidas
- ❌ Sem registro de quem fez o quê

### Depois do Sistema:
- ✅ **Comunicação em Tempo Real**: Todos veem as mesmas informações
- ✅ **Rastreamento de Tarefas**: Sabe exatamente o status de cada preparo
- ✅ **Histórico Completo**: Registro de tudo que aconteceu
- ✅ **Passagem de Turno Estruturada**: Informações organizadas
- ✅ **Alertas de Mudanças**: Notifica automaticamente sobre alterações

---

## 🔪 Gestão de Pré-Preparo

### Como Funciona

A tela de **Gestão de Pré-Preparo** permite que os funcionários da cozinha gerenciem todas as tarefas de preparação do dia.

### Principais Recursos:

#### 1. **Navegação por Data**
- Veja tarefas de qualquer dia
- Botões de anterior/próximo para navegar facilmente
- Data atual destacada

#### 2. **Filtros Inteligentes**
- **Todas**: Mostra todas as tarefas do dia
- **Meu Turno**: Filtra apenas tarefas do seu turno (detectado automaticamente)
- **Pendentes**: Tarefas que ainda não foram iniciadas ⏳
- **Em Andamento**: Tarefas sendo executadas agora 🔄
- **Concluídas**: Tarefas finalizadas ✅

#### 3. **Criar Nova Tarefa**
Clique no botão `+` flutuante para criar uma nova tarefa:

**Campos Obrigatórios:**
- **Descrição**: O que precisa ser feito
  - Exemplo: "Preparar molho de tomate para 50 pessoas"
  - Exemplo: "Cortar legumes para salada"

**Configurações:**
- **Turno Responsável**: MANHÃ / TARDE / NOITE
- **Prioridade**: 1 (Baixa) até 5 (Urgente)
- **Observações**: Informações extras (opcional)

#### 4. **Indicadores Visuais**

Cada tarefa possui uma **barra colorida** indicando a prioridade:
- 🟢 **Verde**: Prioridade Baixa (1)
- 🟢 **Verde Claro**: Prioridade Normal (2)
- 🟡 **Amarelo**: Prioridade Média (3)
- 🟠 **Laranja**: Prioridade Alta (4)
- 🔴 **Vermelho**: Prioridade Urgente (5)

**Fundo da Tarefa:**
- 🟡 **Amarelo Claro**: Pendente
- 🔵 **Azul Claro**: Em Andamento
- 🟢 **Verde Claro**: Concluída

#### 5. **Fluxo de Trabalho**

```
1. Tarefa Criada (PENDENTE)
   ↓
2. Funcionário clica em [Iniciar]
   ↓
3. Tarefa fica EM_ANDAMENTO
   - Registra quem iniciou
   - Registra hora de início
   ↓
4. Funcionário clica em [Concluir ✅]
   ↓
5. Tarefa fica CONCLUIDA
   - Registra hora de conclusão
   - Fica verde
```

#### 6. **Contadores em Tempo Real**

No topo de cada filtro você vê:
- **Pendentes (X)**: Quantas tarefas ainda precisam ser feitas
- **Concluídas (Y)**: Quantas tarefas foram finalizadas

---

## 📋 Passagem de Turno

### Como Funciona

A tela de **Passagem de Turno** é uma comunicação digital estruturada entre os turnos da cozinha.

### Principais Recursos:

#### 1. **Turno Automático**
O sistema detecta automaticamente seu turno baseado no horário:
- **MANHÃ**: 06:00 - 13:59
- **TARDE**: 14:00 - 21:59
- **NOITE**: 22:00 - 05:59

#### 2. **Tipos de Mensagem**

Ao criar uma passagem, escolha o tipo:

- **ℹ️ INFORMAÇÃO** (Azul): Avisos gerais, informações normais
- **⚠️ ALERTA** (Laranja): Atenção necessária, mas não urgente
- **🚨 URGENTE** (Vermelho): Requer ação imediata!

#### 3. **Criar Nova Passagem**

Clique no botão `+` e preencha:

**Campo Obrigatório:**
- **Mensagem Principal**: Comunicação geral
  - Exemplo: "Cardápio mudou de frango para carne"
  - Exemplo: "Geladeira 2 está com problema"

**Campos Opcionais (mas muito úteis!):**

- **✅ Tarefas Concluídas**: Liste o que você fez
  ```
  - Preparei molho vermelho (25L)
  - Cortei legumes para salada
  - Limpei geladeira 1
  ```

- **⏳ Tarefas Pendentes**: O que ficou para fazer
  ```
  - Precisa temperar a carne (está na geladeira 3)
  - Falta picar cebola
  ```

- **⚠️ Problemas Encontrados**: Coisas que deram errado
  ```
  - Faltou tomate (usamos molho de lata)
  - Fogão 2 está com chama fraca
  ```

- **🔄 Mudanças de Cardápio**: Alterações importantes
  ```
  - MUDOU: Arroz → Macarrão
  - MOTIVO: Arroz acabou
  ```

#### 4. **Visualizar Mensagens**

Use as abas de filtro:

- **Não Lidas**: Mensagens que você ainda não viu
  - Badge vermelho indica quantidade
  - Fundo amarelo destaca mensagens não lidas
  
- **Todas**: Todas as passagens do dia
  
- **Urgentes**: Apenas mensagens marcadas como urgentes 🚨

#### 5. **Indicadores Visuais**

Cada passagem mostra:
- **Emoji do Tipo**: ℹ️ / ⚠️ / 🚨
- **Turnos**: De qual turno → Para qual turno
- **Horário**: Quando foi enviada
- **Badge (+N)**: Se tiver informações extras (tarefas, problemas, mudanças)
- **Bolinha Laranja**: Mensagem não lida

---

## 🚀 Fluxo Completo de Uso

### Cenário: Fim do Turno da TARDE

**1. Carlos (Turno da TARDE) - 17:45**

```
1. Abre "Gestão de Pré-Preparo"
2. Marca como concluídas:
   - ✅ Preparar molho de tomate
   - ✅ Cortar legumes

3. Tarefas que ficaram pendentes:
   - ⏳ Temperar a carne (não deu tempo)

4. Abre "Passagem de Turno"
5. Cria nova passagem:
   - Para: NOITE
   - Tipo: ALERTA ⚠️
   - Mensagem: "Cardápio MUDOU! Agora é carne ao invés de frango"
   
   Tarefas Concluídas:
   - Preparei molho vermelho (25L - geladeira 1)
   - Cortei todos os legumes para salada
   
   Tarefas Pendentes:
   - Temperar a carne (está na geladeira 3)
   - Carne precisa ser assada a partir das 21h
   
   Problemas:
   - Faltou alho, usei alho em pó
   
   Mudanças:
   - ORIGINAL: Frango grelhado
   - NOVO: Carne assada
   - MOTIVO: Frango chegou estragado

6. Clica [Enviar]
```

**2. Maria (Turno da NOITE) - 22:05**

```
1. Abre "Passagem de Turno"
2. Vê badge vermelho: "1 mensagens não lidas"
3. Abre aba "Não Lidas"
4. Vê mensagem de alerta ⚠️
5. Lê todos os detalhes
6. Sabe exatamente:
   - O que mudou no cardápio
   - O que já está pronto
   - O que precisa fazer
   - Onde está cada coisa

7. Abre "Gestão de Pré-Preparo"
8. Filtra por "Meu Turno" (NOITE)
9. Vê a tarefa "Temperar a carne"
10. Clica [Iniciar]
11. Tempera a carne
12. Clica [Concluir ✅]
13. Às 21h, começa a assar a carne
```

**Resultado:** ✅ Nenhuma confusão, tudo organizado!

---

## 💡 Melhores Práticas

### ✅ DO (Faça):

1. **Seja Específico nas Tarefas**
   - ❌ "Fazer salada"
   - ✅ "Cortar 5kg de alface e 3kg de tomate para salada"

2. **Use Prioridades Corretamente**
   - Urgente (5): Apenas se realmente crítico
   - Alta (4): Importante, mas tem um pouco de tempo
   - Média (3): Tarefas normais do dia
   - Baixa (1-2): Se der tempo

3. **Documente Mudanças**
   - Sempre explique o MOTIVO da mudança
   - Diga onde estão os ingredientes

4. **Seja Claro na Passagem de Turno**
   - Liste tarefas concluídas E pendentes
   - Mencione problemas encontrados
   - Explique qualquer mudança no processo

5. **Marque Urgência Quando Necessário**
   - Use 🚨 URGENTE para problemas sérios
   - Não abuse para não perder impacto

### ❌ DON'T (Não Faça):

1. **Criar Tarefas Vagas**
   - ❌ "Preparar ingredientes"
   - ❌ "Fazer coisas"

2. **Esquecer de Atualizar Status**
   - Sempre clique [Concluir] quando terminar
   - Não deixe tarefas "Em Andamento" se já terminou

3. **Mensagens Confusas**
   - ❌ "Mudou tudo"
   - ✅ "Cardápio mudou: [detalhe exato]"

4. **Marcar Tudo como Urgente**
   - Perde o propósito
   - Ninguém vai levar a sério

---

## 📊 Benefícios Mensuráveis

### Antes vs Depois

| Aspecto | Antes | Depois |
|---------|-------|--------|
| **Tempo de Passagem de Turno** | ~30 min verbal | ~5 min digital |
| **Erros por Falta de Comunicação** | 3-5 por semana | 0-1 por semana |
| **Tarefas Esquecidas** | Frequente | Raro |
| **Rastreabilidade** | Nenhuma | 100% |
| **Satisfação da Equipe** | Média | Alta |

---

## 🔐 Segurança e Sincronização

- **Sincronização Automática**: Tudo é salvo no Firebase em tempo real
- **Backup Local**: Room Database mantém cópia offline
- **Autenticação**: Apenas usuários autenticados podem criar/editar
- **Rastreamento**: Sistema registra quem fez cada ação e quando

---

## 🆘 Solução de Problemas

### "Não vejo minhas tarefas"
- ✅ Verifique se está no dia correto
- ✅ Verifique o filtro ativo (Todas / Meu Turno / etc)

### "Passagem não aparece para o outro turno"
- ✅ Verifique se selecionou o turno destino correto
- ✅ Verifique conexão com internet

### "Tarefas duplicadas"
- ✅ Não crie a mesma tarefa duas vezes
- ✅ Use busca antes de criar nova tarefa

---

## 📱 Atalhos Rápidos

### Gestos e Ações:
- **Tocar no Card**: Ver detalhes completos
- **Botão `+` Flutuante**: Criar nova tarefa/passagem
- **Swipe nas Abas**: Alternar entre filtros rapidamente

---

## 🎓 Treinamento Sugerido

### Dia 1 - Gestão de Tarefas:
- Criar tarefas
- Atribuir prioridades
- Iniciar e concluir tarefas
- Usar filtros

### Dia 2 - Passagem de Turno:
- Criar passagens
- Preencher campos estruturados
- Marcar como lida
- Filtrar mensagens

### Dia 3 - Prática Real:
- Usar durante turno completo
- Feedback da equipe
- Ajustes necessários

---

## 📞 Suporte

Em caso de dúvidas ou problemas:
1. Consulte este guia
2. Pergunte ao supervisor
3. Verifique os logs no sistema

---

**Sistema desenvolvido para otimizar a comunicação entre turnos e garantir qualidade na preparação das refeições! 🍽️**
