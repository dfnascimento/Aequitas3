package com.example.diego.prototipo.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Diego on 16/10/2017.
 */

public class BDCore extends SQLiteOpenHelper {

    private static final String NOME_BD = "Banco";

    private static final int VERSAO_BD = 1;

    public BDCore(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuario (\n" +
                "  _id INTEGER primary key autoincrement ,\n" +
                "  escolaridade VARCHAR   NOT NULL ,\n" +
                "  perfil VARCHAR   NOT NULL ,\n" +
                "  genero VARCHAR   NOT NULL ,\n" +
                "  nome VARCHAR   NOT NULL ,\n" +
                "  email VARCHAR   NOT NULL ,\n" +
                "  senha VARCHAR   NOT NULL ,\n" +
                "  ano INTEGER   NOT NULL ,\n" +
                "  profissao VARCHAR    ,\n" +
                "  restricao VARCHAR      );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE usuario");
        onCreate(db);

    }
}
