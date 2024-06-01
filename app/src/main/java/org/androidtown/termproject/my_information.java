package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class my_information extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button updateButton;
    private FirebaseUser user;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText currentPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_information);

        // Navigation buttons
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        Button backBtn = findViewById(R.id.button_back);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_information.this, lobby_3.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_information.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_information.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_information.this, mypage_6.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(my_information.this, mypage_6.class));
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        currentPasswordEditText = findViewById(R.id.currentPassword);
        updateButton = findViewById(R.id.update);

        // Fetch and set user information
        fetchUserInfo();

        // Set update button listener
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reauthenticateAndUpdateUser();
            }
        });
    }

    private void fetchUserInfo() {
        DatabaseReference userRef = mDatabase.child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    if (name != null) {
                        nameEditText.setText(name);
                    }

                    if (email != null) {
                        emailEditText.setText(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(my_information.this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reauthenticateAndUpdateUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String currentPassword = currentPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || currentPassword.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user's current email and password for reauthentication
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Reauthentication successful
                        updateEmailAndPassword(email, password, name);
                    } else {
                        Toast.makeText(my_information.this, "재인증에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmailAndPassword(String email, String password, String name) {
        user.updateEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(password)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Update user information in the database
                                        DatabaseReference userRef = mDatabase.child(user.getUid());
                                        userRef.child("name").setValue(name);
                                        userRef.child("email").setValue(email);

                                        Toast.makeText(my_information.this, "사용자 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(my_information.this, mypage_6.class));
                                    } else {
                                        Toast.makeText(my_information.this, "비밀번호 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(my_information.this, "이메일 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
