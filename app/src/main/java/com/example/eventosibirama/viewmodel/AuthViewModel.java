package com.example.eventosibirama.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosibirama.model.Usuario;
import com.example.eventosibirama.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    public final MutableLiveData<Boolean> sucesso = new MutableLiveData<>();
    public final MutableLiveData<String> erro = new MutableLiveData<>();
    public final MutableLiveData<Usuario> usuario = new MutableLiveData<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public void cadastrar(String nome, String email, String senha) {
        repository.cadastrar(nome, email, senha, sucesso, erro);
    }

    public void login(String email, String senha) {
        repository.login(email, senha, sucesso, erro);
    }

    public void logout() {
        repository.logout();
    }

    public FirebaseUser getUsuarioAtual() {
        return repository.getUsuarioAtual();
    }

    public void buscarDadosUsuario() {
        repository.buscarDadosUsuario(usuario, erro);
    }
}

