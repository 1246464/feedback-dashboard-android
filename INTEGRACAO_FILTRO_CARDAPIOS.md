# 📱 Integração: Filtro de Cardápios nas Activities

## 🎯 Objetivo

Adicionar o filtro de visibilidade de cardápios nas activities que exibem cardápios para os usuários.

---

## 📋 Activities a Atualizar

1. **MainActivity.java** - Feed principal
2. **VisaoSemanalActivity.java** - Visão semanal
3. **DashboardCozinhaActivity.java** - Dashboard Cozinha
4. **DashboardCopeiraActivity.java** - Dashboard Copeira
5. **DashboardEstoqueActivity.java** - Dashboard Estoque
6. **DashboardTecnicaActivity.java** - Dashboard Técnica
7. **DashboardLiderActivity.java** - Dashboard Líder

---

## 📌 Padrão de Integração

Todas as activities seguem o mesmo padrão:

```java
// 1. Declarar ViewModel
private CardapiosViewModel cardapiosViewModel;

// 2. No onCreate:
cardapiosViewModel = new ViewModelProvider(this).get(CardapiosViewModel.class);

// 3. Observar dados filtrados
cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
    adapter.setData(cardapios);
});

// 4. Carregar dados
cardapiosViewModel.carregarCardapiosParaUsuario();
```

---

## 1️⃣ MainActivity.java

### ✅ Código para Adicionar

```java
import androidx.lifecycle.ViewModelProvider;
import com.example.visualizadorapp.viewmodel.CardapiosViewModel;

public class MainActivity extends AppCompatActivity {
    
    private RecyclerView rvCardapios;
    private CardapioAdapter adapter;
    private CardapiosViewModel cardapiosViewModel;
    private ProgressBar progressBar;
    private TextView tvRegras;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Inicializar views
        rvCardapios = findViewById(R.id.rv_cardapios);
        progressBar = findViewById(R.id.progress_bar);
        tvRegras = findViewById(R.id.tv_regras);
        
        // Configurar adapter
        adapter = new CardapioAdapter();
        rvCardapios.setAdapter(adapter);
        
        // Inicializar ViewModel
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Observar cardápios filtrados
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
            if (cardapios != null && !cardapios.isEmpty()) {
                rvCardapios.setVisibility(View.VISIBLE);
            }
        });
        
        // Observar regras de visibilidade
        cardapiosViewModel.getDescricaoRegras().observe(this, regras -> {
            tvRegras.setText("📋 " + regras);
            tvRegras.setVisibility(View.VISIBLE);
        });
        
        // Observar carregamento
        cardapiosViewModel.isCarregando().observe(this, carregando -> {
            progressBar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        });
        
        // Observar erros
        cardapiosViewModel.getErro().observe(this, erro -> {
            if (erro != null && !erro.isEmpty()) {
                Toast.makeText(this, "Erro: " + erro, Toast.LENGTH_LONG).show();
            }
        });
        
        // Carregar dados
        carregarCardapios();
    }
    
    private void carregarCardapios() {
        cardapiosViewModel.carregarCardapiosParaUsuario();
    }
}
```

### 📐 Layout Sugerido (activity_main.xml)

Adicione após o RecyclerView existente:

```xml
<!-- Antes do RecyclerView -->
<TextView
    android:id="@+id/tv_regras"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp"
    android:textSize="14sp"
    android:textColor="@android:color/darker_gray"
    android:visibility="gone" />

<ProgressBar
    android:id="@+id/progress_bar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:visibility="gone" />

<!-- RecyclerView existente -->
<RecyclerView
    android:id="@+id/rv_cardapios"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

---

## 2️⃣ VisaoSemanalActivity.java

### ✅ Código para Adicionar

```java
public class VisaoSemanalActivity extends AppCompatActivity {
    
    private RecyclerView rvCardapios;
    private CardapioSemanallAdapter adapter;
    private CardapiosViewModel cardapiosViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao_semanal);
        
        // Inicializar views
        rvCardapios = findViewById(R.id.rv_cardapios_semana);
        
        // Configurar adapter
        adapter = new CardapioSemanalAdapter();
        rvCardapios.setAdapter(adapter);
        
        // Inicializar ViewModel
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Observar cardápios da semana filtrados
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
        });
        
        // Carregar cardápios da próxima semana (filtrados)
        cardapiosViewModel.carregarCardapiosParaUsuario();
    }
}
```

---

## 3️⃣ DashboardCozinhaActivity.java

### ✅ Código para Adicionar

```java
public class DashboardCozinhaActivity extends AppCompatActivity {
    
    private CardapiosViewModel cardapiosViewModel;
    private RecyclerView rvCardapios;
    private CardapioAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_cozinha);
        
        rvCardapios = findViewById(R.id.rv_cardapios);
        adapter = new CardapioAdapter();
        rvCardapios.setAdapter(adapter);
        
        // Inicializar ViewModel
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Observar cardápios
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
        });
        
        // Carregar cardápios filtrados
        cardapiosViewModel.carregarCardapiosParaUsuario();
    }
}
```

---

## 4️⃣ DashboardCopeiraActivity.java

### ✅ Código para Adicionar

```java
public class DashboardCopeiraActivity extends AppCompatActivity {
    
