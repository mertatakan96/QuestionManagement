package com.seproject.questionmanagement.CreateScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;
import com.seproject.questionmanagement.QuestionnaireScreens.QuestionnaireProfile;
import com.seproject.questionmanagement.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateTemplate extends AppCompatActivity 
{

    private EditText mTitle, mExplain;
    private Button mDateButton;
    private ImageView mBack;
    private ImageView mCreate;
    private Date mDate;
    private Context context = this;

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_template);

        //Elements
        mTitle  = findViewById(R.id.main_title);
        mExplain = findViewById(R.id.main_explain);
        mDateButton = findViewById(R.id.main_date);
        mCreate = findViewById(R.id.main_create);
        mBack = findViewById(R.id.back);

        //Firebase
        mRef = db.collection("Questionnaires");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //Clicks
        mBack.setOnClickListener(view -> {
            onBackPressed();
        });
        mDateButton.setOnClickListener(view ->{
            showDateTimeDialog(mDateButton);
        });
        mCreate.setOnClickListener(view -> {
            if(mDate.getTime()<System.currentTimeMillis()){
                Toast.makeText(context, "Please enter a future date", Toast.LENGTH_SHORT).show();
                mDateButton.setError("Please enter a future date!");
            }else {
                save();
            }
        });

    }

    private void save(){
        String title, explain;
        title  = mTitle.getText().toString();
        explain = mExplain.getText().toString();

        if(TextUtils.isEmpty(title)){
            mTitle.setError("Please enter a title!");
            mTitle.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(explain)){
            mExplain.setError("Please enter a explain!");
            mExplain.requestFocus();
            return;
        }
        if(mDate==null){
            mDateButton.setError("Please select date");
            Toast.makeText(context, "Please select end date!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Map<String, Object> mMap = new HashMap<>();

        mMap.put("publisher", user.getUid());


        mMap.put("title", title);
        mMap.put("explain", explain);
        mMap.put("deadline", mDate.getTime());
        mMap.put("created_time", System.currentTimeMillis());


        mRef.add(mMap).addOnSuccessListener(documentReference -> {

            String docID =documentReference.getId();
            final Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("docID",docID );
            mRef.document(docID).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                    Intent  intent = new Intent(context, QuestionnaireProfile.class);
                    intent.putExtra("docID", documentReference.getId());
                    startActivity(intent);
                    finish();
                }
            });



        });




    }


    private void showDateTimeDialog(final Button date_time_in) {
        final Calendar calendar=Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {

            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener= (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                date_time_in.setText(simpleDateFormat.format(calendar.getTime()));

                mDate = calendar.getTime();




            };
          new TimePickerDialog(context,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();

        };

        DatePickerDialog datePickerDialog =  new DatePickerDialog(context,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }


}