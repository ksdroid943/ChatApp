package com.example.sagar.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    Toolbar mToolbar;
    private EditText mregistername;
    private EditText mregisteremail;
    private EditText mregisterpass;
    private Button mregisterbtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=findViewById(R.id.registration_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mregistername=findViewById(R.id.register_name_et);
        mregisteremail=findViewById(R.id.register_email_et);
        mregisterpass=findViewById(R.id.register_password_et);
        mregisterbtn=findViewById(R.id.register_btn);

        mProgressDialog=new ProgressDialog(this);
        mregisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mregistername.getText().toString().trim();
                String email=mregisteremail.getText().toString().trim();
                String pass=mregisterpass.getText().toString().trim();

                createAccount(name,email,pass);

            }
        });

    }

    private void createAccount(final String name, String email, String pass) {

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        else{
            mProgressDialog.setTitle("Creating Account...");
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String user_uid= mAuth.getCurrentUser().getUid();
                               databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid);
                               databaseReference.child("user_name").setValue(name);
                               databaseReference.child("user_status").setValue("Hey I am using Chat App...");
                               databaseReference.child("user_image").setValue("default_pic");
                               databaseReference.child("user_thumb_image").setValue("default_image")
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                   startActivity(intent);

                                               }

                                           }
                                       });


                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Something Wrong.Please Try Again Later..."+task.getException(), Toast.LENGTH_SHORT).show();

                            }

                            mProgressDialog.dismiss();
                        }
                    });

        }
    }
}
