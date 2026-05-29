package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Categoria;
import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.CategoriaRepository;
import com.example.eventosibirama.repository.EventoRepository;

import java.util.List;

public class EventoViewModel extends ViewModel {

    private final EventoRepository     eventoRepository;
    private final CategoriaRepository  categoriaRepository;

    // MediatorLiveData permite trocar a fonte sem memory leak
    private final MediatorLiveData<List<Evento>>    _eventos    = new MediatorLiveData<>();
    private final MediatorLiveData<List<Categoria>> _categorias = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String>  _erro      = new MutableLiveData<>();

    // Rastreia a fonte atual para poder removê-la antes de adicionar outra
    private LiveData<List<Evento>>    eventoSource;
    private LiveData<List<Categoria>> categoriaSource;

    public EventoViewModel() {
        eventoRepository    = new EventoRepository();
        categoriaRepository = new CategoriaRepository();
    }

    // ── Expõe LiveData imutável para a UI ────────────────────────────────────
    public LiveData<List<Evento>>    getEventos()   { return _eventos;    }
    public LiveData<List<Categoria>> getCategorias(){ return _categorias; }
    public LiveData<Boolean>         getIsLoading() { return _isLoading;  }
    public LiveData<String>          getErro()      { return _erro;       }

    // ── Carregamento de Eventos ───────────────────────────────────────────────

    public void carregarTodosEventos() {
        _isLoading.setValue(true);
        _erro.setValue(null);
        trocarFonteEventos(eventoRepository.getTodosEventos());
    }

    public void buscarEventosPorCategoria(String categoriaId) {
        _isLoading.setValue(true);
        _erro.setValue(null);
        trocarFonteEventos(eventoRepository.getEventosPorCategoria(categoriaId));
    }

    public void buscarEventosPorNome(String nome) {
        _isLoading.setValue(true);
        _erro.setValue(null);
        trocarFonteEventos(eventoRepository.buscarPorNome(nome));
    }

    // ── Carregamento de Categorias ────────────────────────────────────────────

    public void carregarCategorias() {
        _isLoading.setValue(true);

        if (categoriaSource != null) {
            _categorias.removeSource(categoriaSource);
        }
        categoriaSource = categoriaRepository.getCategorias();
        _categorias.addSource(categoriaSource, lista -> {
            _isLoading.setValue(false);
            if (lista != null) {
                _categorias.setValue(lista);
            } else {
                _erro.setValue("Erro ao carregar categorias.");
            }
        });
    }

    // ── Auxiliar ──────────────────────────────────────────────────────────────


    private void trocarFonteEventos(LiveData<List<Evento>> novaFonte) {
        if (eventoSource != null) {
            _eventos.removeSource(eventoSource);
        }
        eventoSource = novaFonte;
        _eventos.addSource(eventoSource, lista -> {
            _isLoading.setValue(false);
            if (lista != null) {
                _eventos.setValue(lista);
            } else {
                _erro.setValue("Erro ao carregar eventos.");
            }
        });
    }
}
