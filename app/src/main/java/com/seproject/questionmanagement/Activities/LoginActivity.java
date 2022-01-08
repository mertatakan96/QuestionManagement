package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.seproject.questionmanagement.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.loginEmail);
        passwordText = findViewById(R.id.loginPassword);

        firebaseAuth = FirebaseAuth.getInstance();
/*
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            Intent intentToH = new Intent(LoginActivity.this,HomePageActivity.class);
            startActivity(intentToH);
            finish();
        }*/

    }


    public void loginClickedLogin(View view) {

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "Enter email and password", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intentToHome = new Intent(LoginActivity.this,HomePageActivity.class);
                    //intentToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //finish();
                    startActivity(intentToHome);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

        //Intent intentToHome = new Intent(this,HomePageActivity.class);
        //startActivity(intentToHome);
    }

    public void forgotPasswordClicked(View view) {
        Intent intentToForgotPassword = new Intent(this,ForgetPasswordActivity.class);
        startActivity(intentToForgotPassword);
    }
}