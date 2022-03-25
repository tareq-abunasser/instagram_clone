package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Home.CommentsActivity;
import com.example.myapplication.Models.Like;
import com.example.myapplication.Models.Photo;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.Profile.UserProfileFragment;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Heart;
import com.example.myapplication.Utils.SquareImageView;
import com.example.myapplication.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Photo> mPhoto;
    Photo photo;
    String[] splitUsers;
    String mUsername;
    StringBuilder mUsers;
    ArrayList<String> list;
    int numComment;

    private UserAccountSettings mUserAccountSettings;
    private Boolean mLikedByCurrentUser;
    private String mLikesString = "";
    private Heart mHeart;
    String ProfilePhoto;
    private FirebaseUser firebaseUser;
    // firebase
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;

    public PostAdapter(Context context, List<Photo> photos) {
        mContext = context;
        mPhoto = photos;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public PostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        photo = mPhoto.get(position);

        UniversalImageLoader.setImage(photo.getImage_path(),holder.mPostImage,holder.progressBar,"");
//        Glide.with(mContext).load(photo.getImage_path())
//                .into(holder.mPostImage);
        //  .apply(new RequestOptions().placeholder(R.drawable.placeholder))

        setProfileImage(holder);
        getLikesString(holder);
        setupWidgets(holder);
        testToggle(holder);

        holder.writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("photo", photo);
                intent.putExtra("profilePhoto",ProfilePhoto);
                mContext.startActivity(intent);
                //   mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        );

        holder.mComments.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("photo", photo);
            intent.putExtra("profilePhoto",ProfilePhoto);
            mContext.startActivity(intent);
            //   mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        });
        holder.mProfilePhoto.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                    new UserProfileFragment(photo.getUser_id())).commit();
        }
    });

        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.report:
                                return true;
                            case R.id.block:
                                return true;
                            case R.id.about_this_account:
                                return true;
                            case R.id.restrict:
                                return true;
                            case R.id.hide_your_story:
                                return true;
                            case R.id.copy_profile_url:
                                return true;
                            case R.id.share_this_profile:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.user_profile_menu);
                popupMenu.setGravity(Gravity.CENTER);
                popupMenu.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mPhoto.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {


        ImageView mProfilePhoto, mHeartRed, mHeartWhite, writeComment,mMore;
        SquareImageView mPostImage;
        TextView mImageCaption;
        TextView mTimestamp;
        TextView mLike;
        ProgressBar progressBar;
        // Var
        String userID;


        TextView mComments;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mProfilePhoto = itemView.findViewById(R.id.profile_photo);
            mPostImage = itemView.findViewById(R.id.post_image);
            mHeartRed = itemView.findViewById(R.id.image_heart_red);
            mHeartWhite = itemView.findViewById(R.id.image_heart);
            mTimestamp = itemView.findViewById(R.id.image_time_posted);
            mLike = itemView.findViewById(R.id.image_likes);
            mImageCaption = itemView.findViewById(R.id.image_caption);
            writeComment = itemView.findViewById(R.id.speech_bubble);
            mComments = itemView.findViewById(R.id.image_comments_link);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeart = new Heart(mHeartWhite, mHeartRed);
            mMore = itemView.findViewById(R.id.ivEllipses);
            progressBar=itemView.findViewById(R.id.progressBar);
        }

    }




    private void getLikesString(final PostAdapter.ImageViewHolder holder) {

        StringBuilder  mUsers = new StringBuilder();
        ArrayList<String> list=new ArrayList<>();
        Query query = myRef
                .child(mContext.getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Query query = myRef
                            .child(mContext.getString(R.string.dbname_users))
                            .orderByChild(mContext.getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");
                                list.add(singleSnapshot.getValue(User.class).getUsername());
                            }

                            splitUsers = mUsers.toString().split(",");


                            try {
                                if (mUsers.toString().contains(mUsername+",")) {
                                    Log.d("PostViewActivity","mTru :"+true);

                                    mLikedByCurrentUser = true;
                                        holder.mHeartRed.setVisibility(View.VISIBLE);
                                    holder.mHeartWhite.setVisibility(View.GONE);
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
                                holder.mLike.setVisibility(View.GONE);
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

                            if(mLikesString!=""){
                                holder.mLike.setVisibility(View.VISIBLE);
                                holder.mLike.setText(mLikesString);

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




    private void setProfileImage(final PostAdapter.ImageViewHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .child(photo.getUser_id());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings settings = new UserAccountSettings();
                settings = dataSnapshot.getValue(UserAccountSettings.class);
                Glide.with(mContext).load(settings.getProfile_photo()).into(holder.mProfilePhoto);
                ProfilePhoto=settings.getProfile_photo();
                mUsername = settings.getUsername();
                holder.mImageCaption.setText(mUsername + " " + photo.getCaption());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setupWidgets(final PostAdapter.ImageViewHolder holder) {
        String timestampDiff = getTimestampDifference();
        if (!timestampDiff.equals("0")) {
            holder.mTimestamp.setText(timestampDiff + " DAYS AGO");
        } else {
            holder.mTimestamp.setText("TODAY");
        }
    numComment=photo.getComments().size();
        if(numComment > 0){
            holder.mComments.setText("View all " + (numComment) + " comments");
        }else{
            holder.mComments.setText("");
            holder.mComments.setVisibility(View.GONE);
        }

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









    @SuppressLint("ClickableViewAccessibility")
    private void testToggle(final PostAdapter.ImageViewHolder holder) {
        deleteNotifications(photo.getPhoto_id(), firebaseUser.getUid());
        holder.mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                addNewLike(holder);
                return holder.mHeartWhite.onTouchEvent(motionEvent);
            }
        });



        holder.mHeartRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mHeart.toggleLike();

                addNotification(photo.getUser_id(), photo.getPhoto_id());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child(mContext.getString(R.string.dbname_photos))
                        .child(photo.getPhoto_id())
                        .child(mContext.getString(R.string.field_likes));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            String keyID = singleSnapshot.getKey();
                            //case1: Then user already liked the photo
                            if (singleSnapshot.getValue(Like.class).getUser_id()
                                    .equals(photo.getUser_id())) {
                                myRef.child(mContext.getString(R.string.dbname_photos))
                                        .child(photo.getPhoto_id())
                                        .child(mContext.getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();


                                myRef.child(mContext.getString(R.string.dbname_user_photos))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(photo.getPhoto_id())
                                        .child(mContext.getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();

                                getLikesString(holder);
                                break;
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return holder.mHeartRed.onTouchEvent(motionEvent);
            }


        });
    }





    private void addNewLike(final PostAdapter.ImageViewHolder holder) {

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(photo.getUser_id());

        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mHeart.toggleLike();
        mLikedByCurrentUser=true;
        getLikesString(holder);
    }


    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
