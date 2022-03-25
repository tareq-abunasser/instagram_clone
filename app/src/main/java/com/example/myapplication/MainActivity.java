package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Home.HomeFragment;
import com.example.myapplication.Models.User;
import com.example.myapplication.Notification.NotificationFragment;
import com.example.myapplication.Profile.ProfileFragment;
import com.example.myapplication.Profile.settings.ArchiveFragment;
import com.example.myapplication.Profile.settings.CloseFriendsFragment;
import com.example.myapplication.Profile.settings.DiscoverPeopleFragment;
import com.example.myapplication.Profile.settings.NametagFragment;
import com.example.myapplication.Profile.settings.SavedFragment;
import com.example.myapplication.Profile.settings.Setting.SettingActivity;
import com.example.myapplication.Profile.settings.YourActivityFragment;
import com.example.myapplication.Search.SearchFragment;
import com.example.myapplication.Share.ShareActivity;
import com.example.myapplication.Utils.BottomNavigationViewHelper;
import com.example.myapplication.Utils.UniversalImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationViewEx bottomNavigationViewEx;
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    Fragment selectedFragment;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Fragment fragmentContainer;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;
    FirebaseAuth auth;
    String userID;


    ImageView mainPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPhoto=findViewById(R.id.main_photo);



        setupBottomNavigationView();
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(itemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                new HomeFragment(MainActivity.this ,R.id.container_fragment)).commit();
        toolbar=findViewById(R.id.toolbar_activitymain);
        drawerLayout = findViewById(R.id.drawable_layout);
        setNav();

        mSettings();
        initImageLoader();
        myRef= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        userID=auth.getCurrentUser().getUid();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mainPhoto.setVisibility(View.VISIBLE);

        new Runnable() {
            int updateInterval = 1000; //=one second

            @Override
            public void run() {

                mainPhoto.postDelayed(this, updateInterval);
            }

        }.run();

        mainPhoto.setVisibility(View.GONE);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_archive:
                toolbar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                       new ArchiveFragment()).commit();
                break;
            case R.id.nav_your_activity:
                toolbar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new YourActivityFragment()).commit();
                break;
            case R.id.nav_nametag:
                toolbar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new NametagFragment()).commit();
                break;
            case R.id.nav_saved:
                toolbar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new SavedFragment()).commit();
                break;
            case R.id.nav_close_friends:
                toolbar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new CloseFriendsFragment()).commit();
                break;
            case R.id.nav_discover_people:
                toolbar.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                        new DiscoverPeopleFragment()).commit();
                break;

        }
        item.setChecked(true);
       drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }


    private BottomNavigationViewEx.OnNavigationItemSelectedListener itemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.o_home : {
                    selectedFragment = new HomeFragment(MainActivity.this, R.id.container_fragment);
               toolbar.setVisibility(View.GONE);
                }
                    break;
                case R.id.o_search :{
                    selectedFragment=new SearchFragment();
                    toolbar.setVisibility(View.GONE);
                }
                    break;
                case R.id.o_profile : {
                    selectedFragment = new ProfileFragment(MainActivity.this,R.id.container_fragment);
                    toolbar.setVisibility(View.VISIBLE);
                    final TextView username=findViewById(R.id.username);
                    //FirebaseMethods.userN
                    final User user = new User();
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds:  dataSnapshot.child(getString(R.string.dbname_users)).getChildren()) {
                        if (ds.getKey().equals(userID)) {
                            user.setUsername(ds.getValue(User.class).getUsername());
                        }
                        username.setText(user.getUsername());
                    }}
                        @Override
                        public void onCancelled (@NonNull DatabaseError databaseError){
                        }
                     });

                }
                    break;
                case R.id.o_notification :{
                    selectedFragment=new NotificationFragment();
                    toolbar.setVisibility(View.GONE);
                }
                    break;
                case R.id.o_add :{
//                    selectedFragment=new ShareActivity();
                    selectedFragment=null;
                    toolbar.setVisibility(View.GONE);
                    startActivity(new Intent(MainActivity.this,ShareActivity.class));
                }
                    break;

            }
            if(selectedFragment!=null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment , selectedFragment).commit();
            return true;}
    };








    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
         bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

}



public void setNav(){
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
//    getSupportActionBar().setIcon( R.drawable.ic_settings);
    navigationView = findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(this);
     actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close);
    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
     getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_settings);
    ViewCompat.setLayoutDirection(toolbar, ViewCompat.LAYOUT_DIRECTION_RTL);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        }
    });
}


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

 public void mSettings(){
     ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.i_settings);
//     ImageView imageView=findViewById(R.id.i_settings);
     imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent=new Intent(MainActivity.this, SettingActivity.class);
             startActivity(intent);
         }
     });
 }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
}