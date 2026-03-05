package com.example.visualizadorapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.R;
import com.example.visualizadorapp.model.Ingrediente;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngredienteAdapter extends RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder> {
    
    private List<Ingrediente> ingredientes = new ArrayList<>();
    private OnIngredienteClickListener onIngredienteClickListener;
    private OnStatusChangeListener onStatusChangeListener;
    
    public interface OnIngredienteClickListener {
        void onIngredienteClick(Ingrediente ingrediente);
    }
    
    public interface OnStatusChangeListener {
        void onMarcarDisponivel(Ingrediente ingrediente);
        void onMarcarFaltando(Ingrediente ingrediente);
        void onMarcarParcial(Ingrediente ingrediente);
    }
    
    public IngredienteAdapter(OnIngredienteClickListener clickListener, 
                              OnStatusChangeListener statusListener) {
        this.onIngredienteClickListener = clickListener;
        this.onStatusChangeListener = statusListener;
    }
    
    @NonNull
    @Override
    public IngredienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_ingrediente, parent, false);
        return new IngredienteViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull IngredienteViewHolder holder, int position) {
        Ingrediente ingrediente = ingredientes.get(position);
        
        holder.textNome.setText(ingrediente.getNomeIngrediente());
        holder.textQuantidade.setText("Qtd: " + (ingrediente.getQuantidade() != null ? ingrediente.getQuantidade() : "N/A"));
        holder.textStatus.setText(getStatusTexto(ingrediente.getStatus()));
        
        // Cor do status
        int corStatus = getCorStatus(ingrediente.getStatus());
        holder.cardIngrediente.setCardBackgroundColor(corStatus);
        holder.viewIndicador.setBackgroundColor(getCorIndicador(ingrediente.getStatus()));
        
        // Responsável e timestamp
        if (ingrediente.getResponsavelVerificacao() != null && !ingrediente.getResponsavelVerificacao().isEmpty()) {
            holder.textResponsavel.setText("Verificado por: " + ingrediente.getResponsavelVerificacao());
            holder.textResponsavel.setVisibility(View.VISIBLE);
        } else {
            holder.textResponsavel.setVisibility(View.GONE);
        }
        
        if (ingrediente.getTimestampVerificacao() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            holder.textDataVerificacao.setText(sdf.format(new Date(ingrediente.getTimestampVerificacao())));
            holder.textDataVerificacao.setVisibility(View.VISIBLE);
        } else {
            holder.textDataVerificacao.setVisibility(View.GONE);
        }
        
        // Observação
        if (ingrediente.getObservacao() != null && !ingrediente.getObservacao().isEmpty()) {
            holder.textObservacao.setText("Obs: " + ingrediente.getObservacao());
            holder.textObservacao.setVisibility(View.VISIBLE);
        } else {
            holder.textObservacao.setVisibility(View.GONE);
        }
        
        // Botões de ação
        holder.btnDisponivel.setOnClickListener(v -> {
            if (onStatusChangeListener != null) {
                onStatusChangeListener.onMarcarDisponivel(ingrediente);
            }
        });
        
        holder.btnFaltando.setOnClickListener(v -> {
            if (onStatusChangeListener != null) {
                onStatusChangeListener.onMarcarFaltando(ingrediente);
            }
        });
        
        holder.btnParcial.setOnClickListener(v -> {
            if (onStatusChangeListener != null) {
                onStatusChangeListener.onMarcarParcial(ingrediente);
            }
        });
        
        // Click no card
        holder.cardIngrediente.setOnClickListener(v -> {
            if (onIngredienteClickListener != null) {
                onIngredienteClickListener.onIngredienteClick(ingrediente);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return ingredientes.size();
    }
    
    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
        notifyDataSetChanged();
    }
    
    private String getStatusTexto(String status) {
        switch (status) {
            case "DISPONIVEL":
                return "✓ Disponível";
            case "FALTANDO":
                return "✗ Faltando";
            case "PARCIAL":
                return "⚠ Parcial";
            default:
                return status;
        }
    }
    
    private int getCorStatus(String status) {
        switch (status) {
            case "DISPONIVEL":
                return Color.parseColor("#E8F5E9"); // Verde claro
            case "FALTANDO":
                return Color.parseColor("#FFEBEE"); // Vermelho claro
            case "PARCIAL":
                return Color.parseColor("#FFF3E0"); // Laranja claro
            default:
                return Color.WHITE;
        }
    }
    
    private int getCorIndicador(String status) {
        switch (status) {
            case "DISPONIVEL":
                return Color.parseColor("#4CAF50"); // Verde
            case "FALTANDO":
                return Color.parseColor("#F44336"); // Vermelho
            case "PARCIAL":
                return Color.parseColor("#FF9800"); // Laranja
            default:
                return Color.GRAY;
        }
    }
    
    static class IngredienteViewHolder extends RecyclerView.ViewHolder {
        CardView cardIngrediente;
        View viewIndicador;
        TextView textNome;
        TextView textQuantidade;
        TextView textStatus;
        TextView textResponsavel;
        TextView textDataVerificacao;
        TextView textObservacao;
        Button btnDisponivel;
        Button btnFaltando;
        Button btnParcial;
        
        IngredienteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIngrediente = itemView.findViewById(R.id.cardIngrediente);
            viewIndicador = itemView.findViewById(R.id.viewIndicadorIngrediente);
            textNome = itemView.findViewById(R.id.textNomeIngrediente);
            textQuantidade = itemView.findViewById(R.id.textQuantidadeIngrediente);
            textStatus = itemView.findViewById(R.id.textStatusIngrediente);
            textResponsavel = itemView.findViewById(R.id.textResponsavelIngrediente);
            textDataVerificacao = itemView.findViewById(R.id.textDataVerificacaoIngrediente);
            textObservacao = itemView.findViewById(R.id.textObservacaoIngrediente);
            btnDisponivel = itemView.findViewById(R.id.btnMarcarDisponivel);
            btnFaltando = itemView.findViewById(R.id.btnMarcarFaltando);
            btnParcial = itemView.findViewById(R.id.btnMarcarParcial);
        }
    }
}
