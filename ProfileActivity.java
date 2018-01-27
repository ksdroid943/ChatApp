package com.example.sagar.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private Button mSendRequestBtn, mDeclineRequestBtn;
    private ImageView mProfileImage;
    private TextView mProfileUsernameTv, mProfileStatusTv;
    private DatabaseReference databaseReference;
    private String CURRENT_STATE;
    private DatabaseReference friendRequestRef;
    private FirebaseAuth mAuth;
    private String sender_user_id;
    private String receiver_user_id;
    private DatabaseReference friendRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friends_Requests");
        friendRequestRef.keepSynced(true);
        sender_user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendRef.keepSynced(true);

        mProfileImage = findViewById(R.id.profile_activity_image);
        mProfileUsernameTv = findViewById(R.id.profile_activity_uname_tv);
        mProfileStatusTv = findViewById(R.id.profile_activity_status_tv);
        mSendRequestBtn = findViewById(R.id.send_request_btn);
        mDeclineRequestBtn = findViewById(R.id.decline_request_btn);
        CURRENT_STATE = "not_friends";

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();

        databaseReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String profileUname = dataSnapshot.child("user_name").getValue().toString();
                String profileStatus = dataSnapshot.child("user_status").getValue().toString();
                String profileImage = dataSnapshot.child("user_image").getValue().toString();
                mProfileUsernameTv.setText(profileUname);
                mProfileStatusTv.setText(profileStatus);
                Picasso.with(ProfileActivity.this).load(profileImage).placeholder(R.drawable.default_pic).into(mProfileImage);

                friendRequestRef.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(receiver_user_id)) {
                                String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                if (req_type.equals("sent")) {
                                    CURRENT_STATE = "request_sent";
                                    mSendRequestBtn.setText("Cancle Friend Request");
                                } else if (req_type.equals("received")) {
                                    CURRENT_STATE = "request_received";
                                    mSendRequestBtn.setText("Accept Friend Request");
                                }
                            }

                        } else {
                            friendRef.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                        CURRENT_STATE = "friends";
                                        mSendRequestBtn.setText("Unfriend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       if(!sender_user_id.equals(receiver_user_id)){

           mSendRequestBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   mSendRequestBtn.setEnabled(false);

                   if (CURRENT_STATE.equals("not_friends")) {
                       sendFriendReqToPerson();
                   }
                   if (CURRENT_STATE.equals("request_sent")) {
                       cancleFriendReq();
                   }
                   if (CURRENT_STATE.equals("request_received")) {
                       acceptFrndReq();
                   }
                   if (CURRENT_STATE.equals("friends")) {
                       unFriend();
                   }
               }
           });
       }
       else{
           mSendRequestBtn.setVisibility(View.INVISIBLE);
           mDeclineRequestBtn.setVisibility(View.INVISIBLE);

       }


    }

    private void unFriend() {

        friendRef.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRef.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                mSendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                mSendRequestBtn.setText("Send Friend Request");
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void acceptFrndReq() {
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
//        final String saveCurrentDate = currentDate.format(calendar.getTime());
        final String saveCurrentDate= DateFormat.getDateTimeInstance().format(new Date());

        friendRef.child(sender_user_id).child(receiver_user_id).setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendRef.child(receiver_user_id).child(sender_user_id).setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        friendRequestRef.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    friendRequestRef.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            mSendRequestBtn.setEnabled(true);
                                                            CURRENT_STATE = "friends";
                                                            mSendRequestBtn.setText("Unfriend");
                                                        }
                                                    });
                                                }

                                            }
                                        });


                                    }
                                });

                    }
                });


    }

    private void cancleFriendReq() {

        friendRequestRef.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestRef.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mSendRequestBtn.setEnabled(true);
                            CURRENT_STATE = "not_friends";
                            mSendRequestBtn.setText("Send Friend Request");
                        }
                    });
                }

            }
        });
    }

    private void sendFriendReqToPerson() {

        friendRequestRef.child(sender_user_id).child(receiver_user_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            friendRequestRef.child(receiver_user_id).child(sender_user_id).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                mSendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                mSendRequestBtn.setText("Cancle Friend Request");
                                            }

                                        }
                                    });
                        }

                    }
                });


    }
}
