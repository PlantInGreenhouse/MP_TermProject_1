package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import org.androidtown.termproject.R;

import androidx.appcompat.app.AppCompatActivity;

public class learninglist_5 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_learning);

        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(learninglist_5.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(learninglist_5.this, study_4.class));
            }
        });


        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(learninglist_5.this, lobby_3.class));
            }
        });
    }

}