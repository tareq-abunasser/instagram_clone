package com.example.myapplication.Profile;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Home.CommentsActivity;
import com.example.myapplication.Models.Like;
import com.example.myapplication.Models.Photo;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Heart;
import com.example.myapplication.Utils.SquareImageView;
import com.example.myapplication.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



public class PostViewFragment extends Fragment {
    //Widget
    ImageView mProfilePhoto, mHeartRed, mHeartWhite,writeComment;
    SquareImageView mPostImage;
    TextView mImageCaption;
    TextView mTimestamp;
    ImageView imageBack;
    TextView mLike;
    int numComment;
    // Var
    String userID;
    Photo photo;
    String mUsername;
    StringBuilder mUsers;
    ArrayList<String> list;
    String[] splitUsers;

    private UserAccountSettings mUserAccountSettings;
    private Boolean mLikedByCurrentUser;
    private String mLikesString = "";
    private Heart mHeart;
    private String ProfilePhoto;
    private Context mContext;
    // firebase
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    TextView mComments;
    public PostViewFragment(Photo photo){
        this.photo=photo;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mContext=getActivity();
        View view = inflater.inflate(R.layout.activity_post_view, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
//        photo = getIntent().getExtras().getParcelable("photo");
//        numComment = getIntent().getExtras().getInt("numComment");

        userID = photo.getUser_id();
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mPostImage = view.findViewById(R.id.post_image);
        mHeartRed = view.findViewById(R.id.image_heart_red);
        mHeartWhite = view.findViewById(R.id.image_heart);
        mTimestamp = view.findViewById(R.id.image_time_posted);
        imageBack = view.findViewById(R.id.back);
        mLike = view.findViewById(R.id.image_likes);
        mImageCaption = view.findViewById(R.id.image_caption);
        writeComment = view.findViewById(R.id.speech_bubble);
        mComments = view.findViewById(R.id.image_comments_link);

        setProfileImage();
        setPost();

        mHeartRed.setVisibility(View.GONE);
        mHeartWhite.setVisibility(View.VISIBLE);
        mHeart = new Heart(mHeartWhite, mHeartRed);

        getLikesString();
        setupWidgets();
        testToggle();


        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getActivity().finish();
            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("photo", photo);
                intent.putExtra("profilePhoto",ProfilePhoto);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            }
        );
        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("photo", photo);
                intent.putExtra("profilePhoto",ProfilePhoto);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    return  view;
    }
    private void setProfileImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_user_account_settings))
                .child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings settings = new UserAccountSettings();
                settings = dataSnapshot.getValue(UserAccountSettings.class);
                UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
                //Glide.with(mContext).load(settings.getProfile_photo()).into(mProfilePhoto);
                ProfilePhoto=settings.getProfile_photo();
                mUsername = settings.getUsername();
                mImageCaption.setText(mUsername + " " + photo.getCaption());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setPost() {
        UniversalImageLoader.setImage(photo.getImage_path(), mPostImage, null, "");
//       Glide.with(mContext).load(photo.getImage_path()).into(mPostImage);
    }

    private String getTimestampDifference() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Palestine/Gaza"));
        String postTimeStamp = photo.getDate_created();
        Date today = calendar.getTime();
        sdf.format(today);
        Date timeStamp;
        String differences = "";
        try {
            timeStamp = sdf.parse(postTimeStamp);
            differences = String.valueOf(Math.round((today.getTime() - timeStamp.getTime()) / 1000 / 60 / 60 / 24));
        } catch (ParseException e) {
            differences = "0";
        }
        return differences;
    }

    private void setupWidgets() {
        String timestampDiff = getTimestampDifference();
        if (!timestampDiff.equals("0")) {
            mTimestamp.setText(timestampDiff + " DAYS AGO");
        } else {
            mTimestamp.setText("TODAY");
        }

        if(numComment > 0){
            mComments.setText("View all " + (numComment) + " comments");
        }else{
            mComments.setText("");
            mComments.setVisibility(View.GONE);
        }

    }

//    private ArrayList<Comment> getComments() {
//        ArrayList<Comment> c=new ArrayList<>();
//        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().
//                child(getString(R.string.dbname_photos))
//                .child(getString(R.string.field_comments));
//        ref
//
//    }


    @SuppressLint("ClickableViewAccessibility")
    private void testToggle() {



        mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                addNewLike();
                return mHeartWhite.onTouchEvent(motionEvent);
            }
        });



        mHeartRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mHeart.toggleLike();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child(getString(R.string.dbname_photos))
                        .child(photo.getPhoto_id())
                        .child(getString(R.string.field_likes));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            String keyID = singleSnapshot.getKey();
                            //case1: Then user already liked the photo
                            if (singleSnapshot.getValue(Like.class).getUser_id()
                                    .equals(userID)) {
                                myRef.child(getString(R.string.dbname_photos))
                                        .child(photo.getPhoto_id())
                                        .child(getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();


                                myRef.child(getString(R.string.dbname_user_photos))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(photo.getPhoto_id())
                                        .child(getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();

                                getLikesString();
                                break;
                            }
                        }

//
//                            //case2: The user has not liked the photo
//                            else if (!mLikedByCurrentUser) {
//                                //add new like
//                                addNewLike();
//                                break;
//                            }
//                        }
//                        if (!dataSnapshot.exists()) {
//                            //add new like
//                            addNewLike();
//                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return mHeartRed.onTouchEvent(motionEvent);
            }


            });


    }






    private void getLikesString() {

        mUsers = new StringBuilder();
       list=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");
                                list.add(singleSnapshot.getValue(User.class).getUsername());
//                                Log.d("PostViewActivity","username :"+singleSnapshot.getValue(User.class).getUsername());
                            }



                            splitUsers = mUsers.toString().split(",");

//                Log.d("PostViewActivity","list2 :"+list.get(0));


                            try {
                                if (mUsers.toString().contains(mUsername+",")) {
                                    Log.d("PostViewActivity","mTru :"+true);

                                    mLikedByCurrentUser = true;
                                    mHeartRed.setVisibility(View.VISIBLE);
                                    mHeartWhite.setVisibility(View.GONE);
                               //   mHeart.toggleLike();

                                } else {
                                    mLikedByCurrentUser = false;

                                }
                            } catch (Exception e) {
                                mLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            //      Log.d("PostViewActivity","mUserList :"+ mLikesString);

                            if(length==0) {
                                mLikesString = "";
                                mLike.setVisibility(View.GONE);
                            }
                            else if (length == 1) {

                                mLikesString = "Liked by " + splitUsers[0];
                            } else if (length == 2) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            } else if (length == 3) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];
                            }
                            else if (length > 3) {
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }

//                if (!dataSnapshot.exists()) {
//                    mLikedByCurrentUser = false;
//                    mLikesString = "";
//                }
                            if(mLikesString!=""){
                                mLike.setVisibility(View.VISIBLE);
                                mLike.setText(mLikesString);

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






    private void addNewLike() {

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(userID);

        myRef.child(getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mHeart.toggleLike();
        mLikedByCurrentUser=true;
        getLikesString();
    }


    private String ifLikedByCurrentUser() {
        if (mLikedByCurrentUser)
        return "You";
        else
            return "";
    }
}
//        mHeartPhotoUnSelected.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mHeartPhotoUnSelected.setVisibility(View.INVISIBLE);
//                mHeartPhotoSelected.setVisibility(View.VISIBLE);
//            }
//        });
//        mHeartPhotoSelected.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mHeartPhotoSelected.setVisibility(View.INVISIBLE);
//                mHeartPhotoUnSelected.setVisibility(View.VISIBLE);
//
//            }
//        });