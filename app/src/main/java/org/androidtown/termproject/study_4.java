package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class study_4 extends AppCompatActivity {

    private LinearLayout postsLayout;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private List<Post> allPosts = new ArrayList<>();
    private Button selectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_4);

        postsLayout = findViewById(R.id.postsLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference("study_text");
        firebaseAuth = FirebaseAuth.getInstance();

        loadPosts();

        Button button1 = findViewById(R.id.button_writing);
        ImageButton button2 = findViewById(R.id.homeIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(study_4.this, NewPostActivity.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(study_4.this, lobby_3.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(study_4.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(study_4.this, mypage_6.class));
            }
        });

        // 카테고리 버튼 설정
        final Button allButton = findViewById(R.id.category_all);
        final Button artButton = findViewById(R.id.category_art);
        final Button cookingButton = findViewById(R.id.category_cooking);
        final Button programmingButton = findViewById(R.id.category_programming);
        final Button workoutButton = findViewById(R.id.category_workout);
        final Button photosVideosButton = findViewById(R.id.category_photos_videos);
        final Button etcButton = findViewById(R.id.category_etc);

        selectedButton = allButton; // 초기 선택된 버튼

        // All 버튼을 기본 선택 상태로 설정
        allButton.setBackgroundResource(R.drawable.selected_button);
        allButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        View.OnClickListener categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButton != null) {
                    selectedButton.setBackgroundResource(R.drawable.unselected_button);
                    selectedButton.setTextColor(ContextCompat.getColor(study_4.this, R.color.unselected_text_color));
                }
                selectedButton = (Button) v;
                selectedButton.setBackgroundResource(R.drawable.selected_button);
                selectedButton.setTextColor(ContextCompat.getColor(study_4.this, android.R.color.white));
                filterPostsByCategory(selectedButton.getText().toString());
            }
        };

        allButton.setOnClickListener(categoryClickListener);
        artButton.setOnClickListener(categoryClickListener);
        cookingButton.setOnClickListener(categoryClickListener);
        programmingButton.setOnClickListener(categoryClickListener);
        workoutButton.setOnClickListener(categoryClickListener);
        photosVideosButton.setOnClickListener(categoryClickListener);
        etcButton.setOnClickListener(categoryClickListener);
    }

    private void loadPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allPosts.clear();
                postsLayout.removeAllViews(); // 기존 뷰 삭제
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        allPosts.add(post);
                    }
                }
                filterPostsByCategory(selectedButton.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(study_4.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPostsByCategory(String category) {
        postsLayout.removeAllViews();
        for (Post post : allPosts) {
            if (category.equals("All") || post.category.contains(category)) {
                addPostView(post);
            }
        }
    }

    private void addPostView(Post post) {
        View postLayout = LayoutInflater.from(this).inflate(R.layout.post_layout, postsLayout, false);

        TextView categoryTextView = postLayout.findViewById(R.id.postCategoryTextView);
        TextView titleTextView = postLayout.findViewById(R.id.postTitleTextView);
        TextView authorTextView = postLayout.findViewById(R.id.postAuthorTextView);

        StringBuilder categoriesBuilder = new StringBuilder();
        for (String category : post.category) {
            categoriesBuilder.append(category).append(" ");
        }

        categoryTextView.setText(categoriesBuilder.toString().trim());
        titleTextView.setText(post.title);
        authorTextView.setText(post.author);

        postsLayout.addView(postLayout);
    }

    private static class Post {
        public String title, content, author;
        public List<String> category;

        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public Post(String title, String content, List<String> category, String author) {
            this.title = title;
            this.content = content;
            this.category = category;
            this.author = author;
        }
    }
}
