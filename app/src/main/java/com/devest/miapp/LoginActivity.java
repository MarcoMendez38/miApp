package com.devest.miapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;


public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etNombre;
    private Button btnLogin, btnRegistrar;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre); // para registrar
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.validarUsuario(email, pass)) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.putExtra("email_usuario", email);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegistrar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString();
            String nombre = etNombre.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos para registrar", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean ok = db.registrarUsuario(email, pass, nombre);
            if (ok) {
                Toast.makeText(this, "Usuario registrado. Ahora inicia sesión.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: el email ya existe o hubo un fallo.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}