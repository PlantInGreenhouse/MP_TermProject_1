package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class lecture_8_1 extends AppCompatActivity {

    private ImageView courseImage;
    private TextView courseTitle;
    private TextView courseAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_8_1);

        ImageButton button1 = findViewById(R.id.homeIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        Button about = findViewById(R.id.tab_about);

        button1.setOnClickListener(v -> startActivity(new Intent(lecture_8_1.this, lobby_3.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(lecture_8_1.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(lecture_8_1.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(lecture_8_1.this, mypage_6.class)));
        about.setOnClickListener(v -> {
            Intent intent = new Intent(lecture_8_1.this, lecture_8.class);
            intent.putExtra("userId", getIntent().getStringExtra("userId"));
            intent.putExtra("lectureId", getIntent().getStringExtra("lectureId"));
            startActivity(intent);
        });

        courseImage = findViewById(R.id.course_image);
        courseTitle = findViewById(R.id.course_title);
        courseAuthor = findViewById(R.id.course_author);

        String userId = getIntent().getStringExtra("userId");
        String lectureId = getIntent().getStringExtra("lectureId");
        loadLectureDetails(userId, lectureId);
    }

    private void loadLectureDetails(String userId, String lectureId) {
        DatabaseReference lectureRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("lectures").child(lectureId);

        lectureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String thumbnailUrl = dataSnapshot.child("thumbnail").getValue(String.class);
                    String authorId = dataSnapshot.child("userId").getValue(String.class);

                    courseTitle.setText(title);

                    if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                        Glide.with(lecture_8_1.this).load(thumbnailUrl).into(courseImage);
                    } else {
                        courseImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    }
                    loadAuthorDetails(authorId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadAuthorDetails(String authorId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(authorId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String authorName = dataSnapshot.child("name").getValue(String.class);
                    courseAuthor.setText("By " + authorName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
