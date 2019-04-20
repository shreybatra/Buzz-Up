package com.example.mongohack;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditHashtagActivity extends AppCompatActivity {

    EditText hashtagEditText;
    Button submitButton, dateTillButton, locationButton;

    TextView dateTillEditText, locationEditText;
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
        setContentView(R.layout.activity_edit_hashtag);

        setTitle("Edit Topic Info");

        hashtagEditText = findViewById(R.id.topicNameEditText);
        dateTillEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);

        dateTillButton = findViewById(R.id.dateButton);
        locationButton = findViewById(R.id.locationButton);
        submitButton = findViewById(R.id.submitButton);

        client= Stitch.getDefaultAppClient();
        topics = HomeFragment.topics;

        String hashtagIdString = getIntent().getStringExtra("hashtagId");
        Document top = new Document("_id",new BsonObjectId(new ObjectId(hashtagIdString)));

        final Task<Document> task = topics.find(top).first();
        task.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if(task.isSuccessful()){
                    Document d = task.getResult();
                    hashtagEditText.setText( d.getString("topic_name") );

                    Date dateTill = d.getDate("active_till_date");
                    //dateTillEditText.setText( DateFormat.format("dd",   dateTill).toString() + " " + DateFormat.format("MMM",   dateTill).toString() + ", " + DateFormat.format("yyyy",   dateTill).toString() );
                    dateTillEditText.setText( DateFormat.format("dd MMM, yyyy",   dateTill).toString() );
                    Document locationDocument = (Document)d.get("location");
                    ArrayList<Double> coord = (ArrayList<Double>)locationDocument.get("coordinates");
                    lng = coord.get(0);
                    lat = coord.get(1);
                    locationEditText.setText("Lat-" + lat + " Lng-"+lng);
                }
                else{
                    Log.d("INFO","not open");
                }
            }
        });

        dateTillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditHashtagActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateTillEditText.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));
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

                if (ActivityCompat.checkSelfPermission(EditHashtagActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )  {
                    Log.d("SUC", "Login done.");
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(EditHashtagActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {

                                    lng = location.getLongitude();
                                    lat = location.getLatitude();
                                    locationEditText.setText("Lat-" + lat + " Lng-"+lng);
                                }
                            }
                        });
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }

}
