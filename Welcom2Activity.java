package com.example.sagar.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Welcom2Activity extends AppCompatActivity {
    Button mAlreadyButton;
    Button mNewUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom2);

        mAlreadyButton=findViewById(R.id.already_have_account);
        mNewUserButton=findViewById(R.id.new_user_click);

        mAlreadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginIntent);

            }
        });

        mNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerIntent);

            }
        });
    }
}
