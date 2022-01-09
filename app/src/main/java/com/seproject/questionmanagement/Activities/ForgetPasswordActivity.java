package com.seproject.questionmanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.seproject.questionmanagement.R;

public class ForgetPasswordActivity extends AppCompatActivity {

        private EditText emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        emailText = findViewById(R.id.forgetPasswordEmail);
    }


    public void forgetPasswordSendMailClicked(View view) {

        String email = emailText.getText().toString().trim();

        if (email.isEmpty()){
            Toast.makeText(ForgetPasswordActivity.this, "Please enter your e-mail address.", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ForgetPasswordActivity.this, "E-mail has been sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}