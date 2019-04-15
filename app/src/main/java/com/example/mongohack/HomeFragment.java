package com.example.mongohack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.SyncFindIterable;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.internal.ChangeEvent;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    ListView listView;
    EditText searchEditText;
    ArrayAdapter<String> adapter;

    private StitchAppClient client;
    private RemoteMongoClient rClient;
    public static RemoteMongoCollection topics;
    private FusedLocationProviderClient fusedLocationClient;

    public double lat=0.0;
    public double lng=0.0;

    String[] hashtags = new String[]{};//=new String[]{"Hashtag1","Hashtag2","Hashtag3","Hashtag4","Hashtag5","Hashtag6","Hashtag7","Hashtag8","Hashtag9","Hashtag10","Hashtag11","Hashtag12","Hashtag13","Hashtag14","Hashtag15","Hashtag16","Hashtag17","Hashtag18","Hashtag19","Hashtag20"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

//    private class MyUpdateListener implements ChangeEventListener<Document> {
//
//        @Override
//        public void onEvent(BsonValue documentId, ChangeEvent<Document> event) {
//
//        }
//    }
//
//    private class MyErrorListener implements ErrorListener {
//        @Override
//        public void onError(BsonValue documentId, Exception error) {
//
//        }
//    }

    private String[] getTopics() {
        return new String[]{"Hashtag1","Hashtag2","Hashtag3","Hashtag4","Hashtag5","Hashtag6","Hashtag7","Hashtag8","Hashtag9","Hashtag10","Hashtag11","Hashtag12","Hashtag13","Hashtag14","Hashtag15","Hashtag16","Hashtag17","Hashtag18","Hashtag19","Hashtag20"};
//        final SyncFindIterable task = topics.sync().find();
//
//        String[] topicstr;
//
//        Document geoWithin = new Document("$match", new Document()
//                .append("location", new Document(
//                    "$geoWithin",new Document(
//                            "$centerSphere", new ArrayList<Double>(Arrays.asList(lat,lng))
//                    )
//                )
//                )
//
//        );
//
//        ArrayList<Document> d = new ArrayList<Document>();
//        d.add(new Document(
//                "$geoWithin", new Document().append(
//
//        )
//        ));
//
//        b.add(new BsonDocument().append(
//                "$match", new BsonDocument().append(
//                        "location", new BsonDocument().append(
//                                "$geoWithin", new BsonDocument().append(
//                                        "$centerSphere", new BsonArray().add(
//                                                new BsonArray().add(lat).add(lng);
//                                        )
//                                )
//                        )
//                )
//        ));
//
//        final Task <Document> findtask = topics.sync().aggregate(b).first();
//
//        findtask.addOnCompleteListener(new OnCompleteListener<Document>() {
//            @Override
//            public void onComplete(@NonNull Task<Document> task) {
//                if(task.isSuccessful())
//                {
//                    Log.d("TAG", task.getResult().toString());
//                }
//            }
//        })

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Trending Topics");

//        requestPermission();
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//
//
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )  {
//            Log.d("SUC", "Login done.");
//            return;
//        }
//
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//
//                        if (location != null) {
//                            lat =location.getLatitude();
//                            lng =location.getLongitude();
////                            locationEditText.setText(location.toString());
//                        }
//                    }
//                });
//
//        client = Stitch.getDefaultAppClient();
//        final RemoteMongoClient rc = client.getServiceClient(RemoteMongoClient.factory,"mongodb-atlas");
//        topics = rc.getDatabase("mongohack").getCollection("topics");
//        topics.sync().configure(
//                DefaultSyncConflictResolvers.localWins(),
//                new MyUpdateListener(),
//                new MyErrorListener()
//        );

        hashtags = getTopics();

        listView=view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, hashtags);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String value=adapter.getItem(position);
                Toast.makeText(getActivity(),value,Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(getActivity(),HashtagActivity.class);
                intent.putExtra("hashtagName",value);
                startActivity(intent);
            }
        });

        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                HomeFragment.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

    }
//    private void requestPermission(){
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//    }
}
