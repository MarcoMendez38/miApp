package com.devest.miapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.content.ContentValues;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "app_local.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASS = "password";
    public static final String COL_NOMBRE = "nombre";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASS + " TEXT, " +
                COL_NOMBRE + " TEXT)";
        db.execSQL(sqlUsers);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Registrar usuario
    public boolean registrarUsuario(String email, String pass, String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EMAIL, email);
        cv.put(COL_PASS, pass);
        cv.put(COL_NOMBRE, nombre);
        long id = db.insert(TABLE_USERS, null, cv);
        return id != -1;
    }

    // Verificar login
    public boolean validarUsuario(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COL_ID},
                COL_EMAIL + "=? AND " + COL_PASS + "=?",
                new String[]{email, pass},
                null, null, null);
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    // Obtener nombre por email
    public String obtenerNombre(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COL_NOMBRE},
                COL_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        String nombre = "";
        if (c.moveToFirst()) {
            nombre = c.getString(c.getColumnIndexOrThrow(COL_NOMBRE));
        }
        c.close();
        return nombre;
    }

    // Actualizar nombre
    public boolean actualizarNombre(String email, String nuevoNombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOMBRE, nuevoNombre);
        int rows = db.update(TABLE_USERS, cv, COL_EMAIL + "=?", new String[]{email});
        return rows > 0;
    }
}