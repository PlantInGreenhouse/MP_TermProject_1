package org.androidtown.termproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import org.androidtown.termproject.R;

import androidx.appcompat.app.AppCompatActivity;

public class shoppinglist_7 extends AppCompatActivity {
    private int itemPrice = 12000; // 아이템 가격
    private TextView itemCount;
    private TextView itemPriceView;
    private TextView totalPriceView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist_7);

        sharedPreferences = getSharedPreferences("ShoppingListPrefs", MODE_PRIVATE);

        Button button1 = findViewById(R.id.button_back);
        ImageButton button2 = findViewById(R.id.studyIcon);
        ImageButton button3 = findViewById(R.id.marketIcon);
        ImageButton button4 = findViewById(R.id.myPageIcon);
        ImageButton button5 = findViewById(R.id.homeIcon);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(shoppinglist_7.this, lobby_3.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(shoppinglist_7.this, study_4.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(shoppinglist_7.this, learninglist_5.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(shoppinglist_7.this, mypage_6.class));
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(shoppinglist_7.this, lobby_3.class));
            }
        });

        itemCount = findViewById(R.id.item_count);
        itemPriceView = findViewById(R.id.item_price);
        totalPriceView = findViewById(R.id.total_price);

        // 저장된 값 불러오기
        int savedCount = sharedPreferences.getInt("item_count", 1);
        itemCount.setText(String.valueOf(savedCount));
        updateItemPrice(savedCount);
        updateTotalPrice();

        ImageButton itemMinus = findViewById(R.id.item_minus);
        ImageButton itemPlus = findViewById(R.id.item_plus);

        itemMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(itemCount.getText().toString());
                if (count > 1) {
                    count--;
                    itemCount.setText(String.valueOf(count));
                    updateItemPrice(count);
                    updateTotalPrice();
                    saveData(count);
                }
            }
        });

        itemPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(itemCount.getText().toString());
                count++;
                itemCount.setText(String.valueOf(count));
                updateItemPrice(count);
                updateTotalPrice();
                saveData(count);
            }
        });
    }

    private void updateItemPrice(int count) {
        int totalItemPrice = count * itemPrice;
        itemPriceView.setText(String.valueOf(totalItemPrice));
    }

    private void updateTotalPrice() {
        int count = Integer.parseInt(itemCount.getText().toString());
        int totalItemPrice = count * itemPrice;
        totalPriceView.setText(String.valueOf(totalItemPrice));
    }

    private void saveData(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("item_count", count);
        editor.apply();
    }
}
