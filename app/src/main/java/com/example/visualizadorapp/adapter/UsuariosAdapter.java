package com.example.visualizadorapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualizadorapp.R;
import com.example.visualizadorapp.model.Usuario;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private List<Usuario> usuarios;
    private OnUsuarioActionListener listener;

    public interface OnUsuarioActionListener {
        void onEditarCargo(Usuario usuario);
        void onDeletarUsuario(Usuario usuario);
    }

    public UsuariosAdapter(List<Usuario> usuarios, OnUsuarioActionListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        
        holder.txtNome.setText(usuario.getNome());
        holder.txtEmail.setText(usuario.getEmail());
        holder.txtCargo.setText(usuario.getDescricaoCompleta());

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarCargo(usuario);
            }
        });

        holder.btnDeletar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeletarUsuario(usuario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEmail, txtCargo;
        Button btnEditar, btnDeletar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeUsuario);
            txtEmail = itemView.findViewById(R.id.txtEmailUsuario);
            txtCargo = itemView.findViewById(R.id.txtCargoUsuario);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
            btnDeletar = itemView.findViewById(R.id.btnDeletarUsuario);
        }
    }
}
