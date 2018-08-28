package com.example.breno.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.breno.agenda.model.Aluno;

import java.util.ArrayList;
import java.util.List;

public class AlunoDAO extends SQLiteOpenHelper{

    public AlunoDAO(Context context) {
        super(context,"Agenda", null, 1);
    }

    @Override
    //Cria a tabela no Sql
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Alunos (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, endereco TEXT, telefone TEXT, idade INTEGER, site TEXT, nota REAL);";
        db.execSQL(sql);
    }
    //Funcao para caso algum novo registro seja adicionado como dado
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS Alunos";
        db.execSQL(sql);
        onCreate(db);
    }

    //Metodo Insere
    public void insere(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = PegaDadosDoAluno(aluno);

        db.insert("Alunos", null,dados);
    }


    private ContentValues PegaDadosDoAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
          dados.put("nome",aluno.getNome());
          dados.put("endereco",aluno.getEndereco());
          dados.put("telefone",aluno.getTelefone());
          dados.put("idade",aluno.getIdade());
          dados.put("site",aluno.getSite());
          dados.put("nota",aluno.getNota());
          return dados;
    }

    //Metodo Buscar
    public List<Aluno> buscaAlunos() {
        String sql = "SELECT * FROM Alunos;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Aluno> aluno = new ArrayList<>();
        while (c.moveToNext()) {
            Aluno aluno1 = new Aluno();
            aluno1.setId(c.getLong(c.getColumnIndex("id")));
            aluno1.setNome(c.getString(c.getColumnIndex("nome")));
            aluno1.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno1.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno1.setIdade(c.getInt(c.getColumnIndex("idade")));
            aluno1.setSite(c.getString(c.getColumnIndex("site")));
            aluno1.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.add(aluno1);
        }

        return aluno;
    }

    //Metodo Delete
    public void deletar(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        String[] params = {aluno.getId().toString()};
        db.delete("Alunos", "id = ?", params);
    }

    public void alterar(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = PegaDadosDoAluno(aluno);

        String[] params = {aluno.getId().toString()};
        db.update("Alunos",dados, "id = ?", params);
    }
}
