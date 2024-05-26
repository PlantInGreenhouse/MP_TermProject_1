package org.androidtown.termproject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import org.androidtown.termproject.R;

import androidx.appcompat.app.AppCompatActivity;
public class lectureMode_6_1 extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturemode_6_1);

        Button lectureRegister = findViewById(R.id.lectureRegister);
        lectureRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, lectureModeRegist_6_1_1.class));
            }
        });
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼을 누르면 mypage_6 클래스로 이동
                startActivity(new Intent(lectureMode_6_1.this, mypage_6.class));
            }
        });
    }
}