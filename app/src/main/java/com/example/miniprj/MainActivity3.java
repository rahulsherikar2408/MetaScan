package com.example.miniprj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity3 extends AppCompatActivity {

    CardView b1,b2,b3;
    ImageView im1,im2,im3;
    TextView txt1,txt2,txt3;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("MetaScan");
        setContentView(R.layout.activity_main3);
        txt1=findViewById(R.id.textView6);
        txt2=findViewById(R.id.textView7);
        txt3=findViewById(R.id.textView8);
        im1=findViewById(R.id.btnimg);
        im2=findViewById(R.id.btnimg2);
        im3=findViewById(R.id.btnimg3);
        b1=findViewById(R.id.detect);
        b2=findViewById(R.id.instruct);
        b3=findViewById(R.id.add_number);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity1();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3();
            }
        });
    }

    private void openActivity1() {
        Intent i1=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i1);
    }

    private void openActivity2() {
        Intent i2=new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(i2);
    }

    private void openActivity3() {
        Intent i3=new Intent(getApplicationContext(), MainActivity4.class);
        startActivity(i3);
    }
}