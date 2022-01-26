package com.seproject.questionmanagement.QuestionnaireScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.seproject.questionmanagement.Activities.QuestionnairesActivity;
import com.seproject.questionmanagement.R;

public class AllQuestionnaires extends AppCompatActivity {

    Button showActive;
    Button showExpired;
    ImageView  mBack;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_questionnaires);
        showActive = findViewById(R.id.active);
        showExpired=findViewById(R.id.expired);
        mBack = findViewById(R.id.back);

        mBack.setOnClickListener(view -> onBackPressed());

        showActive.setOnClickListener(view -> {
            Intent  i = new Intent(context, QuestionnairesActivity.class);
            i.putExtra("status",  "active");
            startActivity(i);
        });
        showExpired.setOnClickListener(view -> {
            Intent  i = new Intent(context, QuestionnairesActivity.class);
            i.putExtra("status",  "expired");
            startActivity(i);
        });
    }
}