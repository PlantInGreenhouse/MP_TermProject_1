package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class registeration_of_items extends AppCompatActivity {

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeration_of_items);

        backBtn = findViewById(R.id.button_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(getIntent().getExtras());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ImageButton button1 = findViewById(R.id.homeIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);

        button1.setOnClickListener(v -> startActivity(new Intent(registeration_of_items.this, lobby_3.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(registeration_of_items.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(registeration_of_items.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(registeration_of_items.this, mypage_6.class)));
    }
}
