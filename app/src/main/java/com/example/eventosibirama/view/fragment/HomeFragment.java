package com.example.eventosibirama.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.eventosibirama.view.activity.CadastroCategoriaActivity;
import com.example.eventosibirama.view.activity.CadastroEventoActivity;
import com.example.eventosibirama.view.activity.DetalhesEventoActivity;
import com.example.eventosibirama.view.activity.EventosProximosActivity;
import com.example.eventosibirama.viewmodel.EventoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class HomeFragment extends Fragment {

    private RecyclerView      rvCategorias, rvEventos;
    private TextInputEditText etBusca;
    private ProgressBar       progressBar;
    private LinearLayout      layoutVazioEventos;
    private TextView          tvVazioEventos;
    private MaterialButton    btnLimparFiltro;
    private FloatingActionButton fabCadastrar;
    private MaterialButton    btnEventosProximos;

    private CategoriaAdapter categoriaAdapter;
    private EventoAdapter    eventoAdapter;
    private EventoViewModel  eventoViewModel;
    private TextWatcher      buscaWatcher;
    private boolean          filtroAtivo = false;


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

        etBusca            = view.findViewById(R.id.et_busca);
        rvCategorias       = view.findViewById(R.id.rv_categorias);
        rvEventos          = view.findViewById(R.id.rv_eventos);
        progressBar        = view.findViewById(R.id.progress_bar);
        layoutVazioEventos = view.findViewById(R.id.layout_vazio_eventos);
        tvVazioEventos     = view.findViewById(R.id.tv_vazio_eventos);
        btnLimparFiltro    = view.findViewById(R.id.btn_limpar_filtro);
        fabCadastrar       = view.findViewById(R.id.fab_cadastrar_evento);
        btnEventosProximos = view.findViewById(R.id.btn_eventos_proximos);

        configurarRecyclerCategorias();
        configurarRecyclerEventos();
        observarViewModel();
        configurarBusca();

        btnLimparFiltro.setOnClickListener(v -> resetar());


        fabCadastrar.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CadastroEventoActivity.class)));

        btnEventosProximos.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EventosProximosActivity.class)));


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
            filtroAtivo = true;
            categoriaAdapter.setSelectedId(categoria.getId());
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
            if (isLoading == null) return;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                layoutVazioEventos.setVisibility(View.GONE);
                rvEventos.setVisibility(View.GONE);
            }
        });

        eventoViewModel.getErro().observe(getViewLifecycleOwner(), erro -> {
            if (erro != null) Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
        });

        eventoViewModel.getCategorias().observe(getViewLifecycleOwner(),
                categorias -> categoriaAdapter.setCategorias(categorias));

        eventoViewModel.getEventos().observe(getViewLifecycleOwner(), eventos -> {
            eventoAdapter.setEventos(eventos);
            atualizarEstadoVazio(eventos == null || eventos.isEmpty());
        });
    }

    // ── Estado vazio ──────────────────────────────────────────────────────────

    private void atualizarEstadoVazio(boolean vazio) {
        if (vazio) {
            rvEventos.setVisibility(View.GONE);
            layoutVazioEventos.setVisibility(View.VISIBLE);
            if (filtroAtivo) {
                tvVazioEventos.setText(R.string.nenhum_evento_categoria);
                btnLimparFiltro.setVisibility(View.VISIBLE);
            } else {
                tvVazioEventos.setText(R.string.nenhum_evento);
                btnLimparFiltro.setVisibility(View.GONE);
            }
        } else {
            layoutVazioEventos.setVisibility(View.GONE);
            rvEventos.setVisibility(View.VISIBLE);
        }
    }

    // ── Busca ─────────────────────────────────────────────────────────────────

    private void configurarBusca() {
        buscaWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    filtroAtivo = false;
                    categoriaAdapter.setSelectedId(null);
                    eventoViewModel.carregarTodosEventos();
                } else {
                    filtroAtivo = true;
                    categoriaAdapter.setSelectedId(null);
                    eventoViewModel.buscarEventosPorNome(query);
                }
            }
        };
        etBusca.addTextChangedListener(buscaWatcher);
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    public void resetar() {
        filtroAtivo = false;
        if (etBusca != null && buscaWatcher != null) {
            etBusca.removeTextChangedListener(buscaWatcher);
            etBusca.setText("");
            etBusca.addTextChangedListener(buscaWatcher);
        }
        if (categoriaAdapter != null) categoriaAdapter.setSelectedId(null);
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        eventoViewModel.carregarCategorias();
        eventoViewModel.carregarTodosEventos();
    }
}
