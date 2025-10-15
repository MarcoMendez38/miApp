package com.devest.miapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                        // Mostrar la foto recién tomada
                        imageView.setImageURI(fotoUri);
                        Toast.makeText(this, "📸 Foto tomada con éxito", Toast.LENGTH_SHORT).show();

                        // Guardar en galería
                        guardarEnGaleria(fotoUri);
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    // Método para abrir la cámara del teléfono
    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            fotoArchivo = crearArchivoTemporal();
            if (fotoArchivo != null) {
                fotoUri = FileProvider.getUriForFile(
                        this,
                        "com.devest.miapp.fileprovider", // authority debe coincidir con el Manifest
                        fotoArchivo
                );

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                resultadoCamaraLauncher.launch(intent);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error al crear archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Crear archivo temporal donde se guarda la foto antes de moverla a la galería
    private File crearArchivoTemporal() throws IOException {
        String nombreArchivo = "foto_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }

        return File.createTempFile(nombreArchivo, ".jpg", directorio);
    }

    // Guardar la foto en la galería del teléfono (MediaStore API)
    private void guardarEnGaleria(Uri uriFoto) {
        if (uriFoto == null) {
            Toast.makeText(this, "No se pudo obtener la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Metadatos para la nueva imagen
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "Foto_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MiApp");

            // Crear entrada en la galería
            Uri uriDestino = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uriDestino == null) {
                Toast.makeText(this, "Error al crear registro en galería", Toast.LENGTH_SHORT).show();
                return;
            }

            // Copiar la imagen desde la ruta temporal al destino visible por la galería
            try (InputStream in = getContentResolver().openInputStream(uriFoto);
                 OutputStream out = getContentResolver().openOutputStream(uriDestino)) {

                if (in != null && out != null) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                    Toast.makeText(this, "✅ Foto guardada en la galería", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar en galería: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
