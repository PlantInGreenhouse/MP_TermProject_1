package org.androidtown.termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class shoppinglist_7 extends AppCompatActivity {

    private LinearLayout shoppingListContainer;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView totalPriceTextView;
    private ImageButton button1, button2, button3, button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist_7);

        shoppingListContainer = findViewById(R.id.shopping_list_container);
        totalPriceTextView = findViewById(R.id.total_price);

        button1 = findViewById(R.id.homeIcon);
        button2 = findViewById(R.id.studyIcon);
        button3 = findViewById(R.id.marketIcon);
        button4 = findViewById(R.id.myPageIcon);

        button1.setOnClickListener(v -> startActivity(new Intent(shoppinglist_7.this, lobby_3.class)));
        button2.setOnClickListener(v -> startActivity(new Intent(shoppinglist_7.this, study_4.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(shoppinglist_7.this, learninglist_5.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(shoppinglist_7.this, mypage_6.class)));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadShoppingList();
    }

    private void loadShoppingList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = mDatabase.child("users").child(userId).child("cart");

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    shoppingListContainer.removeAllViews();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemId = snapshot.getKey();
                        String title = snapshot.child("title").getValue(String.class);
                        String price = snapshot.child("price").getValue(String.class);
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        double itemPrice = Double.parseDouble(price);

                        addItemToLayout(itemId, title, itemPrice, imageUrl);
                    }
                    updateTotalPrice();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(shoppinglist_7.this, "Failed to load shopping list", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addItemToLayout(String itemId, String title, double price, String imageUrl) {
        View itemView = getLayoutInflater().inflate(R.layout.item_shoppinglist, null);

        CheckBox itemCheckbox = itemView.findViewById(R.id.item_checkbox);
        TextView titleTextView = itemView.findViewById(R.id.item_title);
        TextView priceTextView = itemView.findViewById(R.id.item_price);
        ImageView imageView = itemView.findViewById(R.id.item_image);
        TextView quantityTextView = itemView.findViewById(R.id.item_count);
        ImageButton plusButton = itemView.findViewById(R.id.item_plus);
        ImageButton minusButton = itemView.findViewById(R.id.item_minus);
        ImageButton deleteButton = itemView.findViewById(R.id.item_title_minus);

        titleTextView.setText(title);
        priceTextView.setText(String.format("%.0f", price)); // Use two decimal places for price
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView);
        }

        itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());

        plusButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(quantityTextView.getText().toString());
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
            if (itemCheckbox.isChecked()) {
                updateTotalPrice();
            }
            updateItemPrice(priceTextView, price, quantity);
        });

        minusButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(quantityTextView.getText().toString());
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
                if (itemCheckbox.isChecked()) {
                    updateTotalPrice();
                }
                updateItemPrice(priceTextView, price, quantity);
            }
        });

        deleteButton.setOnClickListener(v -> {
            shoppingListContainer.removeView(itemView);
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference itemRef = mDatabase.child("users").child(userId).child("cart").child(itemId);
            itemRef.removeValue();
            updateTotalPrice();
        });

        shoppingListContainer.addView(itemView);
    }

    private void updateItemPrice(TextView priceTextView, double price, int quantity) {
        double totalPrice = price * quantity;
        priceTextView.setText(String.format("$%.0f", totalPrice)); // Update price with two decimal places
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (int i = 0; i < shoppingListContainer.getChildCount(); i++) {
            View itemView = shoppingListContainer.getChildAt(i);
            CheckBox itemCheckbox = itemView.findViewById(R.id.item_checkbox);
            if (itemCheckbox.isChecked()) {
                TextView priceTextView = itemView.findViewById(R.id.item_price);
                TextView quantityTextView = itemView.findViewById(R.id.item_count);
                double price = Double.parseDouble(priceTextView.getText().toString().replace("$", ""));
                int quantity = Integer.parseInt(quantityTextView.getText().toString());
                totalPrice += price;
            }
        }
        return totalPrice;
    }

    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice();
        totalPriceTextView.setText(String.format("%.0f", totalPrice)); // Update total price with two decimal places
    }
}
