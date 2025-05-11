package com.example.socialmedia.UI.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.LogIn.LogIn;
import com.example.socialmedia.UI.Activity.Setting.UpdateInformationActivity;
import com.example.socialmedia.UI.Activity.Setting.UpdatePassword;

public class AdminProfile extends AppCompatActivity {
    private User admin;
    private ImageView back, photoProfile;
    private TextView adminName, adminEmail;
    private TextView btnEditInfo, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_dashborad);

        admin = SharedPreferencesHelper.getUser(getBaseContext());
        photoProfile = findViewById(R.id.photoProfile);
        back = findViewById(R.id.back);
        adminName = findViewById(R.id.admin_name);
        adminEmail = findViewById(R.id.admin_email);
        btnLogout = findViewById(R.id.btn_logout);
        btnEditInfo = findViewById(R.id.btn_edit_info);

        Switch s = findViewById(R.id.swithMode);
        if (SharedPreferencesHelper.getTypeMode(getBaseContext())) {
            s.setChecked(true);
        } else {
            s.setChecked(false);
        }

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesHelper.setTypeMode(getBaseContext(), true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    SharedPreferencesHelper.setTypeMode(getBaseContext(), false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });


        if (!admin.getPhotoProfile().isEmpty()) {
            Glide.with(this).load(admin.getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(photoProfile);
        }
        adminName.setText(admin.getName());
        adminEmail.setText(admin.getEmail());

        back.setOnClickListener(v -> {
            finish();
        });

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UpdateInformationActivity.class);
                startActivity(intent);
            }
        });
        TextView updatePassword = findViewById(R.id.updatePassword);
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(), UpdatePassword.class);
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
                        SharedPreferencesHelper.LogOut(getBaseContext());

                        Intent intent = new Intent(getBaseContext(), LogIn.class);
                        startActivity(intent);
                        finishAffinity();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AdminProfile.this, "error .try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}