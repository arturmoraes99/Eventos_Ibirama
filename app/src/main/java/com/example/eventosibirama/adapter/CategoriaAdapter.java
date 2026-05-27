package com.example.eventosibirama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    private List<Categoria> categorias = new ArrayList<>();
    private final OnCategoriaClickListener listener;

    public CategoriaAdapter(OnCategoriaClickListener listener) {
        this.listener = listener;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        holder.bind(categorias.get(position));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class CategoriaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivIcone;
        private final TextView tvNome;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcone = itemView.findViewById(R.id.iv_icone_categoria);
            tvNome  = itemView.findViewById(R.id.tv_nome_categoria);
        }

        public void bind(Categoria categoria) {
            tvNome.setText(categoria.getNome());

            // Carrega ícone — tenta URL remota, senão usa recurso local
            if (categoria.getIconeUrl() != null && !categoria.getIconeUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(categoria.getIconeUrl())
                        .placeholder(R.drawable.ic_categoria_placeholder)
                        .into(ivIcone);
            } else if (categoria.getIconeResId() != 0) {
                ivIcone.setImageResource(categoria.getIconeResId());
            } else {
                ivIcone.setImageResource(R.drawable.ic_categoria_placeholder);
            }

            // Destaque visual na categoria selecionada
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoriaClick(categoria);
                }
            });
        }
    }
}
