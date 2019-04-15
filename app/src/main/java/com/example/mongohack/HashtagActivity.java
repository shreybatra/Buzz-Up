package com.example.mongohack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class HashtagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        String hashtagName = getIntent().getStringExtra("hashtagName");
        setTitle(hashtagName);
    }
}
