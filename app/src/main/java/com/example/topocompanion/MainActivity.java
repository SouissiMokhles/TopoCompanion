package com.example.topocompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void Map(View view){
        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
        finish();
    }


    public void data(View view){
        startActivity(new Intent(getApplicationContext(),Data.class));
        finish();
    }

    public void qrscan (View view){
        startActivity(new Intent(getApplicationContext(),QrScanner.class));
        finish();
    }

    public void geodesic (View view){
        startActivity(new Intent(getApplicationContext(),AddGeodesic.class));
        finish();
    }

    public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
