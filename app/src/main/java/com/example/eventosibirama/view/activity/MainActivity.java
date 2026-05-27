package com.example.eventosibirama.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eventosibirama.R;
import com.example.eventosibirama.view.fragment.FavoritosFragment;
import com.example.eventosibirama.view.fragment.HomeFragment;
import com.example.eventosibirama.view.fragment.MapaFragment;
import com.example.eventosibirama.view.fragment.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Fragment inicial
        if (savedInstanceState == null) {
            carregarFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_favoritos) {
                fragment = new FavoritosFragment();
            } else if (id == R.id.nav_mapa) {
                fragment = new MapaFragment();
            } else if (id == R.id.nav_perfil) {
                fragment = new PerfilFragment();
            }

            if (fragment != null) {
                carregarFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void carregarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
