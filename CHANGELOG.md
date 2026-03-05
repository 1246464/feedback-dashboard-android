# Changelog - VisualizadorApp

Todas as alterações notáveis neste projeto serão documentadas neste arquivo.

## [2.1.0] - 2026-03-02

### 🎉 Sistema de Gestão de Pré-Preparo e Passagem de Turno

Esta atualização adiciona funcionalidades críticas para coordenação entre turnos da cozinha.

### ✨ Novas Funcionalidades

#### Gestão de Pré-Preparo
- **Gerenciamento de Tarefas**: Criação e acompanhamento de tarefas de preparação
- **Sistema de Prioridades**: 5 níveis de prioridade (1-Baixa até 5-Urgente)
- **Atribuição por Turno**: Tarefas organizadas por turno (Manhã/Tarde/Noite)
- **Fluxo de Trabalho**: Pendente → Em Andamento → Concluída
- **Rastreamento Completo**: Registro de responsável, hora de início e conclusão
- **Filtros Inteligentes**: Por status, turno, data
- **Indicadores Visuais**: Cores diferentes para prioridade e status
- **Contadores em Tempo Real**: Quantas tarefas pendentes/concluídas

#### Passagem de Turno Digital
- **Comunicação Estruturada**: Passagem de informações entre turnos
- **Tipos de Mensagem**: Informação, Alerta, Urgente
- **Campos Organizados**:
  - Tarefas Concluídas
  - Tarefas Pendentes
  - Problemas Encontrados
  - Mudanças de Cardápio
- **Sistema de Leitura**: Marca mensagens como lidas automaticamente
- **Notificações**: Contador de mensagens não lidas
- **Detecção Automática**: Sistema detecta turno atual baseado no horário
- **Histórico Completo**: Todas as passagens são registradas

#### Gestão de Ingredientes
- **Status de Ingredientes**: Disponível, Faltando, Parcial
- **Verificação por Data**: Ingredientes necessários para cada cardápio
- **Observações**: Campo para notas sobre ingredientes

#### Rastreamento de Mudanças
- **Histórico de Alterações**: Registro de todas as mudanças de cardápio
- **Notificação aos Turnos**: Sistema alerta sobre mudanças de última hora
- **Motivo da Mudança**: Campo para explicar por que mudou

### 🗄️ Banco de Dados

#### Novas Tabelas Room:
- `ingredientes`: Gerenciamento de ingredientes por cardápio
- `tarefas_preparo`: Tarefas de pré-preparo
- `passagem_turno`: Mensagens entre turnos
- `mudancas_cardapio`: Histórico de alterações

#### Versão do Banco: 1 → 2

### 🎨 Interface

#### Novas Telas:
- `GestaoPreparoActivity`: Interface para gerenciar tarefas de preparo
- `PassagemTurnoActivity`: Interface para comunicação entre turnos

#### Novos Componentes:
- `TarefaPreparoAdapter`: RecyclerView adapter para tarefas
- `PassagemTurnoAdapter`: RecyclerView adapter para passagens
- Dialogs customizados para criar tarefas e passagens
- Sistema de badges para mensagens não lidas

#### Melhorias Visuais:
- Indicadores coloridos de prioridade
- Backgrounds diferentes por status de tarefa
- Emojis para tipos de mensagem (ℹ️⚠️🚨)
- Badge de informações extras nas passagens

### 📱 Navegação

- Novos botões no painel administrativo:
  - 🔪 Gestão de Pré-Preparo
  - 📋 Passagem de Turno

### 🔄 Sincronização

- Sincronização automática Room ↔ Firebase para:
  - Ingredientes
  - Tarefas de Preparo
  - Passagens de Turno
  - Mudanças de Cardápio

### 📚 Documentação

- **GUIA_GESTAO_PREPARO.md**: Guia completo de uso do sistema
  - Instruções passo a passo
  - Cenários de uso
  - Melhores práticas
  - Solução de problemas
  - Métricas de benefícios

### 🐛 Correções

- Melhorada sincronização entre Room e Firebase
- Otimizadas queries do banco de dados

---

## [2.0.0] - 2026-03-02

### 🎉 Versão 2.0 - Grande Atualização!

Esta versão traz melhorias significativas em arquitetura, funcionalidades e experiência do usuário.

### ✨ Novas Funcionalidades

#### Sistema de Reservas
- **Reserva de Pratos**: Funcionários podem reservar pratos caso não consigam almoçar até 14:30
- Gerenciamento de status de reservas (Ativa, Utilizada, Cancelada)
- Notificações de lembrete para reservas
- Estatísticas de reservas para administradores

#### Histórico e Busca
- **Histórico Completo**: Visualização de todos os cardápios anteriores
- **Busca Avançada**: Busca por ingredientes ou tipos de pratos
- **Sistema de Favoritos**: Marcar cardápios preferidos
- Filtros por período (últimos 30 dias, favoritos, todos)

#### Imagens
- **Upload de Imagens**: Administradores podem fazer upload de fotos dos pratos
- Compressão automática de imagens
- Armazenamento no Firebase Storage

#### Exportação de Relatórios
- **Exportação em PDF**: Relatórios formatados e profissionais
- **Exportação em Excel**: Planilhas com estatísticas detalhadas
- Relatórios de cardápios, reservas e comentários
- Estatísticas automáticas incluídas

