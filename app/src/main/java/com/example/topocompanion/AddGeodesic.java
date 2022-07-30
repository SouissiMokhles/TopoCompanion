package com.example.topocompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddGeodesic extends AppCompatActivity implements OnMapReadyCallback, GeodesicDialog.OnInputListener {


    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View dialogView;
    Marker marker;
    DatabaseReference databaseReference,dbUserref;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    GeodesicDb geodesicDb;
    List<LatLng>routePoints;
    List<GeodesicDb> geodesicDbList;
    String uid;
    ImageView addGeo;


    private static final String TAG = "AddGeodesic";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    public void sendInput(String input) {
        Log.d(TAG, "sendInput: got the input: "+input);
        uploadId = input;
    }

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    String uploadId;
    Bundle args;
    private FusedLocationProviderClient mFusedLocationProviderClient;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geodesic);

        builder = new AlertDialog.Builder(AddGeodesic.this);
        inflater = getLayoutInflater();
        builder.setView(dialogView);
        args = new Bundle();
        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dbUserref = database.getReference().child("Utilisateurs").child(uid);
        databaseReference = database.getReference().child("PointsGéodésique");
        geodesicDb = new GeodesicDb();
        geodesicDbList = new ArrayList<>();
        routePoints = new ArrayList<>();
        addGeo = findViewById(R.id.toGeo);


        getLocationPermission();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        LatLng defaultlatLng = new LatLng(33.8439408,9.400138);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (mLocationPermissionGranted) {
            Log.d(TAG, "getDeviceLocation: getting device current location");

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            try{
                if (mLocationPermissionGranted){
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(defaultlatLng, 7, "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(AddGeodesic.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (SecurityException e){
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + snapshot.getKey());
                    if (snapshot.getChildren() != null) {
                        double lat;
                        double lng;
                        if (snapshot.child("Latitude").exists() && snapshot.child("Longitude").exists()) {
                            lat = snapshot.child("Latitude").getValue(Double.class);
                            lng = snapshot.child("Longitude").getValue(Double.class);

                            LatLng latLng = new LatLng(lat, lng);
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng));


                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }

        dbUserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = snapshot.child("Type").getValue().toString();
                if(uType.equals("Administrateur"))
                {
                    addGeo.setOnClickListener(v -> {
                        GeodesicDialog dialog = new GeodesicDialog();
                        dialog.show(getSupportFragmentManager(),"GeodesicDialog");
                    });
                }
                else
                {
                    addGeo.setVisibility(View.INVISIBLE);
                    addGeo.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private  void moveCamera (LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: "+latLng.latitude+", long: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideSoftKeybord();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(AddGeodesic.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permission,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permission,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();

                }
            }
        }
    }

    private void hideSoftKeybord (){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void home (View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

}