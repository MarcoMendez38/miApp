package com.devest.miapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CamaraActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnTomarFoto;
    private Uri fotoUri;
    private File fotoArchivo;

    // Permiso de cámara
    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    abrirCamara();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    // Resultado de cámara
    private final ActivityResultLauncher<Intent> resultadoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (fotoUri != null) {
                        imageView.setImageURI(fotoUri);
                        Toast.makeText(this, "Foto tomada con éxito", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        imageView = findViewById(R.id.imageView);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        btnTomarFoto.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Verificar si hay alguna app que pueda manejar la cámara
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No se encontró una aplicación de cámara en tu dispositivo", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            fotoArchivo = crearArchivoTemporal();
            if (fotoArchivo != null) {
                fotoUri = FileProvider.getUriForFile(this,
                        "com.devest.miapp.fileprovider",
                        fotoArchivo);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                resultadoCamaraLauncher.launch(intent);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error al crear el archivo de imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir la cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private File crearArchivoTemporal() throws IOException {
        String nombreArchivo = "foto_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File directorio = getExternalFilesDir(null);

        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }

        return File.createTempFile(nombreArchivo, ".jpg", directorio);
    }
}