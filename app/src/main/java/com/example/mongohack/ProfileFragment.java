package com.example.mongohack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Profile");

        StitchAppClient client= Stitch.getDefaultAppClient();
        String firstName = client.getAuth().getUser().getProfile().getFirstName();
        String lastName = client.getAuth().getUser().getProfile().getLastName();
        String email = client.getAuth().getUser().getProfile().getEmail();
        String photoUrl = client.getAuth().getUser().getProfile().getPictureUrl();

        TextView nameTextView = view.findViewById(R.id.name);
        nameTextView.setText(firstName+" "+lastName);

        TextView emailTextView = view.findViewById(R.id.email);
        emailTextView.setText(email);

        ImageView profileImageView = view.findViewById(R.id.profileImage);
        Picasso.with(getContext()).load(photoUrl).into(profileImageView);
    }
}