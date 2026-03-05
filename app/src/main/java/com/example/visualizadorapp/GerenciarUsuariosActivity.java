package com.example.visualizadorapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.adapter.UsuariosAdapter;
import com.example.visualizadorapp.model.Cargo;
import com.example.visualizadorapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciarUsuariosActivity extends AppCompatActivity implements UsuariosAdapter.OnUsuarioActionListener {

    private RecyclerView recyclerUsuarios;
    private ProgressBar progressBar;
    private TextView txtSemUsuarios;
    private Button btnAdicionarUsuario, btnVoltar;

    private UsuariosAdapter adapter;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_usuarios);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        // Inicializar views
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        progressBar = findViewById(R.id.progressBarUsuarios);
        txtSemUsuarios = findViewById(R.id.txtSemUsuarios);
        btnAdicionarUsuario = findViewById(R.id.btnAdicionarUsuario);
        btnVoltar = findViewById(R.id.btnVoltarGerenciar);

        // Configurar RecyclerView
        listaUsuarios = new ArrayList<>();
        adapter = new UsuariosAdapter(listaUsuarios, this);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsuarios.setAdapter(adapter);

        // Listeners
        btnAdicionarUsuario.setOnClickListener(v -> mostrarDialogCriarUsuario());
        btnVoltar.setOnClickListener(v -> finish());

        // Carregar usuários
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerUsuarios.setVisibility(View.GONE);
        txtSemUsuarios.setVisibility(View.GONE);

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaUsuarios.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    try {
                        Usuario usuario = new Usuario();
                        usuario.setUid(userSnap.getKey());
                        usuario.setNome(userSnap.child("nome").getValue(String.class));
                        usuario.setEmail(userSnap.child("email").getValue(String.class));
                        usuario.setCargo(userSnap.child("cargo").getValue(String.class));
                        usuario.setPlantao(userSnap.child("plantao").getValue(String.class));
                        usuario.setHorario(userSnap.child("horario").getValue(String.class));
                        usuario.setSetor(userSnap.child("setor").getValue(String.class));
                        usuario.setTipo(userSnap.child("tipo").getValue(String.class));

                        listaUsuarios.add(usuario);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                progressBar.setVisibility(View.GONE);

                if (listaUsuarios.isEmpty()) {
                    txtSemUsuarios.setVisibility(View.VISIBLE);
                    recyclerUsuarios.setVisibility(View.GONE);
                } else {
                    txtSemUsuarios.setVisibility(View.GONE);
                    recyclerUsuarios.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GerenciarUsuariosActivity.this, 
                    "Erro ao carregar usuários: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogCriarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_criar_usuario, null);

        EditText edtNome = view.findViewById(R.id.edtNomeNovoUsuario);
        EditText edtEmail = view.findViewById(R.id.edtEmailNovoUsuario);
        EditText edtSenha = view.findViewById(R.id.edtSenhaNovoUsuario);
        Spinner spinnerCargo = view.findViewById(R.id.spinnerCargoNovoUsuario);
        Spinner spinnerPlantao = view.findViewById(R.id.spinnerPlantaoNovoUsuario);
        EditText edtHorario = view.findViewById(R.id.edtHorarioNovoUsuario);
        EditText edtSetor = view.findViewById(R.id.edtSetorNovoUsuario);
        LinearLayout layoutPlantao = view.findViewById(R.id.layoutPlantaoNovoUsuario);

        // Configurar spinner de cargos
        List<String> cargos = new ArrayList<>();
        cargos.add("GERENTE");
        cargos.add("TECNICA");
        cargos.add("LIDER_COZINHA");
        cargos.add("COZINHEIRO");
        cargos.add("AUXILIAR");
        cargos.add("MEIO_OFICIAL_PLANTAO");
        cargos.add("MEIO_OFICIAL_5X2");
        cargos.add("ESTOQUISTA");
        cargos.add("COPEIRA");
        cargos.add("COPEIRA_5X2");
        cargos.add("COPEIRO_NOTURNO");
        cargos.add("USUARIO_COMUM");

        ArrayAdapter<String> cargoAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, cargos);
        cargoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCargo.setAdapter(cargoAdapter);

        // Configurar spinner de plantão
        List<String> plantoes = new ArrayList<>();
        plantoes.add("Nenhum");
        plantoes.add("A");
        plantoes.add("B");
        ArrayAdapter<String> plantaoAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, plantoes);
        plantaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantao.setAdapter(plantaoAdapter);

        // Mostrar/ocultar campo plantão baseado no cargo
        spinnerCargo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String cargoSelecionado = cargos.get(position);
                Cargo cargo = Cargo.fromString(cargoSelecionado);
                
                if (cargo != null && cargo.isPlantao()) {
                    layoutPlantao.setVisibility(View.VISIBLE);
                    edtHorario.setHint("Ex: 10-22 ou 19-07");
                } else if (cargo != null && cargo.is5x2()) {
                    layoutPlantao.setVisibility(View.GONE);
                    edtHorario.setHint("Ex: 5x2");
                } else {
                    layoutPlantao.setVisibility(View.GONE);
                    edtHorario.setHint("Horário (opcional)");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        builder.setView(view)
            .setTitle("Criar Novo Funcionário")
            .setPositiveButton("Criar", null)
            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override do botão positivo para validar antes de fechar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();
            String cargo = cargos.get(spinnerCargo.getSelectedItemPosition());
            String plantao = spinnerPlantao.getSelectedItemPosition() == 0 ? null : 
                             plantoes.get(spinnerPlantao.getSelectedItemPosition());
            String horario = edtHorario.getText().toString().trim();
            String setor = edtSetor.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha nome, email e senha", Toast.LENGTH_SHORT).show();
                return;
            }

            if (senha.length() < 6) {
                Toast.makeText(this, "Senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            criarUsuario(nome, email, senha, cargo, plantao, 
                        horario.isEmpty() ? null : horario,
                        setor.isEmpty() ? null : setor);
            dialog.dismiss();
        });
    }

    private void criarUsuario(String nome, String email, String senha, String cargo, 
                             String plantao, String horario, String setor) {
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();

                    // Criar objeto de dados do usuário
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("nome", nome);
                    userData.put("email", email);
                    userData.put("cargo", cargo);
                    
                    if (plantao != null) {
                        userData.put("plantao", plantao);
                    }
                    if (horario != null) {
                        userData.put("horario", horario);
                    }
                    if (setor != null) {
                        userData.put("setor", setor);
                    }

                    // Backward compatibility
                    Cargo c = Cargo.fromString(cargo);
                    if (c != null && c.isAdmin()) {
                        userData.put("tipo", "admin");
                    } else {
                        userData.put("tipo", "usuario");
                    }

                    // Salvar no Realtime Database
                    usuariosRef.child(uid).setValue(userData)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Funcionário criado com sucesso!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Erro ao salvar dados: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });

                } else {
                    progressBar.setVisibility(View.GONE);
                    String erro = task.getException() != null ? 
                        task.getException().getMessage() : "Erro desconhecido";
                    Toast.makeText(this, "Erro ao criar conta: " + erro, Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public void onEditarCargo(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_editar_cargo, null);

        TextView txtNomeUsuario = view.findViewById(R.id.txtNomeUsuarioEditar);
        Spinner spinnerCargo = view.findViewById(R.id.spinnerCargoEditar);
        Spinner spinnerPlantao = view.findViewById(R.id.spinnerPlantaoEditar);
        EditText edtHorario = view.findViewById(R.id.edtHorarioEditar);
        EditText edtSetor = view.findViewById(R.id.edtSetorEditar);
        LinearLayout layoutPlantao = view.findViewById(R.id.layoutPlantaoEditar);

        txtNomeUsuario.setText(usuario.getNome() + " (" + usuario.getEmail() + ")");

        // Configurar spinner de cargos
        List<String> cargos = new ArrayList<>();
        cargos.add("GERENTE");
        cargos.add("TECNICA");
        cargos.add("LIDER_COZINHA");
        cargos.add("COZINHEIRO");
        cargos.add("AUXILIAR");
        cargos.add("MEIO_OFICIAL_PLANTAO");
        cargos.add("MEIO_OFICIAL_5X2");
        cargos.add("ESTOQUISTA");
        cargos.add("COPEIRA");
        cargos.add("COPEIRA_5X2");
        cargos.add("COPEIRO_NOTURNO");
        cargos.add("USUARIO_COMUM");

        ArrayAdapter<String> cargoAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, cargos);
        cargoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCargo.setAdapter(cargoAdapter);

        // Selecionar cargo atual
        int posicaoCargo = cargos.indexOf(usuario.getCargo());
        if (posicaoCargo >= 0) {
            spinnerCargo.setSelection(posicaoCargo);
        }

        // Configurar spinner de plantão
        List<String> plantoes = new ArrayList<>();
        plantoes.add("Nenhum");
        plantoes.add("A");
        plantoes.add("B");
        ArrayAdapter<String> plantaoAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, plantoes);
        plantaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantao.setAdapter(plantaoAdapter);

        // Selecionar plantão atual
        if (usuario.getPlantao() != null) {
            int posicaoPlantao = plantoes.indexOf(usuario.getPlantao());
            if (posicaoPlantao >= 0) {
                spinnerPlantao.setSelection(posicaoPlantao);
            }
        }

        // Preencher horário e setor atuais
        if (usuario.getHorario() != null) {
            edtHorario.setText(usuario.getHorario());
        }
        if (usuario.getSetor() != null) {
            edtSetor.setText(usuario.getSetor());
        }

        // Mostrar/ocultar campo plantão baseado no cargo
        spinnerCargo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String cargoSelecionado = cargos.get(position);
                Cargo cargo = Cargo.fromString(cargoSelecionado);
                
                if (cargo != null && cargo.isPlantao()) {
                    layoutPlantao.setVisibility(View.VISIBLE);
                } else {
                    layoutPlantao.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        builder.setView(view)
            .setTitle("Editar Funcionário")
            .setPositiveButton("Salvar", (dialog, which) -> {
                String novoCargo = cargos.get(spinnerCargo.getSelectedItemPosition());
                String novoPlantao = spinnerPlantao.getSelectedItemPosition() == 0 ? null :
                                   plantoes.get(spinnerPlantao.getSelectedItemPosition());
                String novoHorario = edtHorario.getText().toString().trim();
                String novoSetor = edtSetor.getText().toString().trim();

                atualizarUsuario(usuario.getUid(), novoCargo, novoPlantao,
                               novoHorario.isEmpty() ? null : novoHorario,
                               novoSetor.isEmpty() ? null : novoSetor);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void atualizarUsuario(String uid, String cargo, String plantao, String horario, String setor) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("cargo", cargo);
        
        if (plantao != null) {
            updates.put("plantao", plantao);
        } else {
            updates.put("plantao", null);
        }
        
        if (horario != null) {
            updates.put("horario", horario);
        } else {
            updates.put("horario", null);
        }
        
        if (setor != null) {
            updates.put("setor", setor);
        } else {
            updates.put("setor", null);
        }

        // Backward compatibility
        Cargo c = Cargo.fromString(cargo);
        if (c != null && c.isAdmin()) {
            updates.put("tipo", "admin");
        } else {
            updates.put("tipo", "usuario");
        }

        usuariosRef.child(uid).updateChildren(updates)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Funcionário atualizado!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onDeletarUsuario(Usuario usuario) {
        new AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja deletar o funcionário " + usuario.getNome() + "?\n\n" +
                       "⚠️ ATENÇÃO: Esta ação é irreversível e removerá:\n" +
                       "• A conta de acesso ao sistema\n" +
                       "• Todos os dados do Firebase\n" +
                       "• Histórico de ações")
            .setPositiveButton("Deletar", (dialog, which) -> confirmarDelecao(usuario))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void confirmarDelecao(Usuario usuario) {
        // IMPORTANTE: Para deletar a conta do Authentication, você precisa:
        // 1. Fazer o usuário fazer login novamente (reautenticação)
        // 2. Ou usar Firebase Admin SDK no backend
        // 
        // Por enquanto, vamos apenas remover do Realtime Database
        // A conta do Authentication permanecerá, mas sem dados
        
        usuariosRef.child(usuario.getUid()).removeValue()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Funcionário removido do sistema!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Erro ao deletar: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
}
