package com.example.eventosibirama.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.viewmodel.EditarPerfilViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView   ivAvatar;
    private TextInputEditText etNome, etFotoUrl;
    private MaterialButton    btnSalvar;
    private ProgressBar       progressBar;

    private EditarPerfilViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        viewModel = new ViewModelProvider(this).get(EditarPerfilViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivAvatar    = findViewById(R.id.iv_avatar);
        etNome      = findViewById(R.id.et_nome);
        etFotoUrl   = findViewById(R.id.et_foto_url);
        btnSalvar   = findViewById(R.id.btn_salvar);
        progressBar = findViewById(R.id.progress_bar);

        observarViewModel();
        viewModel.carregarUsuario();

        // Atualiza preview da foto ao sair do campo
        etFotoUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) carregarPreviewFoto();
        });

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void observarViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading == null) return;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSalvar.setEnabled(!isLoading);
        });

        viewModel.getErro().observe(this, erro -> {
            if (erro != null) Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
        });

        viewModel.getSucesso().observe(this, ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Toast.makeText(this,
                        getString(R.string.perfil_atualizado),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Preenche campos com dados atuais do usuário
        viewModel.getUsuario().observe(this, usuario -> {
            if (usuario == null) return;
            etNome.setText(usuario.getNome());
            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                etFotoUrl.setText(usuario.getFotoUrl());
                Glide.with(this)
                        .load(usuario.getFotoUrl())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivAvatar);
            }
        });
    }

    private void carregarPreviewFoto() {
        String url = etFotoUrl.getText() != null
                ? etFotoUrl.getText().toString().trim() : "";
        if (!url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivAvatar);
        }
    }

    private void salvar() {
        String nome    = etNome.getText()    != null ? etNome.getText().toString().trim()    : "";
        String fotoUrl = etFotoUrl.getText() != null ? etFotoUrl.getText().toString().trim() : "";
        viewModel.salvarPerfil(nome, fotoUrl);
    }
}

