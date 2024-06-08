package org.androidtown.termproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class lobby_3 extends AppCompatActivity {

    private LinearLayout newClassContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_3);

        ImageButton button1 = findViewById(R.id.cartIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        newClassContainer = findViewById(R.id.newClassContainer);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, shoppinglist_7.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lobby_3.this, mypage_6.class));
            }
        });

        loadNewClasses();
    }

    private void loadNewClasses() {
        DatabaseReference lecturesRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = lecturesRef.orderByChild("lectures/timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newClassContainer.removeAllViews();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot lectureSnapshot : userSnapshot.child("lectures").getChildren()) {
                        Map<String, Object> lectureData = (Map<String, Object>) lectureSnapshot.getValue();

                        if (lectureData != null) {
                            String title = (String) lectureData.get("title");
                            String description = (String) lectureData.get("contents");
                            String thumbnailUrl = (String) lectureData.get("thumbnail");
                            String category = (String) lectureData.get("category");
                            String author = (String) userSnapshot.child("name").getValue(String.class);
                            ArrayList<String> videosList = new ArrayList<>();
                            for (DataSnapshot videoSnapshot : lectureSnapshot.child("videos").getChildren()) {
                                videosList.add((String) videoSnapshot.getValue());
                            }

                            if (title != null && description != null && thumbnailUrl != null) {
                                addNewClassView(title, description, thumbnailUrl, category, author, videosList);
                            } else {
                                if (title == null) System.err.println("Missing title for lecture.");
                                if (description == null) System.err.println("Missing description for lecture.");
                                if (thumbnailUrl == null) System.err.println("Missing thumbnailUrl for lecture.");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(lobby_3.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("InflateParams")
    private void addNewClassView(String title, String description, String thumbnailUrl, String category, String author, ArrayList<String> videosList) {
        View classView = getLayoutInflater().inflate(R.layout.activity_lobby, null);

        ImageView thumbnail = classView.findViewById(R.id.thumbnail);
        TextView titleView = classView.findViewById(R.id.title);
        TextView descriptionView = classView.findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(this).load(thumbnailUrl).into(thumbnail);
        } else {
            thumbnail.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        // 간격 설정
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(5, 16, 5, 16);  // 상하좌우 간격을 16dp로 설정
        classView.setLayoutParams(layoutParams);

        classView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lobby_3.this, lecture_8.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("thumbnailUrl", thumbnailUrl);
                intent.putExtra("category", category);
                intent.putExtra("author", author);
                if (videosList != null && !videosList.isEmpty()) {
                    intent.putStringArrayListExtra("videos", videosList);
                }
                startActivity(intent);
            }
        });
        newClassContainer.addView(classView, 0);
    }
}
