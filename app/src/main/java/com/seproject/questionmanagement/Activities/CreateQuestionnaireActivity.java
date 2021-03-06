package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.seproject.questionmanagement.Questionnaire;
import com.seproject.questionmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class CreateQuestionnaireActivity extends AppCompatActivity implements View.OnClickListener {

    BottomNavigationView bottomNavigationView;

    private LinearLayout linearLayoutName;
    private Button buttonAddName;
    private Button buttonCreateQuestionnaire;
    private ArrayAdapter<CharSequence> adapterDuration;
    private Spinner questionnaireDurationSpinner;
    private EditText titleEditText;

    ArrayList<String> questionList = new ArrayList<>();
    String durationTime;
    String questionnaireTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_questionnaire);

        /*bottomNavigationView = findViewById(R.id.bottomBar);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomePageActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.create:
                        return true;

                    case R.id.questionnaires:
                        startActivity(new Intent(getApplicationContext(),QuestionnairesActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });*/


        /*questionnaireDurationSpinner.findViewById(R.id.spinnerQuestionnaireDuration);
        ArrayAdapter<String> myAdapterDuration = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.questionnaireDurationArray));
        myAdapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionnaireDurationSpinner.setAdapter(myAdapterDuration);*/

        questionnaireDurationSpinner = findViewById(R.id.spinnerQuestionnaireDuration);
        adapterDuration = ArrayAdapter.createFromResource(this, R.array.durationList, android.R.layout.simple_spinner_item);
        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionnaireDurationSpinner.setAdapter(adapterDuration);

        titleEditText = findViewById(R.id.questionnaireTitle);

        linearLayoutName = findViewById(R.id.layout_question_list);

        buttonAddName = findViewById(R.id.button_add_question);
        buttonCreateQuestionnaire = findViewById(R.id.button_create_questionnaire);

        buttonAddName.setOnClickListener(this);
        buttonCreateQuestionnaire.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.button_add_question:
                addView();
                break;


            case R.id.button_create_questionnaire:
                
                if(checkIfValidAndRead()){
                    questionnaireTitle = titleEditText.getText().toString();
                    durationTime = questionnaireDurationSpinner.getSelectedItem().toString();
                    Intent intent = new Intent(this, CreateAnswerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", questionList);
                    bundle.putSerializable("title", questionnaireTitle);
                    bundle.putSerializable("duration", durationTime);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
        }


    }

    private boolean checkIfValidAndRead() {
        questionList.clear();
        boolean result = true;

        for (int i=0;i<linearLayoutName.getChildCount();i++){

            View addQuestionView = linearLayoutName.getChildAt(i);

            EditText editTextName = (EditText)addQuestionView.findViewById(R.id.edit_question_name);

            //Questionnaire questionnaire = new Questionnaire();

            if (!editTextName.getText().equals("")){
                //questionnaire.setQuestion(editTextName.getText().toString());
                questionList.add(editTextName.getText().toString());
            }else{
                result = false;
                break;
            }

            // answerlar?? ekle

            //questionList.add(questionnaire);
        }

        if (questionList.size()==0){
            result = false;
            Toast.makeText(this,"Add Questions First!", Toast.LENGTH_SHORT).show();
        }else if(!result){
            Toast.makeText(this,"Enter All Details Correctly!", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void addView() {

        final View addQuestionView = getLayoutInflater().inflate(R.layout.row_add_question, null, false);


        EditText editText = (EditText)addQuestionView.findViewById(R.id.edit_question_name);
        ImageView imageClose = (ImageView)addQuestionView.findViewById(R.id.question_remove);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(addQuestionView);
            }
        });

        linearLayoutName.addView(addQuestionView);
    }


    private void removeView(View v){
        linearLayoutName.removeView(v);
    }
}