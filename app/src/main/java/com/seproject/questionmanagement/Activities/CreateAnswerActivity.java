package com.seproject.questionmanagement.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seproject.questionmanagement.Questionnaire;
import com.seproject.questionmanagement.R;

import java.util.ArrayList;

public class CreateAnswerActivity extends AppCompatActivity implements View.OnClickListener {

    //RecyclerView recyclerView;

    LinearLayout linearLayoutAnswer;
    Button buttonAddAnswer, buttonSendAnswers;
    TextView questionName;
    ArrayList<String> questionnairesList = new ArrayList<>();
    ArrayList<String> answerList = new ArrayList<>();
    private int questionNumber = 0;
    private int clickCount = 0;
    //Questionnaire questionnaire = new Questionnaire();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_answer);



        linearLayoutAnswer = findViewById(R.id.layout_answer_list);

        questionnairesList = (ArrayList<String>)getIntent().getExtras().getSerializable("list");


        questionName = findViewById(R.id.question_name_answer);

        buttonAddAnswer = findViewById(R.id.button_add_answer);

        buttonSendAnswers = findViewById(R.id.button_send_answers);

        buttonAddAnswer.setOnClickListener(this);

        buttonSendAnswers.setOnClickListener(this);
        //System.out.println("Açılış Ekranı: " + questionNumber); 0
        //updateQuestion(); update ve printi böl
        //System.out.println("Açılış Ekranı: " + questionNumber);
        questionName.setText("Miss Click");


        /*buttonSendAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkIfValidAndRead();
                updateQuestion();
                //checkIfValidAndRead();

            }
        });*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_answer:
                addView();
                break;

            case R.id.button_send_answers:
                if (clickCount == 0){
                    questionName.setText(questionnairesList.get(questionNumber));
                    clickCount++;
                }else{

                    //System.out.println("Update Öncesi: " + questionNumber);
                    updateQuestion();
                    linearLayoutAnswer.removeAllViews(); // bir sonraki soruya geçerken tüm cevapları temizliyor.
                    //System.out.println("Update Sonrası: " + questionNumber);
                }


        }

    }

    private void addView() {

        final View addAnswerView = getLayoutInflater().inflate(R.layout.row_add_answers,null,false);

        TextView textView = (TextView) addAnswerView.findViewById(R.id.edit_answer_name);
        ImageView imageClose = (ImageView) addAnswerView.findViewById(R.id.answer_remove);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(addAnswerView);
            }
        });

        linearLayoutAnswer.addView(addAnswerView);
    }

    private void removeView(View v){
        linearLayoutAnswer.removeView(v);
    }


    private void updateQuestion(){
       if ((questionnairesList.size()) == (questionNumber)){
            Intent intentToProfile = new Intent(this,ProfileActivity.class);
            startActivity(intentToProfile);
            Toast.makeText(getBaseContext(), "Your Questionnaire Created", Toast.LENGTH_LONG).show();
        }else{
           //questionName.setText(questionnairesList.get(questionNumber));
           //System.out.println("Update Q: " + questionNumber);
           checkIfValidAndRead();
           //System.out.println("After Q: " + questionNumber);
           questionNumber++;
           if (questionNumber != questionnairesList.size()){
               questionName.setText(questionnairesList.get(questionNumber));
           }
           else {
               updateQuestion(); //questionNumber setText den önce arttığı için array sınırını aşarsa methoda girip if state'i görsün önce
           }
           //questionName.setText(questionnairesList.get(questionNumber));
           //questionNumber++;
           //System.out.println(questionnairesList);
           //System.out.println(answerList);
           //System.out.println("Soru: " + questionnaire.getQuestion() + " Cevaplar: " + questionnaire.getAnswers());
       }

    }

    private boolean checkIfValidAndRead(){
        answerList.clear();

        //Questionnaire questionnaire = new Questionnaire();
        boolean result = true;

        for (int i=0;i<linearLayoutAnswer.getChildCount();i++){

            View addAnswerView = linearLayoutAnswer.getChildAt(i);

            EditText editTextAnswer = (EditText) addAnswerView.findViewById(R.id.edit_answer_name);

            Questionnaire questionnaire = new Questionnaire();

            if (!editTextAnswer.getText().equals("")){
                questionnaire.setQuestion(questionnairesList.get(questionNumber)); // sorunun adını al
                answerList.add(editTextAnswer.getText().toString());
                questionnaire.setAnswers(answerList);   //sorunun cevaplarını al
                //System.out.println(questionNumber);
                System.out.println("Soru: " + questionnaire.getQuestion() + " Cevaplar: " + questionnaire.getAnswers());
                //answerList.add(editTextAnswer.getText().toString());
            }else {
                result = false;
                break;
            }
        }
        return result;
    }
}