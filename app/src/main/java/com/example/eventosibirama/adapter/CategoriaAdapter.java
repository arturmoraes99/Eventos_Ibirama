package com.example.eventosibirama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    private List<Categoria> categorias = new ArrayList<>();
    private final OnCategoriaClickListener listener;

    public CategoriaAdapter(OnCategoriaClickListener listener) {
        this.listener = listener;
    }

    /** CORRIGIDO: DiffUtil no lugar de notifyDataSetChanged(). */
    public void setCategorias(List<Categoria> newList) {
        if (newList == null) newList = new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new CategoriaDiffCallback(this.categorias, newList));

        this.categorias = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
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

    // ── ViewHolder ────────────────────────────────────────────────────────────

    public class CategoriaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivIcone;
        private final TextView  tvNome;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcone = itemView.findViewById(R.id.iv_icone_categoria);
            tvNome  = itemView.findViewById(R.id.tv_nome_categoria);
        }

        public void bind(Categoria categoria) {
            tvNome.setText(categoria.getNome());

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

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCategoriaClick(categoria);
            });
        }
    }

    // ── DiffCallback ──────────────────────────────────────────────────────────

    private static class CategoriaDiffCallback extends DiffUtil.Callback {

        private final List<Categoria> oldList;
        private final List<Categoria> newList;

        CategoriaDiffCallback(List<Categoria> oldList, List<Categoria> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return Objects.equals(oldList.get(oldPos).getId(),
                    newList.get(newPos).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            return Objects.equals(oldList.get(oldPos).getNome(),
                    newList.get(newPos).getNome());
        }
    }
}
