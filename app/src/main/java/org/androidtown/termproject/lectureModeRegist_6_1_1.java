package org.androidtown.termproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class lectureModeRegist_6_1_1 extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturemoderegist_6_1_1);

        Button videoRegistter = findViewById(R.id.videoRegistter);
        videoRegistter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가는 메서드 호출
                startActivity(new Intent(lectureModeRegist_6_1_1.this, lobby_3.class));
            }
        });
    }
}