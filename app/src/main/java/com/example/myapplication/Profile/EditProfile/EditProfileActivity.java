package com.example.myapplication.Profile.EditProfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.Models.UserSettings;
import com.example.myapplication.R;
import com.example.myapplication.Utils.FirebaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity
        implements GenderFragment.OnInputListener,
        ConfirmFragment.OnConfirmPasswordListener {
    FirebaseMethods mFirebaseMethods;
    private EditText  mFullname, mUsername,mWebsite,mBio,mEmail,mPhoneNumber;
   public TextView mGender,mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    DatabaseReference myRef;
    DatabaseReference reference;
    FirebaseAuth auth;
     String userID;
     String inputPassword;
    Uri imageUri;
    String imageURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mProfilePhoto =  findViewById(R.id.profile_photo);
        mFirebaseMethods = new FirebaseMethods(EditProfileActivity.this);
        myRef=FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        userID=auth.getCurrentUser().getUid();
        mFullname=findViewById(R.id.name);
        mUsername=findViewById(R.id.username);
        mWebsite=findViewById(R.id.website);
        mBio=findViewById(R.id.bio);
        mEmail=findViewById(R.id.email);
        mPhoneNumber=findViewById(R.id.phone_number);
        mGender=findViewById(R.id.mgender);
        mChangeProfilePhoto=findViewById(R.id.changeProfilePhoto);
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  CropImage.activity().setAspectRatio(1,1).start(EditProfileActivity.this);

            }
        });
        ImageView image_close=findViewById(R.id.close);
        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView image_right=findViewById(R.id.right);
        image_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileSettings();
            finish();
            }
        });

        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GenderFragment genderFragment=new GenderFragment();
                genderFragment.show(getSupportFragmentManager(),"GenderFragment");
            }
        });
        getDataFromFirebase();

    }






    private void setProfileImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_user_account_settings))
                .child(userID);

       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               UserAccountSettings settings=new UserAccountSettings();
               settings = dataSnapshot.getValue(UserAccountSettings.class);
               Glide.with(getApplicationContext()).load(settings.getProfile_photo()).into(mProfilePhoto);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) { }
       });

//       UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
 }






    private void getDataFromFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot,userID));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }

    private void setProfileWidgets(UserSettings userSettings){

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
//        String imgURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IeLf";
//        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
        mFullname.setText(settings.getFullname());
         mUsername.setText(String.valueOf(settings.getUsername()));
         mWebsite.setText(String.valueOf(settings.getWebsite()));
        mGender.setText(String.valueOf(settings.getGender()));
        mBio.setText(String.valueOf(settings.getBio()));
        mEmail.setText(String.valueOf(user.getEmail()));
        mPhoneNumber.setText(String.valueOf(user.getPhone_number()));
        setProfileImage();
    }



    private void saveProfileSettings(){

        final String fullname = mFullname.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String bio = mBio.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
        final String gender = mGender.getText().toString();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = new User();
                UserAccountSettings userAccountSettings=new UserAccountSettings();
                for(DataSnapshot ds:  dataSnapshot.child(getString(R.string.dbname_users)).getChildren()) {
                    if (ds.getKey().equals(userID)) {
                        user.setUsername(ds.getValue(User.class).getUsername());
                        user.setEmail(ds.getValue(User.class).getEmail());
                        user.setPhone_number(ds.getValue(User.class).getPhone_number());
                    }

                }
                for(DataSnapshot ds:  dataSnapshot.child(getString(R.string.dbname_user_account_settings)).getChildren()) {
                    if (ds.getKey().equals(userID)) {
                        userAccountSettings.setUsername(ds.getValue(UserAccountSettings.class).getUsername());
                        userAccountSettings.setGender(ds.getValue(UserAccountSettings.class).getGender());
                        userAccountSettings.setBio(ds.getValue(UserAccountSettings.class).getBio());
                        userAccountSettings.setFullname(ds.getValue(UserAccountSettings.class).getFullname());
                        userAccountSettings.setProfile_photo(ds.getValue(UserAccountSettings.class).getProfile_photo());
                        userAccountSettings.setWebsite(ds.getValue(UserAccountSettings.class).getWebsite());
                        userAccountSettings.setProfile_photo(ds.getValue(UserAccountSettings.class).getProfile_photo());
                    }

                }
                // the user changed their username therefore we need to check for uniqueness
                if(!user.getUsername().equals(username)){
               checkUsernameIfExist(username);
                }
                // if the user changed their email
                if(!user.getEmail().equals(email)){

                    // step1) Reauthenticate
                    //          -Confirm the password and email
                    ConfirmFragment dialog = new ConfirmFragment();
                    dialog.show(getSupportFragmentManager(),"ConfirmFragment");

                    // step2) check if the email already is registered
                    //          -'fetchProvidersForEmail(String email)'
                    // step3) change the email
                    //          -submit the new email to the database and authentication
                }
                if(user.getPhone_number()!=phoneNumber){
                    mFirebaseMethods.updatePhoneNumber(phoneNumber);
                }
                if(!userAccountSettings.getGender().equals(gender)){
                    mFirebaseMethods.updateGender(gender);
                }
                if(!userAccountSettings.getFullname().equals(fullname)){
                    mFirebaseMethods.updateFullname(fullname);
                }
                if(!userAccountSettings.getWebsite().equals(website)){
                    mFirebaseMethods.updateWebsite(website);
                }
                if(!userAccountSettings.getBio().equals(bio)){
                    mFirebaseMethods.updateBio(bio);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFirebaseMethods.uploadImage(EditProfileActivity.this,imageUri);


  }

private void checkUsernameIfExist(final String username){


    Query query=reference.
            child("users")
            .orderByChild("username")
            .equalTo(username);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Toast.makeText(EditProfileActivity.this, "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }


public  void changeEmail(final String email){
    // Get auth credentials from the user for re-authentication. The example below shows
    // email and password credentials but there are multiple possible providers,
    // such as GoogleAuthProvider or FacebookAuthProvider.
    AuthCredential credential = EmailAuthProvider
        .getCredential(auth.getCurrentUser().getEmail(), inputPassword);

///////////////////// Prompt the user to re-provide their sign-in credentials
auth.getCurrentUser().reauthenticate(credential)
    .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                ///////////////////////check to see if the email is not already present in the database
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if(task.isSuccessful()){
                        try{
                            if(task.getResult().getSignInMethods().size() == 1){
                                Toast.makeText(EditProfileActivity.this, "That email is already in use", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //////////////////////the email is available so update it
                                auth.getCurrentUser().updateEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EditProfileActivity.this, "email updated", Toast.LENGTH_SHORT).show();
                                                mFirebaseMethods.updateEmail(email);
                                            }
                                        }
                                        });
                            }
                        }catch (NullPointerException e){
                        }
                    }
                }
            });
            }
        }
    });
    }
    @Override
    public void sendGender(String gender) {
        mGender.setText(gender);
    }

    @Override
    public void onConfirmPassword(String password) {
    inputPassword=password;
    changeEmail(mEmail.getText().toString());
    }

//
//    private  String getFileExtention(Uri uri){
//        ContentResolver contentResolver=getContentResolver();
//        MimeTypeMap mime=MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri=result.getUri();
            mProfilePhoto.setImageURI(imageUri);
//            mFirebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), "", 0, imageUrl.toString());


        }
        else
        {
            Toast.makeText(EditProfileActivity.this, "Something is gone wrong!", Toast.LENGTH_SHORT).show();

        }


    }

}