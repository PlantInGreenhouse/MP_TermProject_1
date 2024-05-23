package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.androidtown.termproject.R;

import java.util.ArrayList;
import java.util.List;

public class register_1 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_1);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        CheckBox agreeCheckBox = findViewById(R.id.agreeCheckBox);
        Button registerButton = findViewById(R.id.registerButton);

        CheckBox artCheckBox = findViewById(R.id.artCheckBox);
        CheckBox cookingCheckBox = findViewById(R.id.cookingCheckBox);
        CheckBox programmingCheckBox = findViewById(R.id.programmingCheckBox);
        CheckBox exerciseCheckBox = findViewById(R.id.exerciseCheckBox);
        CheckBox photographyCheckBox = findViewById(R.id.photographyCheckBox);
        CheckBox etcCheckBox = findViewById(R.id.etcCheckBox);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (!agreeCheckBox.isChecked()) {
                    Toast.makeText(register_1.this, "Please agree to the privacy policy.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(register_1.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> selectedCategories = new ArrayList<>();
                if (artCheckBox.isChecked()) selectedCategories.add("미술");
                if (cookingCheckBox.isChecked()) selectedCategories.add("요리");
                if (programmingCheckBox.isChecked()) selectedCategories.add("프로그래밍");
                if (exerciseCheckBox.isChecked()) selectedCategories.add("운동");
                if (photographyCheckBox.isChecked()) selectedCategories.add("사진");
                if (etcCheckBox.isChecked()) selectedCategories.add("기타");

                registerUser(email, password, selectedCategories);
            }
        });
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가는 메서드 호출
                finish();
            }
        });
    }

    private void registerUser(String email, String password, List<String> categories) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user.getUid(), email, categories);
                            Toast.makeText(register_1.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // MainActivity로 돌아가기
                            Intent intent = new Intent(register_1.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 현재 Activity 종료
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(register_1.this, "This email is already registered.", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(register_1.this, "Weak password. Please provide a stronger password.", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(register_1.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(register_1.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void writeNewUser(String userId, String email, List<String> categories) {
        // Generate a unique key for each new user
        String key = mDatabase.child("users").push().getKey();
        User user = new User(userId, email, categories);
        mDatabase.child("users").child(key).setValue(user);
    }

    public static class User {
        public String userId;
        public String email;
        public List<String> categories;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userId, String email, List<String> categories) {
            this.userId = userId;
            this.email = email;
            this.categories = categories;
        }
    }
}

