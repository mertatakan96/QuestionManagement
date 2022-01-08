package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.seproject.questionmanagement.R;

public class QuestionnairesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerQuestionnaires;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaires);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

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
                                    Toast.makeText(QuestionnairesActivity.this, "Only Active User can create Questionnaires", Toast.LENGTH_SHORT).show();
                                    return true;
                                }else {
                                    startActivity(new Intent(getApplicationContext(),CreateQuestionnaireActivity.class));
                                    overridePendingTransition(0,0);
                                    return true;
                                }

                            case R.id.questionnaires:
                                return true;

                            case R.id.profile:
                                if (userRole.equals("0")){
                                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                    overridePendingTransition(0,0);
                                    return true;
                                }else if (userRole.equals("1")){
                                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                    overridePendingTransition(0,0);
                                    return true;
                                }else{
                                    startActivity(new Intent(getApplicationContext(),ProfileAdminActivity.class));
                                    overridePendingTransition(0,0);
                                    return true;
                                }
                        }
                        return true;
                    }
                });
            }
        });



        recyclerQuestionnaires = findViewById(R.id.recycler_questionnaires);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerQuestionnaires.setLayoutManager(layoutManager);
    }
}