#### Notificações
- **Push Notifications**: Via Firebase Cloud Messaging
- Notificações de novos cardápios
- Lembretes de reservas
- Configuração personalizada de notificações

### 🎨 Melhorias de UI/UX

#### Dark Mode
- Tema escuro completo
- Tema claro
- Opção de seguir configuração do sistema
- Cores otimizadas para cada tema
- Persistência de preferência

#### Animações
- Transições suaves entre telas
- Animações de fade in/out
- Animações de slide
- Efeitos de shake para erros
- Animações de pulse para notificações
- Feedback visual em botões

#### Loading States
- Indicadores de carregamento
- Skeleton screens
- Feedback visual durante operações
- Estados vazios com mensagens amigáveis

#### Mensagens
- Mensagens de erro mais claras e user-friendly
- Tradução de erros técnicos para linguagem simples
- Snackbars com ações
- Diálogos de confirmação melhorados

### 🏗️ Arquitetura

#### MVVM Pattern
- Migração completa para arquitetura MVVM
- Separação clara de responsabilidades
- Código mais testável e manutenível

#### Room Database
- **Cache Local**: Banco de dados local com Room
- Funcionamento offline completo
- Sincronização automática com Firebase
- Queries otimizadas com LiveData

#### Repositories
- Camada de repositório para abstração de dados
- Gerenciamento centralizado de fontes de dados
- Sincronização bidirecional Firebase ↔ Room

#### ViewModels
- Gerenciamento de estado com ViewModels
- Sobrevive a mudanças de configuração
- Lógica de negócio separada da UI

### 🚀 Performance

#### Otimizações de Startup
- Inicialização assíncrona de recursos
- Tarefas em background
- Pré-carregamento de dados críticos
- Limpeza automática de dados antigos
- Monitor de performance de startup

#### Otimizações de Firebase
- Queries otimizadas com índices
- Cache de listeners
- Debouncing de eventos
- Paginação de dados
- Batch writes

#### Otimizações de Memória
- Lazy loading em listas
- Reciclagem de views
- Compressão de imagens
- Limpeza automática de cache antigo

### 📚 Bibliotecas Adicionadas

- `androidx.room:room-runtime:2.6.1` - Banco de dados local
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0` - ViewModel
- `com.google.firebase:firebase-messaging` - Notificações push
- `com.google.firebase:firebase-storage` - Armazenamento de imagens
- `com.github.bumptech.glide:glide:4.16.0` - Carregamento de imagens
- `org.apache.poi:poi-ooxml:5.2.3` - Exportação Excel
- `com.itextpdf:itext7-core:7.2.5` - Exportação PDF
- `com.airbnb.android:lottie:6.3.0` - Animações
- `androidx.swiperefreshlayout:swiperefreshlayout:1.1.0` - Pull to refresh
- `androidx.work:work-runtime:2.9.0` - Background tasks
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3` - Coroutines

### 🛠️ Utilitários Criados

- `ThemeManager` - Gerenciamento de temas
- `AnimationHelper` - Helper para animações
- `LoadingHelper` - Helper para loading states
- `MessageHelper` - Helper para mensagens
- `ImageUploadHelper` - Helper para upload de imagens
- `PdfExportHelper` - Helper para exportação PDF
- `ExcelExportHelper` - Helper para exportação Excel
- `FirebaseQueryOptimizer` - Otimizador de queries Firebase
- `AppStartupOptimizer` - Otimizador de startup

### 🔧 Configurações

#### Novas Permissões
- `POST_NOTIFICATIONS` - Notificações push
- `READ_EXTERNAL_STORAGE` - Leitura de arquivos
- `WRITE_EXTERNAL_STORAGE` - Escrita de arquivos
- `ACCESS_NETWORK_STATE` - Status da rede
- `CAMERA` - Câmera para fotos

#### Novas Activities
- `ConfiguracoesActivity` - Configurações do app
- `ReservaActivity` - Reserva de pratos
- `HistoricoActivity` - Histórico de cardápios

#### Novos Serviços
- `MyFirebaseMessagingService` - Serviço de notificações

### 📱 Compatibilidade

- Android 7.0+ (API 24+)
- Suporte total a RTL
- Temas responsivos
- Orientação portrait

### 🐛 Correções

- Melhoria na sincronização offline
- Correções de memory leaks
- Otimização de queries redundantes
- Melhoria no tratamento de erros

### 📝 Documentação

- README atualizado com todas as novas funcionalidades
- Documentação de código melhorada
- Comentários em classes principais
- Changelog criado

---

## [1.0.0] - 2024-XX-XX

### Versão Inicial

#### Funcionalidades Principais
- Login e Cadastro
- Visualização de cardápio
- Dashboard administrativo
- Comentários e avaliações
- Estatísticas com gráficos
- Cardápio semanal
- Sugestões

#### Tecnologias
- Firebase Authentication
- Firebase Realtime Database
- MPAndroidChart
- Material Design

---

## Formato

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

### Tipos de Mudanças

- `✨ Novas Funcionalidades` - para novas funcionalidades
- `🎨 Melhorias de UI/UX` - para melhorias de interface
- `🚀 Performance` - para melhorias de performance
- `🐛 Correções` - para correções de bugs
- `🔒 Segurança` - para correções de segurança
- `📚 Documentação` - para mudanças na documentação
- `🔧 Configurações` - para mudanças em configuração
