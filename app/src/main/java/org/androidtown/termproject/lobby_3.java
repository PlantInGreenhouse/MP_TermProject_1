package org.androidtown.termproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import org.androidtown.termproject.R;

import androidx.appcompat.app.AppCompatActivity;

public class lobby_3 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_3);
        ImageButton button1 = findViewById(R.id.cartIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, shoppinglist_7.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, mypage_6.class));
            }
        });

    }
}
