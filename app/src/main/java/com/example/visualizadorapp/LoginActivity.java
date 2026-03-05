package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;


public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnLogin, btnCriarConta;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCriarConta = findViewById(R.id.btnCriarConta);
        TextView txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

        // BOTÃO LOGIN: Apenas entra no sistema
        btnLogin.setOnClickListener(v -> loginUsuario());

        // BOTÃO CRIAR CONTA: Abre a tela de cadastro que criamos
        btnCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // LINK ESQUECI SENHA: Envia email de recuperação
        txtEsqueciSenha.setOnClickListener(v -> recuperarSenha());
    }

    private void loginUsuario() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Desabilitar botão durante login
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            btnLogin.setEnabled(true);
            btnLogin.setText("Entrar");
            
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                // Referência exata ao seu nó de usuários
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(uid);

                userRef.get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        // Agora usa RedirecionadorActivity para direcionar baseado no cargo
                        Intent intent = new Intent(this, RedirecionadorActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // SE CAIR AQUI: O login deu certo, mas o usuário não foi criado no Database
                        Toast.makeText(this, "Erro: Perfil não encontrado no banco de dados.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> {
                    // SE CAIR AQUI: É problema de permissão (Regras) ou internet
                    Toast.makeText(this, "Erro no Banco: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

            } else {
                Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recuperarSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Senha");
        builder.setMessage("Digite seu email para receber o link de recuperação:");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Email");
        input.setText(edtEmail.getText().toString()); // Preenche com email já digitado
        builder.setView(input);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite um email válido", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> 
                    Toast.makeText(this, "Email de recuperação enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}