package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.FavoritoRepository;

import java.util.List;

public class FavoritosViewModel extends ViewModel {

    private final FavoritoRepository favoritoRepository;

    private final MediatorLiveData<List<Evento>> _favoritos = new MediatorLiveData<>();
    private final MutableLiveData<Boolean>       _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String>        _erro      = new MutableLiveData<>();

    private LiveData<List<Evento>> favoritoSource;

    public FavoritosViewModel() {
        favoritoRepository = new FavoritoRepository();
    }

    public LiveData<List<Evento>> getFavoritos()  { return _favoritos; }
    public LiveData<Boolean>      getIsLoading()  { return _isLoading; }
    public LiveData<String>       getErro()       { return _erro;      }

    public void carregarFavoritos() {
        _isLoading.setValue(true);
        _erro.setValue(null);

        // Remove a fonte anterior antes de adicionar a nova → sem leak
        if (favoritoSource != null) {
            _favoritos.removeSource(favoritoSource);
        }
        favoritoSource = favoritoRepository.getFavoritos();
        _favoritos.addSource(favoritoSource, lista -> {
            _isLoading.setValue(false);
            if (lista != null) {
                _favoritos.setValue(lista);
            } else {
                _erro.setValue("Erro ao carregar favoritos.");
            }
        });
    }
}
