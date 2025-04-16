package com.example.socialmedia.UI.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Control.UserManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.LogIn.LogIn;
import com.example.socialmedia.UI.Activity.Setting.SettingActivity;
import com.example.socialmedia.UI.Activity.Setting.UpdateInformationActivity;

public class AdminProfileDashboard extends AppCompatActivity {
    private User admin;
    private ImageView back,photoProfile;
    private TextView adminName,adminEmail;
    private Button btnEditInfo,btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_admin_profile_dashborad);

         admin= SharedPreferencesManager.getUser(getBaseContext());
         photoProfile=findViewById(R.id.photoProfile);
         back=findViewById(R.id.back);
         adminName=findViewById(R.id.admin_name);
         adminEmail=findViewById(R.id.admin_email);
         btnLogout=findViewById(R.id.btn_logout);
         btnEditInfo=findViewById(R.id.btn_edit_info);

         if(!admin.getPhotoProfile().isEmpty()){
             Glide.with(this).load(admin.getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(photoProfile);
         }
         adminName.setText(admin.getName());
         adminEmail.setText(admin.getEmail());

         back.setOnClickListener(v->{
             finish();
         });

         btnEditInfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(getBaseContext(), UpdateInformationActivity.class);
                 startActivity(intent);
             }
         });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager userManager = new UserManager();
                admin.setOnline(false);
                userManager.UpdateUser(admin, new UserRepository.UserCallBack<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        SharedPreferencesManager.LogOut(getBaseContext());

                        Intent intent = new Intent(getBaseContext(), LogIn.class);
                        startActivity(intent);
                        finishAffinity();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AdminProfileDashboard.this, "error .try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}