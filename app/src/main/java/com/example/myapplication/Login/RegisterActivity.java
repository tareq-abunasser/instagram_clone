package com.example.myapplication.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.R;
import com.example.myapplication.Utils.FirebaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText  fullname , email , password ;
    Button register ;
    TextView txt_login;
   ProgressDialog pd;
    String str_fullname,str_email,str_password,str_username;
    private String append = "";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
     DatabaseReference reference;
    FirebaseUser firebaseuser;
    FirebaseAuth auth;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        txt_login=findViewById(R.id.txt_login);
        auth= FirebaseAuth.getInstance();
      Context mContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();
                 str_fullname=fullname.getText().toString();
                 str_email=email.getText().toString();
                 str_password=password.getText().toString();
                str_username= str_fullname.toLowerCase().replaceAll("\\s","");
                if(TextUtils.isEmpty(str_fullname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password))
                    Toast.makeText(RegisterActivity.this, "All fileds are required!", Toast.LENGTH_SHORT).show();
                else if(str_password.length()<6) {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(str_fullname,str_email,str_password);
                }
            }
        });}



    private void register(final String fullname, final String email,final String password){

        auth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseuser=auth.getCurrentUser();
                             userid=firebaseuser.getUid();
                             reference = FirebaseDatabase.getInstance().getReference();
                           String username= checkIfUsernameExist(str_username);
                            sendVerification();


                            User user =    new User(userid,1,email,username);
                            reference.child("users").child(userid).setValue(user)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(RegisterActivity.this, " something wrong !!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                           });


                            UserAccountSettings userAccountSettings=new UserAccountSettings("gaza",fullname,
                                    0,0,0,
                                    "gs://instagram-clone-b2e34.appspot.com/images.png",
                                    username,
                                    "https://facebook.com","Prefer Not to Say",userid);
                            reference.child("user_account_settings").child(userid).setValue(userAccountSettings)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        Toast.makeText(RegisterActivity.this, "You should verified your email", Toast.LENGTH_SHORT).show();
//                                    Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                    finish();
                                    }
                                    else {
                                        pd.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Something wrong !!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with these email or password", Toast.LENGTH_SHORT).show();
                        }}
                });

    }





    private void sendVerification(){
        FirebaseUser firebaseuser=auth.getCurrentUser();
        firebaseuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    auth.signOut();
                    Toast.makeText(RegisterActivity.this, "You should verified your email", Toast.LENGTH_LONG).show();
                    Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();}
                else {
                    Toast.makeText(RegisterActivity.this, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    auth.signOut();
                }
            }});
    }


private String checkIfUsernameExist(final String username){
    Query query=reference.
            child("users")
            .orderByChild("username")
            .equalTo(username);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot singleSnapShot:dataSnapshot.getChildren()){
                if(singleSnapShot.exists()){
                    append=reference.push().getKey().substring(3,8);
                }
            }

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    return username.concat(append) ;
}
}