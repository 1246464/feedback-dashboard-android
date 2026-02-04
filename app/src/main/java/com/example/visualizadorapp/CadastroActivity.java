package com.example.visualizadorapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {

    // Adicionado o campo Nome
    private EditText edtNome, edtEmail, edtSenha;
    private Spinner spinnerSetores, spinnerPlantao, spinnerPreferencia;
    private Button btnFinalizar;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // --- IMPORTANTE: INICIALIZAR O FIREBASE (Faltava no seu código) ---
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // Ligar componentes do XML
        edtNome = findViewById(R.id.edtNomeCadastro); // Novo campo
        edtEmail = findViewById(R.id.edtEmailCadastro);
        edtSenha = findViewById(R.id.edtSenhaCadastro);
        spinnerSetores = findViewById(R.id.spinner_setores);
        spinnerPlantao = findViewById(R.id.spinner_plantao);
        spinnerPreferencia = findViewById(R.id.spinnerPreferencia);
        btnFinalizar = findViewById(R.id.btnFinalizarCadastro);

        // Lista de Setores
        List<String> setores = Arrays.asList(
                "Selecione o Setor",
                "Hemade",
                "GCM",
                "Zoonoses",
                "Autarquia",
                "Maternidade",
                "SAMU",
                "PS Central"
        );
        List<String> plantoes = Arrays.asList("Plantão A", "Plantão B");
        List<String> preferencias = Arrays.asList(
                "Nenhuma restrição",
                "Vegetariano",
                "Vegano",
                "Diabético",
                "Intolerante à lactose",
                "Alérgico a glúten",
                "Hipertenso (pouco sal)"
        );
        
        ArrayAdapter<String> adapterSetor =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, setores);
        adapterSetor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSetores.setAdapter(adapterSetor);

// Corrigido também para o plantão
        ArrayAdapter<String> adapterPlantao =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plantoes);
        adapterPlantao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantao.setAdapter(adapterPlantao);
        
        ArrayAdapter<String> adapterPreferencia =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, preferencias);
        adapterPreferencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPreferencia.setAdapter(adapterPreferencia);

        btnFinalizar.setOnClickListener(v -> realizarCadastro());
    }

    private void realizarCadastro() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();
        String setor = spinnerSetores.getSelectedItem().toString();
        String plantao = spinnerPlantao.getSelectedItem().toString();
        String preferencia = spinnerPreferencia.getSelectedItem().toString();

        // Verificação incluindo o nome e plantão
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || setor.equals("Selecione o Setor") || plantao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Desabilitar botão durante cadastro
        btnFinalizar.setEnabled(false);
        btnFinalizar.setText("Cadastrando...");

        // Criar utilizado no Firebase Auth
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();

                // Criar o mapa de dados estruturado conforme o seu JSON
                Map<String, Object> dadosUsuario = new HashMap<>();
                dadosUsuario.put("nome", nome);
                dadosUsuario.put("email", email);
                dadosUsuario.put("setor", setor);
                dadosUsuario.put("plantao", plantao);
                dadosUsuario.put("preferencia", preferencia);
                dadosUsuario.put("escala", "12x36");
                dadosUsuario.put("tipo", "comum");

                // Salvar no nó "usuarios"
                database.child("usuarios").child(uid).setValue(dadosUsuario).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    // Adicionado Listener de falha para a escrita no banco de dados
                    Toast.makeText(this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnFinalizar.setEnabled(true);
                    btnFinalizar.setText("Finalizar Cadastro");
                });

            } else {
                String errorMsg = "Erro ao cadastrar";
                if (task.getException() != null && task.getException().getMessage() != null) {
                    String error = task.getException().getMessage();
                    if (error.contains("already in use")) {
                        errorMsg = "Este email já está cadastrado.";
                    } else if (error.contains("network")) {
                        errorMsg = "Sem conexão com a internet.";
                    }
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                btnFinalizar.setEnabled(true);
                btnFinalizar.setText("Finalizar Cadastro");
            }
        });
    }
}