package com.example.topocompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class SaveData extends AppCompatActivity {
    String TAG ="SavaData";
    private static final int PICK_IMAGE_REQUEST = 1;

    String Localisation;
    EditText code;
    Button generate,Save;
    ImageView qrCode;
    ProgressBar progressBar;
    Uri uri;
    File outFile;

    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String filename;

    int Width = 512;
    int Length = 512;

    ArrayList<String> pathArray;
    int arrayPosition;
    public SaveData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);

        Localisation = getIntent().getStringExtra("Localisation");
        code = findViewById(R.id.codeNumber);
        qrCode = findViewById(R.id.qrView);
        generate = findViewById(R.id.Generate);
        pathArray = new ArrayList<>();

        ActivityCompat.requestPermissions(SaveData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(SaveData.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


        progressBar = findViewById(R.id.saveBar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("DataDb");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        generate.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String data = code.getText().toString().trim();
            if(data.isEmpty()){
                code.setError("Veuillez introduire un code.");
            }else{
            QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 500);
            Bitmap qrBits = qrgEncoder.getBitmap();
            qrCode.setImageBitmap(qrBits);
            }
            BitmapDrawable bitmapDrawable = (BitmapDrawable) qrCode.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            FileOutputStream outputStream = null;
            File file = Environment.getExternalStorageDirectory();
            filename = String.format(Localisation+".jpeg",System.currentTimeMillis());
            outFile = new File(file, filename);
            try{
                outputStream = new FileOutputStream(outFile);
            }catch (Exception e){
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream);
            Intent intent = new Intent();
            intent.putExtra("Qr",outFile);
            try{
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(SaveData.this, "QR code Enregistrer sur google photos", Toast.LENGTH_SHORT).show();
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(outFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            qrCode.setImageBitmap(bitmap);


            String Cd = code.getText().toString().trim();
            String Loc = Localisation.trim();

            if(!Cd.equals("")){
                uri = Uri.fromFile(new File(String.valueOf(outFile)));
                final StorageReference sReference = storageReference.child("Data/Qr/"+Loc+".jpg");
                sReference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUri = uri.toString();


                                String Cd = code.getText().toString().trim();
                                String Loc = Localisation.trim();

                                String uploadId = databaseReference.push().getKey();
                                DataDb dataDb = new DataDb(Cd,Loc,downloadUri);

                                databaseReference.child(uploadId).child("Code").setValue(Cd);
                                databaseReference.child(uploadId).child("Localisation").setValue(Loc);
                                databaseReference.child(uploadId).child("Qr").setValue(downloadUri);

                                Toast.makeText(SaveData.this, "Donnée ajoutée", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(getApplicationContext(),Data.class);
                                intent.putExtra("Code",Cd);
                                intent.putExtra("QrImage",downloadUri);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SaveData.this, "Qr enregistrer dans la base de données", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SaveData.this, "Enregistrement du Qr refusé", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    public void previous (View view){
        startActivity(new Intent(getApplicationContext(),AddData.class));
        finish();
    }

    public void home (View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
