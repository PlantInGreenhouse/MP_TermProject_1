package org.androidtown.termproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import org.androidtown.termproject.R;

import androidx.appcompat.app.AppCompatActivity;

public class mypage_6 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_6);

        ImageButton button1 = findViewById(R.id.cartIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, shoppinglist_7.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, learninglist_5.class));
            }
        });

        // 로그아웃 버튼 이건 지우면 안됨
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가는 메서드 호출
                startActivity(new Intent(mypage_6.this, MainActivity.class));
            }
        });

        Button lectureModeButton = findViewById(R.id.lectureMode);
        lectureModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, lectureMode_6_1.class));
            }
        });
    }
}