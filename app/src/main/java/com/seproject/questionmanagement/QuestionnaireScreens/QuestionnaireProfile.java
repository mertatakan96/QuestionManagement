package com.seproject.questionmanagement.QuestionnaireScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;
import com.seproject.questionmanagement.Activities.AdminApproveActivity;
import com.seproject.questionmanagement.Adapters.Questions;
import com.seproject.questionmanagement.CreateScreens.CreateQuestion;
import com.seproject.questionmanagement.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class QuestionnaireProfile extends AppCompatActivity {

    private TextView mTitle,mExplain,mDate;
    private RecyclerView recyclerView;
    private ImageView mAdd, mBack, mDelete;
    private Button mSendAnswers;




    //Firebase
    private  FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mRef;
    private DocumentReference mUserRef;


    private FirestoreRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    private String userID;
    private int joinerCount=0;
    private int option1Count,option2Count,option3Count,option4Count,option5Count;
    private int purpleColor;
    int questionCount=0;
    private String docID;
    private boolean isExpired=false;
    private ArrayList<String> answers = new ArrayList<>();

    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_profile);
        purpleColor  = ContextCompat.getColor(context, R.color.darkPurple);

        // verileri cekmek icin bize onceki sayfadan document id gelmeli
        docID = getIntent().getStringExtra("docID");


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mUserRef = db.collection("users").document(userID);
        mRef = db.collection("Questionnaires").document(docID);


        //Recview
        recyclerView = findViewById(R.id.recView);
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        //Elements
        mTitle= findViewById(R.id.profile_title);
        mExplain = findViewById(R.id.profile_explain);
        mDate = findViewById(R.id.profile_deadline);
        mAdd = findViewById(R.id.profile_add);
        mAdd.setVisibility(View.GONE);
        mBack = findViewById(R.id.back);
        mSendAnswers = findViewById(R.id.button_send_answers);
        mDelete  = findViewById(R.id.delete);
        mDelete.setVisibility(View.GONE);

        ///Clicks
        mBack.setOnClickListener(view -> onBackPressed());
        mAdd.setOnClickListener(view -> {
            Intent intent = new Intent(context, CreateQuestion.class);
            intent.putExtra("docID",docID);
            startActivity(intent);
        });
        mDelete.setOnClickListener(view -> delete());



        mSendAnswers.setOnClickListener(view -> {

            //Answers listesinin uzunluguna gore bir for dongusu aciyoruz.
            for (int i=1; i<=answers.size(); i++){
                saveAnswers(i);
            }
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            finish();


        });

    }



    private void delete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Yes", (dialog, which) -> mRef.delete().addOnSuccessListener(unused -> {
            Toast.makeText(context, "DELETED", Toast.LENGTH_SHORT).show();
            finish();
        })).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveAnswers(int questionID){

        final Map<String, Object> mAnswerMap = new HashMap<>();
        mAnswerMap.put("questionID", questionID);
        mAnswerMap.put("answer", answers.get(questionID-1));
        final Map<String, Object> mUserMap = new HashMap<>();
        mUserMap.put("uid", userID);
        mUserMap.put("join_date", System.currentTimeMillis());

        //Anket katilimcilari arasina mevcut kullaniciyi ekliyoruz.

        mRef.collection("Joiners").document(userID).set(mUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    // Cevaplari Kaydediyoruz
                    mRef.collection("Questions")
                            .document(String.valueOf(questionID))
                            .collection("Answers")
                            .document(userID)
                            .set(mAnswerMap).addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful()){
                            Log.d(TAG, "onComplete: Success!");
                        }
                    });
                }
            }
        });
    }

    private void getQuestions(boolean status){
        Query query = db.collection("Questionnaires").document(docID).collection("Questions").orderBy("docID", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Questions> response = new FirestoreRecyclerOptions.Builder<Questions>().setQuery(query, Questions.class).build();

        if(!status){

            adapter = new FirestoreRecyclerAdapter<Questions, mHolder>(response) {
                @Override
                public void onBindViewHolder(mHolder holder, int position, Questions model) {

                    String option3=model.getOption3();
                    String option4=model.getOption4();
                    String option5=model.getOption5();


                    //  Burada optional olarak istenen degerlerin default olup olmadigi kontrol ediliyor. EEger default ise gorunurlugunu false yapiyor.
                    if(option3.equals("default"))
                    {
                        holder.option3.setVisibility(View.GONE);
                    }
                    else {
                        holder.option3.setVisibility(View.VISIBLE);
                    }
                    if(option4.equals("default"))
                    {
                        holder.option4.setVisibility(View.GONE);
                    }
                    else {
                        holder.option4.setVisibility(View.VISIBLE);
                    }
                    if(option5.equals("default"))
                    {
                        holder.option5.setVisibility(View.GONE);
                    }
                    else {
                        holder.option5.setVisibility(View.VISIBLE);
                    }

                    holder.questionIDText.setText(model.getDocID() +". ");
                    holder.question.setText(model.getQuestion());


                    holder.option1.setText(model.getOption1());
                    holder.option2.setText(model.getOption2());
                    holder.option3.setText(model.getOption3());
                    holder.option4.setText(model.getOption4());
                    holder.option5.setText(model.getOption5());

                    //Soru sayisi kadar liste ogesi olusturuyor cevaplar default olarak tanimlaniyor.
                    answers.add("default");


                    holder.option1.setOnClickListener(view -> {

                        holder.option1.setBackgroundColor(purpleColor);
                        holder.option1.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);

                        //burada answers listemizdeki secili index e deger atamasi yapiyor.

                        answers.set(Integer.valueOf(model.getDocID())-1, "option1");

                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();

                    });
                    holder.option2.setOnClickListener(view -> {
                        holder.option2.setBackgroundColor(purpleColor);
                        holder.option2.setTextColor(Color.WHITE);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);
                        //saveAnswers(model.getDocID(), "option2");
                        answers.set(Integer.valueOf(model.getDocID())-1, "option2");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();
                    });
                    holder.option3.setOnClickListener(view -> {
                        holder.option3.setBackgroundColor(purpleColor);
                        holder.option3.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);
                        answers.set(Integer.valueOf(model.getDocID())-1, "option3");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();
                    });
                    holder.option4.setOnClickListener(view -> {
                        holder.option4.setBackgroundColor(purpleColor);
                        holder.option4.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);
                        answers.set(Integer.valueOf(model.getDocID())-1, "option4");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();

                    });
                    holder.option5.setOnClickListener(view -> {
                        holder.option5.setBackgroundColor(purpleColor);
                        holder.option5.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        answers.set(Integer.valueOf(model.getDocID())-1, "option5");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();
                    });

                    //Burada var olan soru sayimizi aliyoruz.
                    questionCount++;
                }

                @Override
                public mHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.single_item_layout, group, false);

                    return new mHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };

            adapter.notifyDataSetChanged();
            adapter.stopListening();
            recyclerView.setAdapter(adapter);
        }
        else {
            adapter = new FirestoreRecyclerAdapter<Questions, mHolder>(response) {
                @Override
                public void onBindViewHolder(mHolder holder, int position, Questions model) {

                    String option3=model.getOption3();
                    String option4=model.getOption4();
                    String option5=model.getOption5();



                    //  Burada optional olarak istenen degerlerin default olup olmadigi kontrol ediliyor. EEger default ise gorunurlugunu false yapiyor.
                    if(option3.equals("default"))
                    {
                        holder.option3.setVisibility(View.GONE);
                    }
                    else {
                        holder.option3.setVisibility(View.VISIBLE);
                    }
                    if(option4.equals("default"))
                    {
                        holder.option4.setVisibility(View.GONE);
                    }
                    else {
                        holder.option4.setVisibility(View.VISIBLE);
                    }
                    if(option5.equals("default"))
                    {
                        holder.option5.setVisibility(View.GONE);
                    }
                    else {
                        holder.option5.setVisibility(View.VISIBLE);
                    }

                    holder.questionIDText.setText(model.getDocID() +". ");
                    holder.question.setText(model.getQuestion());


                    ////////////////////////////////// Bu kisim anket seceneklerinin oransal degerini veriyor veriyor //////////////////////////

                    //oncelikle ankete katilan kisi sayisini aliyoruz
                    mRef.collection("Joiners").get().addOnSuccessListener(queryDocumentSnapshots -> {
                        joinerCount = queryDocumentSnapshots.size();

                        if(joinerCount==0)
                            ///KATILIMCI OLMADIGINDA HATA 0'a bolme istegi gittiginde crashlememesi icin
                            joinerCount =  1;



                        //her soru icin Answers collection'da answer=option1 iceren answerlarin sayisini aliyoruz.
                        mRef.collection("Questions")
                                .document(String.valueOf(model.getDocID()))
                                .collection("Answers")
                                .whereEqualTo("answer","option1")
                                .get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                            option1Count = queryDocumentSnapshots2.size();

                            //Bu kisimda yuzdelik hesaplama yapiyoruz.
                            holder.option1.setText(model.getOption1() + " %"+String.valueOf(((option1Count*100)/joinerCount)));

                        });
                        mRef.collection("Questions")
                                .document(String.valueOf(model.getDocID()))
                                .collection("Answers")
                                .whereEqualTo("answer","option2")
                                .get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                            option2Count = queryDocumentSnapshots2.size();
                            holder.option2.setText(model.getOption2() + " %"+String.valueOf(((option2Count*100)/joinerCount)));

                        });
                        mRef.collection("Questions")
                                .document(String.valueOf(model.getDocID()))
                                .collection("Answers")
                                .whereEqualTo("answer","option3")
                                .get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                            option3Count = queryDocumentSnapshots2.size();
                            //Toast.makeText(context,"3-> "+ String.valueOf(option3Count), Toast.LENGTH_SHORT).show();
                            holder.option3.setText(option3 + " %"+String.valueOf(((option3Count*100)/joinerCount)));


                        });
                        mRef.collection("Questions")
                                .document(String.valueOf(model.getDocID()))
                                .collection("Answers")
                                .whereEqualTo("answer","option4")
                                .get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                            option4Count = queryDocumentSnapshots2.size();
                            holder.option4.setText(option4 + " %"+String.valueOf(((option4Count*100)/joinerCount)));

                        });
                        mRef.collection("Questions")
                                .document(String.valueOf(model.getDocID()))
                                .collection("Answers")
                                .whereEqualTo("answer","option5")
                                .get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                            option5Count = queryDocumentSnapshots2.size();
                            holder.option5.setText(model.getOption5() + " %"+String.valueOf(((option5Count*100)/joinerCount)));
                        });



                    });

                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



                    //Soru sayisi kadar liste ogesi olusturuyor cevaplar default olarak tanimlaniyor.
                    answers.add("default");

                    holder.option1.setOnClickListener(view -> {
                        holder.option1.setBackgroundColor(purpleColor);
                        holder.option1.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);

                        //burada answers listemizdeki secili index e deger atamasi yapiyor.
                        answers.set(Integer.valueOf(model.getDocID())-1, "option1");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();

                    });
                    holder.option2.setOnClickListener(view -> {
                        holder.option2.setBackgroundColor(purpleColor);
                        holder.option2.setTextColor(Color.WHITE);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);
                        //saveAnswers(model.getDocID(), "option2");
                        answers.set(Integer.valueOf(model.getDocID())-1, "option2");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();
                    });
                    holder.option3.setOnClickListener(view -> {
                        holder.option3.setBackgroundColor(purpleColor);
                        holder.option3.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);
                        answers.set(Integer.valueOf(model.getDocID())-1, "option3");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();
                    });
                    holder.option4.setOnClickListener(view -> {
                        holder.option4.setBackgroundColor(purpleColor);
                        holder.option4.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option5.setBackgroundColor(Color.WHITE);
                        holder.option5.setTextColor(Color.BLACK);
                        answers.set(Integer.valueOf(model.getDocID())-1, "option4");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();

                    });
                    holder.option5.setOnClickListener(view -> {
                        holder.option5.setBackgroundColor(purpleColor);
                        holder.option5.setTextColor(Color.WHITE);
                        holder.option2.setBackgroundColor(Color.WHITE);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundColor(Color.WHITE);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundColor(Color.WHITE);
                        holder.option4.setTextColor(Color.BLACK);
                        holder.option1.setBackgroundColor(Color.WHITE);
                        holder.option1.setTextColor(Color.BLACK);
                        answers.set(Integer.valueOf(model.getDocID())-1, "option5");
                        //Toast.makeText(context, answers.toString(), Toast.LENGTH_SHORT).show();
                    });

                    //Burada var olan soru sayimizi aliyoruz.
                    questionCount++;
                }

                @Override
                public mHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.single_item_layout, group, false);

                    return new mHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };
            adapter.notifyDataSetChanged();
            adapter.stopListening();
            recyclerView.setAdapter(adapter);
        }

        adapter.startListening();

    }

    private void getInfos(){
        mRef.get().addOnSuccessListener(documentSnapshot -> {

            String title = documentSnapshot.getString("title");
            String explain = documentSnapshot.getString("explain");
            long date = documentSnapshot.getLong("deadline");
            isExpired = date <= System.currentTimeMillis();
            if(isExpired){
                mSendAnswers.setVisibility(View.GONE);
            }
            getQuestions(isExpired);
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String convertedDate = formatter.format(date);
            String publisher = documentSnapshot.getString("publisher");


            mUserRef.get().addOnSuccessListener(documentSnapshot1 -> {
                boolean isAdmin = documentSnapshot1.getBoolean("userRole");
                if(isAdmin){
                    mDelete.setVisibility(View.VISIBLE);
                }
            });

            if(userID.equals(publisher)){
                mAdd.setVisibility(View.VISIBLE);
                mDelete.setVisibility(View.VISIBLE);
            }

            mTitle.setText(title);
            mExplain.setText(explain);
            mDate.setText(convertedDate);


        }).addOnFailureListener(e -> {
            Toast.makeText(this, "ERROR: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        //EGER KATILIMCI ISE SEND ANSWER BUTONUNU INVISIABLE YAPIYORUZ.

        mRef.collection("Joiners").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                mSendAnswers.setVisibility(View.GONE);
            }
        });

        //  EGER ANKET SAHIBI ZATEN SORU EKLEMISSE EKLEME BUTONUNU KALDIRIYORUZ
        mRef.collection("Questions").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.size()>0){
                mAdd.setVisibility(View.GONE);
            }
        });
    }

    public class mHolder extends RecyclerView.ViewHolder {

        TextView question, option1,option2,option3,option4,option5, questionIDText;

        public mHolder(View itemView) {
            super(itemView);
            //
            question = itemView.findViewById(R.id.title);
            option1 = itemView.findViewById(R.id.option1);
            option2 = itemView.findViewById(R.id.option2);
            option3 = itemView.findViewById(R.id.option3);
            option4 = itemView.findViewById(R.id.option4);
            option5 = itemView.findViewById(R.id.option5);
            questionIDText = itemView.findViewById(R.id.question_id);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getInfos();
    }
}
