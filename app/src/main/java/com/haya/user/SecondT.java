package com.haya.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondT extends AppCompatActivity implements View.OnClickListener {


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_t);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        setTitle("Time Table - Day");


        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btn10 = findViewById(R.id.btn10);
        Button btn11 = findViewById(R.id.btn11);


        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn5:
                Intent In = new Intent(SecondT.this,ThirdT.class);
                startActivity(In);
            break;

            case R.id.btn6:
                Intent In2 = new Intent(SecondT.this,ThirdT.class);
                startActivity(In2);
                break;

            case R.id.btn7:
                Intent In3 = new Intent(SecondT.this,ThirdT.class);
                startActivity(In3);
                break;

            case R.id.btn8:
                Intent In4 = new Intent(SecondT.this,ThirdT.class);
                startActivity(In4);
                break;

            case R.id.btn9:
                Intent In5 = new Intent(SecondT.this,ThirdT.class);
                startActivity(In5);
                break;

            case R.id.btn10:
                Intent In6 = new Intent(SecondT.this,ThirdT.class);
                startActivity(In6);
                break;

            case R.id.btn11:
                Intent In7 = new Intent(SecondT.this,ThirdT.class);
                startActivity(In7);
                break;

        }
    }
}
