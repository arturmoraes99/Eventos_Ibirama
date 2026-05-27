package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.EventoRepository;
import com.example.eventosibirama.repository.FavoritoRepository;

public class DetalhesEventoViewModel extends ViewModel {

    private final EventoRepository   eventoRepository;
    private final FavoritoRepository favoritoRepository;

    private final MutableLiveData<Evento>  evento    = new MutableLiveData<>();
    private final MutableLiveData<Boolean> favorito  = new MutableLiveData<>();
    private final MutableLiveData<String>  mensagem  = new MutableLiveData<>();

    public DetalhesEventoViewModel() {
        eventoRepository   = new EventoRepository();
        favoritoRepository = new FavoritoRepository();
    }

    public MutableLiveData<Evento>  getEvento()   { return evento; }
    public MutableLiveData<Boolean> isFavorito()  { return favorito; }
    public MutableLiveData<String>  getMensagem() { return mensagem; }

    public void carregarEvento(String eventoId) {
        eventoRepository.getEventoPorId(eventoId).observeForever(e -> {
            if (e != null) evento.setValue(e);
        });
    }

    public void verificarFavorito(String eventoId) {
        favoritoRepository.isFavorito(eventoId).observeForever(isFav -> {
            favorito.setValue(isFav);
        });
    }

    public void adicionarFavorito(String eventoId) {
        favoritoRepository.adicionarFavorito(eventoId).observeForever(sucesso -> {
            if (sucesso) {
                favorito.setValue(true);
                mensagem.setValue("Evento adicionado aos favoritos!");
            } else {
                mensagem.setValue("Erro ao favoritar evento.");
            }
        });
    }

    public void removerFavorito(String eventoId) {
        favoritoRepository.removerFavorito(eventoId).observeForever(sucesso -> {
            if (sucesso) {
                favorito.setValue(false);
                mensagem.setValue("Evento removido dos favoritos.");
            } else {
                mensagem.setValue("Erro ao remover favorito.");
            }
        });
    }
}
