package com.example.visualizadorapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visualizadorapp.utils.ValidadorCPF;
import com.example.visualizadorapp.utils.ValidadorTelefone;
import com.example.visualizadorapp.utils.ValidadorHorario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Atividade de Cadastro - Novo Usuário com Validação de Formato
 */
public class CadastroMelhoradoActivity extends AppCompatActivity {

    // Componentes da interface
    private EditText edtEmail, edtCPF, edtTelefone, edtSenha, edtConfirmaSenha, edtCargo;
    private Spinner spinnerSetor, spinnerPlantao, spinnerHorario, spinnerPreferencia;
    private Button btnFinalizarCadastro;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_melhorado);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // Ligar componentes
        bindViews();

        // Configurar listeners
        setupListeners();

        // Configurar spinners
        setupSpinners();
    }

    private void bindViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtCPF = findViewById(R.id.edtCPF);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmaSenha = findViewById(R.id.edtConfirmaSenha);
        edtCargo = findViewById(R.id.edtCargo);
        
        spinnerSetor = findViewById(R.id.spinnerSetor);
        spinnerPlantao = findViewById(R.id.spinnerPlantao);
        spinnerHorario = findViewById(R.id.spinnerHorario);
        spinnerPreferencia = findViewById(R.id.spinnerPreferencia);
        
        btnFinalizarCadastro = findViewById(R.id.btnFinalizarCadastro);
    }

    private void setupListeners() {
        // Formatar CPF automaticamente
        edtCPF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().replaceAll("\\D", "");
                if (texto.length() <= 11) {
                    edtCPF.removeTextChangedListener(this);
                    edtCPF.setText(ValidadorCPF.formatar(texto));
                    edtCPF.setSelection(edtCPF.getText().length());
                    edtCPF.addTextChangedListener(this);
                }
            }
        });

        // Formatar Telefone automaticamente
        edtTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().replaceAll("\\D", "");
                if (texto.length() <= 11) {
                    edtTelefone.removeTextChangedListener(this);
                    edtTelefone.setText(ValidadorTelefone.formatar(texto));
                    edtTelefone.setSelection(edtTelefone.getText().length());
                    edtTelefone.addTextChangedListener(this);
                }
            }
        });

        // Botão Finalizar Cadastro
        btnFinalizarCadastro.setOnClickListener(v -> finalizarCadastro());
    }

    private void setupSpinners() {
        // Setores (apenas setores hospitalares públicos - Cozinha/Copa/Estoque são criados pelo admin)
        List<String> setores = Arrays.asList(
                "Selecionar Setor",
                "MATERNIDADE", "SAMU", "Zoonoses",
                "Hemade", "GCM", "Autarquia", "PS Central"
        );
        setupSpinner(spinnerSetor, setores);

        // Plantões (exceto para 5x2, que são fixos segunda-sexta)
        List<String> plantoes = Arrays.asList(
                "Selecionar Plantão",
                "Plantão A",
                "Plantão B"
        );
        setupSpinner(spinnerPlantao, plantoes);

        // Horários (validados)
        List<String> horarios = Arrays.asList(
                "Selecionar Horário",
                "07:00-17:00 (5x2)",
                "06:00-18:00 (Manhã)",
                "18:00-06:00 (Noite)"
        );
        setupSpinner(spinnerHorario, horarios);

        // Preferências Alimentares
        List<String> preferencias = Arrays.asList(
                "Nenhuma restrição",
                "Vegetariano", "Vegano", "Diabético",
                "Intolerante à lactose", "Alérgico a glúten", "Hipertenso"
        );
        setupSpinner(spinnerPreferencia, preferencias);
    }

    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void finalizarCadastro() {
        String email = edtEmail.getText().toString().trim();
        String cpf = ValidadorCPF.removerFormatacao(edtCPF.getText().toString().trim());
        String telefone = ValidadorTelefone.removerFormatacao(edtTelefone.getText().toString().trim());
        String cargo = edtCargo.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();
        String confirmaSenha = edtConfirmaSenha.getText().toString().trim();
        
        String setor = spinnerSetor.getSelectedItem().toString();
        String plantao = spinnerPlantao.getSelectedItem().toString();
        String horario = spinnerHorario.getSelectedItem().toString();
        String preferencia = spinnerPreferencia.getSelectedItem().toString();

        // Validações
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidadorCPF.isValido(cpf)) {
            Toast.makeText(this, "CPF inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidadorTelefone.isValido(telefone)) {
            Toast.makeText(this, "Telefone inválido (11 dígitos)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cargo.isEmpty()) {
            Toast.makeText(this, "Digite um cargo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (setor.equals("Selecionar Setor")) {
            Toast.makeText(this, "Selecione um setor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (plantao.equals("Selecionar Plantão")) {
            Toast.makeText(this, "Selecione um plantão", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horario.equals("Selecionar Horário")) {
            Toast.makeText(this, "Selecione um horário", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.length() < 6 || !senha.equals(confirmaSenha)) {
            Toast.makeText(this, "Verifique as senhas (mínimo 6 caracteres)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extrair horário em formato HH:MM-HH:MM
        String horarioFormatado = extrairHorario(horario);

        criarConta(email, senha, cpf, telefone, cargo, setor, plantao, horarioFormatado, preferencia);
    }

    private String extrairHorario(String horarioSelecionado) {
        if (horarioSelecionado.contains("5x2")) return "07:00-17:00";
        if (horarioSelecionado.contains("Manhã")) return "06:00-18:00";
        if (horarioSelecionado.contains("Noite")) return "18:00-06:00";
        return "07:00-17:00";
    }

    private void criarConta(String email, String senha, String cpf, String telefone, 
                           String cargo, String setor, String plantao, String horario, String preferencia) {
        btnFinalizarCadastro.setEnabled(false);
        btnFinalizarCadastro.setText("Cadastrando...");

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                Map<String, Object> dados = new HashMap<>();
                dados.put("uid", uid);
                dados.put("email", email);
                dados.put("cpf", cpf);
                dados.put("telefone", telefone);
                dados.put("cargo", cargo);
                dados.put("setor", setor);
                dados.put("plantao", plantao);
                dados.put("horario", horario);
                dados.put("preferencia", preferencia);
                dados.put("dataRegistro", System.currentTimeMillis());
                dados.put("tipo", "usuario");
                dados.put("validado", true);

                database.child("usuarios").child(uid).setValue(dados)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            btnFinalizarCadastro.setEnabled(true);
                            btnFinalizarCadastro.setText("Finalizar Cadastro");
                            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                btnFinalizarCadastro.setEnabled(true);
                btnFinalizarCadastro.setText("Finalizar Cadastro");
                Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
