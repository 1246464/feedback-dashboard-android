# Guia de Uso - VisualizadorApp

Este guia apresenta as principais funcionalidades do aplicativo e como utilizá-las.

## 📚 Índice
1. [Para Usuários](#para-usuários)
2. [Para Administradores](#para-administradores)
3. [Para Desenvolvedores](#para-desenvolvedores)

---

## Para Usuários

### Primeiro Acesso

1. **Criar Conta**
   - Abra o app
   - Toque em "Cadastrar"
   - Preencha nome, email e senha
   - Confirme a senha
   - Toque em "Cadastrar"

2. **Fazer Login**
   - Digite seu email
   - Digite sua senha
   - Toque em "Entrar"

### Visualizar Cardápio

- Na tela principal, você verá o cardápio do dia
- Informações exibidas:
  - 🍗 Prato Principal
  - 🍖 Guarnição
  - 🍚 Acompanhamento
  - 🥗 Salada
  - 🍮 Sobremesa

### Fazer Reserva de Prato

**Quando usar:** Se você não pode almoçar até 14:30

1. Toque no botão "Reservar Prato"
2. Visualize o cardápio do dia
3. Adicione uma observação (opcional)
4. Toque em "Fazer Reserva"
5. Confirme a reserva

**Importante:**
- Reservas podem ser feitas até às 18:00
- Você pode cancelar sua reserva a qualquer momento
- Receberá notificação de lembrete

### Ver Histórico de Cardápios

1. Acesse o menu principal
2. Toque em "Histórico"
3. Use os filtros:
   - **Todos**: Todos os cardápios
   - **Favoritos**: Apenas seus favoritos
   - **Recentes**: Últimos 30 dias
4. Para buscar: Toque no ícone de lupa
5. Para favoritar: Toque na estrela ao lado do cardápio

### Deixar Comentário

1. Acesse "Comentários"
2. Toque em "Novo Comentário"
3. Escolha uma avaliação (1-5 estrelas)
4. Escreva seu comentário
5. Toque em "Enviar"

### Configurações

1. Acesse o menu
2. Toque em "Configurações"
3. Opções disponíveis:
   - **Tema**: Claro, Escuro ou Sistema
   - **Notificações**: Ativar/desativar notificações
   - **Lembretes**: Configurar lembretes de reserva

### Notificações

Você receberá notificações quando:
- ✅ Novo cardápio for publicado
- ⏰ Tiver uma reserva ativa
- 📢 Houver avisos importantes

---

## Para Administradores

### Acessar Painel Administrativo

1. Faça login com conta de administrador
2. Você será direcionado ao painel admin

### Atualizar Cardápio

1. No painel admin, preencha os campos:
   - Prato Principal
   - Guarnição
   - Acompanhamento
   - Salada
   - Sobremesa
2. **(Opcional)** Adicione uma foto do prato:
   - Toque no ícone de câmera
   - Escolha uma foto da galeria ou tire uma foto
   - Aguarde o upload
3. Toque em "Salvar"

**Dica:** O cardápio é salvo automaticamente para a data de hoje.

### Upload de Imagens

**Como fazer:**
1. Toque no botão de câmera/galeria
2. Selecione a imagem
3. A imagem será comprimida automaticamente
4. Aguarde o upload (você verá uma barra de progresso)
5. A URL da imagem será salva no cardápio

**Formatos aceitos:** JPG, PNG
**Tamanho máximo:** A imagem será redimensionada para 1024x1024

### Gerenciar Reservas

1. Acesse "Reservas" no painel admin
2. Você verá:
   - Todas as reservas do dia
   - Status de cada reserva
   - Nome do usuário
   - Observações
3. Ações disponíveis:
   - Marcar como "Utilizada"
   - Cancelar reserva

### Ver Estatísticas

1. Toque em "Estatísticas"
2. Visualize:
   - Gráfico de escolhas (Pizza)
   - Evolução temporal (Linhas)
   - Comparativo de pratos (Barras)
   - Média de avaliações

### Exportar Relatórios

**Exportar para PDF:**
1. Acesse "Relatórios"
2. Escolha o tipo (Cardápios ou Reservas)
3. Toque em "Exportar PDF"
4. O arquivo será salvo em `Documentos/`

**Exportar para Excel:**
1. Acesse "Relatórios"
2. Escolha o tipo (Cardápios ou Reservas)
3. Toque em "Exportar Excel"
4. O arquivo será salvo em `Documentos/`

**Onde encontrar:** Os arquivos são salvos na pasta "Documentos" do app

### Ver Comentários

1. Acesse "Comentários"
2. Você verá todos os comentários
3. Informações exibidas:
   - Nome do usuário
   - Avaliação (estrelas)
   - Comentário
   - Data

---

## Para Desenvolvedores

### Estrutura do Projeto

```
app/src/main/java/com/example/visualizadorapp/
├── model/              # Entidades Room
│   ├── Cardapio.java
│   ├── Reserva.java
│   └── Comentario.java
├── viewmodel/          # ViewModels
│   ├── CardapioViewModel.java
│   └── ReservaViewModel.java
├── repository/         # Repositórios
│   ├── CardapioRepository.java
│   ├── ReservaRepository.java
│   └── ComentarioRepository.java
├── database/           # Room Database
│   ├── AppDatabase.java
│   ├── CardapioDao.java
│   ├── ReservaDao.java
│   └── ComentarioDao.java
├── adapter/            # RecyclerView Adapters
│   └── CardapioAdapter.java
├── service/            # Serviços
│   └── MyFirebaseMessagingService.java
├── utils/              # Utilitários
│   ├── ThemeManager.java
│   ├── AnimationHelper.java
│   ├── LoadingHelper.java
│   ├── MessageHelper.java
│   ├── ImageUploadHelper.java
│   ├── PdfExportHelper.java
│   ├── ExcelExportHelper.java
│   ├── FirebaseQueryOptimizer.java
│   └── AppStartupOptimizer.java
└── [Activities]        # Activities
    ├── MainActivity.java
    ├── AdminActivity.java
    ├── ConfiguracoesActivity.java
    ├── ReservaActivity.java
    └── HistoricoActivity.java
```

### Como Usar os Helpers

#### ThemeManager
```java
ThemeManager themeManager = new ThemeManager(context);
themeManager.setThemeMode(ThemeManager.THEME_DARK);
```

#### AnimationHelper
```java
AnimationHelper.fadeIn(view);
AnimationHelper.slideInFromBottom(view);
AnimationHelper.shake(view); // Para erros
```

#### LoadingHelper
```java
LoadingHelper loading = new LoadingHelper(context);
loading.showLoading("Carregando dados...");
// ... operação
loading.hideLoading();
```

#### MessageHelper
```java
MessageHelper.showToast(context, "Operação concluída");
MessageHelper.showError(context, "Erro", exception);
MessageHelper.showConfirmDialog(context, "Título", "Mensagem", 
    () -> { /* onConfirm */ });
```

#### ImageUploadHelper
```java
ImageUploadHelper uploadHelper = new ImageUploadHelper(context);
uploadHelper.uploadCardapioImage(imageUri, data, new OnUploadListener() {
    @Override
    public void onSuccess(String imageUrl) {
        // Usar imageUrl
    }
    
    @Override
    public void onFailure(String error) {
        // Tratar erro
    }
    
    @Override
    public void onProgress(int progress) {
        // Atualizar UI com progresso
    }
});
```

### Usar ViewModels

```java
// Na Activity
CardapioViewModel viewModel = new ViewModelProvider(this)
    .get(CardapioViewModel.class);

// Observar LiveData
viewModel.getAllCardapios().observe(this, cardapios -> {
    // Atualizar UI com dados
    adapter.setCardapios(cardapios);
});

// Inserir dados
Cardapio cardapio = new Cardapio();
cardapio.setData("2026-03-02");
cardapio.setPratoPrincipal("Frango Assado");
viewModel.insert(cardapio);
```

### Firebase Queries Otimizadas

```java
// Query com cache
DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cardapio");
FirebaseQueryOptimizer.addCachedListener("cardapio_key", ref, listener);

// Query paginada
Query query = FirebaseQueryOptimizer.createPaginatedQuery(ref, 20);

// Query por data
Query dateQuery = FirebaseQueryOptimizer.createDateRangeQuery(
    ref, "2026-03-01", "2026-03-31"
);
```

### Otimizar Startup

No `MyApplication.java`:
```java
@Override
public void onCreate() {
    super.onCreate();
    
    // Iniciar monitor
    AppStartupOptimizer.StartupMonitor.start();
    
    // Tarefas críticas (síncrono)
    // ...
    
    // Tarefas em background
    AppStartupOptimizer.initializeAsync(this,
        () -> preloadDatabase(),
        () -> cleanOldCache()
    );
    
    // Tarefas com delay
    AppStartupOptimizer.initializeDelayed(
        () -> updateAnalytics(),
        AppStartupOptimizer.InitStrategy.OPTIONAL
    );
}
```

### Exportar Relatórios

```java
// PDF
PdfExportHelper pdfHelper = new PdfExportHelper(context);
pdfHelper.exportCardapios(cardapios, new OnExportListener() {
    @Override
    public void onSuccess(String filePath) {
        MessageHelper.showToast(context, "PDF salvo em: " + filePath);
    }
    
    @Override
    public void onFailure(String error) {
        MessageHelper.showError(context, "Erro ao exportar", error);
    }
});

// Excel
ExcelExportHelper excelHelper = new ExcelExportHelper(context);
excelHelper.exportReservas(reservas, listener);
```

### Testes

Para testar o app:
1. Configure Firebase com dados de teste
2. Execute testes unitários: `./gradlew test`
3. Execute testes instrumentados: `./gradlew connectedAndroidTest`

### Debug

Para debugar:
1. Ative logs do Firebase: `FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);`
2. Use Logcat para ver logs de startup
3. Use o Database Inspector do Android Studio para Room

---

## Dúvidas e Suporte

- **Issues:** Abra uma issue no GitHub
- **Documentação:** Consulte o README.md
- **Changelog:** Veja CHANGELOG.md para histórico de mudanças
