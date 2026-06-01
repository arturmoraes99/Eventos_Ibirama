package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventosibirama.R;
import com.example.eventosibirama.model.Evento;
import com.example.eventosibirama.view.activity.DetalhesEventoActivity;
import com.example.eventosibirama.viewmodel.EventoViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap       mMap;
    private EventoViewModel eventoViewModel;

    private final Map<String, Evento> markerEventoMap = new HashMap<>();

    private static final LatLng IBIRAMA = new LatLng(-27.0572, -49.5197);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventoViewModel = new ViewModelProvider(this).get(EventoViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(IBIRAMA, 13f));

        configurarCliqueMarcador();

        eventoViewModel.getEventos().observe(getViewLifecycleOwner(),
                this::adicionarMarcadores);
        eventoViewModel.carregarTodosEventos();
    }

    // ── Marcadores ────────────────────────────────────────────────────────────

    private void adicionarMarcadores(List<Evento> eventos) {
        if (mMap == null || eventos == null) return;

        mMap.clear();
        markerEventoMap.clear();

        for (Evento evento : eventos) {
            if (evento.getLatitude() != 0 && evento.getLongitude() != 0) {
                LatLng posicao = new LatLng(evento.getLatitude(), evento.getLongitude());

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(posicao)
                        .title(evento.getNome())
                        .snippet(evento.getLocal() + " · " + evento.getData()));

                // NOVO: usa o ID do Marker (gerado pelo Maps) como chave
                if (marker != null) {
                    markerEventoMap.put(marker.getId(), evento);
                }
            }
        }
    }

    // ── Clique nos marcadores ─────────────────────────────────────────────────

    private void configurarCliqueMarcador() {
        mMap.setOnMarkerClickListener(marker -> {
            // Retorna false → Maps exibe a InfoWindow automaticamente
            return false;
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            Evento evento = markerEventoMap.get(marker.getId());
            if (evento != null) {
                Intent intent = new Intent(getActivity(), DetalhesEventoActivity.class);
                intent.putExtra("evento_id", evento.getId());
                startActivity(intent);
            }
        });
    }
}
