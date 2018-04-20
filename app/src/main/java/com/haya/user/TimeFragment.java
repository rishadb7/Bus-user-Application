package com.haya.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


public class TimeFragment extends AppCompatActivity implements View.OnClickListener {

@SuppressLint("RestrictedApi")
@Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_time);
        setTitle("Time Table - Lines");

    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    Button btn1= findViewById(R.id.btn1);
    Button btn2 = findViewById(R.id.btn2);
    Button btn3= findViewById(R.id.btn3);
    Button btn4 = findViewById(R.id.btn4);


    btn1.setOnClickListener(this);
    btn2.setOnClickListener(this);
    btn3.setOnClickListener(this);
    btn4.setOnClickListener(this);
}
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                Intent In = new Intent(TimeFragment.this,SecondT.class);
                startActivity(In);
                break;

            case R.id.btn2:
                Intent In2 = new Intent(TimeFragment.this,SecondT.class);
                startActivity(In2);
                break;

            case R.id.btn3:
                Intent In3 = new Intent(TimeFragment.this, SecondT.class);
                startActivity(In3);
                break;

            case R.id.btn4:
                Intent In4 = new Intent(TimeFragment.this, SecondT.class);
                startActivity(In4);
                break;


        }
    }

}
