# 👥 GUIA DE GESTÃO DE FUNCIONÁRIOS

## 📱 Como Acessar (Para Gerente)

1. Faça login no app com sua conta de **GERENTE**
2. No Painel Administrativo, clique no botão **"👥 Gerenciar Funcionários"**
3. Você verá a lista de todos os funcionários cadastrados

---

## ➕ CRIAR NOVO FUNCIONÁRIO

### Passo a Passo:

1. Clique no botão **"+ Novo Funcionário"** (canto superior direito)
2. Preencha os dados:

   **Campos Obrigatórios:**
   - **Nome Completo**: Nome do funcionário (ex: Maria Silva)
   - **Email**: Email corporativo (será usado para login)
   - **Senha**: Mínimo 6 caracteres (o funcionário poderá alterar depois)
   - **Cargo**: Selecione no dropdown

   **Campos Opcionais (dependem do cargo):**
   - **Plantão**: A ou B (aparece automaticamente para cargos 12x36)
   - **Horário**: Ex: "10-22", "19-07", "5x2"
   - **Setor**: Ex: "Cozinha Central", "Refeitório Principal"

3. Clique em **"Criar"**
4. O sistema criará automaticamente:
   - ✅ Conta de acesso (Authentication)
   - ✅ Perfil no banco de dados
   - ✅ Permissões conforme o cargo

### Exemplo Prático:

```
Nome: João Pedro Santos
Email: joao.santos@empresa.com
Senha: senha123
Cargo: COZINHEIRO
Plantão: A
Horário: 10-22
Setor: Cozinha Central
```

---

## ✏️ EDITAR CARGO/DADOS DE FUNCIONÁRIO

### Quando usar:
- Funcionário foi promovido
- Mudou de plantão (A ↔ B)
- Mudou de horário
- Foi transferido de setor

### Passo a Passo:

1. Na lista de funcionários, localize o funcionário
2. Clique no botão **"✏️ Editar"**
3. Altere os campos desejados:
   - **Cargo**: Selecione o novo cargo
   - **Plantão**: Mude entre A/B (se aplicável)
   - **Horário**: Atualize o horário
   - **Setor**: Novo setor

4. Clique em **"Salvar"**
5. ✅ As mudanças são aplicadas imediatamente
6. ⚠️ No próximo login, o funcionário verá seu novo dashboard

### Exemplo de Promoção:

**Antes:**
```
AUXILIAR → Plantão A (10-22)
```

**Depois:**
```
COZINHEIRO → Plantão A (10-22)
```
✅ O funcionário agora tem permissões de Cozinheiro!

---

## 🗑️ DELETAR FUNCIONÁRIO

### ⚠️ ATENÇÃO - Ação Irreversível!

**Quando usar:**
- Funcionário foi demitido
- Conta duplicada ou criada por erro
- Funcionário não trabalha mais na empresa

### Passo a Passo:

1. Na lista de funcionários, localize o funcionário
2. Clique no botão **"🗑️ Deletar"**
3. Leia o aviso de confirmação:
   ```
   ⚠️ ATENÇÃO: Esta ação é irreversível e removerá:
   • A conta de acesso ao sistema
   • Todos os dados do Firebase
   • Histórico de ações
   ```
4. Clique em **"Deletar"** para confirmar ou **"Cancelar"** para desistir
5. ✅ O funcionário é removido do sistema

### ⚠️ IMPORTANTE:
- O funcionário **não conseguirá mais fazer login**
- Todos os dados dele serão apagados
- Históricos de votos/comentários podem ser afetados
- **NÃO TEM COMO DESFAZER!**

---

## 📋 CARGOS DISPONÍVEIS

### Hierarquia e Dashboards:

| Cargo | Tipo | Dashboard | Descrição |
|-------|------|-----------|-----------|
| **GERENTE** | Admin | Painel Administrativo | Acesso total ao sistema |
| **TECNICA** | Supervisão | Dashboard Técnica | Técnica em Nutrição (12x36) |
| **LIDER_COZINHA** | Gestão | Dashboard Líder | Líder de Cozinha (5x2) |
| **COZINHEIRO** | Produção | Dashboard Cozinha | Equipe de cozinha (12x36) |
| **AUXILIAR** | Produção | Dashboard Cozinha | Equipe de cozinha (12x36) |
| **MEIO_OFICIAL_PLANTAO** | Produção | Dashboard Cozinha | Plantão 12x36 (10-22) |
| **MEIO_OFICIAL_5X2** | Produção | Dashboard Cozinha | Escala 5x2 |
| **ESTOQUISTA** | Logística | Dashboard Estoque | Gestão de estoque (5x2) |
| **COPEIRA** | Serviço | Dashboard Copeiragem | Plantão 12x36 (10-22) |
| **COPEIRA_5X2** | Serviço | Dashboard Copeiragem | Escala 5x2 |
| **COPEIRO_NOTURNO** | Serviço | Dashboard Copeiragem | Noturno (19-07) |
| **USUARIO_COMUM** | Básico | Tela de Reservas | Apenas reservar prato |

---

## 🔐 PERMISSÕES POR CARGO

### GERENTE
✅ Tudo (painel completo de admin)

### TECNICA (Técnica em Nutrição - 12x36)
✅ Pré-Preparo (criar tarefas)  
✅ Passagem de Turno  
✅ Ingredientes (controlar estoque)  
✅ Dashboard de Tarefas  
✅ Histórico  

### LIDER_COZINHA (5x2)
✅ Tudo da Técnica  
✅ Reservas  
✅ Planejamento Semanal  

