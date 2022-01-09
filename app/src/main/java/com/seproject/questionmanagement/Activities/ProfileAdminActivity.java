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
import com.seproject.questionmanagement.R;
import com.squareup.picasso.Picasso;

public class ProfileAdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private String userRole;
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
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                userRole = (String) ds.get("userRole");

                bottomNavigationView = findViewById(R.id.bottomBar);

                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.home:
                                startActivity(new Intent(getApplicationContext(),HomePageActivity.class));
                                overridePendingTransition(0,0);
                                return true;

                            case R.id.create:
                                if (userRole.equals("0")){
                                    Toast.makeText(ProfileAdminActivity.this, "Only Active User can create Questionnaires", Toast.LENGTH_SHORT).show();
                                    return true;
                                }else {
                                    startActivity(new Intent(getApplicationContext(),CreateQuestionnaireActivity.class));
                                    overridePendingTransition(0,0);
                                    return true;
                                }

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
        });
    }

    public void profilePageAdminMyQuestionnairesClicked(View view) {
    }


    public void profilePageAdminUserApplicationsClicked(View view) {

        Intent intentToApproval = new Intent(ProfileAdminActivity.this, AdminApproveActivity.class);
        startActivity(intentToApproval);
    }

    public void profilePageAdminExitProfileClicked(View view) {

        firebaseAuth.signOut();

        Intent signOutIntent = new Intent(ProfileAdminActivity.this,MainActivity.class);
        startActivity(signOutIntent);
        finish();
    }

    private void getData() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                String username = (String) ds.get("username");
                String downloadUrl = (String) ds.get("photo");

                usernameText.setText(username);
                Picasso.get().load(downloadUrl).into(imageViewPhoto);

            }
        });
    }
}