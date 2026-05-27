package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Usuario;
import com.example.eventosibirama.repository.UsuarioRepository;

public class PerfilViewModel extends ViewModel {

    private final UsuarioRepository usuarioRepository;
    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();

    public PerfilViewModel() {
        usuarioRepository = new UsuarioRepository();
    }

    public MutableLiveData<Usuario> getUsuario() { return usuario; }

    public void carregarPerfil() {
        usuarioRepository.getUsuarioLogado().observeForever(u -> {
            usuario.setValue(u);
        });
    }
}
