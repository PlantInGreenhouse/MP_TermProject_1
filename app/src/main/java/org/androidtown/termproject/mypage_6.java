package org.androidtown.termproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class mypage_6 extends AppCompatActivity {
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
        setContentView(R.layout.mypage_6);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pic");
        profileImageView = findViewById(R.id.profile_image);
        userNameTextView = findViewById(R.id.User_name);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userNameTextView.setText(user.name);
                        if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                            Picasso.get().load(user.profileImageUrl).into(profileImageView);
                        }
                    } else {
                        Log.e(TAG, "User data is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e(TAG, "User is not authenticated");
        }

        ImageButton editIcon = findViewById(R.id.edit_icon);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        ImageButton MyInformation_button = findViewById(R.id.MyInformation_button);
        ImageButton LectureMode_button = findViewById(R.id.LectureMode_button);
        LinearLayout MyInformation = findViewById(R.id.MyInformation);
        LinearLayout LectureMode = findViewById(R.id.LectureMode);
        LinearLayout QA = findViewById(R.id.QA);
        ImageButton QAI = findViewById(R.id.QA_button);
        LinearLayout OrderList = findViewById(R.id.OderList);
        ImageButton OrderBtn = findViewById(R.id.OderList_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mypage_6.this, lobby_3.class));
            }
        });

        MyInformation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, my_information.class));

            }

        });
        MyInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, my_information.class));

            }

        });
        LectureMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, lectureMode_6_1.class));

            }

        });
        LectureMode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, lectureMode_6_1.class));

            }

        });
        QA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, qna.class));

            }

        });
        QAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, qna.class));

            }

        });
        OrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, OrderList.class));

            }

        });
        OrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 아이콘 클릭 시 수행할 작업
                startActivity(new Intent(mypage_6.this, OrderList.class));

            }

        });

        // 로그아웃 버튼 이건 지우면 안됨
        ImageButton logoutButton = findViewById(R.id.LogOut_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가는 메서드 호출
                startActivity(new Intent(mypage_6.this, MainActivity.class));
            }
        });
        LinearLayout LogOut = findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 돌아가는 메서드 호출
                startActivity(new Intent(mypage_6.this, MainActivity.class));
            }
        });
        // 나머지 버튼 설정 코드 ...

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                Log.e(TAG, "Error getting image: " + e.getMessage());
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference fileReference = mStorageRef.child(userId + ".jpg");

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        mDatabase.child("users").child(userId).child("profileImageUrl").setValue(downloadUrl);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Image upload failed: " + e.getMessage());
                            }
                        });
            } else {
                Log.e(TAG, "User is not authenticated");
            }
        } else {
            Log.e(TAG, "Image URI is null");
        }
    }

    public static class User {
        public String name;
        public String email;
        public String profileImageUrl;

        public User() {}

        public User(String name, String email, String profileImageUrl) {
            this.name = name;
            this.email = email;
            this.profileImageUrl = profileImageUrl;
        }
    }
}