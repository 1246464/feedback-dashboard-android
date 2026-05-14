# ⚡ REFERÊNCIA RÁPIDA - SISTEMA PLANTÃO + HORÁRIO

## 🎯 O QUE MUDOU

### ❌ Sistema Antigo (DEPRECATED)
```
1 Campo: "Turno"
- MANHA (06:00-18:00)
- NOITE (18:00-06:00)
- 5X2 (07:00-17:00)
```

### ✅ Novo Sistema (Maio 2026)
```
2 Campos complementares:

1. PLANTÃO (A ou B)
   Define QUAL DIA TRABALHA
   
2. HORÁRIO (MANHA/NOITE/5X2)
   Define QUE HORAS TRABALHA
```

---

## 📊 TABELA DE COMBINAÇÕES

### 12x36 (Plantão obrigatório)

| Plantão | Dias | Horário | Exemplo |
|---------|------|---------|---------|
| A | Seg/Qua/Sex | MANHA 06:00-18:00 | Cozinheiro |
| A | Seg/Qua/Sex | NOITE 18:00-06:00 | Copeira Noturna |
| B | Ter/Qui/Sab | MANHA 06:00-18:00 | Auxiliar |
| B | Ter/Qui/Sab | NOITE 18:00-06:00 | Estoquista |

### 5X2 (Sem plantão)

| Plantão | Dias | Horário | Exemplo |
|---------|------|---------|---------|
| N/A | Seg-Sex | 5X2 07:00-17:00 | Líder Cozinha |
| N/A | Seg-Sex | 5X2 07:00-17:00 | Técnica Nutrição |

---

## 🗓️ MAPA DE DIAS

```
Plantão A (dias {1,3,5}):
  Seg(1) Ter(2) Qua(3) Qui(4) Sex(5) Sab(6) Dom(7)
  ✅    ❌    ✅    ❌    ✅    ❌    ❌

Plantão B (dias {2,4,6}):
  Seg(1) Ter(2) Qua(3) Qui(4) Sex(5) Sab(6) Dom(7)
  ❌    ✅    ❌    ✅    ❌    ✅    ❌

5X2 (dias {1,2,3,4,5}):
  Seg(1) Ter(2) Qua(3) Qui(4) Sex(5) Sab(6) Dom(7)
  ✅    ✅    ✅    ✅    ✅    ❌    ❌
```

---

## 🍽️ REGRAS DE FILTRO

### Usuário MANHA (06:00-18:00)
```
Plantão A? Ver TARDE (seg/qua/sex)
Plantão B? Ver TARDE (ter/qui/sab)
Nunca vê: NOITE
```

### Usuário NOITE (18:00-06:00)
```
Plantão A? Ver NOITE (seg/qua/sex)
Plantão B? Ver NOITE (ter/qui/sab)
Nunca vê: TARDE
```

### Usuário 5X2 (07:00-17:00)
```
Ver TARDE apenas de seg-sex (dias que trabalha)
Nunca vê: NOITE, sábado, domingo
```

---

## 💾 FIREBASE SCHEMA

```json
{
  "users": {
    "uid123": {
      "email": "joao@hospital.com",
      "cpf": "12345678901",
      "telefone": "11987654321",
      "cargo": "Cozinheiro",
      "setor": "COZINHA",
      "plantao": "A",
      "horario": "06:00-18:00",
      "preferencia": "VEGETARIANO",
      "dataRegistro": "2026-05-14",
      "tipo": "FUNCIONARIO",
      "validado": false
    }
  }
}
```

---

## 🔢 SETORES DISPONÍVEIS (10)

```
1. COZINHA
2. COPA
3. ESTOQUE
4. MATERNIDADE
5. SAMU
6. Zoonoses
7. HEMADE
8. GCM
9. Autarquia
10. PS Central
```

---

## 👨‍💼 CAMPOS DE CADASTRO

