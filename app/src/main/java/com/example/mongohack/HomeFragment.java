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
import com.mongodb.client.MongoCollection;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.SyncFindIterable;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.internal.ChangeEvent;

import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class HomeFragment extends Fragment {

    ListView listView;
    EditText searchEditText;
    ArrayAdapter<String> adapter;

    private StitchAppClient client;
    public static RemoteMongoCollection topics;
    private FusedLocationProviderClient fusedLocationClient;
    MongoCollection<Document> localCollection;


    public ArrayList<String> hashtagsList = new ArrayList<>();
    List<BsonObjectId> syncids = new ArrayList<>();


    public double lat=0.0;
    public double lng=0.0;


    String[] hashtags = new String[]{};
    ArrayList<ObjectId> hashIds = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Trending Topics");


        requestPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )  {
            Log.d("SUC", "Login done.");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            lat =location.getLatitude();
                            lng =location.getLongitude();
//                            locationEditText.setText(location.toString());
                        }
                    }
                });


        client = Stitch.getDefaultAppClient();
        final RemoteMongoClient rc = client.getServiceClient(RemoteMongoClient.factory,"mongodb-atlas");
        topics = rc.getDatabase("mongohack").getCollection("topics");

        topics.sync().configure(
                DefaultSyncConflictResolvers.localWins(),
                new MyUpdateListener(),
                new MyErrorListener()
        );

        listView = view.findViewById(R.id.listView);

        hashtags = new String[]{"Loading topics....!"};

        setView();


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

    private class MyErrorListener implements ErrorListener {
        @Override
        public void onError(BsonValue documentId, Exception error) {
            Log.e("Stitch", error.getLocalizedMessage());
            Set<BsonValue> docsThatNeedToBeFixed = topics.sync().getPausedDocumentIds();
            for (BsonValue doc_id : docsThatNeedToBeFixed) {
                // Add your logic to inform the user.
                // When errors have been resolved, call
                topics.sync().resumeSyncForDocument(doc_id);
            }
            // refresh the app view, etc.
        }
    }

    private class MyUpdateListener implements ChangeEventListener<Document> {
        @Override
        public void onEvent(final BsonValue documentId, final ChangeEvent<Document> event) {
            if (!event.hasUncommittedWrites()) {
                Log.d("SYNC", documentId.toString() + " synced from local");
            }
            else
            {
                Log.d("SYNC", documentId.toString() + " synced from atlas");
            }
            // refresh the app view, etc.
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getTopics();
        getGeoTopics();
    }

    public void getTopics() {

        Document filter = new Document()
                .append("lat", lat)
                .append("lng", lng)
                .append("radius", 1);
        syncids.clear();

        client.callFunction("getTopicIds", asList(filter.toJson()), ArrayList.class)
            .addOnCompleteListener(new OnCompleteListener<ArrayList>() {
                @Override
                public void onComplete(@NonNull Task<ArrayList> task) {
                    if (task.isSuccessful()) {
                        List<Document> items = task.getResult();

                        for (Document doc : items) {

                            syncids.add(new BsonObjectId(doc.getObjectId("_id")));

                        }
                        topics.sync().syncMany(syncids.toArray(new BsonObjectId[0]));

                    } else
                        Log.e("IDSS", task.getException().toString());

                    Set<BsonObjectId> t = topics.sync().getSyncedIds();

                    ArrayList<BsonValue> desyncs = new ArrayList<>();

                    for(BsonObjectId b : t)
                    {
                        if(!syncids.contains(b))
                            desyncs.add(b);
                    }

                    Log.d("DESYNC", syncids.toString());
                    Log.d("DESYNCED", desyncs.toString());
                    topics.sync().desyncMany(desyncs.toArray(new BsonObjectId[0]));

                    getGeoTopics();

                }
            });

    }

    private void getGeoTopics() {

        SyncFindIterable b;
        if(false)
        {
            Document d = new Document("_id", new Document(
                    "$in", syncids
            ));
            Log.d("DOCC", syncids.toString());
            b = topics.sync().find(d);
        }
        else
        {
            b = topics.sync().find();
        }


        final ArrayList<Document> docs = new ArrayList<>();
        b.into(docs).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    hashtagsList.clear();
                    hashIds.clear();
                    for(Document d : docs)
                    {
                        hashtagsList.add("#" + d.getString("topic_name"));
                        hashIds.add(d.getObjectId("_id"));
                    }

                }
                if(hashtags.length > 0)
                    hashtags = hashtagsList.toArray(new String[0]);
                else
                    hashtags = new String[]{"No topics Found."};
                setView();
            }
        });

    }

    private void setView()
    {
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

//        getTopics();
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }
}
