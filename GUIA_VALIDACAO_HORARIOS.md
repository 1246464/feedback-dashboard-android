# ⏰ Guia: Validação de Horários de Trabalho

## 📌 Objetivo

Este guia mostra como usar a classe `ValidadorHorario` para garantir que apenas os **3 horários padrão** sejam usados no sistema, sem horas quebradas.

---

## 🎯 Os 3 Horários Padrão

```
1. REGIME 5X2:      07:00-17:00  (10 horas)
2. TURNO MANHÃ:     06:00-18:00  (12 horas)
3. TURNO NOITE:     18:00-06:00  (12 horas, cruza dia)
```

---

## 📁 Arquivo Principal

**Localização:**
```
app/src/main/java/com/example/visualizadorapp/utils/ValidadorHorario.java
```

---

## 🔍 Usando ValidadorHorario

### 1️⃣ Validar um Horário

```java
import com.example.visualizadorapp.utils.ValidadorHorario;

// ✅ Válido
if (ValidadorHorario.isValido("07:00-17:00")) {
    // Horário é um dos 3 padrão
}

// ❌ Inválido
if (!ValidadorHorario.isValido("10:00-18:00")) {
    // Horário NÃO é um dos 3 padrão
}
```

### 2️⃣ Obter Constantes

```java
// Horários disponíveis como constantes
String horario1 = ValidadorHorario.HORARIO_5X2;    // "07:00-17:00"
String horario2 = ValidadorHorario.HORARIO_MANHA;  // "06:00-18:00"
String horario3 = ValidadorHorario.HORARIO_NOITE;  // "18:00-06:00"
```

### 3️⃣ Listar Todos os Horários Válidos

```java
String[] horarios = ValidadorHorario.getHorariosValidos();
// Resultado: ["07:00-17:00", "06:00-18:00", "18:00-06:00"]

// Ideal para criar um Spinner:
ArrayAdapter<String> adapter = new ArrayAdapter<>(
    this,
    android.R.layout.simple_spinner_item,
    horarios
);
```

### 4️⃣ Converter Turno para Horário

```java
// Qual é o horário do turno MANHA?
String horario = ValidadorHorario.getHorarioParaTurno("MANHA");
// Resultado: "06:00-18:00"

// Para 5X2
String horario = ValidadorHorario.getHorarioParaTurno("5X2");
// Resultado: "07:00-17:00"
```

### 5️⃣ Converter Horário para Turno

```java
// Qual turno tem o horário "18:00-06:00"?
String turno = ValidadorHorario.getTurnoParaHorario("18:00-06:00");
// Resultado: "NOITE"
```

### 6️⃣ Validar Par Turno-Horário

```java
// Esse turno e horário combinam?
boolean valido = ValidadorHorario.isParValido("MANHA", "06:00-18:00");
// Resultado: true

// Desacordo
boolean invalido = ValidadorHorario.isParValido("MANHA", "07:00-17:00");
// Resultado: false (MANHA deve ter 06:00-18:00)
```

### 7️⃣ Formatar Horário

```java
// Entrada ruim
String resultado = ValidadorHorario.formatar("6-18");
// Tenta converter para "06:00-18:00"

// Entrada já correta
String resultado = ValidadorHorario.formatar("06:00-18:00");
// Retorna: "06:00-18:00"

// Entrada inválida
String resultado = ValidadorHorario.formatar("06:30-18:30");
// Retorna: null (horas quebradas não são permitidas)
```

### 8️⃣ Obter Duração em Horas

```java
int duracao = ValidadorHorario.getDuracao("07:00-17:00");
// Resultado: 10 horas

int duracao = ValidadorHorario.getDuracao("06:00-18:00");
// Resultado: 12 horas

int duracao = ValidadorHorario.getDuracao("18:00-06:00");
// Resultado: 12 horas

// Inválido
int duracao = ValidadorHorario.getDuracao("10:00-18:00");
// Resultado: -1 (erro)
```

### 9️⃣ Obter Descrição Legível

```java
String desc = ValidadorHorario.getDescricao("07:00-17:00");
// Resultado: "Regime 5x2 (7h às 17h)"

String desc = ValidadorHorario.getDescricao("06:00-18:00");
// Resultado: "Turno Manhã (6h às 18h)"

String desc = ValidadorHorario.getDescricao("18:00-06:00");
// Resultado: "Turno Noite (18h às 6h)"
```

---

## 🏢 Usando com Funcionario.java

### Definir Horário com Validação

```java
import com.example.visualizadorapp.model.Funcionario;

Funcionario func = new Funcionario();
func.setTurno("MANHA");

try {
    func.setHorario("06:00-18:00"); // ✅ OK
} catch (IllegalArgumentException e) {
    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
}

try {
    func.setHorario("10:00-18:00"); // ❌ Erro!
    // Exceção: "Horário inválido: '10:00-18:00'. Use um dos valores padrão..."
} catch (IllegalArgumentException e) {
    Log.e("Erro", e.getMessage());
}
```

### Obter Horário Formatado

