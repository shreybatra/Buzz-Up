package com.example.mongohack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.google.GoogleCredential;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 200;
    private GoogleSignInClient g;
    SignInButton signInButton;
    public static StitchAppClient client=null;

//    private SharedPreferences sharedPref;
//    private SharedPreferences.Editor editor;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        File f = new File(
//                "/data/data/your_application_package/shared_prefs/LoginFile.xml");
//        if (f.exists())
        //sharedPref = getSharedPreferences("LoginFile",getApplicationContext().MODE_PRIVATE);
        //editor = sharedPref.edit();

        signInButton = findViewById(R.id.button1);
        signInButton.setOnClickListener(this);

        client = Stitch.initializeDefaultAppClient(getResources().getString(R.string.initialiseClient));

//        String ifGCSaved = sharedPref.getString( getString(R.string.savedGCCode) ,null);
//        if(ifGCSaved!=null){
//            final GoogleCredential googleCredential = new GoogleCredential(ifGCSaved);
//            Log.d("shared prf got", "got");
//            loginStitchClient(googleCredential);
//
//        }

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
            //Log.d("first time","first time made");

//            editor.putString( getString(R.string.savedGCCode), account.getServerAuthCode() );
//            editor.commit();

            //loginStitchClient(googleCredential);
            Stitch.getDefaultAppClient().getAuth().loginWithCredential(googleCredential).addOnCompleteListener(
                    new OnCompleteListener<StitchUser>() {
                        @Override
                        public void onComplete(@NonNull final Task<StitchUser> task) {
                            if (task.isSuccessful()) {
                                //Log.d("SUC", "Login done.");
                                startActivity(new Intent(getApplicationContext(),UserActivity.class));
                                //finish();
                            } else {
                                //Log.d(TAG, "Error logging in with Google", task.getException());
                                //Toast.makeText(MainActivity.this, "error logging with google", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } catch (Exception e) {
            //Log.w(TAG, "signInResult:failed code=" + e.toString());
            Toast.makeText(MainActivity.this, "signInResult:failed code=" + e.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    private void loginStitchClient(GoogleCredential googleCredential){
        //Log.d("SUC", "entered function");

    }
}