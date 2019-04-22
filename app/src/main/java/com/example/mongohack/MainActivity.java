package com.example.mongohack;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.google.GoogleCredential;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 200;
    private GoogleSignInClient g;
    SignInButton signInButton;
    public static StitchAppClient client=null;

    SharedPreferences pref;


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission1();
        requestPermission2();




        try{
            client = Stitch.getDefaultAppClient();
//            Log.d('LOGIN)
            startActivity(new Intent(getApplicationContext(),UserActivity.class));
            finish();
        }
        catch (Exception e) {
            client = Stitch.initializeDefaultAppClient(getResources().getString(R.string.initialiseClient));
            Places.initialize(getApplicationContext(), getResources().getString(R.string.googleApiKey));
        }


        signInButton = findViewById(R.id.button1);
        signInButton.setOnClickListener(this);


    }

    private void singIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getResources().getString(R.string.serverAuthCode))
                .requestEmail()
                .requestProfile()
                .build();

        g = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = g.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        singIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGooglSignInResult(task);
            return;
        }
    }

    private void handleGooglSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult();
            Log.d("TAGGGG", String.valueOf(account.getPhotoUrl()));

            final GoogleCredential googleCredential =
                    new GoogleCredential(account.getServerAuthCode());

            Stitch.getDefaultAppClient().getAuth().loginWithCredential(googleCredential).addOnCompleteListener(
                    new OnCompleteListener<StitchUser>() {
                        @Override
                        public void onComplete(@NonNull final Task<StitchUser> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(),UserActivity.class));
                                finish();
                            } else {
                            }
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "signInResult:failed code=" + e.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission1(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);

    }

    private void requestPermission2(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

    }
}