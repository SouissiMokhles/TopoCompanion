package com.example.topocompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SeeData extends AppCompatActivity {
    private static final String TAG ="SeeData" ;
    RecyclerView mRecyclerView;

    Button delete;
    DatabaseReference mDatabaseRef, dbUserref;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    String item;
    String uri;
    String Loc;
    String Code;
    String uid;
    TextView seeCode;
    ImageView seeQr;
    File Qr;
    List<DataDb> mDataDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_data);

        mDataDb = new ArrayList<>();


        firebaseFirestore = FirebaseFirestore.getInstance();
        item = getIntent().getStringExtra("item");
        Loc = getIntent().getStringExtra("Localisation");
        Code = getIntent().getStringExtra("Code");
        Qr = getIntent().getParcelableExtra("Qr");
        uri=getIntent().getStringExtra("QrImage");
        delete= findViewById(R.id.deleteData);
        storageReference = FirebaseStorage.getInstance().getReference().child("Data/Qr"+Loc +".jpg");

        seeQr = findViewById(R.id.viewQr);
        seeCode = findViewById(R.id.viewCode);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DataDb");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dbUserref = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);

        mDatabaseRef.orderByChild("Localisation").equalTo(item).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String retrieveCode = snapshot.child("Code").getValue().toString();
                String retrieveQr = snapshot.child("Qr").getValue().toString();

                seeCode.setText(retrieveCode);
                Picasso.get()
                        .load(retrieveQr)
                        .into(seeQr);

                delete.setOnClickListener(v -> {
                    snapshot.getRef().removeValue();
                    Toast.makeText(SeeData.this, "Données supprimer", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SeeData.this, Data.class);
                    startActivity(intent);
                    finish();
                });
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

        dbUserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = snapshot.child("Type").getValue().toString();
                if (!uType.equals("Administrateur"))
                {
                    delete.setVisibility(View.INVISIBLE);
                    delete.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}