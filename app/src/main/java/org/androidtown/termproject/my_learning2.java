package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class my_learning2 extends AppCompatActivity {

    private TextView courseTitle;
    private TextView courseAuthor;
    private LinearLayout videoListContainer;

    private String userId;
    private String lectureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lecture_detail);

        ImageButton homeButton = findViewById(R.id.homeIcon);
        ImageButton studyButton = findViewById(R.id.studyIcon);
        ImageButton marketButton = findViewById(R.id.marketIcon);
        ImageButton myPageButton = findViewById(R.id.myPageIcon);
        Button reviewBtn = findViewById(R.id.button_review);

        myPageButton.setOnClickListener(v -> startActivity(new Intent(my_learning2.this, mypage_6.class)));
        studyButton.setOnClickListener(v -> startActivity(new Intent(my_learning2.this, study_4.class)));
        marketButton.setOnClickListener(v -> startActivity(new Intent(my_learning2.this, learninglist_5.class)));
        homeButton.setOnClickListener(v -> startActivity(new Intent(my_learning2.this, lobby_3.class)));

        reviewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(my_learning2.this, review.class);
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });

        courseTitle = findViewById(R.id.title);
        courseAuthor = findViewById(R.id.writerName);
        videoListContainer = findViewById(R.id.video_list_container);

        userId = getIntent().getStringExtra("userId");
        lectureId = getIntent().getStringExtra("lectureId");

        loadLectureDetails(userId, lectureId);
    }

    private void loadLectureDetails(String userId, String lectureId) {
        DatabaseReference lectureRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("lectures")
                .child(lectureId);

        lectureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String authorId = dataSnapshot.child("userId").getValue(String.class);

                    courseTitle.setText(title);
                    loadAuthorDetails(authorId);
                    loadCommentsAndVideos(dataSnapshot.child("comments"), dataSnapshot.child("videos"), userId, lectureId);
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
                    courseAuthor.setText(authorName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadCommentsAndVideos(DataSnapshot commentsSnapshot, DataSnapshot videosSnapshot, String userId, String lectureId) {
        if (commentsSnapshot.exists() && videosSnapshot.exists()) {
            videoListContainer.removeAllViews();
            int index = 0;
            for (DataSnapshot commentSnapshot : commentsSnapshot.getChildren()) {
                String comment = commentSnapshot.getValue(String.class);
                String videoUrl = videosSnapshot.child("Video " + (++index)).getValue(String.class);

                if (comment != null && videoUrl != null) {
                    // (Video URI) 앞까지만 추출
                    int uriIndex = comment.indexOf("(Video URI:");
                    if (uriIndex != -1) {
                        comment = comment.substring(0, uriIndex).trim();
                    }

                    addVideoView(commentSnapshot.getKey(), comment, videoUrl, userId, lectureId);
                }
            }
        }
    }

    private void addVideoView(String videoKey, String title, String videoUrl, String userId, String lectureId) {
        View videoView = getLayoutInflater().inflate(R.layout.contents_list, null);

        TextView videoTitle = videoView.findViewById(R.id.chapter1);
        ImageButton playButton = videoView.findViewById(R.id.playIcon);

        videoTitle.setText(title);

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(my_learning2.this, my_learning3.class);
            intent.putExtra("videoKey", videoKey);
            intent.putExtra("videoUrl", videoUrl); // 동영상 URL을 인텐트에 추가
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });

        videoListContainer.addView(videoView);
    }
}
