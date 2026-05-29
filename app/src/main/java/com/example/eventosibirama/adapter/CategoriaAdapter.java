package com.example.eventosibirama.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Categoria;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    private List<Categoria>        categorias  = new ArrayList<>();
    private final OnCategoriaClickListener listener;

    // FIX #6: ID da categoria atualmente selecionada (null = nenhuma)
    private String selectedId = null;

    public CategoriaAdapter(OnCategoriaClickListener listener) {
        this.listener = listener;
    }

    public void setCategorias(List<Categoria> newList) {
        if (newList == null) newList = new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new CategoriaDiffCallback(this.categorias, newList));

        this.categorias = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
    }


    public void setSelectedId(String newSelectedId) {
        String oldSelectedId = this.selectedId;
        this.selectedId = newSelectedId;

        // Notifica apenas os cards que mudaram de estado
        for (int i = 0; i < categorias.size(); i++) {
            String id = categorias.get(i).getId();
            if (Objects.equals(id, oldSelectedId) || Objects.equals(id, newSelectedId)) {
                notifyItemChanged(i);
            }
        }
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

        private final MaterialCardView cardView;
        private final ImageView        ivIcone;
        private final TextView         tvNome;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView é o MaterialCardView raiz do item_categoria.xml
            cardView = (MaterialCardView) itemView;
            ivIcone  = itemView.findViewById(R.id.iv_icone_categoria);
            tvNome   = itemView.findViewById(R.id.tv_nome_categoria);
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

            // FIX #6: destaque visual — card roxo escuro + borda quando selecionado
            boolean selecionado = Objects.equals(categoria.getId(), selectedId);
            aplicarDestaque(selecionado);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCategoriaClick(categoria);
            });
        }

        private void aplicarDestaque(boolean selecionado) {
            if (selecionado) {
                cardView.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.primary_light));
                cardView.setStrokeColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.primary));
                cardView.setStrokeWidth(4);
            } else {
                cardView.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.card_background));
                cardView.setStrokeWidth(0);
            }
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

        @Override public int getOldListSize() { return oldList.size(); }
        @Override public int getNewListSize() { return newList.size(); }

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
