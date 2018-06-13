package com.example.diego.prototipo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.diego.prototipo.R;
import com.example.diego.prototipo.app.Dificuldade;
import com.example.diego.prototipo.app.Usuario;
import com.example.diego.prototipo.database.BD;

import java.util.ArrayList;

public class TelaCadastro2 extends AppCompatActivity {

    private RatingBar rate1, rate2, rate3;
    private AutoCompleteTextView profissao;
    private AutoCompleteTextView restricao;

    private Spinner escolaridade;
    private ArrayList<String>  escolaridades;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro2);

        rate1 = (RatingBar) findViewById(R.id.ratingBar1);
        rate2 = (RatingBar) findViewById(R.id.ratingBar2);
        rate3 = (RatingBar) findViewById(R.id.ratingBar3);


        rate1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar,
                                                float rating, boolean fromUser) {
                        rate1.setRating((int) Math.ceil(rating));
                    }
                });
        rate2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar,
                                        float rating, boolean fromUser) {
                rate2.setRating((int) Math.ceil(rating));
            }
        });

        rate3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar,
                                        float rating, boolean fromUser) {
                rate3.setRating((int) Math.ceil(rating));
            }
        });


        profissao = (AutoCompleteTextView) findViewById(R.id.profissao);
        restricao = (AutoCompleteTextView) findViewById(R.id.restricao);


        escolaridade= (Spinner) findViewById(R.id.spinnerEscolaridade);

        escolaridades = new ArrayList<String>();
        escolaridades.add("Escolaridade");
        escolaridades.add("Sem instrução");
        escolaridades.add("Ensino Fundamental Incompleto");
        escolaridades.add("Ensino Fundamental Completo");
        escolaridades.add("Ensino Médio Incompleto");
        escolaridades.add("Ensino Médio Completo");
        escolaridades.add("Ensino Superior Incompleto");
        escolaridades.add("Ensino Superior Completo");
        escolaridades.add("Pós Graduação Incompleta");
        escolaridades.add("Pós Graduação Completa");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,escolaridades);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        escolaridade.setAdapter(dataAdapter);


    }


    public void enviar(View view) {


        // Reset errors.
        profissao.setError(null);
        restricao.setError(null);


        String esc = escolaridades.get(escolaridade.getSelectedItemPosition());
        String prof = profissao.getText().toString();
        String rest = restricao.getText().toString();

        int rat1 = (int) rate1.getRating();
        int rat2 = (int) rate2.getRating();
        int rat3 = (int) rate3.getRating();

        Log.d("V", "Num Stars: " + rat1 + " : " + rat2 + " : " + rat3);

        boolean cancel = false;
        View focusView = null;

        //Valida se os campos foram digitados

        if (rat1 == 0 || rat2 == 0 || rat3 == 0 ) {
            //restricao.setError("Este campo é obrigatório");
            Toast toast = Toast.makeText(this, "Nível(is) de difilculdade não selecionados",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0,0);
            toast.show();
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //focusView.requestFocus();
        } else {

            Usuario user = new Usuario(this);

            Intent it = getIntent();

            user.setNome(it.getStringExtra("nome"));
            user.setEmail(it.getStringExtra("email"));
            user.setSenha(it.getStringExtra("senha"));
            user.setAnoNascimento(it.getIntExtra("ano", 0));
            user.setGenero(it.getStringExtra("genero"));

            user.addDificuldade(new Dificuldade("Subidas e Descidas", rat1));
            user.addDificuldade(new Dificuldade("Calçadas com Barreiras", rat2));
            user.addDificuldade(new Dificuldade("Falta de Rebaixamentos", rat3));

            user.setProfissao(prof);
            user.setEscolaridade(esc);
            user.setRestricao(rest);
            user.setPerfil("UsuarioAPP");

            new BD(this).insertUsuario(user);

            Intent principal = new Intent(this, Program.class);
            principal.putExtra("email", user.getEmail());
            this.finish();
            startActivity(principal);
        }
    }



}
