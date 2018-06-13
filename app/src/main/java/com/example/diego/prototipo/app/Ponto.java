package com.example.diego.prototipo.app;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Diego on 04/11/2017.
 */

public class Ponto {

    private String nome;
    private LatLng ponto;

    public LatLng getPonto() {
        return ponto;
    }

    public void setPonto(LatLng ponto) {
        this.ponto = ponto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
