package com.seproject.questionmanagement.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seproject.questionmanagement.R;
import com.seproject.questionmanagement.databinding.ActivityRegisterBinding;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private RadioGroup radioGenderGroup;
    private RadioButton radioGenderButton;
    private EditText emailText, passwordText, passwordAgainText, usernameText;
    private ImageView profilePhoto;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private Uri imageData;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        radioGenderGroup = (RadioGroup) findViewById(R.id.radioGroupGender);
        emailText = findViewById(R.id.registerEmail);
        passwordText = findViewById(R.id.registerPassword);
        passwordAgainText = findViewById(R.id.registerPasswordAgain);
        usernameText = findViewById(R.id.registerUsername);
        profilePhoto = findViewById(R.id.registerUserPhoto);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        registerLauncher();
    }


    private void initDatePicker() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }

        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener , year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // block the future dates
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";
        //Default never happen
        return "JAN";
    }

    public void openDatePicker(View view) {

        datePickerDialog.show();
    }

    public void signUpClickedRegister(View view) {

        int selectedGenderId = radioGenderGroup.getCheckedRadioButtonId();
        radioGenderButton = (RadioButton) findViewById(selectedGenderId);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString().trim();
        String passwordAgain = passwordAgainText.getText().toString().trim();
        String username = usernameText.getText().toString();
        String birthdate = dateButton.getText().toString();
        String selectedGender = radioGenderButton.getText().toString();//null exception eklenebilir
        String userRole = "0"; //0:User, 1:ActiveUser, 2:Admin
        String activeStatus = "0"; // 0:dint apply 1:waiting approval


        if (TextUtils.isEmpty(email)){
            emailText.setError("E-mail is required!");
        }
        if (TextUtils.isEmpty(password)){
            passwordText.setError("Password is required!");
        }
        if (!password.equals(passwordAgain) || TextUtils.isEmpty(passwordAgain)){
            passwordAgainText.setError("Password are not match!");
        }
        if (TextUtils.isEmpty(username)){
            usernameText.setError("Username is required!");
        }
        if (TextUtils.isEmpty(birthdate)){
            dateButton.setError("Birthdate is required");
        }
        /*if (TextUtils.isEmpty(selectedGender)){
            radioGenderButton.setError("Gender is required!");
        }*/
        if (imageData == null){
            Toast.makeText(RegisterActivity.this, "Choose Profile Photo!", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    userID = firebaseAuth.getCurrentUser().getUid();
                    String imageName = "userPhotos/" + userID + ".jpg";
                    //if (imageData != null) {
                        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference photoReference = firebaseStorage.getReference(imageName);
                                photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("username", username);
                                        user.put("email", email);
                                        user.put("gender", selectedGender);
                                        user.put("birthdate", birthdate);
                                        user.put("photo", downloadUrl);
                                        user.put("userRole", userRole);
                                        user.put("activeStatus", activeStatus);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intentToHome = new Intent(RegisterActivity.this, HomePageActivity.class);
                                                intentToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intentToHome);
                                                finish();
                                                Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.getLocalizedMessage().toString());
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"onFailure: " + e.getLocalizedMessage().toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getLocalizedMessage().toString());
                            }
                        });
                    //}

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        System.out.println(email);
        System.out.println(password);
        System.out.println(passwordAgain);
        System.out.println(username);
        System.out.println(birthdate);
        System.out.println(selectedGender);
        System.out.println(imageData);


    }


    public void registerSelectPhoto(View view) {
        //Photo access permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                //ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        imageData = intentFromResult.getData();
                        profilePhoto.setImageURI(imageData);
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    Toast.makeText(RegisterActivity.this,"Permission Needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}