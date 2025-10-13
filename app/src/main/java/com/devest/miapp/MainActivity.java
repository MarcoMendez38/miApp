package com.devest.miapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Carga el layout que pondremos abajo

        // Referencias a los elementos visuales
        TextView tvTitulo = findViewById(R.id.tvTituloMain);
        Button btnIrLogin = findViewById(R.id.btnIrLogin);

        // Texto de bienvenida
        tvTitulo.setText("Bienvenido a mi App");

        // Evento: abrir LoginActivity
        btnIrLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
