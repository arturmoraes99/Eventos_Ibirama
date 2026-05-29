package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Usuario;
import com.example.eventosibirama.repository.AuthRepository;
import com.example.eventosibirama.repository.UsuarioRepository;

public class PerfilViewModel extends ViewModel {

    private final UsuarioRepository usuarioRepository;
    private final AuthRepository    authRepository;

    private final MediatorLiveData<Usuario> _usuario   = new MediatorLiveData<>();
    private final MutableLiveData<Boolean>  _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String>   _erro      = new MutableLiveData<>();

    private LiveData<Usuario> usuarioSource;

    public PerfilViewModel() {
        usuarioRepository = new UsuarioRepository();
        authRepository    = new AuthRepository();
    }

    public LiveData<Usuario> getUsuario()   { return _usuario;   }
    public LiveData<Boolean> getIsLoading() { return _isLoading; }
    public LiveData<String>  getErro()      { return _erro;      }

    public void carregarPerfil() {
        _isLoading.setValue(true);
        if (usuarioSource != null) _usuario.removeSource(usuarioSource);
        usuarioSource = usuarioRepository.getUsuarioLogado();
        _usuario.addSource(usuarioSource, u -> {
            _isLoading.setValue(false);
            if (u != null) {
                _usuario.setValue(u);
            } else {
                _erro.setValue("Erro ao carregar perfil.");
            }
        });
    }

    public void logout() {
        authRepository.logout();
    }
}
