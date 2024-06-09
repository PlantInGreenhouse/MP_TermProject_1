package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class lecture_8_1 extends AppCompatActivity {

    private ImageView courseImage;
    private TextView courseTitle;
    private TextView courseAuthor;
    private LinearLayout reviewContainer;
    private ImageView authorImage;
    private Button orderingItemsButton;
    private String userId, lectureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_8_1);

        Button backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(v -> startActivity(new Intent(lecture_8_1.this, lobby_3.class)));

        ImageButton button1 = findViewById(R.id.homeIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        Button about = findViewById(R.id.tab_about);
        Button reviewTab = findViewById(R.id.tab_reviews);
        authorImage = findViewById(R.id.author_icon);
        orderingItemsButton = findViewById(R.id.ordering_items_button);

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
        reviewContainer = findViewById(R.id.review_container);


        userId = getIntent().getStringExtra("userId");
        lectureId = getIntent().getStringExtra("lectureId");
        loadLectureDetails(userId, lectureId);

        orderingItemsButton.setOnClickListener(v -> orderItems(userId, lectureId));
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
                    String thumbnailUrl = dataSnapshot.child("thumbnail").getValue(String.class);
                    String authorId = dataSnapshot.child("userId").getValue(String.class);

                    courseTitle.setText(title);

                    if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                        Glide.with(lecture_8_1.this).load(thumbnailUrl).into(courseImage);
                    } else {
                        courseImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    }

                    loadAuthorDetails(authorId);
                    loadReviews(dataSnapshot.child("reviews"));
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
                        Glide.with(lecture_8_1.this)
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

    private void loadReviews(DataSnapshot reviewsSnapshot) {
        reviewContainer.removeAllViews();

        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
            Review review = reviewSnapshot.getValue(Review.class);
            addReviewView(review);
        }
    }

    private void addReviewView(Review review) {
        View reviewView = getLayoutInflater().inflate(R.layout.review_itme, null);

        ImageView profileImage = reviewView.findViewById(R.id.profile_image);
        TextView usernameTextView = reviewView.findViewById(R.id.usernameTextView);
        TextView feedbackTextView = reviewView.findViewById(R.id.feedbackTextView);

        feedbackTextView.setText(review.text);
        usernameTextView.setText(review.username);

        if (review.profileImageUrl != null && !review.profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(review.profileImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profile);
        }

        // Add margins to the review view
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 16, 0, 16); // Adjust margins as needed
        reviewView.setLayoutParams(layoutParams);

        reviewContainer.addView(reviewView);
    }

    private void orderItems(String userId, String lectureId) {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("registeration_of_item").child(userId);
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> itemData = (Map<String, Object>) itemSnapshot.getValue();
                        addToCart(itemData);
                    }
                    Toast.makeText(lecture_8_1.this, "Items added to cart", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(lecture_8_1.this, shoppinglist_7.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(lecture_8_1.this, "No items found for this lecture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(lecture_8_1.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(Map<String, Object> itemData) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("cart")
                    .push();
            cartRef.setValue(itemData);
        }
    }

    public static class Review {
        public String username;
        public String profileImageUrl;
        public String text;
        public String userId;

        public Review() {
        }

        public Review(String username, String profileImageUrl, String text, String userId) {
            this.username = username;
            this.profileImageUrl = profileImageUrl;
            this.text = text;
            this.userId = userId;
        }
    }
}
