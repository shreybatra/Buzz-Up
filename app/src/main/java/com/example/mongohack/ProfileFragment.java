package com.example.mongohack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.SyncFindIterable;
import com.squareup.picasso.Picasso;

import org.bson.Document;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    public static RemoteMongoCollection topics;
    String myId;

    ArrayAdapter<String> adapter;
    String[] hashtags = new String[]{"Hashtag1","Hashtag2","Hashtag3","Hashtag4","Hashtag5","Hashtag6","Hashtag7","Hashtag8",};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topics = HomeFragment.topics;

        getActivity().setTitle("Profile");

        StitchAppClient client= Stitch.getDefaultAppClient();
        String firstName = client.getAuth().getUser().getProfile().getFirstName();
        String lastName = client.getAuth().getUser().getProfile().getLastName();
        String email = client.getAuth().getUser().getProfile().getEmail();
        String photoUrl = client.getAuth().getUser().getProfile().getPictureUrl();
//        String myId = client.getAuth().getUser().getId();

        TextView nameTextView = view.findViewById(R.id.name);
        nameTextView.setText(firstName+" "+lastName);

        TextView emailTextView = view.findViewById(R.id.email);
        emailTextView.setText(email);

        ImageView profileImageView = view.findViewById(R.id.profileImage);
        Picasso.with(getContext()).load(photoUrl).into(profileImageView);
        //Log.d("photo",photoUrl+"");

        ListView listView = view.findViewById(R.id.listViewId);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, hashtags);
        listView.setAdapter(adapter);

    }

}