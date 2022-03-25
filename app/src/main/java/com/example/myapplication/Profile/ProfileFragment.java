package com.example.myapplication.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.Models.Comment;
import com.example.myapplication.Models.Like;
import com.example.myapplication.Models.Photo;
import com.example.myapplication.Models.User;
import com.example.myapplication.Models.UserAccountSettings;
import com.example.myapplication.Models.UserSettings;
import com.example.myapplication.Profile.EditProfile.EditProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.FirebaseMethods;
import com.example.myapplication.Utils.GridViewAdapter;
import com.example.myapplication.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final int NUM_GRID_COLUMNS = 3;
    private View view;
    Context mainContext;
    private TextView mPosts, mFollowers, mFollowing, fullname, mUsername;
    private ImageView mBack, mMore;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Context mContext;
    FirebaseMethods mFirebaseMethods;
    int containerFragment;
    ArrayList<Photo> photos;
    String type, userId2;
    Button b_unfollow, b_follow, edit_profile;
    UserAccountSettings mUser;
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;

    public ProfileFragment(Context context) {
        mainContext = context;
    }

    public ProfileFragment(Context context, int container) {
        mainContext = context;
        containerFragment = container;
        type = null;
        userId2 = null;
    }

    public ProfileFragment() {
        type = null;
        userId2 = null;
    }

    public ProfileFragment(String type, String userId2) {
        this.type = type;
        this.userId2 = userId2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        photos = new ArrayList<>();
        fullname = view.findViewById(R.id.fullname);
        mProfilePhoto = view.findViewById(R.id.image_profile);
        mPosts = view.findViewById(R.id.posts);
        mFollowers = view.findViewById(R.id.followers);
        mFollowing = view.findViewById(R.id.following);
        gridView = view.findViewById(R.id.gridview);
        mContext = getActivity();

        // For User Profile
        mBack = view.findViewById(R.id.back);
        mUsername = view.findViewById(R.id.profile_username);
        mMore = view.findViewById(R.id.more);
        b_follow = view.findViewById(R.id.b_follow);
        b_unfollow = view.findViewById(R.id.b_unfollow);

        // For My Profile
        edit_profile = view.findViewById(R.id.edit_profile);

        if (type == null)
            setupWidgetForMyProfile();
        else
            setupWidgetForUserProfile();

        getDataFromFirebase();
        setupGridView();

        //setupImageGrid();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                getChildFragmentManager().beginTransaction().replace(containerFragment,new PostViewActivity());
//
//                Intent intent = new Intent(getContext(), PostViewFragment.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("numComment", photos.get(i).getComments().size());
//                bundle.putParcelable("photo", photos.get(i));
//                intent.putExtras(bundle);
////           intent.putExtra("photo",);
////           intent.putExtra("bundle",bundle);
//                startActivity(intent);

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new PostViewFragment(photos.get(i))).commit();
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        return view;
    }


    private void setupWidgetForUserProfile() {

        edit_profile.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);
        mUsername.setVisibility(View.VISIBLE);
        mMore.setVisibility(View.VISIBLE);
        // b_follow.setVisibility(View.GONE);
        b_unfollow.setVisibility(View.VISIBLE);


        isFollowing();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
    }
});

        b_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUnfollowing();
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userId2)
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(userId2)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();


                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long  countIng= mFirebaseMethods.getFollowingCount(dataSnapshot,FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("following")
                                .setValue(countIng -1);

                        long  countEr= mFirebaseMethods.getFollowersCount(dataSnapshot,userId2);
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(userId2)
                                .child("following")
                                .setValue(countEr - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        b_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFollowing();
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userId2)
                        .child(getString(R.string.field_user_id))
                        .setValue(userId2);

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(userId2)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long  countIng= mFirebaseMethods.getFollowingCount(dataSnapshot,FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("following")
                                .setValue(countIng + 1);
                        Log.d("ProfileFragment","Num2"+countIng);


                        long  countEr= mFirebaseMethods.getFollowersCount(dataSnapshot,userId2);
                        Log.d("ProfileFragment","Num1"+countEr);
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(userId2)
                                .child("following")
                                .setValue(countEr + 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });

        mMore.setOnClickListener(new View.OnClickListener() {
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


    private void setupWidgetForMyProfile() {
        mBack.setVisibility(View.GONE);
        edit_profile.setVisibility(View.VISIBLE);
        mUsername.setVisibility(View.GONE);
        mMore.setVisibility(View.GONE);
        b_follow.setVisibility(View.GONE);
        b_unfollow.setVisibility(View.GONE);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }


    private void setupGridView() {

        String ui;
        if (userId2 == null) {
            ui = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            ui = userId2;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(ui);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    try{
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                            photo.setLikes(likesList);
                        }
                        photos.add(photo);


                        //setup our image grid
                        int gridWidth = getResources().getDisplayMetrics().widthPixels;
                        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
                        gridView.setColumnWidth(imageWidth);

                        ArrayList<String> imgUrls = new ArrayList<String>();
                        for (int i = 0; i < photos.size(); i++) {
                            imgUrls.add(photos.get(i).getImage_path());
                        }
                        GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.grid_view_raw,
                                "", imgUrls);
                        gridView.setAdapter(adapter);

                }catch (NullPointerException e){
                        Log.d("ProfileFragment",e.getMessage());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getDataFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String ui;
        if (userId2 == null) {
            ui = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            ui = userId2;
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot, ui));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings) {
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        //UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
        setProfileImage();
        fullname.setText(settings.getFullname());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mUsername.setText(settings.getUsername());
        getFollowingCount();
        getFollowersCount();
    }


    private void setProfileImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.dbname_user_account_settings));
        DatabaseReference ref;
        if (userId2 == null)
            ref = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        else
            ref = reference.child(userId2);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings mUser = new UserAccountSettings();
                mUser = dataSnapshot.getValue(UserAccountSettings.class);
                UniversalImageLoader.setImage(mUser.getProfile_photo(),mProfilePhoto,null,"");

               // Glide.with(mContext).load(mUser.getProfile_photo()).into(mProfilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void setFollowing() {
        edit_profile.setVisibility(View.GONE);
        b_follow.setVisibility(View.GONE);
        b_unfollow.setVisibility(View.VISIBLE);


    }

    private void setUnfollowing() {
        edit_profile.setVisibility(View.GONE);
        b_follow.setVisibility(View.VISIBLE);
        b_unfollow.setVisibility(View.GONE);



    }

    private void isFollowing() {

       setUnfollowing();
        //setFollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(userId2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    setFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getFollowersCount(){

        mFollowersCount = 0;
        String ui;
        if (userId2 == null) {
            ui = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            ui = userId2;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_followers))
                .child(ui);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    mFollowersCount++;
                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount(){

        mFollowingCount = 0;
        String ui;
        if (userId2 == null) {
            ui = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            ui = userId2;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_following))
                .child(ui);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    mFollowingCount++;
                }
                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        reference.child(mContext.getString(R.string.dbname_user_account_settings))
//                .child(ui).setValue(UserAccountSettings.class)
    }


}

//    @Override
//    public void onAttach(@NonNull Context context) {
//        try{
//            onGridImageSelectedListener =(OnGridImageSelectedListener)getActivity();
//        }catch (Exception e){}
//        super.onAttach(context);
//    }


//        drawerLayout = view.findViewById(R.id.drawable_layout);
//        navigationView = view.findViewById(R.id.navigation_view);
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_archive:
//                break;
//            case R.id.nav_your_activity:
//                break;
//                case R.id.nav_nametag:
//                break;
//            case R.id.nav_saved:
//                break;
//            case R.id.nav_close_friends:
//                break;
//            case R.id.nav_discover_people:
//                break;
//
//        }
//        item.setChecked(true);
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
