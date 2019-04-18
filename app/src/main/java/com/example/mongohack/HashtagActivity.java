package com.example.mongohack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;
import org.bson.types.ObjectId;

public class HashtagActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        getSupportActionBar().hide();

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        backButton = findViewById(R.id.back_button);

        String hashtagIdString = getIntent().getStringExtra("hashtagId");

        RemoteMongoCollection topics = HomeFragment.topics;
        Document top = new Document("_id",new ObjectId(hashtagIdString));

        final Task<Document> task = topics.sync().find(top).first();
        task.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if(task.isSuccessful()){
                    Document d = task.getResult();
                    toolbarTitle.setText( d.getString("topic_name") );
                }
                else{
                    Log.d("INFO","not open");
                }
            }
        });

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HashtagActivity.this, HashtagInfoActivity.class);
                intent.putExtra("hashtagId",hashtagIdString);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HashtagActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
