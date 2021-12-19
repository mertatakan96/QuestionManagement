package com.seproject.questionmanagement.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seproject.questionmanagement.R;

import java.util.ArrayList;

public class CreateAnswerActivity extends AppCompatActivity implements View.OnClickListener {

    //RecyclerView recyclerView;

    LinearLayout linearLayoutAnswer;
    Button buttonAddAnswer;
    TextView questionName;
    ArrayList<String> questionnairesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_answer);

        //recyclerView = findViewById(R.id.recycler_add_answers);

        linearLayoutAnswer = findViewById(R.id.layout_answer_list);

        questionnairesList = (ArrayList<String>)getIntent().getExtras().getSerializable("list");


        questionName = findViewById(R.id.question_name_answer);

        buttonAddAnswer = findViewById(R.id.button_add_answer);

        buttonAddAnswer.setOnClickListener(this);

        for (int i=0; i<questionnairesList.size();i++){
            questionName.setText(questionnairesList.get(i));
        }
    }

    @Override
    public void onClick(View v) {

        addView();

    }

    private void addView() {

        final View addAnswerView = getLayoutInflater().inflate(R.layout.row_add_answers,null,false);

        TextView textView = (TextView) addAnswerView.findViewById(R.id.edit_answer_name);
        ImageView imageClose = (ImageView) addAnswerView.findViewById(R.id.answer_remove);

        linearLayoutAnswer.addView(addAnswerView);
    }
}