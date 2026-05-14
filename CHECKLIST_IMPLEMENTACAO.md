# ✅ Checklist: Implementação de Validação de Horários

## 📋 Guia Passo a Passo

Siga este checklist para implementar o sistema de validação de horários no seu Android Studio.

---

## 🎯 FASE 1: Preparação (5 minutos)

- [ ] 1.1 Abrir Android Studio
- [ ] 1.2 Abrir projeto VisualizadorApp
- [ ] 1.3 Fazer sync do Gradle (caso necessário)
- [ ] 1.4 Ler [RESUMO_VALIDACAO_HORARIOS.md](RESUMO_VALIDACAO_HORARIOS.md)

**Tempo: ~5 min** ⏱️

---

## 🔧 FASE 2: Implementação (15 minutos)

### ✅ Passo 1: Verificar ValidadorHorario.java

- [ ] 2.1 Abrir arquivo: `app/src/main/java/.../utils/ValidadorHorario.java`
- [ ] 2.2 Verificar se tem 8 métodos:
  - [ ] `isValido()`
  - [ ] `getTurnoParaHorario()`
  - [ ] `getHorarioParaTurno()`
  - [ ] `getDuracao()`
  - [ ] `getDescricao()`
  - [ ] `isParValido()`
  - [ ] `formatar()`
  - [ ] `getHorariosValidos()`
- [ ] 2.3 Compilar (Ctrl+B) - deve compilar sem erros

### ✅ Passo 2: Verificar Funcionario.java

- [ ] 2.4 Abrir arquivo: `app/src/main/java/.../model/Funcionario.java`
- [ ] 2.5 Verificar se importa: `ValidadorHorario`
- [ ] 2.6 Verificar se tem os novos métodos:
  - [ ] `setHorario()` com validação (throws IllegalArgumentException)
  - [ ] `getHorarioFormatado()`
  - [ ] `isHorarioValido()`
  - [ ] `getDuracaoTrabalho()`
- [ ] 2.7 Compilar - deve compilar sem erros

### ✅ Passo 3: Verificar Testes

- [ ] 2.8 Abrir arquivo: `app/src/test/java/.../utils/ValidadorHorarioTest.java`
- [ ] 2.9 Contar testes - deve ter 50+
- [ ] 2.10 Rodar testes: Clique direito → Run 'ValidadorHorarioTest'
- [ ] 2.11 Verificar: Todos os testes devem passar (verde ✅)

**Tempo: ~15 min** ⏱️

---

## 📚 FASE 3: Documentação (5 minutos)

### ✅ Verificar Documentos Criados

- [ ] 3.1 [PADRAO_HORARIOS_TRABALHO.md](PADRAO_HORARIOS_TRABALHO.md) existe
- [ ] 3.2 [GUIA_VALIDACAO_HORARIOS.md](GUIA_VALIDACAO_HORARIOS.md) existe
- [ ] 3.3 [RESUMO_VALIDACAO_HORARIOS.md](RESUMO_VALIDACAO_HORARIOS.md) existe
- [ ] 3.4 [LISTA_ARQUIVOS_IMPLEMENTACAO.md](LISTA_ARQUIVOS_IMPLEMENTACAO.md) existe
- [ ] 3.5 [INDICE.md](INDICE.md) foi atualizado

**Tempo: ~5 min** ⏱️

---

## 🧪 FASE 4: Testes Manuais (10 minutos)

### ✅ Testar ValidadorHorario Direto

Abrir Android Studio → Terminal (Alt+F12) e copiar este código em uma classe de teste:

```java
import com.example.visualizadorapp.utils.ValidadorHorario;

// Testes rápidos
boolean t1 = ValidadorHorario.isValido("07:00-17:00"); // true
boolean t2 = ValidadorHorario.isValido("10:00-18:00"); // false

String turno = ValidadorHorario.getTurnoParaHorario("06:00-18:00"); // "MANHA"
String hora = ValidadorHorario.getHorarioParaTurno("NOITE"); // "18:00-06:00"

String desc = ValidadorHorario.getDescricao("07:00-17:00"); // "Regime 5x2..."
int duracao = ValidadorHorario.getDuracao("06:00-18:00"); // 12
```

