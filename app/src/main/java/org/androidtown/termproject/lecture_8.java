package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class lecture_8 extends AppCompatActivity {

    private ImageView courseImage;
    private ImageView authorImage;
    private TextView courseTitle;
    private Button categoryArt;
    private TextView courseAuthor;
    private TextView courseDescription;
    private LinearLayout contentList;
    private Button subscribeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_8);

        ImageButton button1 = findViewById(R.id.homeIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        Button review = findViewById(R.id.tab_reviews);
        subscribeButton = findViewById(R.id.subscribe_button);

        button1.setOnClickListener(v -> startActivity(new Intent(lecture_8.this, lobby_3.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(lecture_8.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(lecture_8.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(lecture_8.this, mypage_6.class)));
        review.setOnClickListener(v -> {
            Intent intent = new Intent(lecture_8.this, lecture_8_1.class);
            intent.putExtra("userId", getIntent().getStringExtra("userId"));
            intent.putExtra("lectureId", getIntent().getStringExtra("lectureId"));
            startActivity(intent);
        });

        courseImage = findViewById(R.id.course_image);
        courseTitle = findViewById(R.id.course_title);
        categoryArt = findViewById(R.id.category_art);
        courseAuthor = findViewById(R.id.course_author);
        courseDescription = findViewById(R.id.course_description);
        contentList = findViewById(R.id.content_list);
        authorImage = findViewById(R.id.author_icon);

        String userId = getIntent().getStringExtra("userId");
        String lectureId = getIntent().getStringExtra("lectureId");
        loadLectureDetails(userId, lectureId);

        subscribeButton.setOnClickListener(v -> subscribeLecture(userId, lectureId));
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
                    String description = dataSnapshot.child("contents").getValue(String.class);
                    String thumbnailUrl = dataSnapshot.child("thumbnail").getValue(String.class);
                    String category = dataSnapshot.child("category").getValue(String.class);
                    String authorId = dataSnapshot.child("userId").getValue(String.class);

                    courseTitle.setText(title);
                    courseDescription.setText(description);
                    categoryArt.setText(category);

                    if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                        Glide.with(lecture_8.this).load(thumbnailUrl).into(courseImage);
                    } else {
                        courseImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    }

                    // 작가 세부 정보를 로드할 때 프로필 이미지 URL을 가져옵니다.
                    loadAuthorDetails(authorId);
                    loadVideos(dataSnapshot.child("comments"));
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
                    String authorPic = dataSnapshot.child("profileImageUrl").getValue(String.class); // 여기에 추가

                    courseAuthor.setText("By " + authorName);

                    if (authorPic != null && !authorPic.isEmpty()) {
                        Log.d("lecture_8", "Author Pic URL: " + authorPic); // URL 로그 확인
                        Glide.with(lecture_8.this)
                                .load(authorPic)
                                .apply(RequestOptions.circleCropTransform())
                                .error(R.drawable.profile) // 오류 발생 시 기본 이미지
                                .into(authorImage);
                    } else {
                        authorImage.setImageResource(R.drawable.profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }



    private void loadVideos(DataSnapshot commentsSnapshot) {
        if (commentsSnapshot.exists()) {
            contentList.removeAllViews();
            int index = 1;
            for (DataSnapshot commentSnapshot : commentsSnapshot.getChildren()) {
                String comment = commentSnapshot.getValue(String.class);
                if (comment != null) {
                    // (Video URI) 앞까지만 추출
                    int uriIndex = comment.indexOf("(Video URI:");
                    if (uriIndex != -1) {
                        comment = comment.substring(0, uriIndex).trim();
                    }

                    TextView contentItem = new TextView(this);
                    contentItem.setText(index + ". " + comment);
                    contentItem.setBackgroundColor(getResources().getColor(R.color.content_item_background));
                    contentItem.setPadding(16, 16, 16, 16);
                    if (index > 1) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.topMargin = 8;
                        contentItem.setLayoutParams(params);
                    }
                    contentList.addView(contentItem);
                    index++;
                }
            }
        }
    }

    private void subscribeLecture(String userId, String lectureId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference subscriptionsRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid())
                    .child("subscriptions")
                    .child(lectureId);

            Map<String, Object> subscriptionData = new HashMap<>();
            subscriptionData.put("subscribed", true);
            subscriptionData.put("timestamp", System.currentTimeMillis());

            subscriptionsRef.setValue(subscriptionData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(lecture_8.this, "Subscribed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(lecture_8.this, "Subscription failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(lecture_8.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
