# Melhores Práticas - VisualizadorApp

Este documento descreve as melhores práticas adotadas no projeto e recomendações para desenvolvimento.

## 🏗️ Arquitetura

### MVVM Pattern

#### Responsabilidades

**Model:**
- Representa os dados da aplicação
- Entidades Room com anotações apropriadas
- POJOs simples e imutáveis quando possível

**View (Activities/Fragments):**
- Apenas código de UI
- Observer de LiveData
- Não contém lógica de negócio
- Delega ações ao ViewModel

**ViewModel:**
- Gerencia o estado da UI
- Expõe LiveData para a View
- Não possui referências à View ou Context
- Sobrevive a mudanças de configuração

**Repository:**
- Abstração da fonte de dados
- Coordena dados do Room e Firebase
- Lógica de cache e sincronização

### Exemplo de Implementação

```java
// ❌ EVITE (lógica na Activity)
public class MainActivity extends AppCompatActivity {
    private DatabaseReference firebaseRef;
    
    private void loadData() {
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Processar dados aqui
            }
        });
    }
}

// ✅ RECOMENDADO (MVVM)
public class MainActivity extends AppCompatActivity {
    private CardapioViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        viewModel = new ViewModelProvider(this).get(CardapioViewModel.class);
        
        viewModel.getAllCardapios().observe(this, cardapios -> {
            // Apenas atualizar UI
            adapter.setCardapios(cardapios);
        });
    }
}
```

## 💾 Banco de Dados

### Room Database

#### Boas Práticas

1. **Sempre use LiveData para queries:**
```java
@Query("SELECT * FROM cardapios")
LiveData<List<Cardapio>> getAllCardapios(); // ✅
```

2. **Operações de escrita em background:**
```java
AppDatabase.databaseWriteExecutor.execute(() -> {
    dao.insert(item);
});
```

3. **Use indexes para queries frequentes:**
```java
@Entity(tableName = "cardapios",
        indices = {@Index(value = "data", unique = true)})
public class Cardapio { ... }
```

4. **Defina relacionamentos apropriadamente:**
```java
@Entity(foreignKeys = @ForeignKey(
    entity = User.class,
    parentColumns = "id",
    childColumns = "userId",
    onDelete = CASCADE
))
```

### Firebase

#### Otimizações

1. **Use persistência offline:**
```java
FirebaseDatabase.getInstance().setPersistenceEnabled(true);
```

2. **Minimize queries:**
```java
// ❌ EVITE
for (String date : dates) {
    ref.child(date).addListenerForSingleValueEvent(...);
}

// ✅ RECOMENDADO
ref.orderByKey()
   .startAt(startDate)
   .endAt(endDate)
   .addListenerForSingleValueEvent(...);
```

3. **Use índices no Firebase:**
```json
{
  "rules": {
    "cardapios": {
      ".indexOn": ["data", "timestamp"]
    }
  }
}
```

4. **Remova listeners quando não necessários:**
```java
@Override
protected void onStop() {
    super.onStop();
    FirebaseQueryOptimizer.removeCachedListener("key", ref);
}
```

## 🎨 UI/UX

### Material Design

1. **Use componentes Material:**
```xml
<!-- ✅ Material Button -->
<com.google.android.material.button.MaterialButton />

<!-- ❌ Button padrão -->
<Button />
```

2. **Respeite elevações:**
- Cards: 4dp
- FAB: 6dp
- Dialogs: 24dp

3. **Use cores do tema:**
```xml
android:textColor="@color/text_primary" <!-- ✅ -->
android:textColor="#000000" <!-- ❌ -->
```

### Animações

1. **Mantenha animações curtas:**
- Transições: 200-300ms
- Feedback: 100ms
- Navegação: 300-400ms

2. **Use interpoladores apropriados:**
```java
AccelerateDecelerateInterpolator // ✅ Suave
LinearInterpolator // ❌ Robótico
```

### Mensagens

1. **Seja específico:**
```java
// ❌ EVITE
"Erro"

// ✅ RECOMENDADO
"Não foi possível enviar o comentário. Verifique sua conexão."
```

2. **Ofereça ações:**
```java
Snackbar.make(view, "Comentário excluído", Snackbar.LENGTH_LONG)
    .setAction("Desfazer", v -> restaurarComentario())
    .show();
```

## 🚀 Performance

### Otimizações Gerais

1. **Use RecyclerView corretamente:**
```java
// ✅ Reutilize ViewHolders
recyclerView.setHasFixedSize(true);
recyclerView.setRecycledViewPool(sharedPool);
```

2. **Carregue imagens eficientemente:**
```java
Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView);
```

