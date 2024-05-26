package org.androidtown.termproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class lectureModeRegist_6_1_1 extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private Button btnUploadVideo;
    private TextView tvUploadStatus;
    private VideoView videoView;
    private Uri videoUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturemoderegist_6_1_1);

        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        videoView = findViewById(R.id.videoView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");

        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });

        Button videoRegister = findViewById(R.id.videoRegistter);
        videoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureModeRegist_6_1_1.this, lobby_3.class));
            }
        });

        Button btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideoFromFirebase();
            }
        });
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            uploadVideo();
        }
    }

    private void uploadVideo() {
        if (videoUri != null) {
            String fileName = "videos/" + System.currentTimeMillis() + ".mp4";
            StorageReference ref = storageReference.child(fileName);
            ref.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    videoUrl = uri.toString();
                                    tvUploadStatus.setText("Upload Successful: " + videoUrl);
                                    saveVideoUrlToDatabase(videoUrl);
                                    Toast.makeText(lectureModeRegist_6_1_1.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            tvUploadStatus.setText("Upload Failed");
                            Log.e("Firebase Upload", "Error: " + e.getMessage());
                            Toast.makeText(lectureModeRegist_6_1_1.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            tvUploadStatus.setText("No file selected");
            Toast.makeText(lectureModeRegist_6_1_1.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVideoUrlToDatabase(String videoUrl) {
        String videoId = databaseReference.push().getKey();
        databaseReference.child(videoId).setValue(videoUrl);
    }

    private void playVideoFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    String videoUrl = videoSnapshot.getValue(String.class);
                    if (videoUrl != null) {
                        playVideo(videoUrl);
                        break; // 처음 발견된 동영상 URL을 재생
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(lectureModeRegist_6_1_1.this, "Failed to load video URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playVideo(String videoUrl) {
        Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}