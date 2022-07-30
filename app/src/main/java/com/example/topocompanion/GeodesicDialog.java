package com.example.topocompanion;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GeodesicDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "GeodesicDialog";

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).equals("Choisir le gouvernerat de la mission")){

        }else{
            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener mOninputListener;

    private EditText codePoint,latitude,longitude,h;
    Button saveMission,cancel;

    DatabaseReference databaseReference;

    Bundle args;
    Double lat;
    Double lng;

    Spinner spinner;
    ArrayAdapter<String> adapter;
    List<String> gouv;
    RadioGroup radioGroup;
    RadioButton twD;
    RadioButton thD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.geodesic_dialog,container, false);
        saveMission = view.findViewById(R.id.saveMission);
        cancel = view.findViewById(R.id.cancel);
        codePoint = view.findViewById(R.id.pCode);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        h = view.findViewById(R.id.h);
        h.setVisibility(View.INVISIBLE);
        twD = view.findViewById(R.id.radio2D);
        thD = view.findViewById(R.id.radio3D);
        args=getArguments();
        radioGroup=view.findViewById(R.id.gradioGroupe);

        spinner = view.findViewById(R.id.spinner2);
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
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, gouv);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        twD.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                h.setVisibility(View.INVISIBLE);
                thD.setChecked(false);
            }
        });
        thD.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                h.setVisibility(View.VISIBLE);
                twD.setChecked(false);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("PointsGéodésique");

        cancel.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing dialog");
            getDialog().dismiss();
        });

        saveMission.setOnClickListener(v -> {
            Log.d(TAG, "onClick: capturing inputs");
            String codeP = codePoint.getText().toString().trim();
            String gouv = spinner.getSelectedItem().toString().trim();
            double x = Double.parseDouble(latitude.getText().toString().trim());
            double y = Double.parseDouble(longitude.getText().toString().trim());
            double z = Double.parseDouble(h.getText().toString().trim());
            //if(!codeM.equals("") && !clientM.equals(""))

            String uploadId = databaseReference.push().getKey();
            GeodesicDb geodesicDb = new GeodesicDb(codeP,gouv,x,y,z);
            databaseReference.child(uploadId).child("Gouvernerat").setValue(gouv);
            databaseReference.child(uploadId).child("CodePoint").setValue(codeP);
            databaseReference.child(uploadId).child("Gouvernerat").setValue(gouv);
            databaseReference.child(uploadId).child("Latitude").setValue(x);
            databaseReference.child(uploadId).child("Longitude").setValue(y);
            databaseReference.child(uploadId).child("h").setValue(z);


            Toast.makeText(getContext(), "Point ajouté", Toast.LENGTH_SHORT).show();

            mOninputListener.sendInput(uploadId);
            getDialog().dismiss();

        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOninputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException"+e.getMessage() );
        }
    }
}
