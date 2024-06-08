package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CategoryActivity extends AppCompatActivity {

    private LinearLayout searchResultsContainer;
    private TextView categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        Button button5 = findViewById(R.id.button_back);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, lobby_3.class));
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, lobby_3.class));
            }
        });

        searchResultsContainer = findViewById(R.id.searchResultsContainer);
        categoryTitle = findViewById(R.id.category);

        String category = getIntent().getStringExtra("category");

        if (category != null) {
            categoryTitle.setText(category);
            searchLecturesByCategory(category);
        }

        findViewById(R.id.button_back).setOnClickListener(v -> finish());
    }

    private void searchLecturesByCategory(String category) {
        DatabaseReference lecturesRef = FirebaseDatabase.getInstance().getReference("users");

        lecturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResultsContainer.removeAllViews();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot lectureSnapshot : userSnapshot.child("lectures").getChildren()) {
                        String lectureCategory = lectureSnapshot.child("category").getValue(String.class);
                        String title = lectureSnapshot.child("title").getValue(String.class);
                        String description = lectureSnapshot.child("contents").getValue(String.class);
                        String thumbnailUrl = lectureSnapshot.child("thumbnail").getValue(String.class);
                        String lectureId = lectureSnapshot.getKey();
                        String userId = userSnapshot.getKey();

                        if (lectureCategory != null && lectureCategory.equalsIgnoreCase(category)) {
                            addSearchResultView(userId, lectureId, title, description, thumbnailUrl);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this, "Failed to load category results.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSearchResultView(String userId, String lectureId, String title, String description, String thumbnailUrl) {
        View resultView = getLayoutInflater().inflate(R.layout.search_result_item, null);

        ImageView thumbnail = resultView.findViewById(R.id.thumbnail);
        TextView titleView = resultView.findViewById(R.id.title);
        TextView descriptionView = resultView.findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(thumbnail);
        } else {
            thumbnail.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        resultView.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, lecture_8.class);
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });

        searchResultsContainer.addView(resultView);
    }
}
