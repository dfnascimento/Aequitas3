package com.example.diego.prototipo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.diego.prototipo.R;

import java.util.ArrayList;

public class TelaCadastro1 extends AppCompatActivity {

    private AutoCompleteTextView nome;
    private AutoCompleteTextView email;
    private EditText pass1;
    private EditText pass2;
    private EditText ano;
    private Spinner genero;
    private ArrayList<String>  gereros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro1);

        nome = (AutoCompleteTextView) findViewById(R.id.nome);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        pass1 = (EditText) findViewById(R.id.password);
        pass2 = (EditText) findViewById(R.id.password2);
        ano = (EditText) findViewById(R.id.ano_nascimento);


        genero= (Spinner) findViewById(R.id.spinnerGenero);

        gereros = new ArrayList<String>();
        gereros.add("Genero");
        gereros.add("Feminino");
        gereros.add("Masculino");
        gereros.add("Outro");
        gereros.add("Prefiro não responder");



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,gereros);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        genero.setAdapter(dataAdapter);

    }

    public void enviar(View view) {


        // Reset errors.
        nome.setError(null);
        email.setError(null);
        pass1.setError(null);
        pass2.setError(null);
        ano.setError(null);

        // Store values at the time of the login attempt.

        String nom = nome.getText().toString();
        String emai = email.getText().toString();
        String p1 = pass1.getText().toString();
        String p2 = pass2.getText().toString();
        String an = ano.getText().toString();
        String gen = gereros.get(genero.getSelectedItemPosition());


//        String gene = genero.getText().toString();
        boolean cancel = false;
        View focusView = null;

        //Valida se os campos foram digitados

        if (TextUtils.isEmpty(nom)) {
            nome.setError("Este campo é obrigatório");
            focusView = nome;
            cancel = true;
        }
        else if (nom.length() < 10) {
            nome.setError("Digite o nome completo");
            focusView = nome;
            cancel = true;
        }
        else if (TextUtils.isEmpty(emai)) {
            email.setError("Este campo é obrigatório");
            focusView = email;
            cancel = true;
        }
        else if (!emai.contains("@") || !emai.contains(".com")){
            email.setError("Digite um email válido");
            focusView = email;
            cancel = true;
        }
        else if (TextUtils.isEmpty(p1)) {
            pass1.setError("Este campo é obrigatório");
            focusView = pass1;
            cancel = true;
        }
        else if (p1.length() < 6) {
            pass1.setError("Digite uma senha maior que 5 digitos");
            focusView = pass1;
            cancel = true;
        }
        else if (TextUtils.isEmpty(p2)) {
            pass2.setError("Este campo é obrigatório");
            focusView = pass2;
            cancel = true;
        }
        else if (!p1.equals(p2)){
            pass1.setError("Senhas não coincidem");
            pass2.setError("Senhas não coincidem");
            focusView = pass1;
            cancel = true;
        }
        else if (TextUtils.isEmpty(an)) {
            ano.setError("Este campo é obrigatório");
            focusView = ano;
            cancel = true;
        }

        else if (Integer.parseInt(an) < 1898 || Integer.parseInt(an) > 2014 ) {
            ano.setError("Digite um ano de nascimento válido");
            focusView = ano;
            cancel = true;
        }
        else if (gen == "Genero") {

            TextView errorText = (TextView)genero.getSelectedView();
            errorText.setError("");
            //errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Selecione uma opção");//changes the selected item text to this
            //genero.setError("Este campo é obrigatório");
            focusView = genero;
            cancel = true;
        }




        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            Intent cadastro = new Intent(this, TelaCadastro2.class);
            cadastro.putExtra("nome", nom);
            cadastro.putExtra("email", emai);
            cadastro.putExtra("senha", p1);
            cadastro.putExtra("ano", Integer.parseInt(an));
            cadastro.putExtra("genero", gen);
            this.finish();
            startActivity(cadastro);
        }
    }


}
