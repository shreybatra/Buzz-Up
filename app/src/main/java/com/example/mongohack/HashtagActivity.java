package com.example.mongohack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;
import org.bson.types.ObjectId;

public class HashtagActivity extends AppCompatActivity {

    TextView topicNameTextView, createdOnTextView, createdByTextView, topicActiveTillTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        String hashtagIdString = getIntent().getStringExtra("hashtagId");

        topicNameTextView = findViewById(R.id.topicNameId);
        createdByTextView = findViewById(R.id.createdById);
        createdOnTextView = findViewById(R.id.createdOnId);
        topicActiveTillTextView = findViewById(R.id.topicActiveTillId);

        RemoteMongoCollection topics = HomeFragment.topics;
        Document top = new Document("_id",new ObjectId(hashtagIdString));

        final Task<Document> task = topics.sync().find(top).first();
        task.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if(task.isSuccessful()){
                    Document d = task.getResult();
                    setTitle( d.getString("topic_name") );
                    topicNameTextView.setText( d.getString("topic_name") );
                    createdByTextView.setText( hashtagIdString );
                    createdOnTextView.setText( d.getDate("created_at").toString() );
                    topicActiveTillTextView.setText( d.getDate("active_till_date").toString() );
                }
                else{
                    Log.d("INFO","not open");
                }
            }
        });
    }
}
