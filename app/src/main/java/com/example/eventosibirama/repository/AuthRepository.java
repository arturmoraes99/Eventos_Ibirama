package com.example.eventosibirama.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventosibirama.model.Usuario;
import com.example.eventosibirama.util.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();
    }

    /** Cadastra usuário e retorna Resource com loading/success/error. */
    public LiveData<Resource<Boolean>> cadastrar(String nome, String email, String senha) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser == null) {
                        liveData.setValue(Resource.error("Erro ao obter usuário."));
                        return;
                    }
                    Usuario usuario = new Usuario(firebaseUser.getUid(), nome, email);
                    db.collection("usuarios")
                            .document(firebaseUser.getUid())
                            .set(usuario)
                            .addOnSuccessListener(unused -> liveData.setValue(Resource.success(true)))
                            .addOnFailureListener(e -> liveData.setValue(Resource.error(e.getMessage())));
                })
                .addOnFailureListener(e -> liveData.setValue(Resource.error(e.getMessage())));

        return liveData;
    }

    /** Faz login e retorna Resource com loading/success/error. */
    public LiveData<Resource<Boolean>> login(String email, String senha) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> liveData.setValue(Resource.success(true)))
                .addOnFailureListener(e  -> liveData.setValue(Resource.error(e.getMessage())));

        return liveData;
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getUsuarioAtual() {
        return auth.getCurrentUser();
    }

    public LiveData<Resource<Usuario>> buscarDadosUsuario() {
        MutableLiveData<Resource<Usuario>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            liveData.setValue(Resource.error("Usuário não logado."));
            return liveData;
        }

        db.collection("usuarios")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        liveData.setValue(Resource.success(document.toObject(Usuario.class)));
                    } else {
                        liveData.setValue(Resource.error("Usuário não encontrado."));
                    }
                })
                .addOnFailureListener(e -> liveData.setValue(Resource.error(e.getMessage())));

        return liveData;
    }
}
