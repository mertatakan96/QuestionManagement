package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private boolean userRole;

    private Button beActive;

    private ImageView imageViewPhoto;
    private TextView usernameText;
    private String tcno;
    private String activeStatus;
    private boolean isAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = findViewById(R.id.profilePageUsername);
        imageViewPhoto = findViewById(R.id.profilePageProfileImage);
        beActive = findViewById(R.id.profilePageBeActive);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        getData();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                isAdmin =  ds.getBoolean("userRole");
                activeStatus = ds.getString("activeStatus");

                bottomNavigationView = findViewById(R.id.bottomBar);

                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
                    }
                });
            }
        });

    }

    public void profilePageMyQuestionnairesClicked(View view) {
        Intent i = new Intent(getApplicationContext(), QuestionnairesActivity.class);
        i.putExtra("status","my");
        startActivity(i);
    }

    public void profilePageEditProfileClicked(View view) {
        Intent intentToEditProfile = new Intent(this,EditProfileActivity.class);
        startActivity(intentToEditProfile);
    }

    public void profilePageExitProfileClicked(View view) {

        firebaseAuth.signOut();

        Intent signOutIntent = new Intent(ProfileActivity.this,MainActivity.class);
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
                String activeStatus  = ds.getString("activeStatus");

                if(activeStatus.equals("1")){
                    beActive.setText("Processing");
                } else if(activeStatus.equals("0")){
                    beActive.setText("Be Active User");
                }else if(activeStatus.equals("2")){
                    beActive.setVisibility(View.GONE);
                }


                usernameText.setText(username);
                Picasso.get().load(downloadUrl).into(imageViewPhoto);

            }
        });


    }

    public void profilePageBeActiveProfileClicked(View view) {

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot ds = task.getResult();
            activeStatus = (String) ds.get("activeStatus");

            if (activeStatus.equals("0")){

                Intent i = new Intent(getApplicationContext(), SendRequestActivity.class);
                startActivity(i);

            } else {
                Toast.makeText(ProfileActivity.this, "You already send your tc for approval!", Toast.LENGTH_SHORT).show();
            }

        });



    }

}