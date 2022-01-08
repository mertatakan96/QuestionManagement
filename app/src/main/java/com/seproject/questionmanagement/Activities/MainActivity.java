package com.seproject.questionmanagement.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.seproject.questionmanagement.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            Intent intentToH = new Intent(MainActivity.this,HomePageActivity.class);
            startActivity(intentToH);
            finish();
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}