package com.example.diego.prototipo.app;

/**
 * Created by Diego on 16/10/2017.
 */

public class Dificuldade {

    private String dificuldade;
    private int valor;

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public Dificuldade(String dificuldade, int valor) {

        this.dificuldade = dificuldade;
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
