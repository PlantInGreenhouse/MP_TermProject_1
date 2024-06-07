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

public class lecture_8 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_8);

        ImageButton button1 = findViewById(R.id.homeIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        Button review = findViewById(R.id.tab_reviews);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8.this, lobby_3.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8.this, mypage_6.class));
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_8.this, lecture_8_1.class));
            }
        });

        ImageView courseImage = findViewById(R.id.course_image);
        TextView courseTitle = findViewById(R.id.course_title);
        Button categoryArt = findViewById(R.id.category_art);
        TextView courseAuthor = findViewById(R.id.course_author);
        TextView courseDescription = findViewById(R.id.course_description);
        LinearLayout contentList = findViewById(R.id.content_list);

        // Intent로 전달된 데이터 가져오기
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
        String category = getIntent().getStringExtra("category");
        String author = getIntent().getStringExtra("author");
        ArrayList<String> videosList = getIntent().getStringArrayListExtra("videos");

        // 데이터 설정
        courseTitle.setText(title);
        courseDescription.setText(description);
        categoryArt.setText(category);
        courseAuthor.setText("By " + author);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(courseImage);
        } else {
            courseImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        // 콘텐츠 리스트를 동적으로 추가하는 부분 (예: 동영상 목록)
        if (videosList != null && !videosList.isEmpty()) {
            contentList.removeAllViews();
            for (int i = 0; i < videosList.size(); i++) {
                TextView contentItem = new TextView(this);
                contentItem.setText((i + 1) + ". " + videosList.get(i));
                contentItem.setBackgroundColor(getResources().getColor(R.color.content_item_background));
                contentItem.setPadding(16, 16, 16, 16);
                if (i > 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.topMargin = 8;
                    contentItem.setLayoutParams(params);
                }
                contentList.addView(contentItem);
            }
        }
    }
}
