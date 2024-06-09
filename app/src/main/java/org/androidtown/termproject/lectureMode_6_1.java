package org.androidtown.termproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class lectureMode_6_1 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView userNameTextView;
    private ImageView profileImageView;
    private RecyclerView lectureRecyclerView;
    private LectureAdapter lectureAdapter;
    private List<Lecture> lectureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturemode_6_1);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userNameTextView = findViewById(R.id.User_name);
        profileImageView = findViewById(R.id.profile_image);
        lectureRecyclerView = findViewById(R.id.lecturerecyclerview);

        lectureRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lectureList = new ArrayList<>();
        lectureAdapter = new LectureAdapter(this, lectureList);
        lectureRecyclerView.setAdapter(lectureAdapter);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mypage_6.User user = dataSnapshot.getValue(mypage_6.User.class);
                    if (user != null) {
                        userNameTextView.setText(user.name);
                        if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                            Picasso.get().load(user.profileImageUrl).into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(lectureMode_6_1.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });

            loadLectures(userId);
        }

        // 기존 코드
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        Button uploadBtn = findViewById(R.id.lectureRegister);
        Button button_back = findViewById(R.id.button_back);
        Button order_check = findViewById(R.id.ordercheck);
        Button Upload_lecture = findViewById(R.id.lectureRegister);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, mypage_6.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, study_4.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, lobby_3.class));
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, lecture_upload.class));
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, mypage_6.class));
            }
        });
        order_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, OrderCheck.class));
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(lectureMode_6_1.this, lecture_upload.class));
            }
        });
    }

    private void loadLectures(String userId) {
        mDatabase.child("users").child(userId).child("lectures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lectureList.clear();
                for (DataSnapshot lectureSnapshot : dataSnapshot.getChildren()) {
                    Lecture lecture = lectureSnapshot.getValue(Lecture.class);
                    lectureList.add(lecture);
                }
                lectureAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(lectureMode_6_1.this, "Failed to load lectures", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImageRef = storageRef.child("profile_pic/" + userId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(e -> {
            Toast.makeText(lectureMode_6_1.this, "프로필 이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    // Lecture 데이터 모델 클래스
    public static class Lecture {
        private String title;
        private String instructor;
        private String thumbnail;

        public Lecture() {
            // Default constructor required for calls to DataSnapshot.getValue(Lecture.class)
        }

        public Lecture(String title, String instructor, String thumbnail) {
            this.title = title;
            this.instructor = instructor;
            this.thumbnail = thumbnail;
        }

        public String getTitle() {
            return title;
        }

        public String getInstructor() {
            return instructor;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }

    // RecyclerView 어댑터 클래스
    public static class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureViewHolder> {
        private Context context;
        private List<Lecture> lectures;

        public LectureAdapter(Context context, List<Lecture> lectures) {
            this.context = context;
            this.lectures = lectures;
        }

        @NonNull
        @Override
        public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.lecture_item, parent, false);
            return new LectureViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
            Lecture lecture = lectures.get(position);
            holder.title.setText(lecture.getTitle());
            holder.instructor.setText(lecture.getInstructor());

            if (lecture.getThumbnail() != null && !lecture.getThumbnail().isEmpty()) {
                Picasso.get().load(lecture.getThumbnail()).into(holder.thumbnail);
            } else {
                holder.thumbnail.setImageResource(R.drawable.not_yet); // 기본 이미지 설정
            }
        }

        @Override
        public int getItemCount() {
            return lectures.size();
        }

        public static class LectureViewHolder extends RecyclerView.ViewHolder {
            TextView title, instructor;
            ImageView thumbnail;

            public LectureViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title_1);
                instructor = itemView.findViewById(R.id.instructor);
                thumbnail = itemView.findViewById(R.id.thumbnail_1);
            }
        }
    }
}
