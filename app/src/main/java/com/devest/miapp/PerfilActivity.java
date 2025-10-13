package com.devest.miapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

public class PerfilActivity extends AppCompatActivity {
    private EditText etNombrePerfil;
    private Button btnGuardar;
    private String emailUsuario;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        db = new DBHelper(this);

        etNombrePerfil = findViewById(R.id.etNombrePerfil);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);

        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";

        String nombreActual = db.obtenerNombre(emailUsuario);
        etNombrePerfil.setText(nombreActual);

        btnGuardar.setOnClickListener(v -> {
            String nuevo = etNombrePerfil.getText().toString().trim();
            if (nuevo.isEmpty()) {
                Toast.makeText(this, "Nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean ok = db.actualizarNombre(emailUsuario, nuevo);
            if (ok) {
                Intent data = new Intent();
                data.putExtra("nombre_editado", nuevo);
                setResult(RESULT_OK, data);
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}