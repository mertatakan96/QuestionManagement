package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.seproject.questionmanagement.R;

public class ProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = findViewById(R.id.bottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomePageActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.questionnaires:
                        startActivity(new Intent(getApplicationContext(),QuestionnairesActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        return true;
                }
                return true;
            }
        });
    }

    public void profilePageMyQuestionnairesClicked(View view) {
    }

    public void profilePageEditProfileClicked(View view) {
        Intent intentToEditProfile = new Intent(this,EditProfileActivity.class);
        startActivity(intentToEditProfile);
    }
}