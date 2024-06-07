package org.androidtown.termproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        ImageView courseImage = findViewById(R.id.course_image);
        TextView courseTitle = findViewById(R.id.course_title);
        Button categoryArt = findViewById(R.id.category_art);
        TextView courseAuthor = findViewById(R.id.course_author);
        TextView courseDescription = findViewById(R.id.course_description);
        LinearLayout contentList = findViewById(R.id.content_list);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
        String category = getIntent().getStringExtra("category");
        String author = getIntent().getStringExtra("author");
        ArrayList<String> videosList = getIntent().getStringArrayListExtra("videos");

        courseTitle.setText(title);
        courseDescription.setText(description);
        categoryArt.setText(category);
        courseAuthor.setText("By " + author);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(courseImage);
        } else {
            courseImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        if (videosList != null && !videosList.isEmpty()) {
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
