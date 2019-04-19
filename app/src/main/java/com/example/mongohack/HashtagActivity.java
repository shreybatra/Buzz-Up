package com.example.mongohack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HashtagActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton button_chatbox_send;
    EditText edittext_chatbox;

    StitchAppClient client;
    RemoteMongoCollection topic_chat;

    MessageListAdapter adapter;
    RecyclerView recyclerView;

    ArrayList<Document> list = new ArrayList<>();
    List<Document> dataList = new ArrayList<>();
    ObjectId topicId;

    Date lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        client = Stitch.getDefaultAppClient();
        final RemoteMongoClient rc = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        topic_chat = rc.getDatabase("mongohack").getCollection("topicChat");



        getSupportActionBar().hide();

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        backButton = findViewById(R.id.back_button);

        String hashtagIdString = getIntent().getStringExtra("hashtagId");
        topicId = new ObjectId(hashtagIdString);

        RemoteMongoCollection topics = HomeFragment.topics;
        Document top = new Document("_id",new ObjectId(hashtagIdString));

        final Task<Document> task = topics.find(top).first();
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





        lastUpdated = new Date(1);
        Toast.makeText(getApplicationContext(), "Getting chat", Toast.LENGTH_SHORT).show();


        getData();


        recyclerView = findViewById(R.id.reyclerview_message_list);
        adapter = new MessageListAdapter(dataList, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        button_chatbox_send = findViewById(R.id.button_chatbox_send);
        edittext_chatbox = findViewById(R.id.edittext_chatbox);

//        getData();

        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = edittext_chatbox.getText().toString();

                if(message.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Cannot Send Empty Message", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Document d = new Document();
                    d.append("message",message);
                    d.append("user_name", client.getAuth().getUser().getProfile().getName());
                    d.append("created_at", new Date());
                    d.append("topic_id", topicId);


                    Task t = topic_chat.insertOne(d);
                    t.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
//                                getData();
                                edittext_chatbox.setText("");
//                                dataList.add(d);
//                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Cannot Send Message", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }


            }
        });


        runLoop();

    }

    private void runLoop() {
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                getData();
                Log.d("CRON","SYNCED");
                handler.postDelayed( this, 5000 );

            }
        }, 5000 );
    }


    private void getData(){


        list.clear();

        Document filter = new Document()
                .append("topic_id", topicId)
                .append("created_at", new Document()
                        .append("$gt", lastUpdated)
                );
        Log.d("DOCFIND", filter.toString());
//        Date newDate = new Date();
//        lastUpdated = newDate;

        RemoteFindIterable t = topic_chat.find(filter);
        t.into(list).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {

//                            Log.d("LIST", String.valueOf(list.size()));
                            for(Document d : list)
                            {
                                dataList.add(d);
                                lastUpdated = d.getDate("created_at");
                                Log.d("LASTUPDATE", lastUpdated.toString());
                            }


                            adapter.notifyDataSetChanged();


                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Cannot get new messages.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


//        list.add(new Document()
//                .append("message","Hello World")
//                .append("created_at", new Date())
//                .append("user_name", "Shrey Batra")
//        );

    }
}