```java
Funcionario func = funcionarioRepository.buscarPorCpf("12345678901");

// Horário bruto
String horario = func.getHorario(); // "06:00-18:00"

// Horário formatado para exibir
String horarioFormatado = func.getHorarioFormatado();
// Resultado: "Turno Manhã (6h às 18h)"

// Duração do trabalho
int duracao = func.getDuracaoTrabalho(); // 12 horas
```

### Validar Consistência Turno-Horário

```java
Funcionario func = new Funcionario();
func.setTurno("MANHA");
func.setHorario("06:00-18:00");

// Verifica se turno e horário são coerentes
if (func.isHorarioValido()) {
    // Tudo está correto
} else {
    // Turno e horário não combinam!
}
```

---

## 💻 Exemplo: Atualizar CadastroMelhoradoActivity

### Exibir Horário no Cadastro

```java
public class CadastroMelhoradoActivity extends AppCompatActivity {
    
    private TextView tvHorarioLabel;
    private FuncionarioRepository funcionarioRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_melhorado);
        
        tvHorarioLabel = findViewById(R.id.tv_horario);
    }
    
    private void buscarFuncionario(String email) {
        funcionarioRepository.searchByEmail(email).observe(this, funcionario -> {
            if (funcionario != null) {
                // Exibir horário formatado
                String horarioFormatado = funcionario.getHorarioFormatado();
                tvHorarioLabel.setText(horarioFormatado);
                
                // Ou exibir horário bruto com informação
                String info = "Horário: " + funcionario.getHorario() + 
                             " (" + funcionario.getDuracaoTrabalho() + " horas)";
                tvHorarioLabel.setText(info);
            }
        });
    }
}
```

### Criar Spinner com Horários (se necessário editar)

```java
// Em um activity que permite editar horário
private void criarSpinnerHorarios() {
    Spinner spinnerHorario = findViewById(R.id.spinner_horario);
    
    // Obter array de horários válidos
    String[] horarios = ValidadorHorario.getHorariosValidos();
    
    // Criar array de descrições para exibir
    String[] descricoes = new String[horarios.length];
    for (int i = 0; i < horarios.length; i++) {
        descricoes[i] = ValidadorHorario.getDescricao(horarios[i]);
    }
    
    // Criar adapter com descrições
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
        descricoes
    );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerHorario.setAdapter(adapter);
    
    // Quando selecionar, salvar o horário bruto
    spinnerHorario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String horarioSelecionado = horarios[position]; // "07:00-17:00"
            // Salvar no banco
        }
        
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    });
}
```

---

## 🛡️ Erros Comuns e Soluções

### Erro 1: Horário com Horas Quebradas

```java
// ❌ ERRADO
try {
    func.setHorario("06:30-18:30");
} catch (IllegalArgumentException e) {
    Log.e("Erro", "Não pode usar 06:30-18:30");
}

// ✅ CORRETO
try {
    func.setHorario("06:00-18:00");
} catch (IllegalArgumentException e) {
    // Não vai chegar aqui
}
```

### Erro 2: Turno e Horário Desacordados

```java
Funcionario func = new Funcionario();
func.setTurno("MANHA");

try {
    // Turno MANHA deve ter 06:00-18:00, não 07:00-17:00
    func.setHorario("07:00-17:00");
    
    if (!func.isHorarioValido()) {
        Log.e("Erro", "Turno " + func.getTurno() + 
              " não combina com horário " + func.getHorario());
    }
} catch (IllegalArgumentException e) {
    Log.e("Erro", e.getMessage());
}
```

### Erro 3: Formato Errado

```java
// ❌ ERRADO (falta "00" dos minutos)
"07-17"

// ❌ ERRADO (falta formatação)
"7-17"

// ✅ CORRETO
"07:00-17:00"
```

---

## 📊 Tabela de Referência

| Turno | Horário Correto | Horas | Descrição |
|-------|-----------------|-------|-----------|
| 5X2 | `07:00-17:00` | 10 | Regime 5x2 (7h às 17h) |
| MANHA | `06:00-18:00` | 12 | Turno Manhã (6h às 18h) |
| NOITE | `18:00-06:00` | 12 | Turno Noite (18h às 6h) |

---

## ✅ Checklist de Implementação

- [ ] Classe `ValidadorHorario.java` criada em `utils/`
- [ ] `Funcionario.java` importa e usa `ValidadorHorario`
- [ ] `setHorario()` valida com `ValidadorHorario.isValido()`
- [ ] `CadastroMelhoradoActivity` exibe `getHorarioFormatado()`
- [ ] Scripts de importação usam apenas 3 horários válidos
- [ ] Testes cobrem todos os 3 horários
- [ ] Documentação atualizada

---

## 🔗 Arquivos Relacionados

- [PADRAO_HORARIOS_TRABALHO.md](PADRAO_HORARIOS_TRABALHO.md) - Especificação dos horários
- [GUIA_IMPORTACAO_FUNCIONARIOS.md](GUIA_IMPORTACAO_FUNCIONARIOS.md) - Como importar dados
- [Funcionario.java](app/src/main/java/com/example/visualizadorapp/model/Funcionario.java) - Modelo
- [ValidadorHorario.java](app/src/main/java/com/example/visualizadorapp/utils/ValidadorHorario.java) - Validador

---

**Versão**: 1.0
**Data**: Janeiro 2024
**Status**: Validação Implementada ✅
