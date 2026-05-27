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
import com.example.eventosibirama.model.Evento;

import java.util.ArrayList;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    public interface OnEventoClickListener {
        void onEventoClick(Evento evento);
    }

    private List<Evento> eventos = new ArrayList<>();
    private final OnEventoClickListener listener;

    public EventoAdapter(OnEventoClickListener listener) {
        this.listener = listener;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
        notifyDataSetChanged();
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

    public class EventoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImagem;
        private final TextView tvNome;
        private final TextView tvData;
        private final TextView tvHora;
        private final TextView tvLocal;

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

            // Carrega imagem com Glide
            Glide.with(itemView.getContext())
                    .load(evento.getImagemUrl())
                    .placeholder(R.drawable.ic_evento_placeholder)
                    .centerCrop()
                    .into(ivImagem);

            // Clique no item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventoClick(evento);
                }
            });
        }
    }
}
