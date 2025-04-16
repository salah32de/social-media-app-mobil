package com.example.socialmedia.UI.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;

public class Dashboard extends AppCompatActivity {

    ImageView photoProfile;
    View reports;
    User admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        photoProfile=findViewById(R.id.profile_photo);
        reports =findViewById(R.id.card_reports);

        admin = SharedPreferencesManager.getUser(getBaseContext());

        if(admin.getPhotoProfile().isEmpty()){
            Glide.with(this).load(R.drawable.user_cicrle_duotone).circleCrop().into(photoProfile);
        }else{
            Glide.with(this).load(admin.getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(photoProfile);
        }

        photoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminProfileDashboard.class);
                startActivity(intent);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), ReviewReports.class);
                startActivity(intent);
            }
        });
    }
}