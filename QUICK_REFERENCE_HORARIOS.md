# ⚡ Quick Reference: Validação de Horários

## 📌 Cole Este Código

### Validar um Horário
```java
import com.example.visualizadorapp.utils.ValidadorHorario;

boolean valido = ValidadorHorario.isValido("07:00-17:00");
```

### Converter Turno → Horário
```java
String horario = ValidadorHorario.getHorarioParaTurno("MANHA");
// Resultado: "06:00-18:00"
```

### Converter Horário → Turno
```java
String turno = ValidadorHorario.getTurnoParaHorario("18:00-06:00");
// Resultado: "NOITE"
```

### Obter Descrição
```java
String desc = ValidadorHorario.getDescricao("07:00-17:00");
// Resultado: "Regime 5x2 (7h às 17h)"
```

### Obter Duração
```java
int horas = ValidadorHorario.getDuracao("06:00-18:00");
// Resultado: 12
```

### Usar com Funcionario
```java
Funcionario func = new Funcionario();
try {
    func.setHorario("06:00-18:00"); // ✅ OK
} catch (IllegalArgumentException e) {
    Log.e("Erro", e.getMessage()); // ❌ Inválido
}

// Exibir
String horarioFormatado = func.getHorarioFormatado();
int duracao = func.getDuracaoTrabalho();
boolean valido = func.isHorarioValido();
```

---

## 📊 Os 3 Horários

| Turno | Horário | Horas |
|-------|---------|-------|
| 5X2 | `07:00-17:00` | 10 |
| MANHA | `06:00-18:00` | 12 |
| NOITE | `18:00-06:00` | 12 |

---

## ❌ Rejeitados

- `07-17` (sem `:`)
- `6-18` (sem formatação)
- `10:00-18:00` (não é um dos 3)
- `06:30-18:30` (horas quebradas)
- `null` / vazio

---

## 📂 Arquivos

```
ValidadorHorario.java
  → utils/ValidadorHorario.java
  
ValidadorHorarioTest.java
  → test/java/.../ValidadorHorarioTest.java
  
Funcionario.java
  → model/Funcionario.java (com integração)
```

---

## 📚 Documentação

| Doc | Objetivo |
|-----|----------|
| PADRAO_HORARIOS_TRABALHO.md | Especificação |
| GUIA_VALIDACAO_HORARIOS.md | Como usar |
| RESUMO_VALIDACAO_HORARIOS.md | Visão geral |
| CHECKLIST_IMPLEMENTACAO.md | Passo a passo |

---

## 🧪 Testar Arquivo JSON

```bash
python script_validador_horarios.py
# Menu → Opção 6 → caminho/arquivo.json
```

---

## ⚡ 5 Métodos Mais Usados

```java
ValidadorHorario.isValido(horario)              // Validar
ValidadorHorario.getDescricao(horario)          // Exibir
ValidadorHorario.getHorarioParaTurno(turno)     // Encontrar
ValidadorHorario.getDuracao(horario)            // Calcular
ValidadorHorario.isParValido(turno, horario)    // Verificar
```

---

## 🎯 Casos de Uso

### Cadastro
```java
if (!ValidadorHorario.isValido(horarioDigitado)) {
    erro("Horário inválido!");
}
```

### Exibição
```java
tvHorario.setText(func.getHorarioFormatado());
// "Turno Manhã (6h às 18h)"
```

### Filtro
```java
if (ValidadorHorario.isParValido(user.turno, user.horario)) {
    mostrarCardapio(user.turno);
}
```

### Validação de Dados
```java
if (!func.isHorarioValido()) {
    salvarErro("Turno e horário não combinam");
}
```

---

## 🚀 Próximo Passo

1. Compilar código (Ctrl+B)
2. Rodar testes (direito → Run)
3. Importar dados de Funcionários
4. Testar no app

---

**Para ajuda completa:** Veja [CHECKLIST_IMPLEMENTACAO.md](CHECKLIST_IMPLEMENTACAO.md)

**Status:** ✅ Pronto para usar
