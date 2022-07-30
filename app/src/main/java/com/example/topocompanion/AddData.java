package com.example.topocompanion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddData extends AppCompatActivity {
    Spinner spinner;
    List<String> gouv;
    ArrayAdapter<String> adapter;
    String locValue;
    FirebaseFirestore fStore;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_add_data);
        spinner= findViewById(R.id.locSpinner);
        gouv = new ArrayList<>();
        gouv.add(0, "Choisir le gouvernerat de la mission");
        gouv.add("Ariana");
        gouv.add("Béja");
        gouv.add("Ben Arous");
        gouv.add("Bizerte");
        gouv.add("Gabès");
        gouv.add("Gafsa");
        gouv.add("Jendouba");
        gouv.add("Kairouan");
        gouv.add("Kasserine");
        gouv.add("Kébili");
        gouv.add("Le Kef");
        gouv.add("Mahdia");
        gouv.add("La Manouba");
        gouv.add("Médenine");
        gouv.add("Monastir");
        gouv.add("Nabeul");
        gouv.add("Sfax");
        gouv.add("Sidi Bouzid");
        gouv.add("Siliana");
        gouv.add("Sousse");
        gouv.add("Tataouine");
        gouv.add("Tozeur");
        gouv.add("Tunis");
        gouv.add("Zaghouan");
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gouv);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fStore= FirebaseFirestore.getInstance();
    }

    public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public void next (View view){
        locValue = spinner.getSelectedItem().toString().trim();
        if (spinner.getSelectedItemPosition() == 0 ){
            TextView errorView =(TextView) spinner.getSelectedView();
            errorView.setError("Veuillez choisir la gouvernerat de la mission");
            return;
        }
        Intent intent = new Intent(AddData.this,SaveData.class);
        intent.putExtra("Localisation", locValue);
        startActivity(intent);
        finish();
    }

    public void previous (View view){
        startActivity(new Intent(getApplicationContext(),Data.class));
        finish();
    }

    public void toScan (View view){
        locValue = spinner.getSelectedItem().toString().trim();
        if (spinner.getSelectedItemPosition() == 0 ){
            TextView errorView =(TextView) spinner.getSelectedView();
            errorView.setError("Veuillez choisir la gouvernerat de la mission");
            return;
        }
        int REQUEST_CODE = 99;
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(AddData.this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public String getLocValue() {
        locValue = spinner.getSelectedItem().toString().trim();
        return locValue;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(path+"/"+getLocValue());
        if (!myDir.exists()) {
            myDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        OutputStream fOut = null;
        File file = new File(myDir, getLocValue()+"_"+timeStamp+".jpg");
        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,85,fOut);
                fOut.flush();
                fOut.close();
                //scannedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void home (View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }


}