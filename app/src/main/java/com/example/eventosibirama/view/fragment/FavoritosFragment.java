package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventosibirama.R;
import com.example.eventosibirama.adapter.EventoAdapter;
import com.example.eventosibirama.view.activity.DetalhesEventoActivity;
import com.example.eventosibirama.viewmodel.FavoritosViewModel;

public class FavoritosFragment extends Fragment {

    private RecyclerView rvFavoritos;
    private TextView tvVazio;
    private EventoAdapter eventoAdapter;
    private FavoritosViewModel favoritosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritosViewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);

        rvFavoritos = view.findViewById(R.id.rv_favoritos);
        tvVazio     = view.findViewById(R.id.tv_vazio);

        configurarRecycler();
        observarViewModel();
    }

    private void configurarRecycler() {
        eventoAdapter = new EventoAdapter(evento -> {
            Intent intent = new Intent(getActivity(), DetalhesEventoActivity.class);
            intent.putExtra("evento_id", evento.getId());
            startActivity(intent);
        });

        rvFavoritos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavoritos.setAdapter(eventoAdapter);
    }

    private void observarViewModel() {
        favoritosViewModel.getFavoritos().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos == null || eventos.isEmpty()) {
                tvVazio.setVisibility(View.VISIBLE);
                rvFavoritos.setVisibility(View.GONE);
            } else {
                tvVazio.setVisibility(View.GONE);
                rvFavoritos.setVisibility(View.VISIBLE);
                eventoAdapter.setEventos(eventos);
            }
        });

        favoritosViewModel.carregarFavoritos();
    }
}
