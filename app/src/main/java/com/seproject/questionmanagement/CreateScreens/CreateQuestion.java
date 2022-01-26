package com.seproject.questionmanagement.CreateScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seproject.questionmanagement.Activities.CreateAnswerActivity;
import com.seproject.questionmanagement.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateQuestion extends AppCompatActivity implements View.OnClickListener {

    private ImageView mCreate, mBack;
    private String docID;
    private LinearLayout questionsLayout;
    private Button buttonAddName;
    private Button buttonCreateQuestionnaire;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mRef;
    private FirebaseUser user;


    int questionCount = 0;
    ArrayList<String> questionList = new ArrayList<>();
    ArrayList<String> option1List = new ArrayList<>();
    ArrayList<String> option2List = new ArrayList<>();
    ArrayList<String> option3List = new ArrayList<>();
    ArrayList<String> option4List = new ArrayList<>();
    ArrayList<String> option5List = new ArrayList<>();


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        // get document id from intent
        docID = getIntent().getStringExtra("docID");

        //Elements
        mCreate = findViewById(R.id.question_create);
        mBack = findViewById(R.id.back);
        buttonAddName = findViewById(R.id.button_add_question);
        buttonCreateQuestionnaire = findViewById(R.id.button_create_questionnaire);
        //Questions LinearLayout
        questionsLayout = findViewById(R.id.layout_question_list);

        //Firebase
        mAuth =FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mRef = db.collection("Questionnaires").document(docID).collection("Questions");



        //Clicks
        buttonAddName.setOnClickListener(this);
        buttonCreateQuestionnaire.setOnClickListener(this);
        mBack.setOnClickListener(view -> onBackPressed());

    }

    private void create(int index){

        final Map<String, Object> mMap = new HashMap<>();

        mMap.put("publisher", user.getUid());
        mMap.put("docID", index+1);
        mMap.put("question", questionList.get(index));
        mMap.put("option1", option1List.get(index));
        mMap.put("option2", option2List.get(index));
        mMap.put("option3", option3List.get(index));
        mMap.put("option4", option4List.get(index));
        mMap.put("option5", option5List.get(index));
        mMap.put("created_time", System.currentTimeMillis());

        //Listedeki index 0 dan basladigi icin 0. index 1. soruya denk geliyor bu yuzden documentID => index+1
        mRef.document(String.valueOf(index+1)).set(mMap).addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                Toast.makeText(context, "CREATED", Toast.LENGTH_SHORT).show();
            }

        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.button_add_question:
                addView();
                break;


            case R.id.button_create_questionnaire:

                //Girilen degerler kriterlere uygunsa questionCount  a ulasana kadar bir for aciyor
                if(checkIfValidAndRead()){

                    for (int i=0; i<questionCount; i++) {
                        create(i);
                    }
                    this.finish();

                }
        }
    }


    private boolean checkIfValidAndRead() {
        questionList.clear();
        boolean result = true;

        for (int i=0 ; i<questionsLayout.getChildCount() ;i++){

            View addQuestionView = questionsLayout.getChildAt(i);

            EditText mQuestion = (EditText)addQuestionView.findViewById(R.id.title);
            EditText mOption1 = (EditText)addQuestionView.findViewById(R.id.option1);
            EditText mOption2 = (EditText)addQuestionView.findViewById(R.id.option2);
            EditText mOption3 = (EditText)addQuestionView.findViewById(R.id.option3);
            EditText mOption4 = (EditText)addQuestionView.findViewById(R.id.option4);
            EditText mOption5 = (EditText)addQuestionView.findViewById(R.id.option5);


            if (!TextUtils.isEmpty(mQuestion.getText().toString())){
                questionList.add(mQuestion.getText().toString());

            }else{
                Toast.makeText(this, "HATA", Toast.LENGTH_SHORT).show();
                mQuestion.setError("Question must be entered!");
                mQuestion.requestFocus();
                result = false;
                break;
            }

            if (!TextUtils.isEmpty(mOption1.getText().toString())){
                option1List.add(mOption1.getText().toString());

            }else{
                mOption1.setError(" At least two options must be entered");
                mOption1.requestFocus();
                result = false;
                break;
            }

            if (!TextUtils.isEmpty(mOption2.getText().toString())){
                //questionnaire.setQuestion(editTextName.getText().toString());
                option2List.add(mOption2.getText().toString());

            }else{
                mOption2.setError(" At least two options must be entered");
                mOption2.requestFocus();
                result = false;
                break;
            }

            if(TextUtils.isEmpty(mOption3.getText().toString())){
                option3List.add("default");
            }
            else {
                option3List.add(mOption3.getText().toString());
            }

            if(TextUtils.isEmpty(mOption4.getText().toString())){
                option4List.add("default");
            }
            else {
                option4List.add(mOption4.getText().toString());
            }


            if(TextUtils.isEmpty(mOption5.getText().toString())){
                option5List.add("default");
            }
            else {
                option5List.add(mOption5.getText().toString());
            }

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

        final View addQuestionView = getLayoutInflater().inflate(R.layout.layout_question, null, false);
       // EditText editText = (EditText)addQuestionView.findViewById(R.id.edit_question_name);
        ImageView imageClose = (ImageView)addQuestionView.findViewById(R.id.remove_question);
        imageClose.setOnClickListener(v -> removeView(addQuestionView));
        questionsLayout.addView(addQuestionView);
        questionCount++;

    }


    private void removeView(View v){
        questionsLayout.removeView(v);
        questionCount--;
    }
}