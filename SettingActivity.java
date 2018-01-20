package com.example.sagar.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private TextView mUserName;
    private TextView mUserStatus;
    private Button mChangeImagebtn;
    private Button mChangeStatusbtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    public final static int GALLERY_CODE = 10;
    private StorageReference mStorageRef;
    Bitmap thumb_bitmap=null;
    private StorageReference thumb_image_ref;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        progressDialog=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        thumb_image_ref=FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        circleImageView = findViewById(R.id.circleImageView);
        mUserName = findViewById(R.id.username);
        mUserStatus = findViewById(R.id.profile_status);
        mChangeImagebtn = findViewById(R.id.change_image_btn);
        mChangeStatusbtn = findViewById(R.id.change_status_btn);
        mChangeStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, StatusActivity.class);
                intent.putExtra("user_status", mUserStatus.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();
                String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                mUserName.setText(name);
                mUserStatus.setText(status);
                if (!image.equals("default_pic")) {
                    Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.default_pic).into(circleImageView);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setMessage("Updataing Profile Image");
                progressDialog.setMessage("Please wait while we are updating profile image...");
                progressDialog.show();
                Uri resultUri = result.getUri();

                //for compression of image------------------
                File thumb_filePathUri=new File(resultUri.getPath());

                String uidd = mAuth.getCurrentUser().getUid();
                try{
                    thumb_bitmap=new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);

                }
                catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] thumb_byte=byteArrayOutputStream.toByteArray();

                StorageReference filePath = mStorageRef.child(uidd + ".jpg");

                final StorageReference thumb_filePath=thumb_image_ref.child(uidd+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingActivity.this, "Change Image Successfully...", Toast.LENGTH_SHORT).show();


                           final String downloadurl = task.getResult().getDownloadUrl().toString();
                           UploadTask uploadTask=thumb_filePath.putBytes(thumb_byte);
                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                   String thumb_download_url=thumb_task.getResult().getDownloadUrl().toString();

                                   if(task.isSuccessful()){

                                       Map update_user_data= new HashMap();
                                       update_user_data.put("user_image",downloadurl);
                                       update_user_data.put("user_thumb_image",thumb_download_url);


                                       databaseReference.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               Toast.makeText(SettingActivity.this, "Profile Image Change Successfully.", Toast.LENGTH_SHORT).show();
                                               progressDialog.dismiss();
                                           }
                                       });
                                   }

                               }
                           });




                        } else {
                            Toast.makeText(SettingActivity.this, "Something Wrong in Uploading...", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
