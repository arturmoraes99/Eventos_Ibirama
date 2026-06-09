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

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.view.activity.AuthActivity;
import com.example.eventosibirama.view.activity.CadastroCategoriaActivity;
import com.example.eventosibirama.view.activity.EditarPerfilActivity;
import com.example.eventosibirama.viewmodel.FavoritosViewModel;
import com.example.eventosibirama.viewmodel.PerfilViewModel;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView        tvNome, tvEmail, tvQtdFavoritos;
    private MaterialButton  btnLogout, btnEditarPerfil;
    private ProgressBar     progressBar;

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

        ivAvatar       = view.findViewById(R.id.iv_avatar);
        tvNome         = view.findViewById(R.id.tv_nome_usuario);
        tvEmail        = view.findViewById(R.id.tv_email_usuario);
        tvQtdFavoritos = view.findViewById(R.id.tv_qtd_favoritos);
        btnLogout      = view.findViewById(R.id.btn_logout);
        btnEditarPerfil = view.findViewById(R.id.btn_editar_perfil);
        progressBar    = view.findViewById(R.id.progress_bar);
        MaterialButton btnCadastrarCategoria = view.findViewById(R.id.btn_cadastrar_categoria);


        // NOVO: abre EditarPerfilActivity
        btnEditarPerfil.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EditarPerfilActivity.class)));

        btnLogout.setOnClickListener(v -> realizarLogout());

        btnCadastrarCategoria.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CadastroCategoriaActivity.class)));
        observarViewModel();

        perfilViewModel.carregarPerfil();
        favoritosViewModel.carregarFavoritos();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // Recarrega para refletir alterações feitas na tela de edição
            perfilViewModel.carregarPerfil();
            favoritosViewModel.carregarFavoritos();
        }
    }

    // Também recarrega ao voltar do EditarPerfilActivity
    @Override
    public void onResume() {
        super.onResume();
        perfilViewModel.carregarPerfil();
    }

    private void observarViewModel() {
        perfilViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null)
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        perfilViewModel.getErro().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
        });

        perfilViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario == null) return;
            tvNome.setText(usuario.getNome());
            tvEmail.setText(usuario.getEmail());

            // NOVO: carrega foto de perfil se houver URL
            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                Glide.with(this)
                        .load(usuario.getFotoUrl())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivAvatar);
            }
        });

        favoritosViewModel.getFavoritos().observe(getViewLifecycleOwner(), eventos ->
                tvQtdFavoritos.setText(eventos != null
                        ? String.valueOf(eventos.size()) : "0"));
    }

    private void realizarLogout() {
        perfilViewModel.logout();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
