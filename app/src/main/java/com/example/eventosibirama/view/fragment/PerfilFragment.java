package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.activity.AuthActivity;
import com.example.eventosibirama.viewmodel.FavoritosViewModel;
import com.example.eventosibirama.viewmodel.PerfilViewModel;
import com.google.android.material.button.MaterialButton;


public class PerfilFragment extends Fragment {

    private TextView       tvNome, tvEmail, tvQtdFavoritos;
    private MaterialButton btnLogout;
    private ProgressBar    progressBar;

    private PerfilViewModel    perfilViewModel;
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

        perfilViewModel    = new ViewModelProvider(this).get(PerfilViewModel.class);
        favoritosViewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);

        tvNome         = view.findViewById(R.id.tv_nome_usuario);
        tvEmail        = view.findViewById(R.id.tv_email_usuario);
        tvQtdFavoritos = view.findViewById(R.id.tv_qtd_favoritos);
        btnLogout      = view.findViewById(R.id.btn_logout);
        progressBar    = view.findViewById(R.id.progress_bar);

        observarViewModel();

        // CORRIGIDO: Fragment apenas chama o ViewModel; Firebase fica no Repository
        btnLogout.setOnClickListener(v -> realizarLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        perfilViewModel.carregarPerfil();
        favoritosViewModel.carregarFavoritos();
    }

    private void observarViewModel() {
        // Loading
        perfilViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Erro
        perfilViewModel.getErro().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
        });

        // Dados do usuário
        perfilViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                tvNome.setText(usuario.getNome());
                tvEmail.setText(usuario.getEmail());
            }
        });

        // Contagem de favoritos
        favoritosViewModel.getFavoritos().observe(getViewLifecycleOwner(), eventos ->
                tvQtdFavoritos.setText(eventos != null ? String.valueOf(eventos.size()) : "0"));
    }


    private void realizarLogout() {
        perfilViewModel.logout();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
