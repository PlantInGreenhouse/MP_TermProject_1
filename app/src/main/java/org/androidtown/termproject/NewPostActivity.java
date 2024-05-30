package org.androidtown.termproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private CheckBox categoryArt, categoryProgramming, categoryCooking, categoryWorkout, categoryPhotoVideos, categoryEtc;
    private Button uploadButton; 

    private DatabaseReference databaseReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    private String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writting);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentsEditText);
        categoryArt = findViewById(R.id.categoryArt);
        categoryCooking = findViewById(R.id.categoryCooking);
        categoryProgramming = findViewById(R.id.categoryProgramming);
        categoryWorkout = findViewById(R.id.categoryWorkout);
        categoryPhotoVideos = findViewById(R.id.PhotoVideos);
        categoryEtc = findViewById(R.id.categoryEtc);

        uploadButton = findViewById(R.id.uploadButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("study_text");
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the author's name
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            usersReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        author = dataSnapshot.child("name").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NewPostActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        List<String> categories = new ArrayList<>();

        if (categoryArt.isChecked()) categories.add("Art");
        if (categoryCooking.isChecked()) categories.add("Cooking");
        if (categoryProgramming.isChecked()) categories.add("Programming");
        if (categoryWorkout.isChecked()) categories.add("Work out");
        if (categoryPhotoVideos.isChecked()) categories.add("Photos & Videos");
        if (categoryEtc.isChecked()) categories.add("etc");

        if (author != null) {
            String postId = databaseReference.push().getKey();
            Post newPost = new Post(title, content, categories, author);

            if (postId != null) {
                databaseReference.child(postId).setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(NewPostActivity.this, "글이 성공적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewPostActivity.this, "글 등록 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(NewPostActivity.this, "사용자 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
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