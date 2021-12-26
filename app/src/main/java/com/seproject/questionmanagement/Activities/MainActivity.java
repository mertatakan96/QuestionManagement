package com.seproject.questionmanagement.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seproject.questionmanagement.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signUpClickedMain(View view){
        Intent intentToSignUp = new Intent(this,RegisterActivity.class);
        startActivity(intentToSignUp);
    }

    public void loginClickedMain(View view){
        Intent intentToLogin = new Intent(this,LoginActivity.class);
        startActivity(intentToLogin);
    }

    public void guestClickedMain(View view) {
        Intent intentToGuestHome = new Intent(this, GuestHomeActivity.class);
        startActivity(intentToGuestHome);
    }
}