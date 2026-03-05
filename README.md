# VisualizadorApp

Aplicativo Android para coleta e análise de feedback de usuários com dashboard administrativo.

## 📱 Sobre o Projeto

Sistema completo de gerenciamento que permite coletar avaliações, visualizar estatísticas em tempo real e gerenciar usuários. Desenvolvido como projeto de portfólio para demonstrar habilidades em desenvolvimento Android nativo com arquitetura MVVM e boas práticas de desenvolvimento.

## 🚀 Funcionalidades

### Autenticação e Usuários
- 🔐 Sistema de autenticação completo (login, cadastro, recuperação de senha)
- 👥 Gerenciamento de usuários com diferentes níveis de acesso
- ⚙️ Tela de configurações personalizáveis

### Cardápios
- 📋 Visualização do cardápio do dia
- 📅 Cardápio semanal
- 🔍 **Busca avançada** de cardápios por ingrediente ou prato
- 📖 **Histórico completo** de cardápios anteriores
- ⭐ **Sistema de favoritos** para salvar cardápios preferidos
- 📸 **Upload de imagens** dos pratos (administradores)

### Reservas
- 🍽️ **Sistema de reserva de pratos** para funcionários que não podem almoçar até 14:30
- 📱 Notificações de lembrete de reservas
- ✅ Gerenciamento de status (Ativa, Utilizada, Cancelada)
- 📊 Estatísticas de reservas para administradores

### Gestão de Pré-Preparo e Passagem de Turno (NOVO! 🔥)
- 🔪 **Gestão de Tarefas de Pré-Preparo**
  - Criação e acompanhamento de tarefas de preparação
  - Sistema de prioridades (1-Baixa até 5-Urgente)
  - Atribuição por turno (Manhã/Tarde/Noite)
  - Fluxo: Pendente → Em Andamento → Concluída
  - Rastreamento completo (responsável, horários)
  - Filtros inteligentes e contadores em tempo real

- 📋 **Passagem de Turno Digital**
  - Comunicação estruturada entre turnos da cozinha
  - Tipos de mensagem: Informação / Alerta / Urgente
  - Campos organizados: Tarefas concluídas/pendentes, problemas, mudanças
  - Sistema de leitura e notificações
  - Detecção automática de turno
  - Histórico completo de passagens

- 📦 **Gestão de Ingredientes**
  - Status de ingredientes (Disponível/Faltando/Parcial)
  - Verificação por data
  - Observações e notas

- 🔄 **Rastreamento de Mudanças**
  - Histórico de alterações de cardápio
  - Notificações automáticas sobre mudanças
  - Documentação de motivos

### Feedback e Análise
- 💬 Sistema de comentários e feedback
- 📊 Dashboard com gráficos e estatísticas em tempo real
- 📈 Análise de dados com gráficos interativos (Pizza, Barras, Linhas)
- 💡 Sistema de sugestões

### Exportação e Relatórios (NOVO! 📄)
- 📄 **Exportação de relatórios em PDF**
- 📊 **Exportação de relatórios em Excel**
- 📈 Estatísticas detalhadas de cardápios, reservas e comentários

### Recursos Técnicos
- 🔄 Sincronização em tempo real com Firebase
- 💾 **Cache local com Room Database** para funcionar offline
- 🔔 **Notificações push** via Firebase Cloud Messaging
- 🎨 **Dark Mode** completo (Claro/Escuro/Sistema)
- ✨ **Animações e transições** suaves
- ⏳ **Loading states** e skeleton screens
- 🛡️ **Mensagens de erro** claras e intuitivas
- 🚀 **Performance otimizada** com lazy loading e cache
- 📱 Interface moderna e intuitiva com Material Design 3

## 🏗️ Arquitetura

O projeto utiliza **arquitetura MVVM (Model-View-ViewModel)** com as seguintes camadas:

```
app/
├── model/              # Entidades Room e modelos de dados
├── viewmodel/          # ViewModels para gerenciamento de estado
├── repository/         # Repositórios para acesso a dados
├── database/           # Room Database e DAOs
├── service/            # Serviços (Firebase Messaging, etc.)
├── adapter/            # Adapters para RecyclerView
├── utils/              # Classes utilitárias
└── activities/         # Activities da aplicação
```

### Tecnologias Utilizadas

- **Linguagem:** Java
- **Arquitetura:** MVVM
- **Banco de Dados Local:** Room Database v2 (7 entidades)
  - `Cardapio`: Cardápios e pratos
  - `Reserva`: Reservas de pratos
  - `Comentario`: Feedback dos usuários
  - `Ingrediente`: Gestão de ingredientes
  - `TarefaPreparo`: Tarefas de pré-preparo
  - `PassagemTurno`: Comunicação entre turnos
  - `MudancaCardapio`: Histórico de mudanças
