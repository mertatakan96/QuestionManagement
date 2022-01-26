package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seproject.questionmanagement.CreateScreens.CreateTemplate;
import com.seproject.questionmanagement.QuestionnaireScreens.AllQuestionnaires;
import com.seproject.questionmanagement.R;

public class HomePageActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private boolean isAdmin;
    private String isActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot ds = task.getResult();
            isAdmin =  ds.getBoolean("userRole");
            isActive = ds.getString("activeStatus");

            bottomNavigationView = findViewById(R.id.bottomBar);

            bottomNavigationView.setOnItemSelectedListener(item -> {

                switch (item.getItemId()){
                    case R.id.home:
                        return true;

                    case R.id.create:
                        if(isAdmin){
                            Toast.makeText(this, "You are admin! You can not create Questionnaire", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else if (isActive.equals("0")){
                           // startActivity(new Intent(getApplicationContext(), CreateTemplate.class));
                            Toast.makeText(HomePageActivity.this, "Only Active User can create Questionnaires", Toast.LENGTH_SHORT).show();
                            return true;
                        }else if(isActive.equals("1")) {
                            Toast.makeText(HomePageActivity.this, "Your request in  progress", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        else {

                            startActivity(new Intent(getApplicationContext(), CreateTemplate.class));
                            return true;
                        }

                    case R.id.questionnaires:
                        startActivity(new Intent(getApplicationContext(), AllQuestionnaires.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:

                        if (isAdmin){
                            startActivity(new Intent(getApplicationContext(),ProfileAdminActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                        }

                }
                return true;
            });
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}