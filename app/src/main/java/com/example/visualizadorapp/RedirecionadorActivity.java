package com.example.visualizadorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visualizadorapp.model.Cargo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity que redireciona o usuário para a tela correta baseado no seu cargo
 */
public class RedirecionadorActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Não logado, volta para login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Carregar cargo do usuário e redirecionar
        carregarCargoERedirecionar(user.getUid());
    }

    private void carregarCargoERedirecionar(String uid) {
        database.child("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(RedirecionadorActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RedirecionadorActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                // Pegar cargo e tipo
                String cargoStr = snapshot.child("cargo").getValue(String.class);
                String tipo = snapshot.child("tipo").getValue(String.class);

                // Tratamento de compatibilidade com sistema antigo
                if (cargoStr == null || cargoStr.isEmpty()) {
                    // Sistema antigo: usar "tipo" (admin ou comum)
                    if ("admin".equalsIgnoreCase(tipo)) {
                        startActivity(new Intent(RedirecionadorActivity.this, AdminActivity.class));
                    } else {
                        startActivity(new Intent(RedirecionadorActivity.this, MainActivity.class));
                    }
                    finish();
                    return;
                }

                // Sistema novo: usar "cargo"
                Cargo cargo = Cargo.fromString(cargoStr);
                redirecionarPorCargo(cargo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RedirecionadorActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RedirecionadorActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void redirecionarPorCargo(Cargo cargo) {
        Intent intent;

        switch (cargo) {
            case GERENTE:
                // Gerente vai para painel administrativo
                intent = new Intent(this, AdminActivity.class);
                break;

            case TECNICA:
                // Técnica tem dashboard específico
                intent = new Intent(this, DashboardTecnicaActivity.class);
                break;

            case LIDER_COZINHA:
                // Líder de cozinha tem dashboard específico
                intent = new Intent(this, DashboardLiderActivity.class);
                break;

            case COZINHEIRO:
            case AUXILIAR:
            case MEIO_OFICIAL_PLANTAO:
            case MEIO_OFICIAL_5X2:
                // Pessoal da cozinha vê tarefas de preparo
                intent = new Intent(this, DashboardCozinhaActivity.class);
                break;

            case ESTOQUISTA:
                // Estoquista tem dashboard de estoque
                intent = new Intent(this, DashboardEstoqueActivity.class);
                break;

            case COPEIRA:
            case COPEIRA_5X2:
            case COPEIRO_NOTURNO:
                // Copeiras/copeiros veem cardápio e reservas
                intent = new Intent(this, DashboardCopeiraActivity.class);
                break;

            case USUARIO_COMUM:
            default:
                // Usuário comum vê cardápio e pode votar/avaliar
                intent = new Intent(this, MainActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
}
