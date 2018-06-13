package com.example.diego.prototipo.app;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Diego on 05/11/2017.
 */

public class Tupla {
    private LatLng latlong;
    private double acess; //Acessibilidade 0 ~ 100

    private int tipo; // 1 para calçada e 2 para rebaixamento
    private String rua;
    private String ruaFim;
    private String next; // direita ou esquerda

    private int calcada_piso_irregular;
    private int calcada_paralelepipedo;
    private int calcada_ausencia;
    private int obstaculos_arvores;
    private int obstaculos_obras;
    private int obstaculos_outros;
    private int rebaixamento_ausencia;
    private int rebaixamento_problema;
    private int subida;
    private int descida;

    public Tupla(LatLng l , double a, int cpi, int cp, int ca, int oa, int ob, int oo, int ra, int rp, int s, int d, int tipo, String rua, String ruaFim, String next){
        latlong = l;
        acess = a;

        calcada_piso_irregular = cpi;
        calcada_paralelepipedo = cp;
        calcada_ausencia = ca;
        obstaculos_arvores = oa;
        obstaculos_obras = ob;
        obstaculos_outros = oo;
        rebaixamento_ausencia = ra;
        rebaixamento_problema = rp;
        subida = s;
        descida = d;

        this.tipo = tipo;
        this.rua = rua;
        this.ruaFim = ruaFim;
        this.next = next;
    }

    public int getTipo() {
        return tipo;
    }

    public String getRua() {
        return rua;
    }

    public String getRuaFim() {
        return ruaFim;
    }

    public String getNext() {
        return next;
    }

    public String descricao(){

        String s = "Nível de acessibilidade: " +  (int) Math.round((double)acess) + "%\n\n";
        //String s = "Accessibility level: " +  (int) Math.round((double)acess) + "%\n\n";

        if((calcada_piso_irregular +
        calcada_paralelepipedo +
        calcada_ausencia +
        obstaculos_arvores +
        obstaculos_obras +
        obstaculos_outros +
        rebaixamento_ausencia +
        rebaixamento_problema) > 0) {
            s = s + "Problemas Reportados (número de usuários que reportaram): \n\n";
            //s = s + " Reported Issues (number of users): \n\n";
        }
        if (calcada_piso_irregular > 0 || calcada_paralelepipedo > 0 || calcada_ausencia > 0){
            s = s + "Calçada: \n";
            //s = s + "Sidewalk: \n";

        }

        if (calcada_piso_irregular > 0){
            s = s + "- Piso irregular: " + calcada_piso_irregular + "\n";
            //s = s + "- Irregular sidewalk: " + calcada_piso_irregular + "\n";


        }


        if (calcada_paralelepipedo > 0){
            s = s + "- Paralelepipedo ou Petit-Pavé: " + calcada_paralelepipedo + "\n";
            //s = s + "- Paving-stone or Petit-Pavé: " + calcada_paralelepipedo + "\n";
            //paving-stone
        }

        if (calcada_ausencia > 0){
            s = s + "- Ausência de Calçada: " + calcada_ausencia + "\n";

            //s = s + "- Absence of sidewalk: " + calcada_ausencia + "\n";

        }

        if (obstaculos_arvores > 0 ||obstaculos_outros > 0 || obstaculos_obras > 0){
            s = s + "\nObstaculos: \n";
            //s = s + "\nObstacles: \n";
        }

        if (obstaculos_arvores > 0){
            s = s + "- Árvores: " + obstaculos_arvores + "\n";
        }

        if (obstaculos_obras > 0){
            s = s + "- Obras: " + obstaculos_obras + "\n";
        }

        if (obstaculos_outros > 0){
            s = s + "- Outros obstaculos: " + obstaculos_outros + "\n";
            //s = s + "- Others obstaculos: " + obstaculos_outros + "\n";
        }


        if (rebaixamento_ausencia > 0 ||rebaixamento_problema > 0){
            s = s + "Rebaixamentos: \n";
        }

        if (rebaixamento_ausencia > 0){
            s = s + "- Ausencia de Rebaixamentos: " + rebaixamento_ausencia + "\n";
        }

        if (rebaixamento_problema > 0){
            s = s + "- Problema com Rebaixamento: " + rebaixamento_problema + "\n";
        }

        if (subida > 0){

            s = s + "\nSubida: " + subida + " graus \n";
            //s = s + "\nUphill: " + subida + " degrees \n";

        }

        if (descida > 0){

            s = s + "\nDescida: " + descida + " graus \n";
        }

        return s;

    }

    public LatLng getLatlong() {
        return latlong;
    }

    public double getAcess() {
        return acess;
    }
}
