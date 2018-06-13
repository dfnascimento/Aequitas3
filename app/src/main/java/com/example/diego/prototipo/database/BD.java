package com.example.diego.prototipo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.diego.prototipo.app.Usuario;

/**
 * Created by Diego on 16/10/2017.
 */

public class BD {
    private  SQLiteDatabase db;

    public BD(Context context) {
        db = new BDCore(context).getWritableDatabase();

    }

    public void insertUsuario(Usuario u){

        ContentValues values = new ContentValues();
        values.put("nome", u.getNome());
        values.put("email", u.getEmail());
        values.put("senha", u.getSenha());
        values.put("ano", u.getAnoNascimento());
        values.put("genero", u.getGenero());
        values.put("profissao", u.getProfissao());
        values.put("escolaridade", u.getEscolaridade());
        values.put("restricao", u.getRestricao());
        values.put("perfil", u.getPerfil());

        db.insert("usuario", null, values);
    }

    public Usuario getUsuario(String email){

        Usuario u = new Usuario(null);

        Cursor c = db.rawQuery("SELECT nome, email, senha FROM usuario where email = '"+ email + "'", null );
        if(c.moveToFirst()){
            do{
                u.setNome(c.getString(0));
                u.setEmail(c.getString(1));
                u.setSenha(c.getString(2));

            }while(c.moveToNext());
        }

        return u;
    }

    public void updateUsuario(Usuario u){

        ContentValues values = new ContentValues();
        values.put("nome", u.getNome());
        values.put("email", u.getEmail());
        values.put("senha", u.getSenha());
        values.put("ano", u.getAnoNascimento());
        values.put("genero", u.getGenero());
        values.put("profissao", u.getProfissao());
        values.put("escolaridade", u.getEscolaridade());
        values.put("restricao", u.getRestricao());

        //db.update("usuario", null, "_id = ?", new String[]("" + ));
    }



    public Usuario searchUser(String email){

       return null;
    }
}