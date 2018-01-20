package com.example.sagar.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.alluers_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("List All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.all_users_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AllUsersModel, AllUsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AllUsersModel, AllUsersViewHolder>
                        (
                                AllUsersModel.class,
                                R.layout.all_users_single_row,
                                AllUsersViewHolder.class,
                                databaseReference
                        ) {
                    @Override
                    protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsersModel model, final int position) {

                        viewHolder.setUser_name(model.getUser_name());
                        viewHolder.setUser_status(model.getUser_status());
                        viewHolder.setUser_thumb_image(getApplicationContext(), model.getUser_thumb_image());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id=getRef(position).getKey();
                                Intent intent=new Intent(AllUsersActivity.this,ProfileActivity.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                startActivity(intent);

                            }
                        });


                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUser_name(String user_name) {

            TextView uname = mView.findViewById(R.id.allusers_username);
            uname.setText(user_name);
        }

        public void setUser_status(String userStatus) {
            TextView ustatus = mView.findViewById(R.id.allusers_status);
            ustatus.setText(userStatus);

        }

        public void setUser_thumb_image(Context c, String user_thumb_image) {
            CircleImageView uimage = mView.findViewById(R.id.allusers_circleimage);
            Picasso.with(c).load(user_thumb_image).placeholder(R.drawable.default_pic).into(uimage);
        }

    }
}
