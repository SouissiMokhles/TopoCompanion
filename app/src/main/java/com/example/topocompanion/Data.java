package com.example.topocompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Data extends AppCompatActivity {
    String Localisation,uid;
    File Qr;
    DatabaseReference databaseReference,dbUserref;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ListView listView;
    List<String> Loc = new ArrayList<>();
    ArrayAdapter <String> arrayAdapter;
    ImageView addData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("DataDb");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dbUserref = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);


        Qr = getIntent().getParcelableExtra("Qr");
        Localisation = getIntent().getStringExtra("Localisation");

        listView = findViewById(R.id.listView);
        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item,Loc);
        listView.setAdapter(arrayAdapter);
        addData = findViewById(R.id.imageView5);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item =(String) parent.getItemAtPosition(position);
            Intent intent = new Intent(getApplicationContext(), SeeData.class);
            intent.putExtra("item",item);
            startActivity(intent);
            finish();
        });

        addData.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),AddData.class));
            finish();
        });

        dbUserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = snapshot.child("Type").getValue().toString();
                if (!uType.equals("Administrateur"))
                {
                    addData.setVisibility(View.INVISIBLE);
                    addData.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void goHome (View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }



    public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}