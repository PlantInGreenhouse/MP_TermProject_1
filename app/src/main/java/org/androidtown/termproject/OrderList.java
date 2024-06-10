package org.androidtown.termproject;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class OrderList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private LinearLayout itemContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderlist);

        itemContainer = findViewById(R.id.item_container);

        // 네비게이션 버튼 설정
        ImageButton button1 = findViewById(R.id.myPageIcon);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.homeIcon);
        button1.setOnClickListener(v -> startActivity(new Intent(OrderList.this, mypage_6.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(OrderList.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(OrderList.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(OrderList.this, lobby_3.class)));

        // 기존 아이템을 불러옵니다.
        loadLectureDetails();
    }

    private void loadLectureDetails() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot cartSnapshot = userSnapshot.child("cart");
                    for (DataSnapshot itemSnapshot : cartSnapshot.getChildren()) {
                        String title = itemSnapshot.child("title").getValue(String.class);
                        String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);
                        String priceStr = itemSnapshot.child("price").getValue(String.class);
                        double price = priceStr != null ? Double.parseDouble(priceStr) : 0.0;
                        int quantity = 1; // 기본 수량 설정

                        addLectureView(title, price, imageUrl, quantity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderList.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLectureView(String title, double price, String imageUrl, int quantity) {
        View itemView = getLayoutInflater().inflate(R.layout.item_orderlist, null);

        TextView titleTextView = itemView.findViewById(R.id.item_title);
        TextView priceTextView = itemView.findViewById(R.id.item_price);
        ImageView imageView = itemView.findViewById(R.id.item_image);
        TextView quantityTextView = itemView.findViewById(R.id.item_quantity);

        titleTextView.setText(title);
        priceTextView.setText(String.format("%.0f", price));
        quantityTextView.setText(String.valueOf(quantity));
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView);
        }

        // 새로운 아이템을 리스트의 맨 위에 추가
        itemContainer.addView(itemView, 0);
    }
}
