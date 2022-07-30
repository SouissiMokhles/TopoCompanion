package com.example.topocompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail,mPassword,mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userID;
    Spinner uTypeSpinner;
    ArrayAdapter<String> adapter;
    List<String> uType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.NomPrenom);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Mdp);
        mPhone = findViewById(R.id.Phone);
        mRegisterBtn = findViewById(R.id.Submit);
        mLoginBtn = findViewById(R.id.TextView3);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        uTypeSpinner = findViewById(R.id.uTypespinner);
        uType = new ArrayList<>();
        uType.add(0,"Choisir le  type d'utilisateur");
        uType.add("Administrateur");
        uType.add("Lecteur");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        uTypeSpinner.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String uType = uTypeSpinner.getSelectedItem().toString().trim();
                final String fullName = mFullName.getText().toString().trim();
                final String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("Merci d'introduire votre nom et prénom");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Merci d'introduire votre Email");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Merci d'introduire un mot de passe");
                    return;
                }
                if (password.length() <6 ){
                    mPassword.setError("Le mot de passe doit contenir en moin 7 caractère");
                    return;
                }

                if (uTypeSpinner.getSelectedItemPosition() == 0){
                    TextView errorView = (TextView) uTypeSpinner.getSelectedView();
                    errorView.setError("Veuillez choisir le type d'utilisateur");
                    return;
                }

                if (uTypeSpinner.getSelectedItemPosition() == 1)
                {
                    showDialog();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //Inscrire l'utilisateur
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "Vous êtes inscris avec succés", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            databaseReference = firebaseDatabase.getReference("Utilisateurs").child(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Nom d'utilisateur",fullName);
                            user.put("email",email);
                            user.put("Numéro de téléphone",phone);
                            user.put("Type",uType);
                            databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User profile is created for "+userID);
                                }
                            });
                            startActivity(new Intent (getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Une erreur est survenu"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }
    private void showDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(Register.this)
                .setTitle("Confirmation d'un compte administrateur")
                .setMessage("Veuillez saisir le code secret.")
                .setView(input)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String code = input.getText().toString().trim();
                        if (!code.equals("1988"))
                        {
                            Toast.makeText(Register.this, "Code incorrect", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(Register.this, "Code correct", Toast.LENGTH_SHORT).show();
                            final String email = mEmail.getText().toString().trim();
                            String password = mPassword.getText().toString().trim();
                            String uType = uTypeSpinner.getSelectedItem().toString().trim();
                            final String fullName = mFullName.getText().toString().trim();
                            final String phone = mPhone.getText().toString();

                            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(Register.this, "Vous êtes inscris avec succés", Toast.LENGTH_SHORT).show();
                                        userID = fAuth.getCurrentUser().getUid();
                                        databaseReference = firebaseDatabase.getReference("Utilisateurs").child(userID);
                                        Map<String,Object> user = new HashMap<>();
                                        user.put("Nom d'utilisateur",fullName);
                                        user.put("email",email);
                                        user.put("Numéro de téléphone",phone);
                                        user.put("Type",uType);
                                        databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: User profile is created for "+userID);
                                            }
                                        });
                                        startActivity(new Intent (getApplicationContext(),MainActivity.class));
                                    }else{
                                        Toast.makeText(Register.this, "Une erreur est survenu"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}