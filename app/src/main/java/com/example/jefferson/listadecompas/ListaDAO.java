package com.example.jefferson.listadecompas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ListaDAO extends SQLiteOpenHelper {

    private static final int VERSAO = 1;
    private static final String TABELA = "Produto";
    private static final String DATABASE =  "DadosProduto";

    public ListaDAO(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    public void inserirProduto(Produto produto){

        ContentValues values = new ContentValues();

        values.put("Produto", produto.getProduto());


        getWritableDatabase().insert(TABELA, null, values);
    }

    public void apagarProduto (Produto produto){
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {produto.id};
        db.delete(TABELA, "ID=?", args);
    }


    public void onCreate(SQLiteDatabase db) {
        String ddl = "CREATE TABLE " + TABELA
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " Produto TEXT NOT NULL);";

        db.execSQL(ddl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<Produto> getLista(){

        List<Produto> produtos = new ArrayList<Produto>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABELA + " ORDER BY Produto;", null);

        Log.d("meuLog", "passou aqui " + c.getColumnName(1));


        while(c.moveToNext()) {
            Produto p = new Produto();
            p.setId(c.getString(c.getColumnIndex("ID")));
            p.setProduto(c.getString(c.getColumnIndex("Produto")));
            Log.d("meuLog", "id: " + c.getString(c.getColumnIndex("ID")));
            Log.d("meuLog", "Produto: " + c.getString(c.getColumnIndex("Produto")));
            produtos.add(p);
        }
        c.close();



        return produtos;
    }



    public boolean isContato(Produto produto){
        String[] parametros = {produto.produto};
        Cursor rawQuery = getReadableDatabase().rawQuery("SELECT * FROM " + TABELA + " WHERE Produto = ?", parametros);
        int total = rawQuery.getCount();
        return total > 0;
    }
    public void deletaTabela() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABELA + " ORDER BY Produto;", null);
        while(c.moveToNext()) {
            SQLiteDatabase db = getWritableDatabase();
            String[] args = {c.getString(c.getColumnIndex("ID"))};
            db.delete(TABELA, "ID=?", args);
        }
        c.close();
    }

}
