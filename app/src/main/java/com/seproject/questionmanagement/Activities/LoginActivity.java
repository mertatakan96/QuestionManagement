package com.seproject.questionmanagement.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seproject.questionmanagement.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void loginClickedLogin(View view) {
        Intent intentToHome = new Intent(this,HomePageActivity.class);
        startActivity(intentToHome);
    }

    public void forgotPasswordClicked(View view) {
        Intent intentToForgotPassword = new Intent(this,ForgetPasswordActivity.class);
        startActivity(intentToForgotPassword);
    }
}