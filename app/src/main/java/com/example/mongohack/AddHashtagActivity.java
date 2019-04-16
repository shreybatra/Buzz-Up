package com.example.mongohack;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddHashtagActivity extends AppCompatActivity {

    EditText hashtagEditText;
    Button submitButton, dateButton, locationButton;

    EditText dateEditText, locationEditText;
    private int mYear, mMonth, mDay;
    private Date tillDate;
    public double lat=0.0;
    public double lng=0.0;

    private StitchAppClient client;
    public static RemoteMongoCollection topics;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hashtag);

        hashtagEditText = findViewById(R.id.topicNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);

        dateButton = findViewById(R.id.dateButton);
        locationButton = findViewById(R.id.locationButton);
        submitButton = findViewById(R.id.submitButton);

        client= Stitch.getDefaultAppClient();
        topics = HomeFragment.topics;

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddHashtagActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateEditText.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));
                                Calendar c1 = GregorianCalendar.getInstance();
                                c1.set(year, monthOfYear, dayOfMonth+1);
                                tillDate = c1.getTime();
                                Log.d("TAG",tillDate.toString());
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }
        });

        requestPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(AddHashtagActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )  {
                    Log.d("SUC", "Login done.");
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(AddHashtagActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {
                                    locationEditText.setText(location.toString());
                                    lng = location.getLongitude();
                                    lat = location.getLatitude();
                                }
                            }
                        });
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newHashtag = hashtagEditText.getText().toString();
                Log.d("hashtag name",newHashtag);

                Document d = new Document()
                        .append("topic_name",newHashtag)
                        .append(
                                "location",new Document().append(
                                        "type","Point"
                                ).append(
                                "coordinates",
                                        new ArrayList<Double>( Arrays.asList(lng,lat) )
                                )
                        )
                        .append("created_at",new Date())
                        .append("active_till_date",tillDate)
                        .append("owner_id",client.getAuth().getUser().getId());


                Task findtask = topics.insertOne(d);

                findtask.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.d("hashtag adding TAG", task.getResult().toString());
                            Toast.makeText(AddHashtagActivity.this, "hashtag could added", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(AddHashtagActivity.this, "hashtag could not be added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }
}