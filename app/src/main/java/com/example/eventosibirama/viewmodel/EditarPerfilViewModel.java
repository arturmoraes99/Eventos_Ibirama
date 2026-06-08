package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Usuario;
import com.example.eventosibirama.repository.UsuarioRepository;
import com.example.eventosibirama.util.SingleLiveEvent;

public class EditarPerfilViewModel extends ViewModel {

    private final UsuarioRepository repository;

    private final MediatorLiveData<Usuario> _usuario   = new MediatorLiveData<>();
    private final MutableLiveData<Boolean>  _isLoading = new MutableLiveData<>(false);
    private final SingleLiveEvent<String>   _erro      = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean>  _sucesso   = new SingleLiveEvent<>();

    private LiveData<Usuario>  usuarioSource;
    private LiveData<Boolean>  salvarSource;

    public EditarPerfilViewModel() {
        repository = new UsuarioRepository();
    }

    public LiveData<Usuario> getUsuario()   { return _usuario;   }
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String>  getErro()      { return _erro;      }
    public LiveData<Boolean> getSucesso()   { return _sucesso;   }

    public void carregarUsuario() {
        if (usuarioSource != null) _usuario.removeSource(usuarioSource);
        usuarioSource = repository.getUsuarioLogado();
        _usuario.addSource(usuarioSource, u -> {
            if (u != null) _usuario.setValue(u);
        });
    }

    public void salvarPerfil(String nome, String fotoUrl) {
        if (nome == null || nome.trim().isEmpty()) {
            _erro.setValue("O nome não pode estar vazio.");
            return;
        }

        _isLoading.setValue(true);

        MediatorLiveData<Void> mediator = new MediatorLiveData<>();
        salvarSource = repository.atualizarPerfil(nome.trim(), fotoUrl);
        mediator.addSource(salvarSource, ok -> {
            _isLoading.setValue(false);
            if (Boolean.TRUE.equals(ok)) {
                _sucesso.setValue(true);
            } else {
                _erro.setValue("Erro ao salvar perfil.");
            }
            mediator.removeSource(salvarSource);
        });
        mediator.observeForever(v -> {});
    }
}

