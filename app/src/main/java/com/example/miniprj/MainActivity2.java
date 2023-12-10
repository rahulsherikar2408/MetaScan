package com.example.miniprj;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity2 extends AppCompatActivity {

    private ImageView img2;
    TextView txt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        txt1=findViewById(R.id.textView1);
        img2=findViewById(R.id.left_icon2);
        //txt1.setText("MetaScan is a cutting-edge mobile application designed to revolutionize security and convenience across the various environments. Leveraging state-of-the-art technology, MetaScan provides users with a powerful tool for detecting metal objects and electronic devices in real-time. Whether you're concerned about safety in public spaces, airports, or simply want to locate your misplaced electronic devices, MetaScan has you covered.");
        //Glide.with(this).asGif().load(R.drawable.your_gif_file).into(gImageView);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}