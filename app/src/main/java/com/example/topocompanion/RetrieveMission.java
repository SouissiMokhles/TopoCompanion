package com.example.topocompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class RetrieveMission extends AppCompatActivity {
    TextView codeMission, nomClient, gouvernerat, superficie, date;
    String id,uid;
    ImageView back;
    Button dMission;
    DatabaseReference databaseReference,dbUserref;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_mission);
        codeMission = findViewById(R.id.codeClient);
        nomClient = findViewById(R.id.nClient);
        gouvernerat = findViewById(R.id.gouvernerat);
        superficie = findViewById(R.id.superficie);
        date = findViewById(R.id.mDate);
        dMission = findViewById(R.id.deleteMission);
        id = getIntent().getStringExtra("id");
        back = findViewById(R.id.getBack);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("MissionDb");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dbUserref = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);

        databaseReference.orderByChild("MarkerId").equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String cMission = snapshot.child("CodeMission").getValue().toString().trim();
                String nClient = snapshot.child("Client").getValue().toString().trim();
                String gouv = snapshot.child("Gouvernerat").getValue().toString().trim();
                String theDate = snapshot.child("Date").getValue().toString().trim();
                String sup = snapshot.child("Superficie").getValue().toString().trim();

                codeMission.setText(cMission);
                nomClient.setText(nClient);
                gouvernerat.setText(gouv);
                date.setText(theDate);
                superficie.setText(sup);

                dMission.setOnClickListener(v -> {
                    snapshot.getRef().removeValue();
                    Toast.makeText(RetrieveMission.this, "Mission supprimée", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RetrieveMission.this, MapsActivity.class);
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
                if (!uType.equals("Administrateur")){
                    dMission.setClickable(false);
                    dMission.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(v -> {
            Intent getBack = new Intent(RetrieveMission.this, MapsActivity.class);
            startActivity(getBack);
            finish();
        });
    }



}