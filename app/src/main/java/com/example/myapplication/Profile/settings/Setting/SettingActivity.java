package com.example.myapplication.Profile.settings.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.StartActivity;
import com.example.myapplication.Utils.ListViewAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
        TextView addAccount,logout;
        FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageView imageBack= findViewById(R.id.back);
        addAccount=findViewById(R.id.add_account_setting);
        logout=findViewById(R.id.logout_settings);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth= FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent=new Intent(SettingActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        setFragment();
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });
    }


    public void setFragment(){
        ListView listView=findViewById(R.id.listview_setting);
        String text[] = {"Follow and invite Friends", "Notification", "Privacy", "Ads", "Account","Help","About","Theme"};
        int images[] = {R.drawable.ic_add_person, R.drawable.ic_notification, R.drawable.ic_privacy, R.drawable.ic_security, R.drawable.ic_ads, R.drawable.ic_tpayments, R.drawable.ic_account, R.drawable.ic_help, R.drawable.ic_about, R.drawable.ic_theme};

        ListViewAdapter adapter = new ListViewAdapter(this,  text, images,R.layout.listview_raw);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sett=adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SettingActivity.this, sett, Toast.LENGTH_SHORT).show();
                if(i==0);
                else if(i==1);
                else if(i==2);
                else if(i==3);
                else if(i==4);
                else if(i==5);
                else if(i==6);
                else if(i==7);
            }
        });

    }


}




