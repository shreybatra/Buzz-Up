package com.example.mongohack;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public class HashtagInfoActivity extends AppCompatActivity {

    TextView topicNameTextView, createdOnTextView, createdByTextView, topicActiveTillTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag_info);

        getSupportActionBar().hide();

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        String hashtagIdString = getIntent().getStringExtra("hashtagId");

        //topicNameTextView = findViewById(R.id.topicNameId);
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
                    toolbarTitle.setText( d.getString("topic_name") );
                    //topicNameTextView.setText( d.getString("topic_name") );
                    createdByTextView.setText( hashtagIdString );
                    Date date = d.getDate("created_at");
                    createdOnTextView.setText( DateFormat.format("dd",   date).toString() + " " + DateFormat.format("MMM",   date).toString() + ", " + DateFormat.format("yyyy",   date).toString() );

                    Date dateTill = d.getDate("active_till_date");
                    topicActiveTillTextView.setText( DateFormat.format("dd",   dateTill).toString() + " " + DateFormat.format("MMM",   dateTill).toString() + ", " + DateFormat.format("yyyy",   dateTill).toString() );
                }
                else{
                    Log.d("INFO","not open");
                }
            }
        });

    }
}
