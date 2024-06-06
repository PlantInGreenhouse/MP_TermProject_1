package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class view_study_post extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView postTitleTextView, writerNameTextView, postContentTextView, postDateTextView;
    private LinearLayout commentLayout;
    private EditText editTextComment;
    private Button buttonUpload, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_study_post);

        mAuth = FirebaseAuth.getInstance();
        postTitleTextView = findViewById(R.id.post_title);
        writerNameTextView = findViewById(R.id.writerName);
        postContentTextView = findViewById(R.id.post_content);
        postDateTextView = findViewById(R.id.like_count1);
        commentLayout = findViewById(R.id.comment_layout);
        editTextComment = findViewById(R.id.editTextComment);
        buttonUpload = findViewById(R.id.buttonUpload);
        backBtn = findViewById(R.id.button_back);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("POST_ID");

        if (postId != null) {
            loadPostData(postId);
            loadComments(postId);
        } else {
            Toast.makeText(this, "Error loading post.", Toast.LENGTH_SHORT).show();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view_study_post.this, study_4.class));
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = editTextComment.getText().toString().trim();
                if (!TextUtils.isEmpty(commentText)) {
                    addComment(postId, commentText);
                } else {
                    Toast.makeText(view_study_post.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPostData(String postId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("study_text").child(postId);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String author = dataSnapshot.child("author").getValue(String.class);
                    String content = dataSnapshot.child("content").getValue(String.class);
                    String postDate = dataSnapshot.child("postDate").getValue(String.class); // Assuming postDate is stored as a string

                    postTitleTextView.setText(title);
                    writerNameTextView.setText(author);
                    postContentTextView.setText(content);
                    postDateTextView.setText(postDate);
                } else {
                    Toast.makeText(view_study_post.this, "Post not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view_study_post.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments(String postId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId);

        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentLayout.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String commentId = snapshot.getKey();
                    String authorId = snapshot.child("userId").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);

                    if (authorId != null) {
                        loadCommentAuthorName(postId, commentId, authorId, content);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view_study_post.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCommentAuthorName(String postId, String commentId, String authorId, String content) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(authorId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String authorName = dataSnapshot.child("name").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    addCommentView(postId, commentId, authorName, content, authorId, profileImageUrl);
                } else {
                    Toast.makeText(view_study_post.this, "Author not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view_study_post.this, "Failed to load author name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment(String postId, String commentText) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String userName = user.getDisplayName();

            DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId);
            String commentId = commentsRef.push().getKey();

            Map<String, Object> commentData = new HashMap<>();
            commentData.put("author", userName);
            commentData.put("content", commentText);
            commentData.put("userId", userId);

            commentsRef.child(commentId).setValue(commentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(view_study_post.this, "Comment added", Toast.LENGTH_SHORT).show();
                        editTextComment.setText(""); // Clear comment input field
                    } else {
                        Toast.makeText(view_study_post.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addCommentView(String postId, String commentId, String author, String content, String userId, String profileImageUrl) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View commentView = inflater.inflate(R.layout.comment_layout, commentLayout, false);

        ImageView writerImageView = commentView.findViewById(R.id.writer1);
        TextView writerNameTextView = commentView.findViewById(R.id.writer1Name);
        TextView commentContentTextView = commentView.findViewById(R.id.commentContent);
        Button modifyButton = commentView.findViewById(R.id.modify);
        Button deleteButton = commentView.findViewById(R.id.delete);

        writerNameTextView.setText(author);
        commentContentTextView.setText(content);

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this).load(profileImageUrl).into(writerImageView);
        } else {
            writerImageView.setImageResource(R.drawable.ic_avatar); // 기본 이미지
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(userId)) {
            modifyButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            modifyButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditCommentDialog(postId, commentId, commentContentTextView.getText().toString());
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment(postId, commentId);
            }
        });

        commentLayout.addView(commentView);
    }

    private void showEditCommentDialog(String postId, String commentId, String oldContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Comment");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.edit_comment_dialog, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = viewInflated.findViewById(R.id.editTextComment);
        input.setText(oldContent);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String newContent = input.getText().toString();
            updateComment(postId, commentId, newContent);
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateComment(String postId, String commentId, String newContent) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments").child(postId).child(commentId);
        commentRef.child("content").setValue(newContent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(view_study_post.this, "Comment updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(view_study_post.this, "Failed to update comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteComment(String postId, String commentId) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments").child(postId).child(commentId);
        commentRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(view_study_post.this, "Comment deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(view_study_post.this, "Failed to delete comment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
