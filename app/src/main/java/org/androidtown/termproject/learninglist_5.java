package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class learninglist_5 extends AppCompatActivity {

    private LinearLayout learningListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_learning);

        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);

        button1.setOnClickListener(v -> startActivity(new Intent(learninglist_5.this, mypage_6.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(learninglist_5.this, study_4.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(learninglist_5.this, lobby_3.class)));

        learningListContainer = findViewById(R.id.learning_list_container);

        loadSubscribedLectures();
    }

    private void loadSubscribedLectures() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference subscriptionsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
                .child("subscriptions");

        subscriptionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                learningListContainer.removeAllViews();

                for (DataSnapshot subscriptionSnapshot : dataSnapshot.getChildren()) {
                    if (Boolean.TRUE.equals(subscriptionSnapshot.getValue(Boolean.class))) {
                        String lectureId = subscriptionSnapshot.getKey();
                        loadLectureDetails(lectureId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(learninglist_5.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLectureDetails(String lectureId) {
        DatabaseReference lectureRef = FirebaseDatabase.getInstance().getReference("users");

        lectureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot lecturesSnapshot = userSnapshot.child("lectures").child(lectureId);
                    if (lecturesSnapshot.exists()) {
                        String title = lecturesSnapshot.child("title").getValue(String.class);
                        String thumbnailUrl = lecturesSnapshot.child("thumbnail").getValue(String.class);
                        String userId = lecturesSnapshot.child("userId").getValue(String.class);

                        addLectureView(lectureId, title, thumbnailUrl, userId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(learninglist_5.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLectureView(String lectureId, String title, String thumbnailUrl, String userId) {
        View lectureView = getLayoutInflater().inflate(R.layout.my_learning_item, null);

        ImageView thumbnail = lectureView.findViewById(R.id.thumbnail_1);
        TextView titleView = lectureView.findViewById(R.id.title_1);
        TextView lecturerView = lectureView.findViewById(R.id.lecturer);

        if (titleView != null) {
            titleView.setText(title);
        }

        if (thumbnail != null) {
            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                Glide.with(this).load(thumbnailUrl).into(thumbnail);
            } else {
                thumbnail.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String authorName = dataSnapshot.child("name").getValue(String.class);
                    if (lecturerView != null) {
                        lecturerView.setText(authorName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(learninglist_5.this, "Failed to load lecturer info.", Toast.LENGTH_SHORT).show();
            }
        });

        lectureView.setOnClickListener(v -> {
            Intent intent = new Intent(learninglist_5.this, my_learning2.class);
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });

        learningListContainer.addView(lectureView);
    }
}
