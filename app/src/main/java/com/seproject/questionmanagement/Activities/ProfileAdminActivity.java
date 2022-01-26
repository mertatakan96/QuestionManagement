package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

public class ProfileAdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private String activeStatus;
    private boolean isAdmin;
    private ImageView imageViewPhoto;
    private TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        usernameText = findViewById(R.id.profilePageUsername);
        imageViewPhoto = findViewById(R.id.profilePageProfileImage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        getData();

        //getData();

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot ds = task.getResult();
            isAdmin =  ds.getBoolean("userRole");
            activeStatus = ds.getString("activeStatus");

            bottomNavigationView = findViewById(R.id.bottomBar);

            bottomNavigationView.setOnItemSelectedListener(item -> {

                switch (item.getItemId()){
                    case R.id.home:
                        Intent intentToH = new Intent(getApplicationContext(),HomePageActivity.class);
                        startActivity(intentToH);
                        finish();
                        return true;

                    case R.id.create:
                        if (activeStatus.equals("0")){
                            // startActivity(new Intent(getApplicationContext(), CreateTemplate.class));
                            Toast.makeText(getApplicationContext(), "Only Active User can create Questionnaires", Toast.LENGTH_SHORT).show();
                            return true;
                        }else if(activeStatus.equals("1")) {
                            Toast.makeText(getApplicationContext(), "Your request in  progress", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else {
                            //startActivity(new Intent(getApplicationContext(),CreateQuestionnaireActivity.class));
                            //overridePendingTransition(0,0);
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


    public void profilePageAdminUserApplicationsClicked(View view) {

        Intent intentToApproval = new Intent(ProfileAdminActivity.this, AdminApproveActivity.class);
        startActivity(intentToApproval);
    }
    public void profilePageEditProfileClicked(View view) {
        Intent intentToEditProfile = new Intent(this,EditProfileActivity.class);
        startActivity(intentToEditProfile);
    }

    public void profilePageAllQuestionnaires(View view) {
        Intent i = new Intent(getApplicationContext(), QuestionnairesActivity.class);
        i.putExtra("status","all");
        startActivity(i);
    }

    public void profilePageAdminExitProfileClicked(View view) {

        firebaseAuth.signOut();

        Intent signOutIntent = new Intent(ProfileAdminActivity.this,MainActivity.class);
        startActivity(signOutIntent);
        finish();
    }

    private void getData() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot ds = task.getResult();
            String username = (String) ds.get("username");
            String downloadUrl = (String) ds.get("photo");

            usernameText.setText(username);
            Picasso.get().load(downloadUrl).into(imageViewPhoto);

        });
    }
}