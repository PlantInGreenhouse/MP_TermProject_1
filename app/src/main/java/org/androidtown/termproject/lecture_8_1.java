package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class lecture_8_1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_8_1);

        ImageButton button1 = findViewById(R.id.homeIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        Button about = findViewById(R.id.tab_about);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8_1.this, lobby_3.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8_1.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8_1.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8_1.this, mypage_6.class));
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8_1.this, lecture_8.class));
            }
        });

        ImageView courseImage = findViewById(R.id.course_image);
        TextView courseTitle = findViewById(R.id.course_title);
        TextView courseAuthor = findViewById(R.id.course_author);

        // Intent로 전달된 데이터 가져오기
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
        String category = getIntent().getStringExtra("category");
        String author = getIntent().getStringExtra("author");
        ArrayList<String> videosList = getIntent().getStringArrayListExtra("videos");

        // 데이터 설정
        courseTitle.setText(title);
        courseAuthor.setText("By " + author);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(courseImage);
        } else {
            courseImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        // 콘텐츠 리스트를 동적으로 추가하는 부분 (예: 동영상 목록)
        }
    }