package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.FavoritoRepository;

import java.util.List;

public class FavoritosViewModel extends ViewModel {

    private final FavoritoRepository favoritoRepository;
    private final MutableLiveData<List<Evento>> favoritos = new MutableLiveData<>();

    public FavoritosViewModel() {
        favoritoRepository = new FavoritoRepository();
    }

    public MutableLiveData<List<Evento>> getFavoritos() { return favoritos; }

    public void carregarFavoritos() {
        favoritoRepository.getFavoritos().observeForever(lista -> {
            favoritos.setValue(lista);
        });
    }
}
