package org.androidtown.termproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class lectureModeRegist_6_1_1 extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private Button btnUploadVideo;
    private TextView tvUploadStatus;
    private Uri videoUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturemoderegist_6_1_1);

        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
                // 이전 페이지로 돌아가는 메서드 호출
                startActivity(new Intent(lectureModeRegist_6_1_1.this, lobby_3.class));
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
            StorageReference ref = storageReference.child("videos/" + System.currentTimeMillis() + ".mp4");
            ref.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            tvUploadStatus.setText("Upload Successful");
                            Toast.makeText(lectureModeRegist_6_1_1.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            tvUploadStatus.setText("Upload Failed");
                            Toast.makeText(lectureModeRegist_6_1_1.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}