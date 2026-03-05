package com.example.visualizadorapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.R;
import com.example.visualizadorapp.model.TarefaPreparo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TarefaPreparoAdapter extends RecyclerView.Adapter<TarefaPreparoAdapter.TarefaViewHolder> {
    
    private List<TarefaPreparo> tarefas = new ArrayList<>();
    private OnTarefaClickListener onTarefaClickListener;
    private OnTarefaIniciarListener onTarefaIniciarListener;
    private OnTarefaConcluirListener onTarefaConcluirListener;
    
    public interface OnTarefaClickListener {
        void onTarefaClick(TarefaPreparo tarefa);
    }
    
    public interface OnTarefaIniciarListener {
        void onTarefaIniciar(TarefaPreparo tarefa);
    }
    
    public interface OnTarefaConcluirListener {
        void onTarefaConcluir(TarefaPreparo tarefa);
    }
    
    public TarefaPreparoAdapter(OnTarefaClickListener clickListener,
                                OnTarefaIniciarListener iniciarListener,
                                OnTarefaConcluirListener concluirListener) {
        this.onTarefaClickListener = clickListener;
        this.onTarefaIniciarListener = iniciarListener;
        this.onTarefaConcluirListener = concluirListener;
    }
    
    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_tarefa_preparo, parent, false);
        return new TarefaViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        TarefaPreparo tarefa = tarefas.get(position);
        
        holder.textDescricao.setText(tarefa.getDescricaoTarefa());
        holder.textTurno.setText("Turno: " + tarefa.getTurnoResponsavel());
        holder.textStatus.setText(getStatusTexto(tarefa.getStatus()));
        holder.textPrioridade.setText("Prioridade: " + tarefa.getPrioridade());
        
        // Cor da prioridade
        holder.viewPrioridade.setBackgroundColor(getCorPrioridade(tarefa.getPrioridade()));
        
        // Status visual
        holder.cardTarefa.setCardBackgroundColor(getCorStatus(tarefa.getStatus()));
        
        // Responsável
        if (tarefa.getResponsavel() != null && !tarefa.getResponsavel().isEmpty()) {
            holder.textResponsavel.setText("Responsável: " + tarefa.getResponsavel());
            holder.textResponsavel.setVisibility(View.VISIBLE);
        } else {
            holder.textResponsavel.setVisibility(View.GONE);
        }
        
        // Timestamps
        if (tarefa.getTimestampInicio() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.textHoraInicio.setText("Iniciada: " + sdf.format(new Date(tarefa.getTimestampInicio())));
            holder.textHoraInicio.setVisibility(View.VISIBLE);
        } else {
            holder.textHoraInicio.setVisibility(View.GONE);
        }
        
        if (tarefa.getTimestampConclusao() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.textHoraConclusao.setText("Concluída: " + sdf.format(new Date(tarefa.getTimestampConclusao())));
            holder.textHoraConclusao.setVisibility(View.VISIBLE);
        } else {
            holder.textHoraConclusao.setVisibility(View.GONE);
        }
        
        // Botões de ação
        configurarBotoes(holder, tarefa);
        
        // Click no card
        holder.cardTarefa.setOnClickListener(v -> {
            if (onTarefaClickListener != null) {
                onTarefaClickListener.onTarefaClick(tarefa);
            }
        });
    }
    
    private void configurarBotoes(TarefaViewHolder holder, TarefaPreparo tarefa) {
        String status = tarefa.getStatus();
        
        if ("PENDENTE".equals(status)) {
            holder.btnIniciar.setVisibility(View.VISIBLE);
            holder.btnConcluir.setVisibility(View.GONE);
            holder.btnIniciar.setOnClickListener(v -> {
                if (onTarefaIniciarListener != null) {
                    onTarefaIniciarListener.onTarefaIniciar(tarefa);
                }
            });
        } else if ("EM_ANDAMENTO".equals(status)) {
            holder.btnIniciar.setVisibility(View.GONE);
            holder.btnConcluir.setVisibility(View.VISIBLE);
            holder.btnConcluir.setOnClickListener(v -> {
                if (onTarefaConcluirListener != null) {
                    onTarefaConcluirListener.onTarefaConcluir(tarefa);
                }
            });
        } else { // CONCLUIDA
            holder.btnIniciar.setVisibility(View.GONE);
            holder.btnConcluir.setVisibility(View.GONE);
        }
    }
    
    private String getStatusTexto(String status) {
        switch (status) {
            case "PENDENTE": return "⏳ Pendente";
            case "EM_ANDAMENTO": return "🔄 Em Andamento";
            case "CONCLUIDA": return "✅ Concluída";
            default: return status;
        }
    }
    
    private int getCorPrioridade(int prioridade) {
        switch (prioridade) {
            case 1: return Color.parseColor("#4CAF50"); // Verde - Baixa
            case 2: return Color.parseColor("#8BC34A"); // Verde claro - Normal
            case 3: return Color.parseColor("#FFC107"); // Amarelo - Média
            case 4: return Color.parseColor("#FF9800"); // Laranja - Alta
            case 5: return Color.parseColor("#F44336"); // Vermelho - Urgente
            default: return Color.GRAY;
        }
    }
    
    private int getCorStatus(String status) {
        switch (status) {
            case "PENDENTE": return Color.parseColor("#FFF9C4"); // Amarelo claro
            case "EM_ANDAMENTO": return Color.parseColor("#E1F5FE"); // Azul claro
            case "CONCLUIDA": return Color.parseColor("#E8F5E9"); // Verde claro
            default: return Color.WHITE;
        }
    }
    
    @Override
    public int getItemCount() {
        return tarefas.size();
    }
    
    public void setTarefas(List<TarefaPreparo> tarefas) {
        this.tarefas = tarefas;
        notifyDataSetChanged();
    }
    
    static class TarefaViewHolder extends RecyclerView.ViewHolder {
        CardView cardTarefa;
        View viewPrioridade;
        TextView textDescricao, textTurno, textStatus, textPrioridade;
        TextView textResponsavel, textHoraInicio, textHoraConclusao;
        Button btnIniciar, btnConcluir;
        
        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTarefa = itemView.findViewById(R.id.cardTarefa);
            viewPrioridade = itemView.findViewById(R.id.viewPrioridade);
            textDescricao = itemView.findViewById(R.id.textDescricaoTarefa);
            textTurno = itemView.findViewById(R.id.textTurnoTarefa);
            textStatus = itemView.findViewById(R.id.textStatusTarefa);
            textPrioridade = itemView.findViewById(R.id.textPrioridadeTarefa);
            textResponsavel = itemView.findViewById(R.id.textResponsavelTarefa);
            textHoraInicio = itemView.findViewById(R.id.textHoraInicio);
            textHoraConclusao = itemView.findViewById(R.id.textHoraConclusao);
            btnIniciar = itemView.findViewById(R.id.btnIniciarTarefa);
            btnConcluir = itemView.findViewById(R.id.btnConcluirTarefa);
        }
    }
}
