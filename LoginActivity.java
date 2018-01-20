package com.example.sagar.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Toolbar mToolbar;
    private EditText mLoginEmailEt;
    private EditText mLoginPassEt;
    Button mLoginBtn;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        mToolbar=findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressDialog=new ProgressDialog(this);

        mLoginEmailEt=findViewById(R.id.login_email_et);
        mLoginPassEt=findViewById(R.id.login_password_et);
        mLoginBtn=findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginEmail=mLoginEmailEt.getText().toString().trim();
                String loginPass=mLoginPassEt.getText().toString().trim();
                loginUser(loginEmail,loginPass);
            }
        });
    }

    private void loginUser(String loginEmail, String loginPass) {

        if(TextUtils.isEmpty(loginEmail)){
            Toast.makeText(LoginActivity.this, "Please Enter Email Address...", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(loginPass)){

            Toast.makeText(LoginActivity.this, "Please Enter Password Address...", Toast.LENGTH_SHORT).show();


        }
        else{
            mProgressDialog.setTitle("Login...");
            mProgressDialog.setMessage("Please Wait for User Authentication");
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                    else{

                        Toast.makeText(LoginActivity.this, "Please Enter valid Email and Password...", Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();

                }
            });

        }
    }
}
