package com.example.eventosibirama.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void cadastrar(String nome, String email, String senha,
                          MutableLiveData<Boolean> sucesso,
                          MutableLiveData<String> erro) {

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser != null) {
                        Usuario usuario = new Usuario(
                                firebaseUser.getUid(), nome, email
                        );
                        db.collection("usuarios")
                                .document(firebaseUser.getUid())
                                .set(usuario)
                                .addOnSuccessListener(unused -> sucesso.setValue(true))
                                .addOnFailureListener(e -> erro.setValue(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> erro.setValue(e.getMessage()));
    }

    public void login(String email, String senha,
                      MutableLiveData<Boolean> sucesso,
                      MutableLiveData<String> erro) {

        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> sucesso.setValue(true))
                .addOnFailureListener(e -> erro.setValue(e.getMessage()));
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getUsuarioAtual() {
        return auth.getCurrentUser();
    }

    public void buscarDadosUsuario(MutableLiveData<Usuario> usuarioLiveData,
                                   MutableLiveData<String> erro) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) return;

        db.collection("usuarios")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Usuario usuario = document.toObject(Usuario.class);
                        usuarioLiveData.setValue(usuario);
                    }
                })
                .addOnFailureListener(e -> erro.setValue(e.getMessage()));
    }
}
