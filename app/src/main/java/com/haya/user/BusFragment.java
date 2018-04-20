package com.haya.user;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BusFragment extends AppCompatActivity {

@SuppressLint("RestrictedApi")
@Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bus);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    setTitle("Bus Lines");
       /* view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

    }

    public BusFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus, container, false);

    }

}