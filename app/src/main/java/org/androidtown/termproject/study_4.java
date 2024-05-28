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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class study_4 extends AppCompatActivity {

    private LinearLayout postsLayout;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

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
    }

    private void loadPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsLayout.removeAllViews(); // 기존 뷰 삭제
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        addPostView(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(study_4.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPostView(Post post) {
        RelativeLayout postLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.post_layout, null);

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