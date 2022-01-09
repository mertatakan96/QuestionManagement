package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.seproject.questionmanagement.R;

public class MyQuestionnairesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questionnaires);
        bottomNavigationView = findViewById(R.id.bottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:
                        Intent intentToHome = new Intent(MyQuestionnairesActivity.this, HomePageActivity.class);
                        startActivity(intentToHome);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.questionnaires:
                        startActivity(new Intent(MyQuestionnairesActivity.this,QuestionnairesActivity.class));
                        overridePendingTransition(0,0);
                }
                return true;
            }
        });
    }
}