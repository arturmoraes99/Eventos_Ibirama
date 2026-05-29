package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventosibirama.R;
import com.example.eventosibirama.adapter.CategoriaAdapter;
import com.example.eventosibirama.adapter.EventoAdapter;
import com.example.eventosibirama.view.activity.DetalhesEventoActivity;
import com.example.eventosibirama.viewmodel.EventoViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class HomeFragment extends Fragment {

    private RecyclerView      rvCategorias, rvEventos;
    private TextInputEditText etBusca;
    private ProgressBar       progressBar;

    private CategoriaAdapter  categoriaAdapter;
    private EventoAdapter     eventoAdapter;
    private EventoViewModel   eventoViewModel;

    private TextWatcher buscaWatcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventoViewModel = new ViewModelProvider(this).get(EventoViewModel.class);

        etBusca      = view.findViewById(R.id.et_busca);
        rvCategorias = view.findViewById(R.id.rv_categorias);
        rvEventos    = view.findViewById(R.id.rv_eventos);
        progressBar  = view.findViewById(R.id.progress_bar);

        configurarRecyclerCategorias();
        configurarRecyclerEventos();
        observarViewModel();
        configurarBusca();

        carregarDadosIniciais();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) resetar();
    }

    // ── RecyclerViews ─────────────────────────────────────────────────────────

    private void configurarRecyclerCategorias() {
        categoriaAdapter = new CategoriaAdapter(categoria -> {
            // FIX #6: marca a categoria clicada como selecionada no adapter
            categoriaAdapter.setSelectedId(categoria.getId());

            // Limpa o campo de busca sem disparar o watcher
            if (etBusca != null && buscaWatcher != null) {
                etBusca.removeTextChangedListener(buscaWatcher);
                etBusca.setText("");
                etBusca.addTextChangedListener(buscaWatcher);
            }

            eventoViewModel.buscarEventosPorCategoria(categoria.getId());
        });

        rvCategorias.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategorias.setAdapter(categoriaAdapter);
    }

    private void configurarRecyclerEventos() {
        eventoAdapter = new EventoAdapter(evento -> {
            Intent intent = new Intent(getActivity(), DetalhesEventoActivity.class);
            intent.putExtra("evento_id", evento.getId());
            startActivity(intent);
        });

        rvEventos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEventos.setAdapter(eventoAdapter);
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    private void observarViewModel() {
        eventoViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null)
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        eventoViewModel.getErro().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
        });

        eventoViewModel.getCategorias().observe(getViewLifecycleOwner(),
                categorias -> categoriaAdapter.setCategorias(categorias));

        eventoViewModel.getEventos().observe(getViewLifecycleOwner(),
                eventos -> eventoAdapter.setEventos(eventos));
    }

    // ── Busca por texto ───────────────────────────────────────────────────────

    private void configurarBusca() {
        buscaWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // FIX #6: ao apagar a busca manualmente, remove destaque da categoria
                    categoriaAdapter.setSelectedId(null);
                    eventoViewModel.carregarTodosEventos();
                } else {
                    // Busca por texto → remove seleção de categoria
                    categoriaAdapter.setSelectedId(null);
                    eventoViewModel.buscarEventosPorNome(query);
                }
            }
        };
        etBusca.addTextChangedListener(buscaWatcher);
    }

    // ── Reset / carga inicial ─────────────────────────────────────────────────

    private void resetar() {
        if (etBusca != null && buscaWatcher != null) {
            etBusca.removeTextChangedListener(buscaWatcher);
            etBusca.setText("");
            etBusca.addTextChangedListener(buscaWatcher);
        }

        // FIX #6: remove o destaque de categoria ao voltar para a tela inicial
        if (categoriaAdapter != null) {
            categoriaAdapter.setSelectedId(null);
        }

        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        eventoViewModel.carregarCategorias();
        eventoViewModel.carregarTodosEventos();
    }
}
