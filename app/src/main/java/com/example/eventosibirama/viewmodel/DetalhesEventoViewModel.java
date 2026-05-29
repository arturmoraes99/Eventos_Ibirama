package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.EventoRepository;
import com.example.eventosibirama.repository.FavoritoRepository;
import com.example.eventosibirama.util.SingleLiveEvent;


public class DetalhesEventoViewModel extends ViewModel {

    private final EventoRepository   eventoRepository;
    private final FavoritoRepository favoritoRepository;

    private final MediatorLiveData<Evento>  _evento   = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> _favorito = new MediatorLiveData<>();
    private final MutableLiveData<Boolean>  _isLoading = new MutableLiveData<>(false);

    /** SingleLiveEvent garante que o toast só aparece uma vez por evento. */
    private final SingleLiveEvent<String> _mensagem = new SingleLiveEvent<>();

    // Fontes atuais (para poder trocar sem leak)
    private LiveData<Evento>  eventoSource;
    private LiveData<Boolean> favoritoSource;
    private LiveData<Boolean> toggleSource;

    public DetalhesEventoViewModel() {
        eventoRepository   = new EventoRepository();
        favoritoRepository = new FavoritoRepository();
    }

    public LiveData<Evento>  getEvento()    { return _evento;    }
    public LiveData<Boolean> isFavorito()   { return _favorito;  }
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String>  getMensagem()  { return _mensagem;  }

    public void carregarEvento(String eventoId) {
        _isLoading.setValue(true);
        if (eventoSource != null) _evento.removeSource(eventoSource);
        eventoSource = eventoRepository.getEventoPorId(eventoId);
        _evento.addSource(eventoSource, e -> {
            _isLoading.setValue(false);
            if (e != null) _evento.setValue(e);
        });
    }

    public void verificarFavorito(String eventoId) {
        if (favoritoSource != null) _favorito.removeSource(favoritoSource);
        favoritoSource = favoritoRepository.isFavorito(eventoId);
        _favorito.addSource(favoritoSource, isFav -> {
            if (isFav != null) _favorito.setValue(isFav);
        });
    }

    public void adicionarFavorito(String eventoId) {
        if (toggleSource != null) _favorito.removeSource(toggleSource);
        toggleSource = favoritoRepository.adicionarFavorito(eventoId);
        _favorito.addSource(toggleSource, sucesso -> {
            if (sucesso != null) {
                if (sucesso) {
                    _favorito.setValue(true);
                    _mensagem.setValue("Evento adicionado aos favoritos!");
                } else {
                    _mensagem.setValue("Erro ao favoritar evento.");
                }
            }
        });
    }

    public void removerFavorito(String eventoId) {
        if (toggleSource != null) _favorito.removeSource(toggleSource);
        toggleSource = favoritoRepository.removerFavorito(eventoId);
        _favorito.addSource(toggleSource, sucesso -> {
            if (sucesso != null) {
                if (sucesso) {
                    _favorito.setValue(false);
                    _mensagem.setValue("Evento removido dos favoritos.");
                } else {
                    _mensagem.setValue("Erro ao remover favorito.");
                }
            }
        });
    }
}
