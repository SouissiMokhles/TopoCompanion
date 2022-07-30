package com.example.topocompanion;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.topocompanion.Home.CHANEL_ID;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MyCustomDialog.OnInputListener{


    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View dialogView;
    String uid;
    Marker marker;
    DatabaseReference databaseReference, dbUserref;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    MissionDb missionDb;
    List<LatLng>routePoints;
    List<MissionDb> missionDbList;
    GPSTracker gpsTracker;
    Double gpsLat, gpsLng;

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    @Override
    public void sendInput(String input) {
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
        setContentView(R.layout.activity_maps);

        builder = new AlertDialog.Builder(MapsActivity.this);
        inflater = getLayoutInflater();
        builder.setView(dialogView);
        args = new Bundle();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("MissionDb");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dbUserref = database.getReference().child("Utilisateurs").child(uid);
        missionDb = new MissionDb();
        missionDbList = new ArrayList<>();
        routePoints = new ArrayList<>();
        gpsTracker = new GPSTracker(this);
        gpsLat = gpsTracker.getLatitude();
        gpsLng = gpsTracker.getLongitude();

        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (mLocationPermissionGranted) {
           getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            
            dbUserref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String uType =snapshot.child("Type").getValue().toString();
                    if (uType.equals("Administrateur"))
                    {
                        mMap.setOnMapClickListener(latLng -> {
                            final Marker mMarker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(latLng)
                                            .draggable(true)
                            );
                            double latitude = mMarker.getPosition().latitude;
                            double longitude = mMarker.getPosition().longitude;
                            String markerId = mMarker.getId();

                            mMap.setOnMarkerClickListener(marker -> {
                                Intent theMarkerIntent = new Intent(MapsActivity.this, RetrieveMission.class);
                                theMarkerIntent.putExtra("id",markerId);
                                startActivity(theMarkerIntent);
                                finish();
                                return false;
                            });
                            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                @Override
                                public void onMarkerDragStart(@NonNull Marker marker) {

                                    mMarker.remove();
                                    Toast.makeText(MapsActivity.this, "Mission supprimée", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onMarkerDrag(@NonNull Marker marker) {

                                }

                                @Override
                                public void onMarkerDragEnd(@NonNull Marker marker) {

                                }
                            });

                            args.putDouble("lat",latitude);
                            args.putDouble("lng",longitude);
                            args.putString("markerId",markerId);

                            MyCustomDialog dialog = new MyCustomDialog();
                            dialog.setArguments(args);
                            dialog.show(getSupportFragmentManager(),"MyCustomDialog");
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.getChildren() != null) {
                        double lat;
                        double lng;
                        if (snapshot.child("Latitude").exists() && snapshot.child("Longitude").exists()) {
                            lat = snapshot.child("Latitude").getValue(Double.class);
                            lng = snapshot.child("Longitude").getValue(Double.class);

                            LatLng latLng = new LatLng(lat, lng);
                            marker = mMap.addMarker(new MarkerOptions()
                                    .draggable(false)
                                    .position(latLng));
                            mMap.setOnMarkerClickListener(marker -> {
                                String id = marker.getId();
                                Intent markerIntent = new Intent(MapsActivity.this, RetrieveMission.class);
                                markerIntent.putExtra("id",id);
                                startActivity(markerIntent);
                                finish();
                                return false;
                            });
                            dbUserref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            LatLng pos = marker.getPosition();
                            Location currLoc = new Location("");

                            currLoc.setLatitude(gpsLat);
                            currLoc.setLongitude(gpsLng);

                            Location markersLoc = new Location("");
                            markersLoc.setLatitude(marker.getPosition().latitude);
                            markersLoc.setLongitude(marker.getPosition().longitude);


                            float distance = currLoc.distanceTo(markersLoc);
                            Log.d(TAG, "distance: " + distance);
                            if (distance<50) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    NotificationChannel channel = new NotificationChannel("My Notif", "My Notif", NotificationManager.IMPORTANCE_DEFAULT);
                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                    manager.createNotificationChannel(channel);
                                }
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"My Notif");
                                builder.setContentTitle("Mission");
                                builder.setContentText("Vous êtes prét d'une mission");
                                builder.setSmallIcon(R.drawable.notifcnct);
                                builder.setAutoCancel(true);
                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                                managerCompat.notify(1, builder.build());
                            }
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


    }

    private void getDeviceLocation (){
        Log.d(TAG, "getDeviceLocation: getting device current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if (mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private  void moveCamera (LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
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
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();

                }
            }
        }
    }





    public void home (View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

}