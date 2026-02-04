package com.example.visualizadorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComentariosActivity extends AppCompatActivity {

    private Spinner spinnerData;
    private ListView listViewComentarios;
    private TextView txtSemComentarios;
    private DatabaseReference database;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayAdapter<String> comentariosAdapter;
    private List<String> datasDisponiveis = new ArrayList<>();
    private List<String> comentariosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        database = FirebaseDatabase.getInstance("https://insights-cardapio-default-rtdb.firebaseio.com/").getReference();

        spinnerData = findViewById(R.id.spinnerDataComentarios);
        listViewComentarios = findViewById(R.id.listViewComentarios);
        txtSemComentarios = findViewById(R.id.txtSemComentarios);

        configurarSpinner();
        configurarListView();
        buscarDatasDisponiveis();
    }

    private void configurarSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datasDisponiveis);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerData.setAdapter(spinnerAdapter);

        spinnerData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!datasDisponiveis.isEmpty()) {
                    carregarComentarios(datasDisponiveis.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarListView() {
        comentariosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comentariosList);
        listViewComentarios.setAdapter(comentariosAdapter);
    }

    private void buscarDatasDisponiveis() {
        database.child("avaliacoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datasDisponiveis.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    datasDisponiveis.add(dataSnapshot.getKey());
                }
                if (datasDisponiveis.isEmpty()) {
                    txtSemComentarios.setVisibility(View.VISIBLE);
                    txtSemComentarios.setText("Nenhuma avaliação encontrada");
                } else {
                    Collections.sort(datasDisponiveis, Collections.reverseOrder());
                }
                spinnerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void carregarComentarios(String data) {
        database.child("avaliacoes").child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comentariosList.clear();
                
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String nivel = userSnapshot.child("nivel").getValue(String.class);
                    String comentario = userSnapshot.child("comentario").getValue(String.class);
                    String setor = userSnapshot.child("setor").getValue(String.class);
                    Long timestamp = userSnapshot.child("data").getValue(Long.class);

                    String emoji = getEmojiPorNivel(nivel);
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(emoji).append(" ").append(nivel != null ? nivel : "Sem avaliação");
                    sb.append("\nSetor: ").append(setor != null ? setor : "Não informado");
                    
                    if (comentario != null && !comentario.trim().isEmpty()) {
                        sb.append("\n💬 ").append(comentario);
                    }
                    
                    if (timestamp != null) {
                        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
                        sb.append("\n🕐 ").append(hora);
                    }
                    
                    comentariosList.add(sb.toString());
                }
                
                if (comentariosList.isEmpty()) {
                    txtSemComentarios.setVisibility(View.VISIBLE);
                    txtSemComentarios.setText("Nenhum comentário para esta data");
                    listViewComentarios.setVisibility(View.GONE);
                } else {
                    txtSemComentarios.setVisibility(View.GONE);
                    listViewComentarios.setVisibility(View.VISIBLE);
                }
                
                comentariosAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String getEmojiPorNivel(String nivel) {
        if (nivel == null) return "❓";
        // Remove emojis que possam já estar no texto
        nivel = nivel.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "").trim();
        switch (nivel) {
            case "Ruim": return "😞";
            case "Regular": return "😐";
            case "Bom": return "😊";
            case "Ótimo": return "😍";
            default: return "❓";
        }
    }
}
