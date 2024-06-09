package org.androidtown.termproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class registeration_of_items extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "registeration_of_items";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private Uri imageUri;

    private EditText titleInput;
    private EditText priceInput;
    private ImageButton uploadImageButton;
    private Button uploadButton;
    private LinearLayout itemContainer;
    private Button backBtn;
    private String lectureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeration_of_items);

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // 강의 ID 가져오기
        lectureId = getIntent().getStringExtra("lectureId");

        // Plus 버튼 클릭 리스너 추가
        ImageView plusBtn = findViewById(R.id.plusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        itemContainer = findViewById(R.id.item_container);

        // 인텐트로부터 삭제 플래그 확인
        Intent intent = getIntent();
        boolean shouldDeleteItems = intent.getBooleanExtra("DELETE_ITEMS", false);
        if (shouldDeleteItems) {
            itemContainer.removeAllViews();
        } else {
            loadItemsFromDatabase();
        }

        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        backBtn = findViewById(R.id.button_back);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registeration_of_items.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registeration_of_items.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registeration_of_items.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registeration_of_items.this, lobby_3.class));
            }
        });

        // Handle back button click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(getIntent().getExtras());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.window_registeration_of_items, null);
        builder.setView(dialogView);

        titleInput = dialogView.findViewById(R.id.title_input);
        priceInput = dialogView.findViewById(R.id.price_input);
        uploadImageButton = dialogView.findViewById(R.id.upload_image);
        uploadButton = dialogView.findViewById(R.id.upload_button);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        AlertDialog alertDialog = builder.create();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!titleInput.getText().toString().isEmpty() && !priceInput.getText().toString().isEmpty()) {
                    uploadItem();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(registeration_of_items.this, "Title and Price cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageButton.setImageURI(imageUri);
        }
    }

    private void uploadItem() {
        if (imageUri != null) {
            StorageReference fileReference = mStorageRef.child("uploads/" + System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        saveItemToDatabase(downloadUri.toString());
                                    } else {
                                        Toast.makeText(registeration_of_items.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(registeration_of_items.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            saveItemToDatabase(null);
        }
    }

    private void saveItemToDatabase(String imageUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userRef.child("name").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userName = task.getResult().getValue(String.class);
                    DatabaseReference itemRef = mDatabase.child("registeration_of_item").child(userId).push();

                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("title", titleInput.getText().toString());
                    itemData.put("price", priceInput.getText().toString());
                    itemData.put("userName", userName);
                    itemData.put("lectureId", lectureId); // 강의 ID 추가
                    if (imageUrl != null) {
                        itemData.put("imageUrl", imageUrl);
                    }

                    itemRef.setValue(itemData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(registeration_of_items.this, "Item uploaded successfully", Toast.LENGTH_SHORT).show();
                                addItemToLayout(itemRef.getKey(), titleInput.getText().toString(), priceInput.getText().toString(), imageUrl);
                            } else {
                                Toast.makeText(registeration_of_items.this, "Failed to upload item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(registeration_of_items.this, "Failed to get user name", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addItemToLayout(String itemId, String title, String price, String imageUrl) {
        // 레이아웃에 새 아이템 추가하는 코드
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemLayout = inflater.inflate(R.layout.item_layout, itemContainer, false);

        ImageView itemImage = itemLayout.findViewById(R.id.item_image);
        TextView itemTitle = itemLayout.findViewById(R.id.item_title);
        TextView itemPrice = itemLayout.findViewById(R.id.item_price);
        Button deleteButton = itemLayout.findViewById(R.id.delete);

        itemTitle.setText(title);
        itemPrice.setText(price);
        if (imageUrl != null) {
            // Firebase Storage에 저장된 이미지를 로드
            Glide.with(this).load(imageUrl).into(itemImage);
        } else {
            itemImage.setImageResource(R.drawable.ic_fileupload); // 기본 이미지
        }

        // 삭제 버튼 클릭 리스너 설정
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemContainer.removeView(itemLayout);
                // Firebase에서 항목 삭제
                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference itemRef = mDatabase.child("registeration_of_item").child(userId).child(itemId);
                itemRef.removeValue();
            }
        });

        // 새 아이템을 맨 위에 추가
        itemContainer.addView(itemLayout, 0);
    }

    private void loadItemsFromDatabase() {
        itemContainer.removeAllViews();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference itemRef = mDatabase.child("registeration_of_item").child(userId);

            itemRef.orderByChild("lectureId").equalTo(lectureId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    itemContainer.removeAllViews();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemId = snapshot.getKey();
                        String title = snapshot.child("title").getValue(String.class);
                        String price = snapshot.child("price").getValue(String.class);
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        addItemToLayout(itemId, title, price, imageUrl);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(registeration_of_items.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
