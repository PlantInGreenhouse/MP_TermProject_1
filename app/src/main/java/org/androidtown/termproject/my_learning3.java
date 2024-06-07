package org.androidtown.termproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class my_learning3 extends AppCompatActivity {

    private VideoView videoView;
    private TextView courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        courseTitle = findViewById(R.id.title);
        videoView = findViewById(R.id.videoView);

        ImageButton homeButton = findViewById(R.id.homeIcon);
        ImageButton studyButton = findViewById(R.id.studyIcon);
        ImageButton marketButton = findViewById(R.id.marketIcon);
        ImageButton myPageButton = findViewById(R.id.myPageIcon);
        Button backBtn = findViewById(R.id.button_back);

        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_learning3.this, mypage_6.class));
            }
        });

        studyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_learning3.this, study_4.class));
            }
        });

        marketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_learning3.this, learninglist_5.class));
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_learning3.this, lobby_3.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_learning3.this, my_learning2.class));
            }
        });

        String videoKey = getIntent().getStringExtra("videoKey");
        String userId = getIntent().getStringExtra("userId");
        String lectureId = getIntent().getStringExtra("lectureId");

        loadLectureDetails(userId, lectureId);
        loadVideo(videoKey, userId, lectureId);
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
                    courseTitle.setText(title);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadVideo(String videoKey, String userId, String lectureId) {
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("lectures")
                .child(lectureId)
                .child("videos")
                .child(videoKey);

        videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String videoUrl = dataSnapshot.getValue(String.class);
                if (videoUrl != null) {
                    Uri uri = Uri.parse(videoUrl);
                    videoView.setVideoURI(uri);

                    // 미디어 컨트롤러 추가
                    MediaController mediaController = new MediaController(my_learning3.this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);

                    videoView.start();
                } else {
                    // 로그 또는 디버깅 메시지 추가
                    Log.e("my_learning3", "Video URL is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
