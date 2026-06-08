package com.example.eventosibirama.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventosibirama.R;
import com.example.eventosibirama.adapter.EventoAdapter;
import com.example.eventosibirama.viewmodel.EventosProximosViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class EventosProximosActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1001;

    private RecyclerView   rvEventos;
    private TextView       tvVazio, tvRaio;
    private ProgressBar    progressBar;

    private EventoAdapter            eventoAdapter;
    private EventosProximosViewModel viewModel;
    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_proximos);

        viewModel    = new ViewModelProvider(this).get(EventosProximosViewModel.class);
        fusedClient  = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvEventos   = findViewById(R.id.rv_eventos);
        tvVazio     = findViewById(R.id.tv_vazio);
        tvRaio      = findViewById(R.id.tv_raio);
        progressBar = findViewById(R.id.progress_bar);

        configurarRecycler();
        observarViewModel();
        verificarPermissaoEBuscar();
    }

    private void configurarRecycler() {
        eventoAdapter = new EventoAdapter(evento -> {
            Intent intent = new Intent(this, DetalhesEventoActivity.class);
            intent.putExtra("evento_id", evento.getId());
            startActivity(intent);
        });
        rvEventos.setLayoutManager(new LinearLayoutManager(this));
        rvEventos.setAdapter(eventoAdapter);
    }

    private void observarViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading == null) return;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErro().observe(this, erro -> {
            if (erro != null) Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
        });

        viewModel.getEventos().observe(this, eventos -> {
            if (eventos == null || eventos.isEmpty()) {
                tvVazio.setVisibility(View.VISIBLE);
                rvEventos.setVisibility(View.GONE);
            } else {
                tvVazio.setVisibility(View.GONE);
                rvEventos.setVisibility(View.VISIBLE);
                eventoAdapter.setEventos(eventos);
            }
        });
    }

    // ── Localização ───────────────────────────────────────────────────────────

    private void verificarPermissaoEBuscar() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacao();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    private void obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Solicita localização atual com alta precisão
        fusedClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        tvRaio.setText(getString(R.string.buscando_em_raio, 20));
                        viewModel.carregarEventosProximos(
                                location.getLatitude(),
                                location.getLongitude());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this,
                                getString(R.string.erro_localizacao),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            getString(R.string.erro_localizacao),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obterLocalizacao();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this,
                        getString(R.string.permissao_localizacao_negada),
                        Toast.LENGTH_LONG).show();
                tvVazio.setVisibility(View.VISIBLE);
                tvVazio.setText(R.string.permissao_localizacao_negada);
            }
        }
    }
}
