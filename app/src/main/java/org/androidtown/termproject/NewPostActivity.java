package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private Spinner categorySpinner;
    private Button submitPostButton;

    private DatabaseReference databaseReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    private String author;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writting);

        Button backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewPostActivity.this, study_4.class));
            }
        });

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentsEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitPostButton = findViewById(R.id.UploadButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("study_text");
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // 스피너 항목 배열 정의
        String[] items = {"Art", "Cooking", "Programming", "Work out", "Photos & Videos", "etc"};

        // 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // 스피너 항목 선택 이벤트 처리
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), selectedCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

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

        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (author != null) {
            String postId = databaseReference.push().getKey();
            List<String> categories = new ArrayList<>();
            categories.add(selectedCategory);
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