- **Backend:** Firebase (Authentication, Realtime Database, Storage, Cloud Messaging)
- **UI:** Material Design 3
- **Gráficos:** MPAndroidChart
- **Exportação:** Apache POI (Excel), iText (PDF)
- **Imagens:** Glide
- **Animações:** Lottie
- **Async:** Coroutines, ExecutorService

## 📸 Screenshots

<div align="center">
  <img src="screenshots/painel_admin.png" width="250" alt="Painel Administrativo">
  <img src="screenshots/atualizar_cardapio.png" width="250" alt="Atualização de Cardápio">
  <img src="screenshots/cardapio_dia.png" width="250" alt="Cardápio do Dia">
</div>

## 📋 Pré-requisitos

- Android Studio Arctic Fox ou superior
- JDK 17 ou superior
- Conta no Firebase
- Android API 24+ (Android 7.0+)

## ⚙️ Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/SEU_USUARIO/VisualizadorApp.git
cd VisualizadorApp
```

### 2. Configure o Firebase

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
2. Adicione um aplicativo Android com o package name: `com.example.visualizadorapp`
3. Ative os seguintes serviços:
   - Authentication (Email/Password)
   - Realtime Database
   - Cloud Storage
   - Cloud Messaging
4. Baixe o arquivo `google-services.json`
5. Coloque o arquivo em `app/google-services.json`

### 3. Configure as variáveis locais

Crie o arquivo `local.properties` na raiz do projeto com o seguinte conteúdo:

```properties
sdk.dir=CAMINHO_DO_SEU_ANDROID_SDK
```

### 4. (Opcional) Gere sua keystore para releases

```bash
keytool -genkey -v -keystore app/my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

## 🏗️ Build

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## 📱 Instalação

Após o build, o APK estará em:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 🎯 Funcionalidades Implementadas (Versão 2.0)

### ✅ Novas Funcionalidades
- [x] Sistema de reserva de pratos
- [x] Upload de imagens dos pratos
- [x] Histórico completo de cardápios
- [x] Busca avançada
- [x] Sistema de favoritos
- [x] Exportação de relatórios (PDF/Excel)
- [x] Notificações push

### ✅ Melhorias de UI/UX
- [x] Dark Mode completo
- [x] Animações e transições suaves
- [x] Loading states e skeleton screens
- [x] Mensagens de erro mais claras
- [x] Navegação com gestos

### ✅ Otimizações de Performance
- [x] Cache local com Room Database
- [x] Lazy loading de listas
- [x] Queries Firebase otimizadas
- [x] Startup otimizado do app
- [x] Gerenciamento de memória

## 🔒 Segurança

**⚠️ IMPORTANTE:** Nunca compartilhe ou faça commit dos seguintes arquivos:
- `google-services.json` - Contém chaves de API do Firebase
- `local.properties` - Contém configurações locais
- `*.keystore` ou `*.jks` - Arquivos de assinatura do app
- `keystore-info.txt` - Informações de credenciais

Esses arquivos já estão listados no `.gitignore` e não serão incluídos no repositório.

## 📚 Documentação Adicional

Para mais informações sobre o desenvolvimento e uso do sistema:

### Guias de Uso:
- [GUIA_DE_USO.md](GUIA_DE_USO.md) - Guia completo de uso geral do aplicativo
- [GUIA_GESTAO_PREPARO.md](GUIA_GESTAO_PREPARO.md) - Guia completo do sistema de pré-preparo e passagem de turno
- [GUIA_RAPIDO_PREPARO.md](GUIA_RAPIDO_PREPARO.md) - Guia de referência rápida (para impressão)
- [MELHORES_PRATICAS.md](MELHORES_PRATICAS.md) - Melhores práticas de desenvolvimento
- [CHANGELOG.md](CHANGELOG.md) - Histórico de alterações

### Referências Externas:
- [Documentação do Firebase](https://firebase.google.com/docs)
- [Material Design 3](https://m3.material.io/)
- [Android Developers](https://developer.android.com/)

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 👨‍💻 Autor

Desenvolvido como projeto de portfólio para demonstrar habilidades em:
- Desenvolvimento Android Nativo
- Arquitetura MVVM
- Firebase Integration
- Material Design
- Boas práticas de código
- Performance e otimização

---

⭐ Se este projeto foi útil para você, considere dar uma estrela!

## 👤 Autor

**Maicon** - Desenvolvedor Android

- GitHub: [@seu-usuario](https://github.com/seu-usuario)
- LinkedIn: [Seu perfil](https://linkedin.com/in/seu-perfil)

---

💡 **Nota:** Este projeto foi desenvolvido como parte do meu portfólio para demonstrar habilidades em:
- Desenvolvimento Android (Java)
- Integração com Firebase (Authentication, Realtime Database)
- Criação de interfaces modernas
- Visualização de dados com gráficos
- Arquitetura de aplicativos Android
