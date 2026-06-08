package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.repository.EventoRepository;
import com.example.eventosibirama.util.SingleLiveEvent;

public class CadastroEventoViewModel extends ViewModel {

    private final EventoRepository repository;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final SingleLiveEvent<String>  _erro      = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> _sucesso   = new SingleLiveEvent<>();

    // Para modo edição: carrega o evento existente
    private final MediatorLiveData<Evento> _evento    = new MediatorLiveData<>();
    private LiveData<Evento>                eventoSource;
    private LiveData<String>                salvarSource;
    private LiveData<Boolean>               atualizarSource;

    public CadastroEventoViewModel() {
        repository = new EventoRepository();
    }

    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String>  getErro()      { return _erro;      }
    public LiveData<Boolean> getSucesso()   { return _sucesso;   }
    public LiveData<Evento>  getEvento()    { return _evento;    }


    public void carregarEvento(String eventoId) {
        if (eventoSource != null) _evento.removeSource(eventoSource);
        eventoSource = repository.getEventoPorId(eventoId);
        _evento.addSource(eventoSource, e -> {
            if (e != null) _evento.setValue(e);
            else _erro.setValue("Evento não encontrado.");
        });
    }


    public void salvarEvento(Evento evento) {
        _isLoading.setValue(true);

        if (salvarSource != null) _sucesso.removeObserver(ok -> {});
        salvarSource = repository.salvarEvento(evento);

        MediatorLiveData<Void> mediator = new MediatorLiveData<>();
        mediator.addSource(salvarSource, id -> {
            _isLoading.setValue(false);
            if (id != null) {
                _sucesso.setValue(true);
            } else {
                _erro.setValue("Erro ao salvar evento.");
            }
            mediator.removeSource(salvarSource);
        });
        mediator.observeForever(v -> {});
    }

    public void atualizarEvento(Evento evento) {
        _isLoading.setValue(true);

        MediatorLiveData<Void> mediator = new MediatorLiveData<>();
        atualizarSource = repository.atualizarEvento(evento);
        mediator.addSource(atualizarSource, ok -> {
            _isLoading.setValue(false);
            if (Boolean.TRUE.equals(ok)) {
                _sucesso.setValue(true);
            } else {
                _erro.setValue("Erro ao atualizar evento.");
            }
            mediator.removeSource(atualizarSource);
        });
        mediator.observeForever(v -> {});
    }
}

