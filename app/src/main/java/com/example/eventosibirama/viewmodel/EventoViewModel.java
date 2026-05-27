package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Categoria;
import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.CategoriaRepository;
import com.example.eventosibirama.repository.EventoRepository;

import java.util.List;

public class EventoViewModel extends ViewModel {

    private final EventoRepository eventoRepository;
    private final CategoriaRepository categoriaRepository;

    private final MutableLiveData<List<Evento>>    eventos    = new MutableLiveData<>();
    private final MutableLiveData<List<Categoria>> categorias = new MutableLiveData<>();

    public EventoViewModel() {
        eventoRepository    = new EventoRepository();
        categoriaRepository = new CategoriaRepository();
    }

    public MutableLiveData<List<Evento>> getEventos() { return eventos; }
    public MutableLiveData<List<Categoria>> getCategorias() { return categorias; }

    public void carregarTodosEventos() {
        eventoRepository.getTodosEventos().observeForever(lista -> {
            if (lista != null) eventos.setValue(lista);
        });
    }

    public void carregarCategorias() {
        categoriaRepository.getCategorias().observeForever(lista -> {
            if (lista != null) categorias.setValue(lista);
        });
    }

    public void buscarEventosPorCategoria(String categoriaId) {
        eventoRepository.getEventosPorCategoria(categoriaId).observeForever(lista -> {
            if (lista != null) eventos.setValue(lista);
        });
    }

    public void buscarEventosPorNome(String nome) {
        eventoRepository.buscarPorNome(nome).observeForever(lista -> {
            if (lista != null) eventos.setValue(lista);
        });
    }
}
