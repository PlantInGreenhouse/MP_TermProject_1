package org.androidtown.termproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class lecture_upload extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int REGISTER_ITEMS_REQUEST = 3;
    private static final String TAG = "mypage_6";

    private FirebaseAuth mAuth;
    private Spinner categorySpinner;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private LinearLayout commentContainer;
    private String selectedCategory;
    private Uri imageUri;
    private Uri videoUri;
    private Map<String, String> videoSubtitles = new HashMap<>();
    private int videoCount = 0;

    private EditText titleEditText, contentsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_upload);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        Button backBtn = findViewById(R.id.button_back);
        Button item_registeration = findViewById(R.id.item_registeration);
        Button UploadBtn = findViewById(R.id.lectureRegister);
        ImageButton contentsPlusBtn = findViewById(R.id.contentsPlusBtn);
        ImageButton thumbnailUploadBtn = findViewById(R.id.thumbnailUploadBtn);
        commentContainer = findViewById(R.id.commentContainer);
        categorySpinner = findViewById(R.id.categorySpinner);

        titleEditText = findViewById(R.id.titleEditText);
        contentsEditText = findViewById(R.id.contentsEditText);

        String[] items = {"Art", "Cooking", "Programming", "Workout", "Photos & Videos", "etc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

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
                saveCurrentState();
                Intent intent = new Intent(lecture_upload.this, registeration_of_items.class);
                startActivityForResult(intent, REGISTER_ITEMS_REQUEST);
            }
        });

        // 업로드 버튼 설정
        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    uploadLecture();
                    startActivity(new Intent(lecture_upload.this, lectureMode_6_1.class));
                }
            }
        });

        // contentsPlusBtn 클릭 시 새로운 EditText 추가
        contentsPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEditText();
            }
        });

        // thumbnailUploadBtn 클릭 시 사진 업로드
        thumbnailUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        } else if (getIntent().getExtras() != null) {
            restoreSavedState(getIntent().getExtras());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCurrentStateToBundle(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            String subtitle = "Video " + (++videoCount);
            videoSubtitles.put(subtitle, videoUri.toString());
            addNewEditTextWithVideo(subtitle, videoUri);
        } else if (requestCode == REGISTER_ITEMS_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                restoreSavedState(data.getExtras());
            }
        }
    }

    private void saveCurrentState() {
        Bundle outState = new Bundle();
        saveCurrentStateToBundle(outState);
        Intent intent = new Intent(this, registeration_of_items.class);
        intent.putExtras(outState);
        startActivity(intent);
    }

    private void saveCurrentStateToBundle(Bundle outState) {
        outState.putString("title", titleEditText.getText().toString());
        outState.putString("contents", contentsEditText.getText().toString());
        outState.putString("selectedCategory", selectedCategory);
        outState.putInt("videoCount", videoCount);

        for (Map.Entry<String, String> entry : videoSubtitles.entrySet()) {
            outState.putString(entry.getKey(), entry.getValue());
        }
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        titleEditText.setText(savedInstanceState.getString("title"));
        contentsEditText.setText(savedInstanceState.getString("contents"));
        selectedCategory = savedInstanceState.getString("selectedCategory");

        int position = ((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(selectedCategory);
        categorySpinner.setSelection(position);

        videoCount = savedInstanceState.getInt("videoCount");
        for (int i = 0; i < videoCount; i++) {
            String subtitle = "Video " + (i + 1);
            String videoUriString = savedInstanceState.getString(subtitle);
            videoSubtitles.put(subtitle, videoUriString);
            addNewEditTextWithVideo(subtitle, Uri.parse(videoUriString));
        }
    }

    private void addNewEditText() {
        EditText newEditText = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 25); // 기존 EditText와 동일한 아래 간격

        newEditText.setLayoutParams(params);
        newEditText.setBackground(getResources().getDrawable(R.drawable.stroke));
        newEditText.setPadding(10, 10, 10, 10);
        newEditText.setHint("Please enter your comment");
        newEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blueadd, 0);
        newEditText.setCompoundDrawablePadding(10);

        newEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo(newEditText);
            }
        });

        commentContainer.addView(newEditText);
    }

    private void addNewEditTextWithVideo(String subtitle, Uri videoUri) {
        EditText newEditText = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 25); // 기존 EditText와 동일한 아래 간격

        newEditText.setLayoutParams(params);
        newEditText.setBackground(getResources().getDrawable(R.drawable.stroke));
        newEditText.setPadding(10, 10, 10, 10);
        newEditText.setText(subtitle);
        newEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blueadd, 0);
        newEditText.setCompoundDrawablePadding(10);

        newEditText.setTag(videoUri); // 비디오 URI를 태그로 저장

        commentContainer.addView(newEditText);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void chooseVideo(EditText editText) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    private boolean validateFields() {
        if (titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (contentsEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Contents are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (videoSubtitles.isEmpty()) {
            Toast.makeText(this, "At least one video is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadLecture() {
        if (imageUri != null) {
            uploadImage();
        } else {
            saveLectureData(null);
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = mStorageRef.child("thumbnails/" + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.dismiss();
                                saveLectureData(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(lecture_upload.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new com.google.firebase.storage.OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }

    private void saveLectureData(String imageUrl) {
        String title = titleEditText.getText().toString();
        String contents = contentsEditText.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        String name = mAuth.getCurrentUser().getDisplayName();
        long timestamp = System.currentTimeMillis();

        Map<String, Object> lectureData = new HashMap<>();
        lectureData.put("title", title);
        lectureData.put("thumbnail", imageUrl);
        lectureData.put("category", selectedCategory);
        lectureData.put("contents", contents);
        lectureData.put("userId", userId);
        lectureData.put("name", name);
        lectureData.put("timestamp", timestamp);
        lectureData.put("videos", videoSubtitles);

        mDatabase.child(userId).child("lectures").push().setValue(lectureData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(lecture_upload.this, "Lecture Uploaded", Toast.LENGTH_SHORT).show();
                        clearFields();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(lecture_upload.this, "Failed to upload lecture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearFields() {
        titleEditText.setText("");
        contentsEditText.setText("");
        categorySpinner.setSelection(0);
        commentContainer.removeAllViews();
        videoSubtitles.clear();
        videoCount = 0;
        imageUri = null;
        videoUri = null;
    }
}