```
1. Email (validation pattern)
2. CPF (11 dígitos, módulo 11)
3. Telefone (11 dígitos)
4. Cargo (EditText - texto livre)
5. Setor (Spinner - 10 opções)
6. Plantão (Spinner - A/B)
7. Horário (Spinner - 3 opções)
8. Preferência (Spinner)
9. Senha (min 6 caracteres)
10. Confirmar Senha
```

---

## 🏗️ ARQUITETURA

### CardapiosViewModel Flow

```
1. User faz login
   ↓
2. CardapiosViewModel.carregarCardapiosParaUsuario()
   ↓
3. FuncionarioRepository busca uid do user
   ↓
4. Obtém: horario + plantao do Firebase
   ↓
5. FiltroVisibilidadeCardapio.podeVerCardapio()
   ↓
6. Retorna lista filtrada (cardapiosVisiveis)
   ↓
7. Dashboard mostra apenas cardápios permitidos
```

### Classe Principal: FiltroVisibilidadeCardapio

```java
public static boolean podeVerCardapio(
    String horarioUsuario,      // "06:00-18:00", "18:00-06:00", "07:00-17:00"
    String turnoCardapio,        // "TARDE" ou "NOITE"
    String dataCardapio,         // "2026-05-14"
    int[] diasTrabalho           // {1,3,5} ou {2,4,6} ou {1,2,3,4,5}
)
```

---

## 📱 ACTIVITIES ATUALIZADAS (8)

```
1. CadastroMelhoradoActivity
   - Form com novos campos
   - Salva plantao em Firebase

2-7. 6 Dashboards
   - DashboardCozinhaActivity
   - DashboardCopeiraActivity
   - DashboardEstoqueActivity
   - DashboardTecnicaActivity
   - DashboardLiderActivity
   - DashboardPreparoActivity
   
   Todos: Usam CardapiosViewModelFactory

8. ReservaActivity
   - Usa CardapiosViewModelFactory
   - Mostra cardápio filtrado

9. HistoricoActivity
   - Usa CardapiosViewModelFactory
   - Busca com filtro
```

---

## 🚨 VALIDAÇÕES IMPORTANTES

```
❌ Não deixa salvar:
- Plantão sem um horário 12x36
- 5x2 com Plantão selecionado
- Horário vazio

✅ Deixa salvar:
- Qualquer cargo (EditText livre)
- Combinação válida de Plantão + Horário
- Dados incompletos no Firebase (usa fallback)
```

---

## 🐛 TROUBLESHOOTING

### "Cardápios não aparecem"
- Verificar se usuário tem `horario` + `plantao` salvos no Firebase
- Logs em CardapiosViewModel.carregarCardapiosParaUsuario()
- Validar FiltroVisibilidadeCardapio.podeVerCardapio()

### "Room schema error"
- Incrementar versão em AppDatabase.java
- Limpar banco: Logcat mostra hash esperado vs encontrado
- Banco é reconstruído automaticamente

### "ViewModel null"
- Verificar se CardapiosViewModelFactory é usada
- Não usar ViewModelProvider(this).get() direto
- Sempre passar factory com repositórios

---

## 📚 DOCUMENTAÇÃO

```
✅ GUIA_ATUALIZACAO_SISTEMA_2026.md
   Guia completo com exemplos práticos

✅ RESUMO_FINAL_MAIO_2026.md
   Estado final do projeto

✅ RESUMO_SESSAO_MAI14_2026.md
   Como resolvemos os crashes

✅ REFERENCIA_RAPIDA_PLANTAO_HORARIO.md (este arquivo)
   Referência rápida
```

---

## 🔮 PRÓXIMAS VERSÕES

- [ ] Room Migrations (preservar dados)
- [ ] Offline-first cardápios
- [ ] Push notifications
- [ ] Analytics dashboard
- [ ] App web

---

**Última atualização:** 14 maio 2026  
**Status:** ✅ Pronto para uso  
**Versão:** 2.0.0
