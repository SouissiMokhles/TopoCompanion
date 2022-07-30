package com.example.topocompanion;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyCustomDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MyCustomDialog";

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

    EditText codeMission,client,sup,myDate;
    Button saveMission,cancel;
    DatabaseReference databaseReference;

    Bundle args;
    Double lat;
    Double lng;
    Double rad;
    String markerId;

    Spinner spinner;
    final Calendar calendar = Calendar.getInstance();
    ArrayAdapter<String> adapter;
    List<String> gouv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_custom,container, false);
        saveMission = view.findViewById(R.id.saveMission);
        cancel = view.findViewById(R.id.cancel);
        codeMission = view.findViewById(R.id.mCode);
        client = view.findViewById(R.id.mClient);
        myDate = view.findViewById(R.id.date);
        sup = view.findViewById(R.id.sup);
        args=getArguments();
        lat = args.getDouble("lat");
        lng = args.getDouble("lng");
        rad = args.getDouble("rad");
        markerId = args.getString("markerId");

        spinner = view.findViewById(R.id.spinner1);
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

        DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updatelabel();
        };

        myDate.setOnClickListener(v -> new DatePickerDialog(getContext(), date, calendar
        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());






        databaseReference = FirebaseDatabase.getInstance().getReference("MissionDb");


        saveMission.setOnClickListener(v -> {
            Log.d(TAG, "onClick: capturing inputs");
            String codeM = codeMission.getText().toString().trim();
            String clientM = client.getText().toString().trim();
            String gouv = spinner.getSelectedItem().toString().trim();
            String s = sup.getText().toString().trim();
            String thedate = myDate.getText().toString().trim();

            if(TextUtils.isEmpty(codeM)){
                codeMission.setError("Veuillez introduire un code de mission");
                return;
            }


            if (spinner.getSelectedItemPosition() == 0 ){
                TextView errorView =(TextView) spinner.getSelectedView();
                errorView.setError("Veuillez choisir la gouvernerat de la mission");
                return;
            }


            if(TextUtils.isEmpty(clientM)){
                client.setError("Veuillez introduire le nom du client");
                return;
            }

            if (TextUtils.isEmpty(s)){
                sup.setError("Veuillez introduire la superficie de la zone de mission");
            }




            String uploadId = databaseReference.push().getKey();
            MissionDb missionDb = new MissionDb(codeM,clientM,gouv,lat,lng,s,thedate,markerId);
            databaseReference.child(uploadId).child("Gouvernerat").setValue(gouv);
            databaseReference.child(uploadId).child("CodeMission").setValue(codeM);
            databaseReference.child(uploadId).child("Client").setValue(clientM);
            databaseReference.child(uploadId).child("Gouvernerat").setValue(gouv);
            databaseReference.child(uploadId).child("Date").setValue(thedate);
            databaseReference.child(uploadId).child("Superficie").setValue(s);
            databaseReference.child(uploadId).child("Latitude").setValue(lat);
            databaseReference.child(uploadId).child("Longitude").setValue(lng);
            databaseReference.child(uploadId).child("MarkerId").setValue(markerId);

            Toast.makeText(getContext(), "Mission ajouté", Toast.LENGTH_SHORT).show();

            mOninputListener.sendInput(uploadId);
            getDialog().dismiss();

        });

        cancel.setOnClickListener(v ->
                getDialog().dismiss());
        return view;
    }

    private void updatelabel() {
        String format ="dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
        myDate.setText(sdf.format(calendar.getTime()));
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
