package com.example.mongohack;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HashtagInfoActivity extends AppCompatActivity {

    private TextView topicNameTextView, createdOnTextView, createdByTextView, topicActiveTillTextView;
    Button locationButton;
    private Double lng, lat;
    ImageButton backButton;

    StitchAppClient client;
    RemoteMongoCollection users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag_info);

        client = Stitch.getDefaultAppClient();

        final RemoteMongoClient rc = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        users = rc.getDatabase("mongohack").getCollection("users");


        getSupportActionBar().hide();

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        backButton = findViewById(R.id.back_button);

        String hashtagIdString = getIntent().getStringExtra("hashtagId");



        //topicNameTextView = findViewById(R.id.topicNameId);
        createdByTextView = findViewById(R.id.createdById);
        createdOnTextView = findViewById(R.id.createdOnId);
        topicActiveTillTextView = findViewById(R.id.topicActiveTillId);
        locationButton = findViewById(R.id.locationButton);

        RemoteMongoCollection topics = HomeFragment.topics;
        Document top = new Document("_id",new BsonObjectId(new ObjectId(hashtagIdString)));

        Log.d("USERDOC", top.toString());

        final Task<Document> t = users.find(new Document("_id",client.getAuth().getUser().getId())).first();
        t.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if(task.isSuccessful())
                {

                    Document d = task.getResult();
                    Document data = (Document) d.get("data");
//                    Log.d("USER", d.toString());
                    createdByTextView.setText(data.getString("name"));
                }
                else
                {
                    Log.d("USER", task.getException().toString());
                }
            }
        });


        final Task<Document> task = topics.find(top).first();
        task.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if(task.isSuccessful()){
                    Document d = task.getResult();
                    toolbarTitle.setText( d.getString("topic_name") );
                    //topicNameTextView.setText( d.getString("topic_name") );
//                    createdByTextView.setText( hashtagIdString );
                    Date date = d.getDate("created_at");
                    createdOnTextView.setText( DateFormat.format("dd",   date).toString() + " " + DateFormat.format("MMM",   date).toString() + ", " + DateFormat.format("yyyy",   date).toString() );

                    Date dateTill = d.getDate("active_till_date");
                    topicActiveTillTextView.setText( DateFormat.format("dd",   dateTill).toString() + " " + DateFormat.format("MMM",   dateTill).toString() + ", " + DateFormat.format("yyyy",   dateTill).toString() );

                    Document locationDocument = (Document)d.get("location");
                    ArrayList<Double> coord = (ArrayList<Double>)locationDocument.get("coordinates");
                    lng = coord.get(0);
                    lat = coord.get(1);
                }
                else{
                    Log.d("INFO","not open");
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String uriStr = String.format(Locale.ENGLISH, "geo:%f,%f", 28.4089, 77.3178);
                //String uriStr = "http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345";
                //String uriStr = "geo:" + lat.toString() + "," + lng.toString();
                String uriStr = "http://maps.google.com/maps?daddr=" + lat.toString() + "," + lng.toString();
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr));
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HashtagInfoActivity.this, HashtagActivity.class);
                intent.putExtra("hashtagId",hashtagIdString);
                startActivity(intent);
                finish();
            }
        });

    }
}
