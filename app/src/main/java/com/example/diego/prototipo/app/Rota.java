package com.example.diego.prototipo.app;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Diego on 05/11/2017.
 */

public class Rota {

    private Ponto inicio;
    private Ponto fim;

    private ArrayList<Tupla> lista1;
    private ArrayList<Tupla> lista2;
    private ArrayList<Tupla> lista3;

    private int rotaSelecionada;



    public Rota (Ponto i, Ponto f){
        inicio = i;
        fim = f;

        lista1 = new ArrayList<Tupla>();
        lista2 = new ArrayList<Tupla>();
        lista3 = new ArrayList<Tupla>();
    }

    public ArrayList<String> textoRota(int i){
        ArrayList<Tupla> list;

        if (i == 1)
            list = lista1;
        else if (i == 2)
            list = lista2;
        else if (i == 3)
            list = lista3;
        else
            list = new ArrayList<Tupla>();

        ArrayList<String> s = new ArrayList<String>();
        int count = 1;

        for (Tupla t : list) {

            if(t.getTipo() == 1){

                if(t.getRuaFim() != "fim") {
                    s.add(count + " - Siga em frente pela " + t.getRua() + " até o cruzamento com a " + t.getRuaFim());
                } else {
                    s.add(count + " - Siga em frente pela " + t.getRua() + " até o seu destino");
                }
                count++;
            }
            else if (t.getTipo() == 2){
                s.add(count + " - Atravesse a " + t.getRua() + " na faixa de pedestre");
                count++;
            }

            if (t.getNext() != " " ){
                s.add(count + " - Vire a " + t.getNext());
                count++;
            }

        }

        s.add(count + " - Chegou ao seu destino");

        return s;
    }

    public void addTupla(Tupla t, int lista){

        if (lista == 1) {
            lista1.add(t);
        }else if (lista == 2){
            lista2.add(t);
        }else if (lista == 3){
            lista3.add(t);
        }

    }

    public int getRotaSelecionada() {
        return rotaSelecionada;
    }

    public void setRotaSelecionada(int rotaSelecionada) {
        this.rotaSelecionada = rotaSelecionada;
    }

    public Ponto getInicio() {
        return inicio;
    }

    public Ponto getFim() {
        return fim;
    }

    public ArrayList<Tupla> getLista1() {
        return lista1;
    }

    public ArrayList<Tupla> getLista2() {
        return lista2;
    }

    public ArrayList<Tupla> getLista3() {
        return lista3;
    }

    public int getDistancia(int i){

        float distancia = 0;
        ArrayList<Tupla> list;
        if (i == 1)
            list = lista1;
        else if (i == 2)
            list = lista2;
        else if (i == 3)
            list = lista3;
        else
            list = new ArrayList<Tupla>();

        ArrayList<String> s = new ArrayList<String>();
        int count = 1;

        for (int j = 0; j < list.size()-1; j++) {
            float[] results = new float[1];
            Location.distanceBetween(list.get(j).getLatlong().latitude, list.get(j).getLatlong().longitude,
                    list.get(j+1).getLatlong().latitude, list.get(j+1).getLatlong().longitude, results);

            distancia = distancia + results[0];

            Log.d("A", "Rota: " + i + " Distancia: " + results[0]);

        }

        return Math.round(distancia);
    }
}
