package com.example.miniprj;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity5 extends AppCompatActivity {

    EditText name_input, number_input;
    Button add_button;
    ImageView img4;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        name_input = findViewById(R.id.name_input);
        number_input = findViewById(R.id.number_input);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity5.this);
                myDB.addNumber(name_input.getText().toString(),
                        number_input.getText().toString());
            }
        });
        img4=findViewById(R.id.left_icon5);
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}