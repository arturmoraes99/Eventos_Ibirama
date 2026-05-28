package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.activity.AuthActivity;
import com.example.eventosibirama.viewmodel.FavoritosViewModel;
import com.example.eventosibirama.viewmodel.PerfilViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class PerfilFragment extends Fragment {

    private TextView tvNome, tvEmail, tvQtdFavoritos;
    private MaterialButton btnLogout;
    private PerfilViewModel perfilViewModel;
    private FavoritosViewModel favoritosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        perfilViewModel   = new ViewModelProvider(this).get(PerfilViewModel.class);
        favoritosViewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);

        tvNome         = view.findViewById(R.id.tv_nome_usuario);
        tvEmail        = view.findViewById(R.id.tv_email_usuario);
        tvQtdFavoritos = view.findViewById(R.id.tv_qtd_favoritos);
        btnLogout      = view.findViewById(R.id.btn_logout);

        observarViewModel();

        btnLogout.setOnClickListener(v -> realizarLogout());
    }

    private void observarViewModel() {
        // Carrega nome e email
        perfilViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                tvNome.setText(usuario.getNome());
                tvEmail.setText(usuario.getEmail());
            }
        });
        perfilViewModel.carregarPerfil();

        // Carrega contagem de favoritos
        favoritosViewModel.getFavoritos().observe(getViewLifecycleOwner(), eventos -> {
            if (eventos != null) {
                tvQtdFavoritos.setText(String.valueOf(eventos.size()));
            } else {
                tvQtdFavoritos.setText("0");
            }
        });
        favoritosViewModel.carregarFavoritos();
    }

    private void realizarLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}