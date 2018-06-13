package com.example.diego.prototipo.app;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.diego.prototipo.R;
import com.example.diego.prototipo.database.BD;

import java.util.ArrayList;

/**
 * Created by Diego on 16/10/2017.
 */

public class Usuario{

    private String nome;
    private String email;
    private String senha;
    private int anoNascimento;
    private String profissao;
    private String restricao;
    private String escolaridade;
    private String perfil;
    private String genero;
    private ArrayList<Dificuldade> dificuldades;
    private Context context;

    public Usuario(Context context) {
        this.context = context;
        dificuldades = new ArrayList<Dificuldade>();
    }

    public ArrayList<Dificuldade> getDificuldades() {
        return dificuldades;
    }

    public void addDificuldade(Dificuldade d) {
        this.dificuldades.add(d);
    }

    public String getRestricao() {

        return restricao;
    }

    public void setRestricao(String restricao) {
        this.restricao = restricao;
    }

    public String getProfissao() {

        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public int getAnoNascimento() {
        return anoNascimento;

    }

    public void setAnoNascimento(int anoNascimento) {
        this.anoNascimento = anoNascimento;
    }

    public String getSenha() {

        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {

        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getPerfil() {

        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getEscolaridade() {

        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public void insertDB(){
        new BD(context).insertUsuario(this);
    }


}
