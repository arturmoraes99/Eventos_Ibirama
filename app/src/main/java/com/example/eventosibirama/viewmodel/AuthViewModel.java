package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.repository.AuthRepository;
import com.example.eventosibirama.util.Resource;
import com.example.eventosibirama.util.SingleLiveEvent;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    private final MediatorLiveData<Resource<Boolean>> _authState = new MediatorLiveData<>();
    private final SingleLiveEvent<Void>               _navegar   = new SingleLiveEvent<>();


    private final MutableLiveData<Boolean> _sucesso = new MutableLiveData<>();
    private final Observer<Boolean> sucessoObserver = ok -> {
        if (Boolean.TRUE.equals(ok)) {
            _navegar.call();
        }
    };

    private LiveData<Resource<Boolean>> authSource;

    public AuthViewModel() {
        repository = new AuthRepository();
        // Registra o observer — será removido em onCleared()
        _sucesso.observeForever(sucessoObserver);
    }

    public LiveData<Resource<Boolean>> getAuthState() { return _authState; }
    public LiveData<Void>              getNavegar()   { return _navegar;   }

    public void login(String email, String senha) {
        trocarFonte(repository.login(email, senha));
    }

    public void cadastrar(String nome, String email, String senha) {
        trocarFonte(repository.cadastrar(nome, email, senha));
    }

    public FirebaseUser getUsuarioAtual() {
        return repository.getUsuarioAtual();
    }

    private void trocarFonte(LiveData<Resource<Boolean>> novaFonte) {
        if (authSource != null) _authState.removeSource(authSource);
        authSource = novaFonte;
        _authState.addSource(authSource, resource -> {
            _authState.setValue(resource);
            if (resource != null && resource.isSuccess()) {
                _sucesso.setValue(true);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        _sucesso.removeObserver(sucessoObserver);
    }
}
