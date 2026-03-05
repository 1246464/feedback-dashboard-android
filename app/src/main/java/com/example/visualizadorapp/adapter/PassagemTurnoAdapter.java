package com.example.visualizadorapp.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.R;
import com.example.visualizadorapp.model.PassagemTurno;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PassagemTurnoAdapter extends RecyclerView.Adapter<PassagemTurnoAdapter.PassagemViewHolder> {
    
    private List<PassagemTurno> passagens = new ArrayList<>();
    private OnPassagemClickListener onClickListener;
    private OnPassagemLidaListener onLidaListener;
    
    public interface OnPassagemClickListener {
        void onPassagemClick(PassagemTurno passagem);
    }
    
    public interface OnPassagemLidaListener {
        void onPassagemLida(PassagemTurno passagem);
    }
    
    public PassagemTurnoAdapter(OnPassagemLidaListener lidaListener, OnPassagemClickListener clickListener) {
        this.onLidaListener = lidaListener;
        this.onClickListener = clickListener;
    }
    
    @NonNull
    @Override
    public PassagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_passagem_turno, parent, false);
        return new PassagemViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PassagemViewHolder holder, int position) {
        PassagemTurno passagem = passagens.get(position);
        
        // Emoji de tipo
        String emoji = getTipoEmoji(passagem.getTipoMensagem());
        
        holder.textTipo.setText(emoji);
        holder.textMensagem.setText(passagem.getMensagem());
        holder.textTurnos.setText(passagem.getTurnoOrigem() + " → " + passagem.getTurnoDestino());
        holder.textUsuario.setText(passagem.getUsuarioOrigem() != null ? passagem.getUsuarioOrigem() : "Sistema");
        
        // Hora
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.textHora.setText(sdf.format(new Date(passagem.getTimestamp())));
        
        // Estilo para não lidas
        if (!passagem.isLida()) {
            holder.cardPassagem.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // Laranja claro
            holder.textMensagem.setTypeface(null, Typeface.BOLD);
            holder.viewIndicadorNaoLida.setVisibility(View.VISIBLE);
        } else {
            holder.cardPassagem.setCardBackgroundColor(Color.WHITE);
            holder.textMensagem.setTypeface(null, Typeface.NORMAL);
            holder.viewIndicadorNaoLida.setVisibility(View.GONE);
        }
        
        // Cor da borda por tipo
        holder.viewBordaTipo.setBackgroundColor(getCorTipo(passagem.getTipoMensagem()));
        
        // Badge se tiver informações extras
        int badgeCount = contarInformacoesExtras(passagem);
        if (badgeCount > 0) {
            holder.textBadgeExtras.setText("+" + badgeCount);
            holder.textBadgeExtras.setVisibility(View.VISIBLE);
        } else {
            holder.textBadgeExtras.setVisibility(View.GONE);
        }
        
        // Click listeners
        holder.cardPassagem.setOnClickListener(v -> {
            if (onLidaListener != null) {
                onLidaListener.onPassagemLida(passagem);
            }
            if (onClickListener != null) {
                onClickListener.onPassagemClick(passagem);
            }
        });
    }
    
    private String getTipoEmoji(String tipo) {
        switch (tipo) {
            case "INFORMACAO": return "ℹ️";
            case "ALERTA": return "⚠️";
            case "URGENTE": return "🚨";
            default: return "📝";
        }
    }
    
    private int getCorTipo(String tipo) {
        switch (tipo) {
            case "INFORMACAO": return Color.parseColor("#2196F3"); // Azul
            case "ALERTA": return Color.parseColor("#FF9800"); // Laranja
            case "URGENTE": return Color.parseColor("#F44336"); // Vermelho
            default: return Color.GRAY;
        }
    }
    
    private int contarInformacoesExtras(PassagemTurno passagem) {
        int count = 0;
        if (passagem.getTarefasConcluidas() != null && !passagem.getTarefasConcluidas().isEmpty()) count++;
        if (passagem.getTarefasPendentes() != null && !passagem.getTarefasPendentes().isEmpty()) count++;
        if (passagem.getProblemas() != null && !passagem.getProblemas().isEmpty()) count++;
        if (passagem.getMudancas() != null && !passagem.getMudancas().isEmpty()) count++;
        return count;
    }
    
    @Override
    public int getItemCount() {
        return passagens.size();
    }
    
    public void setPassagens(List<PassagemTurno> passagens) {
        this.passagens = passagens;
        notifyDataSetChanged();
    }
    
    static class PassagemViewHolder extends RecyclerView.ViewHolder {
        CardView cardPassagem;
        View viewBordaTipo, viewIndicadorNaoLida;
        TextView textTipo, textMensagem, textTurnos, textUsuario, textHora, textBadgeExtras;
        
        public PassagemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPassagem = itemView.findViewById(R.id.cardPassagem);
            viewBordaTipo = itemView.findViewById(R.id.viewBordaTipo);
            viewIndicadorNaoLida = itemView.findViewById(R.id.viewIndicadorNaoLida);
            textTipo = itemView.findViewById(R.id.textTipoPassagem);
            textMensagem = itemView.findViewById(R.id.textMensagemPassagem);
            textTurnos = itemView.findViewById(R.id.textTurnosPassagem);
            textUsuario = itemView.findViewById(R.id.textUsuarioPassagem);
            textHora = itemView.findViewById(R.id.textHoraPassagem);
            textBadgeExtras = itemView.findViewById(R.id.textBadgeExtras);
        }
    }
}
