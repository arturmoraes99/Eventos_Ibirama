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
import com.example.eventosibirama.model.Evento;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    public interface OnEventoClickListener {
        void onEventoClick(Evento evento);
    }

    private List<Evento> eventos = new ArrayList<>();
    private final OnEventoClickListener listener;

    public EventoAdapter(OnEventoClickListener listener) {
        this.listener = listener;
    }

    /**
     * CORRIGIDO: usa DiffUtil para calcular apenas as diferenças entre
     * a lista antiga e a nova, evitando redesenhar todo o RecyclerView
     * com notifyDataSetChanged().
     */
    public void setEventos(List<Evento> newList) {
        if (newList == null) newList = new ArrayList<>();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new EventoDiffCallback(this.eventos, newList));

        this.eventos = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        holder.bind(eventos.get(position));
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    public class EventoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImagem;
        private final TextView  tvNome;
        private final TextView  tvData;
        private final TextView  tvHora;
        private final TextView  tvLocal;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagem = itemView.findViewById(R.id.iv_imagem_evento);
            tvNome   = itemView.findViewById(R.id.tv_nome_evento);
            tvData   = itemView.findViewById(R.id.tv_data_evento);
            tvHora   = itemView.findViewById(R.id.tv_hora_evento);
            tvLocal  = itemView.findViewById(R.id.tv_local_evento);
        }

        public void bind(Evento evento) {
            tvNome.setText(evento.getNome());
            tvData.setText(evento.getData());
            tvHora.setText(evento.getHora());
            tvLocal.setText(evento.getLocal());

            Glide.with(itemView.getContext())
                    .load(evento.getImagemUrl())
                    .placeholder(R.drawable.ic_evento_placeholder)
                    .centerCrop()
                    .into(ivImagem);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEventoClick(evento);
            });
        }
    }

    // ── DiffCallback ──────────────────────────────────────────────────────────

    private static class EventoDiffCallback extends DiffUtil.Callback {

        private final List<Evento> oldList;
        private final List<Evento> newList;

        EventoDiffCallback(List<Evento> oldList, List<Evento> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            // Compara pela identidade — mesmo ID = mesmo item
            return Objects.equals(oldList.get(oldPos).getId(),
                    newList.get(newPos).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            // Compara pelo conteúdo relevante para exibição
            Evento o = oldList.get(oldPos);
            Evento n = newList.get(newPos);
            return Objects.equals(o.getNome(),  n.getNome())  &&
                    Objects.equals(o.getData(),  n.getData())  &&
                    Objects.equals(o.getHora(),  n.getHora())  &&
                    Objects.equals(o.getLocal(), n.getLocal());
        }
    }
}
