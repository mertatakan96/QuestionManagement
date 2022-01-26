package com.seproject.questionmanagement.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.seproject.questionmanagement.R;

public class SendRequestActivity extends AppCompatActivity {


    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mRef;
    private FirebaseUser user;

    EditText tcInput;
    ImageView mBack;
    Button mSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(view -> onBackPressed());
        tcInput = findViewById(R.id.edt_text);
        mSend =findViewById(R.id.button_send);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mSend.setOnClickListener(view -> {
            String tcno = tcInput.getText().toString();
            int length = tcno.length();

            if(length != 11){
                tcInput.setError("Please enter a valid TC!");
                tcInput.requestFocus();
                return;
            }

            DocumentReference documentReference1 = db.collection("users").document(user.getUid());
            documentReference1.update("tcno", tcno).addOnSuccessListener(unused -> documentReference1.update("activeStatus", "1").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(SendRequestActivity.this, "Your approval has been sent!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }));
        });
    }
}