3. **Evite trabalho na Main Thread:**
```java
// ❌ EVITE
File file = new File(...);
String content = readFile(file); // I/O na main thread

// ✅ RECOMENDADO
AppStartupOptimizer.executeAsync(
    () -> readFile(file),
    content -> updateUI(content)
);
```

### Memory Leaks

1. **Evite contextos estáticos:**
```java
// ❌ EVITE
private static Context context;

// ✅ RECOMENDADO
Use ApplicationContext ou WeakReference
```

2. **Cancele operações assíncronas:**
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    disposable.dispose();
    executorService.shutdown();
}
```

## 🔒 Segurança

### Dados Sensíveis

1. **Nunca commite credenciais:**
```java
// ❌ EVITE
String API_KEY = "abc123...";

// ✅ RECOMENDADO
String API_KEY = BuildConfig.API_KEY;
```

2. **Use ProGuard/R8:**
```gradle
buildTypes {
    release {
        minifyEnabled true
        shrinkResources true
    }
}
```

3. **Valide entrada do usuário:**
```java
public boolean isEmailValid(String email) {
    return email != null && 
           Patterns.EMAIL_ADDRESS.matcher(email).matches();
}
```

### Firebase Security Rules

```json
{
  "rules": {
    "cardapios": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('admins').child(auth.uid).exists()"
    }
  }
}
```

## 📱 Compatibilidade

### Versões Android

1. **Verifique versão antes de usar recursos novos:**
```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    // Código para Android 8.0+
}
```

2. **Use bibliotecas AndroidX:**
```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
```

### Orientação

1. **Salve estado da UI:**
```java
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("data", currentData);
}
```

## 📝 Código Limpo

### Nomenclatura

```java
// ✅ BOAS
public class CardapioRepository { }
private String nomeUsuario;
public void salvarCardapio() { }

// ❌ RUINS
public class CP { }
private String n;
public void save() { }
```

### Métodos

1. **Um método, uma responsabilidade:**
```java
// ❌ EVITE
public void processarEEnviarESalvar() { }

// ✅ RECOMENDADO
public void processar() { }
public void enviar() { }
public void salvar() { }
```

2. **Métodos pequenos (< 20 linhas):**
```java
// Se um método está muito grande, divida em métodos menores
```

### Comentários

```java
// ❌ Comentário óbvio
// Incrementa i
i++;

// ✅ Comentário útil
// Limita a 10 tentativas para evitar loop infinito em casos de falha
if (attempts > 10) return;
```

## 🧪 Testes

### Testes Unitários

```java
@Test
public void dadoEmailValido_quandoValidar_deveRetornarTrue() {
    assertTrue(validator.isEmailValid("test@email.com"));
}
```

### Testes de Integração

```java
@Test
public void dadoCardapio_quandoInserir_deveAparecerNaLista() {
    // Given
    Cardapio cardapio = new Cardapio();
    
    // When
    viewModel.insert(cardapio);
    
    // Then
    List<Cardapio> lista = viewModel.getAllCardapios().getValue();
    assertTrue(lista.contains(cardapio));
}
```

## 📚 Documentação

1. **JavaDoc para APIs públicas:**
```java
/**
 * Salva um cardápio no banco de dados e sincroniza com Firebase.
 * 
 * @param cardapio O cardápio a ser salvo
 * @throws IllegalArgumentException se cardapio for null
 */
public void salvar(Cardapio cardapio) { }
```

2. **README atualizado:**
- Mantenha o README sempre atualizado
- Documente novas funcionalidades
- Inclua exemplos de uso

3. **Changelog:**
- Documente todas as mudanças
- Use versionamento semântico
- Agrupe por tipo de mudança

## 🔄 Versionamento

### Git

1. **Commits semânticos:**
```
feat: adiciona sistema de reservas
fix: corrige crash ao exportar PDF
docs: atualiza documentação de instalação
refactor: reorganiza estrutura de pastas
perf: otimiza queries do Firebase
```

2. **Branching:**
```
main         - Produção
develop      - Desenvolvimento
feature/*    - Novas funcionalidades
bugfix/*     - Correções
hotfix/*     - Correções urgentes
```

## 🎯 Checklist de Release

Antes de cada release, verificar:

- [ ] Todos os testes passando
- [ ] Sem warnings de lint
- [ ] ProGuard configurado
- [ ] Versionamento atualizado
- [ ] CHANGELOG atualizado
- [ ] README atualizado
- [ ] Credenciais removidas
- [ ] APK testado em diferentes devices
- [ ] Performance otimizada
- [ ] Segurança revisada

---

**Lembre-se:** Código limpo não é escrito de primeira. É refatorado até ficar limpo. 🚀
