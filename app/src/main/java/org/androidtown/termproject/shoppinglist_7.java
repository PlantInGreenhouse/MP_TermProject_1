package org.androidtown.termproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.androidtown.termproject.R;
import androidx.appcompat.app.AppCompatActivity;

public class shoppinglist_7 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist_7);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가는 메서드 호출
                finish();
            }
        });
    }
}