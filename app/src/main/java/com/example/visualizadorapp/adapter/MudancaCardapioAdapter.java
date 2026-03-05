package com.example.visualizadorapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.R;
import com.example.visualizadorapp.model.MudancaCardapio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MudancaCardapioAdapter extends RecyclerView.Adapter<MudancaCardapioAdapter.MudancaViewHolder> {
    
    private List<MudancaCardapio> mudancas = new ArrayList<>();
    
    @NonNull
    @Override
    public MudancaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_mudanca_cardapio, parent, false);
        return new MudancaViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MudancaViewHolder holder, int position) {
        MudancaCardapio mudanca = mudancas.get(position);
        
        holder.textItem.setText(formatarItemAlterado(mudanca.getItemAlterado()));
        holder.textAnterior.setText("Anterior: " + (mudanca.getValorAnterior() != null ? mudanca.getValorAnterior() : "N/A"));
        holder.textNovo.setText("Novo: " + mudanca.getValorNovo());
        holder.textUsuario.setText("Por: " + mudanca.getUsuarioResponsavel());
        
        SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date data = new Date(mudanca.getTimestamp());
        
        holder.textData.setText(sdfData.format(data));
        holder.textHora.setText(sdfHora.format(data));
        
        if (mudanca.getMotivoMudanca() != null && !mudanca.getMotivoMudanca().isEmpty()) {
            holder.textMotivo.setText("Motivo: " + mudanca.getMotivoMudanca());
            holder.textMotivo.setVisibility(View.VISIBLE);
        } else {
            holder.textMotivo.setVisibility(View.GONE);
        }
        
        // Indicador de notificação
        if (mudanca.isNotificadoTurnos()) {
            holder.textNotificado.setText("✓ Turnos notificados");
            holder.textNotificado.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.textNotificado.setText("⏳ Aguardando notificação");
            holder.textNotificado.setTextColor(Color.parseColor("#FF9800"));
        }
    }
    
    @Override
    public int getItemCount() {
        return mudancas.size();
    }
    
    public void setMudancas(List<MudancaCardapio> mudancas) {
        this.mudancas = mudancas;
        notifyDataSetChanged();
    }
    
    private String formatarItemAlterado(String item) {
        switch (item) {
            case "PRATO_PRINCIPAL":
                return "🍗 Prato Principal";
            case "GUARNICAO":
                return "🍖 Guarnição";
            case "ACOMPANHAMENTO":
                return "🍚 Acompanhamento";
            case "SALADA":
                return "🥗 Salada";
            case "SOBREMESA":
                return "🍮 Sobremesa";
            default:
                return item;
        }
    }
    
    static class MudancaViewHolder extends RecyclerView.ViewHolder {
        CardView cardMudanca;
        TextView textItem;
        TextView textAnterior;
        TextView textNovo;
        TextView textUsuario;
        TextView textData;
        TextView textHora;
        TextView textMotivo;
        TextView textNotificado;
        
        MudancaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMudanca = itemView.findViewById(R.id.cardMudanca);
            textItem = itemView.findViewById(R.id.textItemAlterado);
            textAnterior = itemView.findViewById(R.id.textValorAnterior);
            textNovo = itemView.findViewById(R.id.textValorNovo);
            textUsuario = itemView.findViewById(R.id.textUsuarioMudanca);
            textData = itemView.findViewById(R.id.textDataMudanca);
            textHora = itemView.findViewById(R.id.textHoraMudanca);
            textMotivo = itemView.findViewById(R.id.textMotivoMudanca);
            textNotificado = itemView.findViewById(R.id.textNotificado);
        }
    }
}
