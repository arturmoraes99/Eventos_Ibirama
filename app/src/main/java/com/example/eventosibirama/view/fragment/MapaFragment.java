package com.example.eventosibirama.view.fragment;

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
import com.example.eventosibirama.viewmodel.EventoViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EventoViewModel eventoViewModel;

    // Coordenadas de Ibirama - SC
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

        // Centraliza em Ibirama
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(IBIRAMA, 13f));

        // Observa eventos e adiciona marcadores
        eventoViewModel.getEventos().observe(getViewLifecycleOwner(), this::adicionarMarcadores);
        eventoViewModel.carregarTodosEventos();
    }

    private void adicionarMarcadores(List<Evento> eventos) {
        if (mMap == null || eventos == null) return;

        mMap.clear();

        for (Evento evento : eventos) {
            if (evento.getLatitude() != 0 && evento.getLongitude() != 0) {
                LatLng posicao = new LatLng(evento.getLatitude(), evento.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(posicao)
                        .title(evento.getNome())
                        .snippet(evento.getLocal()));
            }
        }
    }
}
