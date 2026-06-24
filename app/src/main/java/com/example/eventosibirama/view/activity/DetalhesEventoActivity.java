package com.example.eventosibirama.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.viewmodel.DetalhesEventoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class DetalhesEventoActivity extends AppCompatActivity {

    private ImageView      ivImagem;
    private TextView       tvNome, tvData, tvHora, tvLocal, tvDescricao;
    private MaterialButton btnVerMapa, fabFavorito, btnEditar, btnExcluir;
    private ProgressBar    progressBar;

    private DetalhesEventoViewModel viewModel;
    private String eventoId;
    private boolean isFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_evento);

        eventoId  = getIntent().getStringExtra("evento_id");
        viewModel = new ViewModelProvider(this).get(DetalhesEventoViewModel.class);

        inicializarViews();
        observarViewModel();

        if (eventoId != null) {
            viewModel.carregarEvento(eventoId);
            viewModel.verificarFavorito(eventoId);
        }
    }

    private void inicializarViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivImagem    = findViewById(R.id.iv_imagem_evento);
        tvNome      = findViewById(R.id.tv_nome_evento);
        tvData      = findViewById(R.id.tv_data_evento);
        tvHora      = findViewById(R.id.tv_hora_evento);
        tvLocal     = findViewById(R.id.tv_local_evento);
        tvDescricao = findViewById(R.id.tv_descricao_evento);
        btnVerMapa  = findViewById(R.id.btn_ver_mapa);
        fabFavorito = findViewById(R.id.fab_favorito);
        btnEditar   = findViewById(R.id.btn_editar);
        btnExcluir  = findViewById(R.id.btn_excluir);
        progressBar = findViewById(R.id.progress_bar);

        // Botões de edição ficam ocultos até confirmar que o usuário é o criador
        btnEditar.setVisibility(View.GONE);
        btnExcluir.setVisibility(View.GONE);
    }

    private void observarViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null)
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getEvento().observe(this, evento -> {
            if (evento != null) preencherDados(evento);
        });

        viewModel.isFavorito().observe(this, favorito -> {
            isFavorito = Boolean.TRUE.equals(favorito);
            fabFavorito.setIconResource(
                    isFavorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            fabFavorito.setText(isFavorito ? R.string.favoritado : R.string.favoritar);
            fabFavorito.setTextColor(getColor(
                    isFavorito ? R.color.favorite_active : R.color.primary));
            fabFavorito.setIconTint(getColorStateList(
                    isFavorito ? R.color.favorite_active : R.color.primary));
        });

        viewModel.getMensagem().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // NOVO: observa resultado da exclusão
        viewModel.getExcluido().observe(this, excluido -> {
            if (Boolean.TRUE.equals(excluido)) {
                Toast.makeText(this,
                        getString(R.string.evento_excluido), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void preencherDados(Evento evento) {
        tvNome.setText(evento.getNome());
        tvData.setText(evento.getData());
        tvHora.setText(evento.getHora());
        tvLocal.setText(evento.getLocal());
        tvDescricao.setText(evento.getDescricao());

        Glide.with(this)
                .load(evento.getImagemUrl())
                .placeholder(R.drawable.ic_evento_placeholder)
                .centerCrop()
                .into(ivImagem);

        btnVerMapa.setOnClickListener(v -> abrirMapa(evento));

        fabFavorito.setOnClickListener(v -> {
            if (isFavorito) viewModel.removerFavorito(eventoId);
            else            viewModel.adicionarFavorito(eventoId);
        });

        // NOVO: exibe botões de editar/excluir apenas para o criador
        String uidAtual = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (evento.foiCriadoPor(uidAtual)) {
            btnEditar.setVisibility(View.VISIBLE);
            btnExcluir.setVisibility(View.VISIBLE);

            btnEditar.setOnClickListener(v -> {
                Intent intent = new Intent(this, CadastroEventoActivity.class);
                intent.putExtra(CadastroEventoActivity.EXTRA_EVENTO_ID, eventoId);
                startActivity(intent);
            });

            btnExcluir.setOnClickListener(v -> confirmarExclusao());
        }
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.excluir_evento))
                .setMessage(getString(R.string.confirmar_exclusao))
                .setPositiveButton(getString(R.string.excluir), (dialog, which) ->
                        viewModel.excluirEvento(eventoId))
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }

    private void abrirMapa(Evento evento) {
        String uri = "geo:" + evento.getLatitude() + "," + evento.getLongitude()
                + "?q=" + evento.getLatitude() + "," + evento.getLongitude()
                + "(" + Uri.encode(evento.getNome()) + ")";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.maps_nao_encontrado, Toast.LENGTH_SHORT).show();
        }
    }
}