- [ ] 4.1 Copiar código acima em um teste
- [ ] 4.2 Rodar teste manualmente
- [ ] 4.3 Verificar todos os resultados (devem ser corretos)
- [ ] 4.4 ✅ Se tudo passar, continue

### ✅ Testar Funcionario.java com Validação

```java
Funcionario func = new Funcionario();
func.setTurno("MANHA");

try {
    func.setHorario("06:00-18:00"); // ✅ Deve funcionar
    System.out.println("✅ Horário válido: " + func.getHorarioFormatado());
} catch (IllegalArgumentException e) {
    System.out.println("❌ Erro (não esperado): " + e.getMessage());
}

try {
    func.setHorario("10:00-18:00"); // ❌ Deve lançar exceção
    System.out.println("❌ Horário inválido aceito (problema!)");
} catch (IllegalArgumentException e) {
    System.out.println("✅ Exceção correta: " + e.getMessage());
}
```

- [ ] 4.5 Copiar código acima em um teste
- [ ] 4.6 Rodar teste
- [ ] 4.7 Verificar:
  - [ ] Primeiro try-catch imprime "✅ Horário válido: Turno Manhã (6h às 18h)"
  - [ ] Segundo try-catch imprime "✅ Exceção correta: ..."
- [ ] 4.8 ✅ Se tudo passar, continue

**Tempo: ~10 min** ⏱️

---

## 🔗 FASE 5: Integração (20 minutos)

### ✅ Integração com CadastroMelhoradoActivity

- [ ] 5.1 Abrir: `app/src/main/java/.../CadastroMelhoradoActivity.java`
- [ ] 5.2 Localizar onde exibe informações do funcionário
- [ ] 5.3 Adicionar exibição de horário:

```java
// No método que exibe dados do Funcionario
private void exibirFuncionario(Funcionario func) {
    // ... código existente ...
    
    // ✅ NOVO: Exibir horário formatado
    TextView tvHorario = findViewById(R.id.tv_horario);
    if (tvHorario != null && func.getHorario() != null) {
        tvHorario.setText("Horário: " + func.getHorarioFormatado());
    }
}
```

- [ ] 5.4 Compilar - deve compilar sem erros
- [ ] 5.5 ✅ Se compilou, pode testar no app

### ✅ Teste no Emulador/Dispositivo

- [ ] 5.6 Rodar app no emulador: Shift+F10
- [ ] 5.7 Ir para tela de cadastro
- [ ] 5.8 Inserir email de um funcionário existente
- [ ] 5.9 Verificar se aparece horário formatado
- [ ] 5.10 Tentar inserir horário inválido manualmente
  - [ ] Deve ser rejeitado ou gerar erro

**Tempo: ~20 min** ⏱️

---

## 📊 FASE 6: Importação de Dados (15 minutos)

### ✅ Preparar Dados para Importação

- [ ] 6.1 Ler [PADRAO_HORARIOS_TRABALHO.md](PADRAO_HORARIOS_TRABALHO.md)
- [ ] 6.2 Entender os 3 horários padrão:
  - [ ] `"07:00-17:00"` (5X2)
  - [ ] `"06:00-18:00"` (MANHA)
  - [ ] `"18:00-06:00"` (NOITE)
- [ ] 6.3 Preparar arquivo JSON com dados dos funcionários
  - [ ] Usar os 3 horários acima
  - [ ] Sem horas quebradas
  - [ ] Turno deve corresponder ao horário

### ✅ Validar Arquivo JSON

- [ ] 6.4 Abrir terminal: Terminal → New Terminal
- [ ] 6.5 Executar script Python:

```bash
python script_validador_horarios.py
```

- [ ] 6.6 Menu → Opção 6
- [ ] 6.7 Digitar caminho do arquivo JSON
- [ ] 6.8 Verificar resultados:
  - [ ] Todos os funcionários devem estar ✅ VÁLIDO
  - [ ] Se houver ❌, corrigir antes de importar
