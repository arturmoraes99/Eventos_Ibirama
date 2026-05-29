package com.example.eventosibirama.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.fragment.FavoritosFragment;
import com.example.eventosibirama.view.fragment.HomeFragment;
import com.example.eventosibirama.view.fragment.MapaFragment;
import com.example.eventosibirama.view.fragment.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private HomeFragment      homeFragment;
    private FavoritosFragment favoritosFragment;
    private MapaFragment      mapaFragment;
    private PerfilFragment    perfilFragment;
    private Fragment          activeFragment;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            // Primeira criação: instancia e adiciona todos os fragments
            configurarFragments();
        } else {
            // Restauração após rotação: recupera instâncias existentes
            restaurarFragments();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                mostrarFragment(homeFragment);
            } else if (id == R.id.nav_favoritos) {
                mostrarFragment(favoritosFragment);
            } else if (id == R.id.nav_mapa) {
                mostrarFragment(mapaFragment);
            } else if (id == R.id.nav_perfil) {
                mostrarFragment(perfilFragment);
            } else {
                return false;
            }
            return true;
        });
    }

    /** Adiciona todos os fragments de uma vez, escondendo os que não são ativos. */
    private void configurarFragments() {
        homeFragment      = new HomeFragment();
        favoritosFragment = new FavoritosFragment();
        mapaFragment      = new MapaFragment();
        perfilFragment    = new PerfilFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.fragment_container, perfilFragment,    "perfil").hide(perfilFragment)
                .add(R.id.fragment_container, mapaFragment,      "mapa").hide(mapaFragment)
                .add(R.id.fragment_container, favoritosFragment, "favoritos").hide(favoritosFragment)
                .add(R.id.fragment_container, homeFragment,      "home")
                .commit();

        activeFragment = homeFragment;
    }

    /** Recupera os fragments salvos pelo FragmentManager após rotação/recriação. */
    private void restaurarFragments() {
        FragmentManager fm = getSupportFragmentManager();
        homeFragment      = (HomeFragment)      fm.findFragmentByTag("home");
        favoritosFragment = (FavoritosFragment) fm.findFragmentByTag("favoritos");
        mapaFragment      = (MapaFragment)      fm.findFragmentByTag("mapa");
        perfilFragment    = (PerfilFragment)    fm.findFragmentByTag("perfil");

        // Descobre qual está visível
        for (Fragment f : fm.getFragments()) {
            if (!f.isHidden()) { activeFragment = f; break; }
        }
        if (activeFragment == null) activeFragment = homeFragment;
    }

    /** Alterna entre fragments sem recriar — usa show/hide para preservar o estado. */
    private void mostrarFragment(Fragment target) {
        if (target == null || target == activeFragment) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment).show(target).commit();
        activeFragment = target;
    }
}
