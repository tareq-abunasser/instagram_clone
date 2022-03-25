package com.example.myapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.myapplication.Home.HomeFragment;
import com.example.myapplication.Notification.NotificationFragment;
import com.example.myapplication.Profile.ProfileFragment;
import com.example.myapplication.R;
import com.example.myapplication.Search.SearchFragment;
import com.example.myapplication.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
        private static final String TAG = "BottomNavigationViewHel";

        public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
            Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
            bottomNavigationViewEx.enableAnimation(true);
            bottomNavigationViewEx.enableItemShiftingMode(false);
            bottomNavigationViewEx.enableShiftingMode(false);
            bottomNavigationViewEx.setTextVisibility(false);
        }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.o_home:
                        Intent intent1 = new Intent(context, HomeFragment.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;

                    case R.id.o_search:
                        Intent intent2  = new Intent(context, SearchFragment.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;

                    case R.id.o_add:
                        Intent intent3 = new Intent(context, ShareActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        break;

                    case R.id.o_notification:
                        Intent intent4 = new Intent(context, NotificationFragment.class);//ACTIVITY_NUM = 3
                        context.startActivity(intent4);
                        break;

                    case R.id.o_profile:
                        Intent intent5 = new Intent(context, ProfileFragment.class);//ACTIVITY_NUM = 4
                        context.startActivity(intent5);
                        break;
                }


                return false;
            }
        });
    }
}

