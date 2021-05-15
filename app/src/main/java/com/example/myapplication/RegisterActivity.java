package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText etusername, etphonenumber, etemail, etpassword;
    Button btnregister;
    TextView warningtext, tvlogin;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;


    public static final String regexemail = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
    public static final String regexname = "^[A-Za-z]\\w{3,29}$";
    public static final String regexphone = "^(\\d{3}[- .]?){2}\\d{4}$";

    private String sname, sphone, semail, spassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etusername = findViewById(R.id.etrusername);
        etphonenumber = findViewById(R.id.etrphonenumber);
        etemail = findViewById(R.id.etremail);
        etpassword = findViewById(R.id.etrpassword);

        btnregister = findViewById(R.id.btnrregister);

        warningtext = findViewById(R.id.warningtextreg);
        tvlogin = findViewById(R.id.logintv);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserRegister();
            }
        });

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navToLogin();
            }
        });
    }

    private void validateUserRegister(){
        sname = etusername.getText().toString();
        sphone = etphonenumber.getText().toString();
        semail = etemail.getText().toString();
        spassword = etpassword.getText().toString();

        Pattern pattern = Pattern.compile(regexname);
        Matcher matcher = pattern.matcher(sname);
        if(!matcher.matches()){
            warningtext.setText("Please enter valid username");
        }
        else{
            pattern = Pattern.compile(regexphone);
            matcher = pattern.matcher(sphone);
            if(!matcher.matches()){
                warningtext.setText("Please enter valid phone number");
            }
            else{
                pattern = Pattern.compile(regexemail);
                matcher = pattern.matcher(semail);
                if(!matcher.matches()){
                    warningtext.setText("Please enter valid email");
                }
                else{
                    if(spassword.isEmpty()||spassword.length()<8){
                        warningtext.setText("Please enter valid password:min length-8");
                    }
                    else{
                        warningtext.setText("");
                        createProfile(semail,spassword);
                        Toast.makeText(this, "Registration success!", Toast.LENGTH_SHORT).show();
//                        navToLogin();
                    }
                }
            }
        }
    }

    private void navToLogin() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void createProfile(String email, String password){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser currentUser) {
        String keyid = mDatabase.push().getKey();
        mDatabase.child(keyid).setValue(user); //adding user info to database
        Intent loginIntent = new Intent(this, HomeActivity.class);
        startActivity(loginIntent);
    }
}