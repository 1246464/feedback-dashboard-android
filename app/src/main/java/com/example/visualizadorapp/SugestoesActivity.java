package com.example.visualizadorapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.app.AlertDialog;

public class SugestoesActivity extends AppCompatActivity {

    private EditText edtSugestao;
    private Button btnEnviarSugestao;
    private ListView listViewSugestoes;
    private TextView txtSemSugestoes;
    private DatabaseReference database;
    private String uid;
    private SugestoesAdapter sugestoesAdapter;
    private List<Map<String, Object>> sugestoesList = new ArrayList<>();
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugestoes);

        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        edtSugestao = findViewById(R.id.edtSugestao);
        btnEnviarSugestao = findViewById(R.id.btnEnviarSugestao);
        listViewSugestoes = findViewById(R.id.listViewSugestoes);
        txtSemSugestoes = findViewById(R.id.txtSemSugestoes);

        verificarTipoUsuario();
        configurarListView();
        carregarSugestoes();

        btnEnviarSugestao.setOnClickListener(v -> enviarSugestao());
    }

    private void verificarTipoUsuario() {
        database.child("usuarios").child(uid).child("tipo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tipo = snapshot.getValue(String.class);
                        isAdmin = "admin".equals(tipo);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void configurarListView() {
        sugestoesAdapter = new SugestoesAdapter();
        listViewSugestoes.setAdapter(sugestoesAdapter);
    }

    private void carregarSugestoes() {
        database.child("sugestoes").orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sugestoesList.clear();
                        
                        for (DataSnapshot sugestaoSnapshot : snapshot.getChildren()) {
                            Map<String, Object> sugestao = new HashMap<>();
                            sugestao.put("key", sugestaoSnapshot.getKey());
                            sugestao.put("prato", sugestaoSnapshot.child("prato").getValue(String.class));
                            sugestao.put("nomeUsuario", sugestaoSnapshot.child("nomeUsuario").getValue(String.class));
                            sugestao.put("setor", sugestaoSnapshot.child("setor").getValue(String.class));
                            sugestao.put("timestamp", sugestaoSnapshot.child("timestamp").getValue(Long.class));
                            sugestao.put("status", sugestaoSnapshot.child("status").getValue(String.class));
                            
                            Long votos = sugestaoSnapshot.child("votos").getValue(Long.class);
                            sugestao.put("votos", votos != null ? votos : 0L);
                            
                            // Verificar se o usuário já votou nesta sugestão
                            boolean jaVotou = false;
                            if (sugestaoSnapshot.child("votantes").hasChild(uid)) {
                                jaVotou = true;
                            }
                            sugestao.put("jaVotou", jaVotou);
                            
                            sugestoesList.add(sugestao);
                        }
                        
                        // Inverter para mostrar mais recentes primeiro
                        Collections.reverse(sugestoesList);
                        
                        if (sugestoesList.isEmpty()) {
                            txtSemSugestoes.setVisibility(android.view.View.VISIBLE);
                            listViewSugestoes.setVisibility(android.view.View.GONE);
                        } else {
                            txtSemSugestoes.setVisibility(android.view.View.GONE);
                            listViewSugestoes.setVisibility(android.view.View.VISIBLE);
                        }
                        
                        sugestoesAdapter.notifyDataSetChanged();
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void enviarSugestao() {
        String sugestao = edtSugestao.getText().toString().trim();
        
        if (sugestao.isEmpty()) {
            Toast.makeText(this, "Por favor, escreva uma sugestão de prato", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnEnviarSugestao.setEnabled(false);
        btnEnviarSugestao.setText("Enviando...");
        
        // Buscar nome e setor do usuário
        database.child("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nome = snapshot.child("nome").getValue(String.class);
                String setor = snapshot.child("setor").getValue(String.class);
                
                Map<String, Object> sugestaoData = new HashMap<>();
                sugestaoData.put("prato", sugestao);
                sugestaoData.put("nomeUsuario", nome);
                sugestaoData.put("setor", setor);
                sugestaoData.put("uid", uid);
                sugestaoData.put("timestamp", System.currentTimeMillis());
                sugestaoData.put("votos", 0);
                sugestaoData.put("status", "pendente");
                
                database.child("sugestoes").push().setValue(sugestaoData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SugestoesActivity.this, "✓ Sugestão enviada com sucesso!", Toast.LENGTH_SHORT).show();
                            edtSugestao.setText("");
                            btnEnviarSugestao.setEnabled(true);
                            btnEnviarSugestao.setText("Enviar Sugestão");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SugestoesActivity.this, "Erro ao enviar sugestão", Toast.LENGTH_SHORT).show();
                            btnEnviarSugestao.setEnabled(true);
                            btnEnviarSugestao.setText("Enviar Sugestão");
                        });
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnEnviarSugestao.setEnabled(true);
                btnEnviarSugestao.setText("Enviar Sugestão");
            }
        });
    }

    private void votarSugestao(String sugestaoKey, boolean jaVotou) {
        DatabaseReference sugestaoRef = database.child("sugestoes").child(sugestaoKey);
        
        if (jaVotou) {
            // Remover voto
            sugestaoRef.child("votantes").child(uid).removeValue();
            sugestaoRef.child("votos").get().addOnSuccessListener(snapshot -> {
                Long votosAtuais = snapshot.getValue(Long.class);
                if (votosAtuais != null && votosAtuais > 0) {
                    sugestaoRef.child("votos").setValue(votosAtuais - 1);
                }
            });
        } else {
            // Adicionar voto
            sugestaoRef.child("votantes").child(uid).setValue(true);
            sugestaoRef.child("votos").get().addOnSuccessListener(snapshot -> {
                Long votosAtuais = snapshot.getValue(Long.class);
                sugestaoRef.child("votos").setValue((votosAtuais != null ? votosAtuais : 0) + 1);
            });
        }
    }

    private void alterarStatusSugestao(String sugestaoKey, String novoStatus) {
        database.child("sugestoes").child(sugestaoKey).child("status").setValue(novoStatus)
                .addOnSuccessListener(aVoid -> {
                    String mensagem = novoStatus.equals("aprovado") ? "✓ Sugestão aprovada!" : "✗ Sugestão negada";
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao alterar status", Toast.LENGTH_SHORT).show();
                });
    }

    private class SugestoesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sugestoesList.size();
        }

        @Override
        public Object getItem(int position) {
            return sugestoesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_sugestao, parent, false);
            }

            Map<String, Object> sugestao = sugestoesList.get(position);
            String key = (String) sugestao.get("key");
            String prato = (String) sugestao.get("prato");
            String nomeUsuario = (String) sugestao.get("nomeUsuario");
            String setor = (String) sugestao.get("setor");
            Long timestamp = (Long) sugestao.get("timestamp");
            Long votos = (Long) sugestao.get("votos");
            String status = (String) sugestao.get("status");
            boolean jaVotou = (boolean) sugestao.get("jaVotou");

            TextView txtConteudo = convertView.findViewById(R.id.txtConteudoSugestao);
            TextView txtStatus = convertView.findViewById(R.id.txtStatusSugestao);
            Button btnVotar = convertView.findViewById(R.id.btnVotarSugestao);
            Button btnAprovar = convertView.findViewById(R.id.btnAprovarSugestao);
            Button btnNegar = convertView.findViewById(R.id.btnNegarSugestao);

            // Conteúdo
            StringBuilder sb = new StringBuilder();
            sb.append("👍 ").append(votos).append(" votos\n");
            sb.append("🍽️ ").append(prato != null ? prato : "Sem descrição");
            sb.append("\n👤 Por: ").append(nomeUsuario != null ? nomeUsuario : "Anônimo");
            sb.append(" (").append(setor != null ? setor : "").append(")");
            
            if (timestamp != null) {
                String data = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(new Date(timestamp));
                sb.append("\n📅 ").append(data);
            }
            txtConteudo.setText(sb.toString());

            // Status
            if (status == null || status.equals("pendente")) {
                txtStatus.setText("⏳ Aguardando análise");
                txtStatus.setTextColor(0xFF757575);
            } else if (status.equals("aprovado")) {
                txtStatus.setText("✓ Aprovado");
                txtStatus.setTextColor(0xFF4CAF50);
            } else if (status.equals("negado")) {
                txtStatus.setText("✗ Negado");
                txtStatus.setTextColor(0xFFF44336);
            }

            // Botão votar (visível para usuários comuns)
            if (!isAdmin) {
                btnVotar.setVisibility(View.VISIBLE);
                btnVotar.setText(jaVotou ? "❤️ Votado" : "👍 Votar");
                btnVotar.setBackgroundColor(jaVotou ? 0xFFFF5722 : 0xFF2196F3);
                btnVotar.setOnClickListener(v -> votarSugestao(key, jaVotou));
                
                btnAprovar.setVisibility(View.GONE);
                btnNegar.setVisibility(View.GONE);
            } else {
                // Botões admin (apenas se pendente)
                btnVotar.setVisibility(View.GONE);
                
                if (status == null || status.equals("pendente")) {
                    btnAprovar.setVisibility(View.VISIBLE);
                    btnNegar.setVisibility(View.VISIBLE);
                    btnAprovar.setOnClickListener(v -> alterarStatusSugestao(key, "aprovado"));
                    btnNegar.setOnClickListener(v -> alterarStatusSugestao(key, "negado"));
                } else {
                    btnAprovar.setVisibility(View.GONE);
                    btnNegar.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }
}
