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

public class register_1 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_1);

        // FirebaseAuth 및 DatabaseReference 초기화
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // UI 요소 초기화
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        CheckBox agreeCheckBox = findViewById(R.id.agreeCheckBox);
        Button registerButton = findViewById(R.id.nextButton);
        Button backButton = findViewById(R.id.backButton);

        // 회원가입 버튼 클릭 리스너 설정
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 값 가져오기
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // 약관 동의 체크 확인
                if (!agreeCheckBox.isChecked()) {
                    Toast.makeText(register_1.this, "Please agree to the privacy policy.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 일치 여부 확인
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(register_1.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 사용자 등록
                registerUser(email, password);
            }
        });

        // 뒤로 가기 버튼 클릭 리스너 설정
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가기
                finish();
            }
        });
    }

    // 사용자 등록 메서드
    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            writeNewUser(userId, email);
                            Toast.makeText(register_1.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // category로 이동
                            Intent intent = new Intent(register_1.this, category.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish(); // 현재 Activity 종료
                        } else {
                            // 회원가입 실패
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

    // 새로운 사용자 데이터를 데이터베이스에 저장하는 메서드
    private void writeNewUser(String userId, String email) {
        User user = new User(userId, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    // 사용자 클래스 정의
    public static class User {
        public String userId;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userId, String email) {
            this.userId = userId;
            this.email = email;
        }
    }
}
