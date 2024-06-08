package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import java.util.Map;

public class lobby_3 extends AppCompatActivity {

    private LinearLayout newClassContainer;
    private LinearLayout likedCategoriesContainer;
    private String userCategoryPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_3);

        EditText searchBar = findViewById(R.id.searchBar);
        ImageButton button1 = findViewById(R.id.cartIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        newClassContainer = findViewById(R.id.newClassContainer);
        likedCategoriesContainer = findViewById(R.id.likedCategoriesContainer);

        // 사용자 카테고리 선호도를 불러옵니다.
        loadUserPreferences();

        button1.setOnClickListener(v -> startActivity(new Intent(lobby_3.this, shoppinglist_7.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(lobby_3.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(lobby_3.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(lobby_3.this, mypage_6.class)));

        // Category buttons
        setupCategoryButton(R.id.button_art, "Art");
        setupCategoryButton(R.id.button_cooking, "Cooking");
        setupCategoryButton(R.id.button_programming, "Programming");
        setupCategoryButton(R.id.button_workout, "Workout");
        setupCategoryButton(R.id.button_photos_videos, "Photos & Videos");
        setupCategoryButton(R.id.button_etc, "Etc");

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchBar.getText().toString();
                Intent intent = new Intent(lobby_3.this, after_search.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }
            return false;
        });

        loadNewClasses();
    }

    private void setupCategoryButton(int buttonId, String category) {
        ImageButton button = findViewById(buttonId);  // Change FrameLayout to ImageButton
        button.setOnClickListener(v -> {
            Intent intent = new Intent(lobby_3.this, CategoryActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
    }

    private void loadUserPreferences() {
        // 여기에서 Firebase 또는 다른 소스에서 사용자의 카테고리 선호도를 불러옵니다.
        // 예시로 하드코딩된 문자열을 사용하겠습니다.
        userCategoryPreferences = "Art, Cooking, Programming";
    }

    private void loadNewClasses() {
        DatabaseReference lecturesRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = lecturesRef.orderByChild("lectures/timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newClassContainer.removeAllViews();
                likedCategoriesContainer.removeAllViews();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot lectureSnapshot : userSnapshot.child("lectures").getChildren()) {
                        Map<String, Object> lectureData = (Map<String, Object>) lectureSnapshot.getValue();

                        if (lectureData != null) {
                            String title = (String) lectureData.get("title");
                            String description = (String) lectureData.get("contents");
                            String thumbnailUrl = (String) lectureData.get("thumbnail");
                            String category = (String) lectureData.get("category");
                            String author = (String) userSnapshot.child("name").getValue(String.class);
                            String lectureId = lectureSnapshot.getKey();
                            String userId = userSnapshot.getKey();

                            if (title != null && description != null && thumbnailUrl != null) {
                                addNewClassView(userId, lectureId, title, description, thumbnailUrl, category, author);

                                // 사용자의 선호 카테고리와 일치하는 강의를 추가
                                if (matchesUserPreferences(category)) {
                                    addLikedCategoryClassView(userId, lectureId, title, description, thumbnailUrl, category, author);
                                }
                            } else {
                                if (title == null) System.err.println("Missing title for lecture.");
                                if (description == null) System.err.println("Missing description for lecture.");
                                if (thumbnailUrl == null) System.err.println("Missing thumbnailUrl for lecture.");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(lobby_3.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean matchesUserPreferences(String category) {
        if (userCategoryPreferences == null || category == null) {
            return false;
        }
        String[] preferences = userCategoryPreferences.split(",");
        for (String preference : preferences) {
            if (category.trim().equalsIgnoreCase(preference.trim())) {
                return true;
            }
        }
        return false;
    }

    private void addNewClassView(String userId, String lectureId, String title, String description, String thumbnailUrl, String category, String author) {
        View classView = getLayoutInflater().inflate(R.layout.activity_lobby, null);

        ImageView thumbnail = classView.findViewById(R.id.thumbnail);
        TextView titleView = classView.findViewById(R.id.title);
        TextView descriptionView = classView.findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(thumbnail);
        } else {
            thumbnail.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        classView.setOnClickListener(v -> {
            Intent intent = new Intent(lobby_3.this, lecture_8.class);
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });
        newClassContainer.addView(classView, 0);
    }

    private void addLikedCategoryClassView(String userId, String lectureId, String title, String description, String thumbnailUrl, String category, String author) {
        View classView = getLayoutInflater().inflate(R.layout.liked_category_item, null);

        ImageView thumbnail = classView.findViewById(R.id.thumbnail);
        TextView titleView = classView.findViewById(R.id.title);
        TextView descriptionView = classView.findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(thumbnail);
        } else {
            thumbnail.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        classView.setOnClickListener(v -> {
            Intent intent = new Intent(lobby_3.this, lecture_8.class);
            intent.putExtra("userId", userId);
            intent.putExtra("lectureId", lectureId);
            startActivity(intent);
        });
        likedCategoriesContainer.addView(classView, 0);
    }
}
