package org.androidtown.termproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class review extends AppCompatActivity {

    private TextView courseTitle;
    private EditText reviewInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        courseTitle = findViewById(R.id.title);
        reviewInput = findViewById(R.id.review_input);
        submitButton = findViewById(R.id.submit_button);

        String userId = getIntent().getStringExtra("userId");
        String lectureId = getIntent().getStringExtra("lectureId");

        // Load course title
        courseTitle.setText("Review for Lecture");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview(userId, lectureId);
            }
        });
    }

    private void submitReview(String userId, String lectureId) {
        String reviewText = reviewInput.getText().toString();
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please write a review before submitting", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentUserName = dataSnapshot.child("name").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    DatabaseReference lectureRef = FirebaseDatabase.getInstance().getReference("users")
                            .child(userId)
                            .child("lectures")
                            .child(lectureId)
                            .child("reviews");

                    String reviewId = lectureRef.push().getKey(); // Generate unique key for the review
                    Review review = new Review(currentUserName, profileImageUrl, reviewText, currentUserId);
                    lectureRef.child(reviewId).setValue(review).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(review.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the review activity and go back
                        } else {
                            Toast.makeText(review.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(review.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
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
