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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mStatusChange;
    private Button mSaveStatusBtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mAuth=FirebaseAuth.getInstance();
        String userid=mAuth.getCurrentUser().getUid().toString();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        mToolbar=findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);

        mSaveStatusBtn=findViewById(R.id.status_save_btn);
        mStatusChange=findViewById(R.id.status_change_et);
        mStatusChange.setText(getIntent().getExtras().get("user_status").toString());

        mSaveStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newStatus= mStatusChange.getText().toString().trim();
                changeStatus(newStatus);
            }
        });

    }

    private void changeStatus(String newStatus) {

        if(TextUtils.isEmpty(newStatus)){
            Toast.makeText(this, "Please Write Status...", Toast.LENGTH_SHORT).show();

        }
        else {
            progressDialog.setMessage("Please wait while your status is updating");
            progressDialog.show();
            databaseReference.child("user_status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(StatusActivity.this, SettingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(StatusActivity.this, "Error occured in updating status", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}
