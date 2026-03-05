package com.example.visualizadorapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.visualizadorapp.R;
import com.example.visualizadorapp.model.Cardapio;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.CardapioViewHolder> {
    private List<Cardapio> cardapios = new ArrayList<>();
    private OnCardapioClickListener listener;

    public interface OnCardapioClickListener {
        void onCardapioClick(Cardapio cardapio);
        void onFavoritoClick(Cardapio cardapio);
    }

    public CardapioAdapter(OnCardapioClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardapioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardapio_historico, parent, false);
        return new CardapioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardapioViewHolder holder, int position) {
        Cardapio cardapio = cardapios.get(position);
        holder.bind(cardapio);
    }

    @Override
    public int getItemCount() {
        return cardapios.size();
    }

    public void setCardapios(List<Cardapio> cardapios) {
        this.cardapios = cardapios;
        notifyDataSetChanged();
    }

    class CardapioViewHolder extends RecyclerView.ViewHolder {
        TextView txtData, txtPratoPrincipal, txtGuarnicao;
        TextView txtAcompanhamento, txtSalada, txtSobremesa;
        ImageButton btnFavorito;

        CardapioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtData = itemView.findViewById(R.id.txtData);
            txtPratoPrincipal = itemView.findViewById(R.id.txtPratoPrincipal);
            txtGuarnicao = itemView.findViewById(R.id.txtGuarnicao);
            txtAcompanhamento = itemView.findViewById(R.id.txtAcompanhamento);
            txtSalada = itemView.findViewById(R.id.txtSalada);
            txtSobremesa = itemView.findViewById(R.id.txtSobremesa);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }

        void bind(Cardapio cardapio) {
            // Formatar data
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(cardapio.getData());
                txtData.setText(outputFormat.format(date));
            } catch (Exception e) {
                txtData.setText(cardapio.getData());
            }

            // Preencher dados
            txtPratoPrincipal.setText("🍗 Prato: " + 
                (cardapio.getPratoPrincipal() != null ? cardapio.getPratoPrincipal() : "---"));
            txtGuarnicao.setText("🍖 Guarnição: " + 
                (cardapio.getGuarnicao() != null ? cardapio.getGuarnicao() : "---"));
            txtAcompanhamento.setText("🍚 Acompanhamento: " + 
                (cardapio.getAcompanhamento() != null ? cardapio.getAcompanhamento() : "---"));
            txtSalada.setText("🥗 Salada: " + 
                (cardapio.getSalada() != null ? cardapio.getSalada() : "---"));
            txtSobremesa.setText("🍮 Sobremesa: " + 
                (cardapio.getSobremesa() != null ? cardapio.getSobremesa() : "---"));

            // Ícone de favorito
            btnFavorito.setImageResource(
                cardapio.isFavorito() ? 
                android.R.drawable.star_big_on : 
                android.R.drawable.star_big_off
            );

            // Listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardapioClick(cardapio);
            });

            btnFavorito.setOnClickListener(v -> {
                if (listener != null) listener.onFavoritoClick(cardapio);
            });
        }
    }
}
