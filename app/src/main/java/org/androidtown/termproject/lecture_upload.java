package org.androidtown.termproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class lecture_upload extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "mypage_6";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ImageView profileImageView;
    private TextView userNameTextView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_upload);
        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        Button backBtn = findViewById(R.id.button_back);
        Button item_registeration = findViewById(R.id.item_registeration);
        Button UploadBtn = findViewById(R.id.lectureRegister);

        // 네비게이션 버튼들 설정
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_upload.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_upload.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_upload.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_upload.this, lobby_3.class));
            }
        });

        // 뒤로가기 버튼 설정
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이템 등록 초기화 없이 돌아가기
                Intent intent = new Intent(lecture_upload.this, lectureMode_6_1.class);
                startActivity(intent);
            }
        });

        // 아이템 등록 버튼 설정
        item_registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lecture_upload.this, registeration_of_items.class));
            }
        });

        // 업로드 버튼 설정
        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lecture_upload.this, registeration_of_items.class);
                intent.putExtra("DELETE_ITEMS", true); // 아이템을 지우는 플래그 추가
                startActivity(intent);
            }
        });
    }
}
