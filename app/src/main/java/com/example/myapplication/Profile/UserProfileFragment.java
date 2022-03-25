package com.example.myapplication.Profile;

import android.content.Context;
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


public class UserProfileFragment extends Fragment {

    private static final int NUM_GRID_COLUMNS = 3;
    private View view;
    Context mainContext;
    String userId;
    private TextView mPosts, mFollowers, mFollowing, fullname, mUsername;
    private ImageView mBack, mMore;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Context mContext;
    FirebaseMethods mFirebaseMethods;
    int containerFragment;
    ArrayList<Photo> photos;
    Button b_unfollow, b_follow;


    public UserProfileFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        photos = new ArrayList<>();
        fullname = view.findViewById(R.id.fullname);
        mProfilePhoto = view.findViewById(R.id.image_profile);
        mPosts = view.findViewById(R.id.posts);
        mFollowers = view.findViewById(R.id.followers);
        mFollowing = view.findViewById(R.id.following);
        gridView = view.findViewById(R.id.gridview);
        mContext = getActivity();
        mBack = view.findViewById(R.id.back);
        mUsername = view.findViewById(R.id.profile_username);
        mMore = view.findViewById(R.id.more);
        b_follow = view.findViewById(R.id.b_follow);
        b_unfollow = view.findViewById(R.id.b_unfollow);
        setupWidgetForUserProfile();

        getDataFromFirebase();
        setupGridView();
        isFollowing();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //setupImageGrid();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                getChildFragmentManager().beginTransaction().replace(containerFragment,new PostViewActivity());
//
//                Intent intent = new Intent(getContext(), PostViewActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("numComment", photos.get(i).getComments().size());
//                bundle.putParcelable("photo", photos.get(i));
//                intent.putExtras(bundle);
//           intent.putExtra("photo",);
//           intent.putExtra("bundle",bundle);
      //          startActivity(intent);

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new PostViewFragment(photos.get(i))).commit();
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        return view;
    }


    private void setupWidgetForUserProfile() {

        // b_follow.setVisibility(View.GONE);
        b_unfollow.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        b_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUnfollowing();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userId)
                        .removeValue();

                reference
                        .child(getString(R.string.dbname_followers))
                        .child(userId)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();

                reference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long countIng = mFirebaseMethods.getFollowingCount(dataSnapshot, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mContext.getString(R.string.dbname_following))
                                .setValue(countIng - 1);

                        long countEr = mFirebaseMethods.getFollowersCount(dataSnapshot, userId);
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(userId)
                                .child(getString(R.string.dbname_followers))
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userId)
                        .child(getString(R.string.field_user_id))
                        .setValue(userId);

                reference
                        .child(getString(R.string.dbname_followers))
                        .child(userId)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());


                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long countIng = mFirebaseMethods.getFollowingCount(dataSnapshot, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mContext.getString(R.string.dbname_following))
                                .setValue(countIng + 1);
                        //  Log.d("ProfileFragment","Num2"+countIng);


                        long countEr = mFirebaseMethods.getFollowersCount(dataSnapshot, userId);
                        //   Log.d("ProfileFragment","Num1"+countEr);
                        reference.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(userId)
                                .child(mContext.getString(R.string.dbname_followers))
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


    private void setupGridView() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    try {
                        photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
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
                    } catch (Exception e) {
                        Log.d("UserProfileFragment", e.getMessage());
                    }
                }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getDataFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot, userId));

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
    }


    private void setProfileImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.dbname_user_account_settings)).child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccountSettings settings = new UserAccountSettings();
                settings = dataSnapshot.getValue(UserAccountSettings.class);
                UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
                //Glide.with(mContext).load(settings.getProfile_photo()).into(mProfilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void setFollowing() {
        b_follow.setVisibility(View.GONE);
        b_unfollow.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing() {
        b_follow.setVisibility(View.VISIBLE);
        b_unfollow.setVisibility(View.GONE);
    }

    private void isFollowing() {
        setUnfollowing();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(userId);
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

