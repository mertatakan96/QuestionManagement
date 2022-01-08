package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
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
import com.seproject.questionmanagement.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private String userRole;
    private ImageView imageViewPhoto;
    private TextView usernameText;
    private String tcno;
    private String activeStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = findViewById(R.id.profilePageUsername);
        imageViewPhoto = findViewById(R.id.profilePageProfileImage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        getData();

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
                                    Toast.makeText(ProfileActivity.this, "Only Active User can create Questionnaires", Toast.LENGTH_SHORT).show();
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

    public void profilePageMyQuestionnairesClicked(View view) {
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

                usernameText.setText(username);
                Picasso.get().load(downloadUrl).into(imageViewPhoto);

            }
        });
    }

    public void profilePageBeActiveProfileClicked(View view) {

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                activeStatus = (String) ds.get("activeStatus");

                if (activeStatus.equals("0")){
                    AlertDialog.Builder myDialog = new AlertDialog.Builder(ProfileActivity.this);
                    myDialog.setTitle("Enter your TC for approval");

                    final EditText tcInput = new EditText(ProfileActivity.this);
                    tcInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    myDialog.setView(tcInput);

                    myDialog.setPositiveButton("Send For Approval", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tcno = tcInput.getText().toString();
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                            documentReference.update("tcno", tcno).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    documentReference.update("activeStatus", "1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProfileActivity.this, "Your approval has been sent!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

                    myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    myDialog.show();
                } else {
                    Toast.makeText(ProfileActivity.this, "You already send your tc for approval!", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}