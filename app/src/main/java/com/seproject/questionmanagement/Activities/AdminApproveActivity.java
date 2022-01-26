package com.seproject.questionmanagement.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.seproject.questionmanagement.Adapters.ListApplicationRecyclerAdapter;
import com.seproject.questionmanagement.R;

import java.util.ArrayList;
import java.util.Map;

public class AdminApproveActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    ListApplicationRecyclerAdapter applicationRecyclerAdapter;
    private ArrayList<String> applicationUsernameList;
    private ArrayList<String> applicationEmailList;
    private ArrayList<String> applicationTcnoList;
    private ArrayList<String> applicationBirthdateList;
    private ArrayList<String> applicationUserIDList;

    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve);

        applicationUsernameList = new ArrayList<>();
        applicationEmailList = new ArrayList<>();
        applicationTcnoList = new ArrayList<>();
        applicationBirthdateList = new ArrayList<>();
        applicationUserIDList = new ArrayList<>();

        mBack=findViewById(R.id.back);

        mBack.setOnClickListener(view -> onBackPressed());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewApprove);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminApproveActivity.this));
        applicationRecyclerAdapter = new ListApplicationRecyclerAdapter(applicationUsernameList,applicationEmailList,applicationTcnoList,applicationBirthdateList, applicationUserIDList);
        recyclerView.setAdapter(applicationRecyclerAdapter);



        getData();
/*
        RecyclerView recyclerView = findViewById(R.id.recyclerViewApprove);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationRecyclerAdapter = new ListApplicationRecyclerAdapter(applicationUsernameList,applicationEmailList,applicationTcnoList,applicationBirthdateList);
        recyclerView.setAdapter(applicationRecyclerAdapter);

        applicationRecyclerAdapter.setOnItemClickListener(new ListApplicationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onApproveClicked(int position) {
                System.out.println("Clicked" + applicationUsernameList.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminApproveActivity.this);
                builder.setMessage("Are you sure for the approvement");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //CollectionReference collectionReference = firebaseFirestore.collection("users");

                        //CollectionReference collectionReference = (CollectionReference) firebaseFirestore.collection("users").whereEqualTo("username",applicationUsernameList.get(position)).whereEqualTo("birthdate", applicationBirthdateList.get(position)).;

                        //collectionReference.up
                    }
                });//negative
            }
        });*/


    }

    private void getData(){
        applicationUsernameList.clear();
        applicationEmailList.clear();
        applicationTcnoList.clear();
        applicationBirthdateList.clear();
        applicationUserIDList.clear();
        CollectionReference collectionReference = firebaseFirestore.collection("users");

        collectionReference.whereEqualTo("activeStatus", "1").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();

                        String username = (String) data.get("username");
                        String email = (String) data.get("email");
                        String tcno = (String) data.get("tcno");
                        String birthdate = (String) data.get("birthdate");
                        String userID = (String) data.get("userID");

                        applicationUsernameList.add(username);
                        applicationEmailList.add(email);
                        applicationTcnoList.add(tcno);
                        applicationBirthdateList.add(birthdate);
                        applicationUserIDList.add(userID);


                        applicationRecyclerAdapter.setOnItemClickListener(new ListApplicationRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onApproveClicked(int position) {
                                System.out.println("Clicked: " + applicationUsernameList.get(position) + " UserID: " + applicationUserIDList.get(position));
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminApproveActivity.this);
                                builder.setMessage("Are you sure for the approvement");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        DocumentReference documentReference = firebaseFirestore.collection("users").document(applicationUserIDList.get(position));

                                        documentReference.update("activeStatus", "2");

                                      finish();


                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        });

                        applicationRecyclerAdapter.notifyDataSetChanged();


                    }
                }
            }
        });

    }
}