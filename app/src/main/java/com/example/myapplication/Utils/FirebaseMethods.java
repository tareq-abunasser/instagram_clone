package com.example.myapplication.Utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.Photo;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.Models.UserSettings;
import com.example.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;
    public static String userN;
    private Context mContext;
    private StorageReference mStorageReference;
    private double mPhotoUploadProgress = 0;
    private String newPhotoKey;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;
        mStorageReference = FirebaseStorage.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
        newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
    }

    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot) {
        User user = new User();
        for (DataSnapshot ds : datasnapshot.child(userID).getChildren()) {
            user.setUsername(ds.getValue(User.class).getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Register a new email and password to Firebase Authentication
     *
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username, final ProgressDialog pd) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        } else if (task.isSuccessful()) {
                            userID = mAuth.getCurrentUser().getUid();
                            pd.dismiss();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }


    public UserSettings getUserSettings(DataSnapshot dataSnapshot, String userID) {

       // String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            // user_account_settings node
            if (ds.getKey().equals("user_account_settings")) {
                try {

                    settings.setFullname(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFullname()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    settings.setBio(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getBio()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );


                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );
                    settings.setGender(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getGender());
                    settings.setUserId(
                            ds.child(userID)
                            .getValue(UserAccountSettings.class)
                            .getUserId());


                } catch (NullPointerException e) {
                }
            }

            // users node
            if (ds.getKey().equals("users")) {

                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );

                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()
                );
                userN = user.getUsername();
            }
        }

        return new UserSettings(user, settings);

    }


    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: upadting username to: " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child("username")
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child("username")
                .setValue(username);
    }

    public void updatePhoneNumber(long phoneNumber) {
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child("phone_number")
                .setValue(phoneNumber);
    }

    public void updateGender(String gender) {
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child("gender")
                .setValue(gender);
    }

    public void updateFullname(String fullname) {
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child("fullname")
                .setValue(fullname);
    }

    public void updateWebsite(String website) {
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child("website")
                .setValue(website);
    }

    public void updateBio(String bio) {
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child("bio")
                .setValue(bio);
    }


    public void updateEmail(String email) {
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child("email")
                .setValue(email);

    }
//    public void updateFollowing(String s) {
//        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
//                .child(userID)
//                .child("following")
//                .setValue(gender);
//    }
//    public void updateFollowers(String id,String s) {
//        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
//                .child(userID)
//                .child("following")
//                .setValue(gender);
//    }


    public long getPostCount(DataSnapshot dataSnapshot) {
        long count = 0;
        UserAccountSettings settings = new UserAccountSettings();
        settings.setPosts(dataSnapshot.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .getValue(UserAccountSettings.class)
                .getPosts());
        count = settings.getPosts();
        return count;

    }
    public long getFollowingCount(DataSnapshot dataSnapshot,String ui) {
        long count = 0;
        UserAccountSettings settings = new UserAccountSettings();
        settings.setPosts(dataSnapshot.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(ui)
                .getValue(UserAccountSettings.class)
                .getFollowing());
        count = settings.getFollowing();
        return count;

    }
    public long getFollowersCount(DataSnapshot dataSnapshot,String ui) {
        long count = 0;
        UserAccountSettings settings = new UserAccountSettings();
        settings.setPosts(dataSnapshot.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(ui)
                .getValue(UserAccountSettings.class)
                .getFollowers());
        count = settings.getFollowers();
        return count;

    }


    public void uploadNewPhoto(String photoType, final String caption, final long count, final String imgUrl,Context ct) {
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            StorageReference storageReference = mStorageReference.child(FilePaths.FIREBASE_IMAGE_STORAGE + "/" + userID + "/photo" + (count + 1));
            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return storageReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    final String myURI = downloadUri.toString();
                    addPhotoToDatabase(caption, myURI);
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(mContext.getString(R.string.dbname_user_account_settings))
                            .child(userID)
                            .child("posts")
                            .setValue(count + 1);
                    Intent intent= new Intent(ct, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    ct.startActivity(intent);
                    // ct.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(ct, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    ct.startActivity(intent);
                    // ct.finish();
                }
            });
           }


    }



    private String getTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Palestine/Gaza"));
        return simpleDateFormat.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");
        String tags = StringManipulation.getTags(caption);
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(userID);
        photo.setPhoto_id(newPhotoKey);
        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(userID).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    public void uploadImage(final Context context, Uri imageUri) {


        if (imageUri != null) {
            StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference storageReference = mStorageReference.child(FilePaths.FIREBASE_IMAGE_STORAGE + "/" + userID + "/profile_photo");
            UploadTask uploadTask = storageReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return storageReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        final String myURI = downloadUri.toString();

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(userID)
                                .child("profile_photo")
                                .setValue(myURI);
                    } else
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(context, "No image Selected!", Toast.LENGTH_SHORT).show();
    }
        }

//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
//                    myRef.child(mContext.getString(R.string.dbname_user_account_settings))
//                            .child(userID)
//                            .child("profile_photo")
//                            .setValue(firebaseUrl.toString());
//
//                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
//                    //add the new photo to 'photos' node and 'user_photos' node
//
//                    //navigate to the main feed so the user can see their photo
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG, "onFailure: Photo upload failed.");
//                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
//                }
//            });