    private CardapiosViewModel cardapiosViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_copeira);
        
        // Inicializar ViewModel
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Observar e exibir cardápios (só verá TARDE se MANHA)
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            exibirCardapios(cardapios);
        });
        
        cardapiosViewModel.carregarCardapiosParaUsuario();
    }
    
    private void exibirCardapios(List<CardapioTurno> cardapios) {
        // Atualizar RecyclerView ou outro componente
        adapter.setData(cardapios);
    }
}
```

---

## 5️⃣ DashboardEstoqueActivity.java

### ✅ Código para Adicionar

```java
public class DashboardEstoqueActivity extends AppCompatActivity {
    
    private CardapiosViewModel cardapiosViewModel;
    private RecyclerView rvCardapios;
    private CardapioAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_estoque);
        
        rvCardapios = findViewById(R.id.rv_cardapios);
        adapter = new CardapioAdapter();
        rvCardapios.setAdapter(adapter);
        
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Para estoque, pode carregar todos os cardápios (sem filtro)
        // Ou filtrar como o usuário (sua escolha):
        
        // Opção 1: Filtrado por turno do usuário (recomendado)
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
        });
        cardapiosViewModel.carregarCardapiosParaUsuario();
        
        // Opção 2: Ver todos os cardápios (descomente se preferir)
        // cardapiosViewModel.carregarCardapiosAdmin(hoje, proxSemana);
    }
}
```

---

## 6️⃣ DashboardTecnicaActivity.java

### ✅ Código para Adicionar

```java
public class DashboardTecnicaActivity extends AppCompatActivity {
    
    private CardapiosViewModel cardapiosViewModel;
    private RecyclerView rvCardapios;
    private CardapioAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_tecnica);
        
        rvCardapios = findViewById(R.id.rv_cardapios);
        adapter = new CardapioAdapter();
        rvCardapios.setAdapter(adapter);
        
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
        });
        
        // Técnica vê todos os cardápios para planejamento
        cardapiosViewModel.carregarCardapiosAdmin(
            FiltroVisibilidadeCardapio.getDataHoje(),
            FiltroVisibilidadeCardapio.getDataFutura(30)
        );
    }
}
```

---

## 7️⃣ DashboardLiderActivity.java

### ✅ Código para Adicionar

```java
public class DashboardLiderActivity extends AppCompatActivity {
    
    private CardapiosViewModel cardapiosViewModel;
    private RecyclerView rvCardapios;
    private CardapioAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_lider);
        
        rvCardapios = findViewById(R.id.rv_cardapios);
        adapter = new CardapioAdapter();
        rvCardapios.setAdapter(adapter);
        
        cardapiosViewModel = new ViewModelProvider(this)
            .get(CardapiosViewModel.class);
        
        // Líder vê cardápios filtrados por seu turno (5X2)
        cardapiosViewModel.getCardapiosVisiveis().observe(this, cardapios -> {
            adapter.setData(cardapios);
        });
        
        cardapiosViewModel.carregarCardapiosParaUsuario();
    }
}
```

---

## 🔧 Imports Necessários

Adicione esses imports em cada activity:

```java
import androidx.lifecycle.ViewModelProvider;
import com.example.visualizadorapp.viewmodel.CardapiosViewModel;
import com.example.visualizadorapp.utils.FiltroVisibilidadeCardapio;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
```

---

## ✅ Checklist de Implementação

Para cada activity:

- [ ] Importar `CardapiosViewModel`
- [ ] Importar `FiltroVisibilidadeCardapio`
- [ ] Declarar `private CardapiosViewModel cardapiosViewModel;`
- [ ] No `onCreate()`: inicializar ViewModel
- [ ] Observar `getCardapiosVisiveis()` e atualizar adapter
- [ ] Observar `getDescricaoRegras()` (opcional)
- [ ] Observar `isCarregando()` (opcional)
- [ ] Chamar `carregarCardapiosParaUsuario()`
- [ ] Testar no emulador

---

## 🧪 Como Testar

### Teste 1: Usuário MANHA
1. Logar como usuário MANHA
2. Abrir MainActivity
3. ✅ Deve ver cardápios TARDE
4. ❌ Não deve ver cardápios NOITE

### Teste 2: Usuário NOITE
1. Logar como usuário NOITE
2. Abrir MainActivity
3. ❌ Não deve ver cardápios TARDE
4. ✅ Deve ver cardápios NOITE

### Teste 3: Usuário 5X2
1. Logar como usuário 5X2
2. Abrir VisaoSemanalActivity
3. ✅ Deve ver seg-sex
4. ❌ Não deve ver sábado/domingo

---

## 🚀 Próximos Passos

1. Copiar snippets acima para cada activity
2. Adicionar imports necessários
3. Ajustar names de componentes (rvCardapios, adapter, etc) conforme seu layout
4. Compilar no Android Studio
5. Testar com diferentes usuários

---

**Versão**: 1.0  
**Data**: Maio 2026  
**Status**: Pronto para copiar/colar no Android Studio
