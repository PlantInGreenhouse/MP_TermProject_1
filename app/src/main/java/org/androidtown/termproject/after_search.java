package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

public class after_search extends AppCompatActivity {

    private LinearLayout searchResultsContainer;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_search);
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        ImageButton button5 = findViewById(R.id.cartIcon);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(after_search.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(after_search.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(after_search.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(after_search.this, lobby_3.class));
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(after_search.this, shoppinglist_7.class));
            }
        });

        searchResultsContainer = findViewById(R.id.searchResultsContainer);
        searchBar = findViewById(R.id.searchBar);

        String query = getIntent().getStringExtra("query");

        if (query != null) {
            searchBar.setText(query);
            searchLectures(query);
        }
    }

    private void searchLectures(String query) {
        DatabaseReference lecturesRef = FirebaseDatabase.getInstance().getReference("users");

        lecturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResultsContainer.removeAllViews();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot lectureSnapshot : userSnapshot.child("lectures").getChildren()) {
                        String title = lectureSnapshot.child("title").getValue(String.class);
                        String description = lectureSnapshot.child("contents").getValue(String.class);
                        String thumbnailUrl = lectureSnapshot.child("thumbnail").getValue(String.class);
                        String lectureId = lectureSnapshot.getKey();
                        String userId = userSnapshot.getKey();

                        if (title != null && description != null && thumbnailUrl != null && title.contains(query)) {
                            addSearchResultView(userId, lectureId, title, description, thumbnailUrl);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(after_search.this, "Failed to load search results.", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(after_search.this, lecture_8.class);
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });

        searchResultsContainer.addView(resultView);
    }
}
