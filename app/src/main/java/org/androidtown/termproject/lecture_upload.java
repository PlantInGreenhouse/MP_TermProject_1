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

        // Existing code
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
                Toast.makeText(parent.getContext(), selectedCategory + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        // Navigation buttons
        button1.setOnClickListener(v -> startActivity(new Intent(lecture_upload.this, mypage_6.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(lecture_upload.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(lecture_upload.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(lecture_upload.this, lobby_3.class)));

        // Back button
        backBtn.setOnClickListener(v -> {
            // Use finish() instead of creating a new intent
            finish();
        });

        // Item registration button
        item_registeration.setOnClickListener(v -> {
            Intent intent = new Intent(lecture_upload.this, registeration_of_items.class);
            intent.putExtras(getCurrentState());
            startActivityForResult(intent, REGISTER_ITEMS_REQUEST);
        });

        // Upload button
        UploadBtn.setOnClickListener(v -> {
            if (validateFields()) {
                uploadLecture();
            }
        });

        // Add new EditText on plus button click
        contentsPlusBtn.setOnClickListener(v -> addNewEditText());

        // Choose image on thumbnail upload button click
        thumbnailUploadBtn.setOnClickListener(v -> chooseImage());

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
            for (int i = 0; i < commentContainer.getChildCount(); i++) {
                View view = commentContainer.getChildAt(i);
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    if ("pending".equals(editText.getTag())) {
                        String subtitle = "Video " + (++videoCount);
                        videoSubtitles.put(subtitle, videoUri.toString());
                        editText.setText(subtitle);
                        editText.setTag(videoUri);
                        break;
                    }
                }
            }
        } else if (requestCode == REGISTER_ITEMS_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                restoreSavedState(data.getExtras());
            }
        }
    }

    private Bundle getCurrentState() {
        Bundle outState = new Bundle();
        saveCurrentStateToBundle(outState);
        return outState;
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
        params.setMargins(0, 0, 0, 25);

        newEditText.setLayoutParams(params);
        newEditText.setBackground(getResources().getDrawable(R.drawable.stroke));
        newEditText.setPadding(10, 10, 10, 10);
        newEditText.setHint("Please enter your comment");
        newEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blueadd, 0);
        newEditText.setCompoundDrawablePadding(10);

        newEditText.setOnClickListener(v -> chooseVideo(newEditText));

        commentContainer.addView(newEditText);
    }

    private void addNewEditTextWithVideo(String subtitle, Uri videoUri) {
        EditText newEditText = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 25);

        newEditText.setLayoutParams(params);
        newEditText.setBackground(getResources().getDrawable(R.drawable.stroke));
        newEditText.setPadding(10, 10, 10, 10);
        newEditText.setText(subtitle);
        newEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blueadd, 0);
        newEditText.setCompoundDrawablePadding(10);

        newEditText.setTag(videoUri);

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
        editText.setTag("pending");
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
            uploadAllVideosAndSaveLectureData(null);
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
                                uploadAllVideosAndSaveLectureData(uri.toString());
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

    private void uploadAllVideosAndSaveLectureData(final String imageUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Videos...");
        progressDialog.show();

        if (videoSubtitles.isEmpty()) {
            saveLectureData(imageUrl, videoSubtitles);
            return;
        }

        final Map<String, String> uploadedVideos = new HashMap<>();
        final int[] uploadCounter = {0};

        for (final Map.Entry<String, String> entry : videoSubtitles.entrySet()) {
            Uri videoUri = Uri.parse(entry.getValue());
            final String subtitle = entry.getKey();
            final StorageReference videoRef = mStorageRef.child("videos/" + UUID.randomUUID().toString());

            videoRef.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadedVideos.put(subtitle, uri.toString());
                                    uploadCounter[0]++;
                                    if (uploadCounter[0] == videoSubtitles.size()) {
                                        progressDialog.dismiss();
                                        saveLectureData(imageUrl, uploadedVideos);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(lecture_upload.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveLectureData(String imageUrl, Map<String, String> uploadedVideos) {
        String title = titleEditText.getText().toString();
        String contents = contentsEditText.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        String name = mAuth.getCurrentUser().getDisplayName();
        long timestamp = System.currentTimeMillis();

        // EditText의 내용들과 동영상 URI들을 저장
        Map<String, String> comments = new HashMap<>();
        for (int i = 0; i < commentContainer.getChildCount(); i++) {
            View view = commentContainer.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                String comment = editText.getText().toString();
                String videoUri = editText.getTag() != null ? editText.getTag().toString() : null;
                if (videoUri != null) {
                    comments.put("comment_" + i, comment + " (Video URI: " + videoUri + ")");
                } else {
                    comments.put("comment_" + i, comment);
                }
            }
        }

        Map<String, Object> lectureData = new HashMap<>();
        lectureData.put("title", title);
        lectureData.put("thumbnail", imageUrl);
        lectureData.put("category", selectedCategory);
        lectureData.put("contents", contents);
        lectureData.put("userId", userId);
        lectureData.put("name", name);
        lectureData.put("timestamp", timestamp);
        lectureData.put("videos", uploadedVideos);
        lectureData.put("comments", comments);

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
