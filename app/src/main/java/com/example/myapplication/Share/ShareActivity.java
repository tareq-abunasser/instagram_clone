package com.example.myapplication.Share;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Permissions;
import com.example.myapplication.Utils.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ShareActivity extends AppCompatActivity {

    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
         mViewPager =  findViewById(R.id.container);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                setupViewPager();
            } else {
                requestPermission(Permissions.PERMISSIONS); } }



    }

        public boolean checkPermissionArray(String[] permissions){

            for(int i = 0; i< permissions.length; i++){
                String check = permissions[i];
                if(!checkPermission(check)){
                    return false;
                }
            }
            return true;

        }


        public boolean checkPermission(String permission){
            int permissionVar =   ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission);
            return  permissionVar == PackageManager.PERMISSION_GRANTED ;
        }
        public void requestPermission(String[] permissions){
            ActivityCompat.requestPermissions(
                    ShareActivity.this,permissions,1);
        }
    private boolean allPermissionsGranted(){

        for(String permission : Permissions.PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(ShareActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
                ;
            else
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();

    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter =  new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new VideoFragment());
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout =  findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
        tabLayout.getTabAt(2).setText(getString(R.string.video));
    }
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();

    }
    }