- [ ] 6.9 ✅ Se todos válidos, importar no Firebase

**Tempo: ~15 min** ⏱️

---

## 🚀 FASE 7: Resumo Final (5 minutos)

### ✅ Verificação Final

- [ ] 7.1 ValidadorHorario.java compilando ✅
- [ ] 7.2 ValidadorHorarioTest.java com 50+ testes passando ✅
- [ ] 7.3 Funcionario.java com validação de horário ✅
- [ ] 7.4 CadastroMelhoradoActivity exibindo horário ✅
- [ ] 7.5 Arquivo JSON de importação validado ✅
- [ ] 7.6 Documentação completa e acessível ✅

### ✅ Status

```
✅ ValidadorHorario: PRONTO
✅ Testes: PASSANDO
✅ Integração: COMPLETA
✅ Documentação: COMPLETA
✅ Dados: VALIDADOS
✅ App: TESTADO
```

**Status Geral: ✅ PRONTO PARA PRODUÇÃO**

**Tempo: ~5 min** ⏱️

---

## ⏱️ Tempo Total

| Fase | Tempo |
|------|-------|
| 1. Preparação | 5 min |
| 2. Implementação | 15 min |
| 3. Documentação | 5 min |
| 4. Testes Manuais | 10 min |
| 5. Integração | 20 min |
| 6. Importação | 15 min |
| 7. Resumo | 5 min |
| **TOTAL** | **75 min** |

---

## 🆘 Troubleshooting

### ❌ Problema: ValidadorHorario não compila

**Solução:**
1. Verificar caminho: `utils/ValidadorHorario.java`
2. Verificar imports
3. Fazer Invalidate Cache: File → Invalidate Caches

### ❌ Problema: Testes não rodam

**Solução:**
1. Verificar arquivo em: `app/src/test/java/.../ValidadorHorarioTest.java`
2. Rodar: Clique direito → Run
3. Se falhar, fazer sync Gradle

### ❌ Problema: Funcionario.java diz "Cannot resolve ValidadorHorario"

**Solução:**
1. Adicionar import: `import com.example.visualizadorapp.utils.ValidadorHorario;`
2. Fazer Sync Gradle (Ctrl+Shift+S)
3. Invalidate Cache (File → Invalidate Caches)

### ❌ Problema: Script Python não roda

**Solução:**
1. Verificar Python instalado: `python --version`
2. Rodar com: `python3` (em alguns sistemas)
3. Ou executar direto: `python script_validador_horarios.py`

### ❌ Problema: Horário não aparece na UI

**Solução:**
1. Verificar se `getHorarioFormatado()` retorna algo
2. Verificar se tem TextView com ID `tv_horario`
3. Verificar se Funcionario tem horário preenchido

---

## 📞 Links Úteis

- [PADRAO_HORARIOS_TRABALHO.md](PADRAO_HORARIOS_TRABALHO.md) - Especificação
- [GUIA_VALIDACAO_HORARIOS.md](GUIA_VALIDACAO_HORARIOS.md) - Implementação
- [RESUMO_VALIDACAO_HORARIOS.md](RESUMO_VALIDACAO_HORARIOS.md) - Visão geral
- [LISTA_ARQUIVOS_IMPLEMENTACAO.md](LISTA_ARQUIVOS_IMPLEMENTACAO.md) - Arquivos
- [INDICE.md](INDICE.md) - Documentação completa

---

## ✅ Conclusão

Se você marcou todos os ✅ nas 7 fases, o sistema de validação de horários está **100% implementado e testado**.

Você pode:
- ✅ Validar horários sem horas quebradas
- ✅ Converter turno ↔️ horário
- ✅ Exibir horários no app
- ✅ Importar dados verificados
- ✅ Garantir dados consistentes

**Próximo passo: Integrar cardápios por turno!** 🍲

---

**Data:** Janeiro 2024
**Status:** ✅ CHECKLIST PRONTO
