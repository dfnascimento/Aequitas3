package com.example.diego.prototipo.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diego.prototipo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TelaInicalFragment extends Fragment {

    public TelaInicalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tela_inical, container, false);
    }
}
