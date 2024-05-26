package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class category extends AppCompatActivity {

    private CheckBox cbArt, cbCooking, cbProgramming, cbWorkout, cbPhotosVideos, cbEtc;
    private DatabaseReference mDatabase;
    private String userId, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        // Firebase Database 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get userId and email passed from register_1 activity
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        email = intent.getStringExtra("email");

        // Find views
        cbArt = findViewById(R.id.cb_art);
        cbCooking = findViewById(R.id.cb_cooking);
        cbProgramming = findViewById(R.id.cb_programming);
        cbWorkout = findViewById(R.id.cb_workout);
        cbPhotosVideos = findViewById(R.id.cb_photos_videos);
        cbEtc = findViewById(R.id.cb_etc);
        Button signUpButton = findViewById(R.id.SignUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoriesAndProceed();
            }
        });
    }

    private void saveCategoriesAndProceed() {
        Map<String, Boolean> categories = new HashMap<>();
        categories.put("Art", cbArt.isChecked());
        categories.put("Cooking", cbCooking.isChecked());
        categories.put("Programming", cbProgramming.isChecked());
        categories.put("Workout", cbWorkout.isChecked());
        categories.put("Photos & Videos", cbPhotosVideos.isChecked());
        categories.put("Etc", cbEtc.isChecked());

        // Convert the categories map to a string format for passing via Intent
        StringBuilder categoriesString = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : categories.entrySet()) {
            if (entry.getValue()) {
                if (categoriesString.length() > 0) {
                    categoriesString.append(", ");
                }
                categoriesString.append(entry.getKey());
            }
        }

        // Save categories to database
        mDatabase.child("users").child(userId).child("category").setValue(categoriesString.toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(category.this, "Categories saved successfully", Toast.LENGTH_SHORT).show();
                        // MainActivity로 이동
                        Intent intent = new Intent(category.this, MainActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish(); // 현재 Activity 종료
                    } else {
                        Toast.makeText(category.this, "Failed to save categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
