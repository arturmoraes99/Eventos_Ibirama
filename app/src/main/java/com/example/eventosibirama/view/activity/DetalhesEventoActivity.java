package com.example.eventosibirama.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.viewmodel.DetalhesEventoViewModel;
import com.google.android.material.button.MaterialButton;

public class DetalhesEventoActivity extends AppCompatActivity {

    private ImageView ivImagem;
    private TextView tvNome, tvData, tvHora, tvLocal, tvDescricao;
    private MaterialButton btnVerMapa, fabFavorito;

    private DetalhesEventoViewModel viewModel;
    private String eventoId;
    private boolean isFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_evento);

        eventoId = getIntent().getStringExtra("evento_id");
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

        // Botão voltar funcionando
        toolbar.setNavigationOnClickListener(v -> finish());

        ivImagem    = findViewById(R.id.iv_imagem_evento);
        tvNome      = findViewById(R.id.tv_nome_evento);
        tvData      = findViewById(R.id.tv_data_evento);
        tvHora      = findViewById(R.id.tv_hora_evento);
        tvLocal     = findViewById(R.id.tv_local_evento);
        tvDescricao = findViewById(R.id.tv_descricao_evento);
        btnVerMapa  = findViewById(R.id.btn_ver_mapa);
        fabFavorito = findViewById(R.id.fab_favorito);
    }

    private void observarViewModel() {
        viewModel.getEvento().observe(this, evento -> {
            if (evento != null) preencherDados(evento);
        });

        viewModel.isFavorito().observe(this, favorito -> {
            isFavorito = favorito;
            fabFavorito.setIconResource(
                    favorito ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );
            fabFavorito.setText(favorito ? "Favoritado" : "Favoritar");
        });

        viewModel.getMensagem().observe(this, mensagem -> {
            if (mensagem != null)
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
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

        btnVerMapa.setOnClickListener(v -> {
            String uri = "geo:" + evento.getLatitude() + "," + evento.getLongitude()
                    + "?q=" + Uri.encode(evento.getLocal());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Google Maps não encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        fabFavorito.setOnClickListener(v -> {
            if (isFavorito) {
                viewModel.removerFavorito(eventoId);
            } else {
                viewModel.adicionarFavorito(eventoId);
            }
        });
    }
}