### COZINHEIRO / AUXILIAR / MEIO_OFICIAL
✅ Cardápio (visualizar)  
✅ Tarefas de Pré-Preparo (executar)  
✅ Passagem de Turno (registrar)  
✅ Ingredientes (consultar apenas)  

### ESTOQUISTA (5x2)
✅ Cardápio (para planejamento de compras)  
✅ Ingredientes (gestão completa)  
✅ Lista de Compras Semanal  
✅ Visão Semanal  

### COPEIRA / COPEIRA_5X2 / COPEIRO_NOTURNO
✅ Cardápio (para servir)  
✅ Reservas (ver quem reservou)  
✅ Visão Semanal  

### USUARIO_COMUM
✅ Reservar Prato (escolher ovo/peixe/frango)  
✅ Ver Cardápio do Dia  

---

## 📱 FLUXO DE ACESSO DO FUNCIONÁRIO

### Como o funcionário faz login pela primeira vez:

1. **Gerente cria a conta** (ex: joao.santos@empresa.com / senha123)
2. **Gerente informa ao funcionário**: Email e senha inicial
3. **Funcionário abre o app** e clica em "Entrar"
4. **Digita email e senha** fornecidos pela gerente
5. **Sistema redireciona automaticamente** para o dashboard correto baseado no cargo
6. **Funcionário pode usar as funcionalidades** do seu cargo

### Exemplo Real:

```
Gerente cadastra:
Nome: Maria Técnica
Email: maria.tecnica@empresa.com
Senha: maria2024
Cargo: TECNICA
Plantão: A
```

```
Maria faz login:
1. Digite: maria.tecnica@empresa.com + maria2024
2. Sistema detecta cargo = TECNICA
3. Redireciona para: Dashboard Técnica
4. Maria vê: Pré-Preparo, Passagem Turno, Ingredientes, Dashboard, Histórico
```

---

## ❓ PERGUNTAS FREQUENTES

### 1. Posso alterar o email de um funcionário?
**NÃO.** O email é a identificação única no Firebase Authentication. Para alterar:
1. Delete o funcionário antigo
2. Crie um novo com o email correto

### 2. E se eu esquecer a senha de um funcionário?
Você pode:
- **Opção 1**: Deletar e recriar a conta
- **Opção 2**: O funcionário pode usar "Esqueci minha senha" na tela de login

### 3. Posso mudar um funcionário de COPEIRA para COZINHEIRO?
**SIM!** Use o botão "✏️ Editar" e selecione o novo cargo.

### 4. O que acontece quando mudo o cargo?
- ✅ No próximo login, o funcionário vê o dashboard novo
- ✅ Ele perde acesso às funções do cargo anterior
- ✅ Ele ganha acesso às funções do cargo novo

### 5. Plantão é obrigatório?
- **SIM** para: TECNICA, COZINHEIRO, AUXILIAR, MEIO_OFICIAL_PLANTAO, COPEIRA
- **NÃO** para: LIDER_COZINHA, MEIO_OFICIAL_5X2, ESTOQUISTA, COPEIRA_5X2
- **NÃO** para: GERENTE, COPEIRO_NOTURNO, USUARIO_COMUM

### 6. Quantos funcionários posso cadastrar?
**Ilimitado!** Não há restrição de quantidade.

### 7. Posso ter 2 gerentes?
**SIM!** Você pode criar múltiplas contas com cargo GERENTE.

### 8. Como faço para um funcionário trocar de plantão (A ↔ B)?
1. Localize o funcionário na lista
2. Clique em "✏️ Editar"
3. Mude o Plantão de A para B (ou vice-versa)
4. Salvar

### 9. O que significa "5x2"?
- **5x2** = Trabalha 5 dias, folga 2 dias
- **12x36** = Trabalha 12 horas, folga 36 horas (Plantão A/B)

### 10. Posso ver quantos funcionários tenho de cada cargo?
Atualmente não, mas a lista mostra todos os funcionários com seus cargos visíveis.

---

## 🚨 PROBLEMAS COMUNS

### "Erro ao criar conta: email já existe"
**Causa:** Já existe uma conta com esse email  
**Solução:** Use outro email ou delete a conta antiga

### "Funcionário não consegue fazer login"
**Verificar:**
1. Email está correto? (sem espaços extras)
2. Senha tem no mínimo 6 caracteres?
3. Funcionário foi realmente criado? (verifique na lista)

### "Funcionário vê tela errada após login"
**Causa:** Cargo pode estar incorreto  
**Solução:** Edite o funcionário e corrija o cargo

### "Campo Plantão não aparece"
**Causa:** Cargo selecionado não é 12x36  
**Solução:** Normal! Plantão só aparece para cargos de plantão

---

## 💡 DICAS IMPORTANTES

1. **Crie senhas temporárias simples** (ex: nome2024) e peça ao funcionário para trocar depois
2. **Use emails corporativos padronizados** (ex: nome.sobrenome@empresa.com)
3. **Sempre confirme o cargo** antes de criar (evita ter que editar depois)
4. **Anote as senhas** que você cria em um local seguro (até entregar ao funcionário)
5. **Teste o login** de um novo funcionário antes de entregar as credenciais
6. **Explique ao funcionário** qual é o dashboard dele e o que ele pode fazer

---

## 📞 SUPORTE

Em caso de dúvidas ou problemas, consulte o desenvolvedor do sistema.

**Versão:** 2.0  
**Última atualização:** 2024
