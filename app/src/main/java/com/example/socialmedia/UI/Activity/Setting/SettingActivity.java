package com.example.socialmedia.UI.Activity.Setting;

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

import com.example.socialmedia.Control.AuthenticationManager;
import com.example.socialmedia.Control.PostManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Control.UserManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.LogIn.LogIn;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Switch s = findViewById(R.id.swithMode);
        if (SharedPreferencesManager.getTypeMode(getBaseContext())) {
            s.setChecked(true);
        } else {
            s.setChecked(false);
        }

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesManager.setTypeMode(getBaseContext(), true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    SharedPreferencesManager.setTypeMode(getBaseContext(), false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });

        ImageView back = findViewById(R.id.backSetting);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                SharedPreferencesManager.LogOut(getBaseContext());
                Intent intent = new Intent(getBaseContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });


        TextView deleteAccount = findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = SharedPreferencesManager.getUser(getBaseContext());

                UserManager userManager = new UserManager();
                userManager.DeleteUser(user.getId(), new UserRepository.UserCallBack() {
                    @Override
                    public void onSuccess(Object value) {
                        Toast.makeText(getBaseContext(), "delete account success", Toast.LENGTH_SHORT).show();

                        AuthenticationManager authenticationManager = new AuthenticationManager(SettingActivity.this);
                        authenticationManager.DeleteUser();

                        SharedPreferencesManager.LogOut(getBaseContext());
                        Intent intent = new Intent(getBaseContext(), LogIn.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(SettingActivity.this, "delete account failure .try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        TextView updateInformation = findViewById(R.id.updateInformation);
        updateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),UpdateInformationActivity.class);
                startActivity(intent);
            }
        });
        TextView updatePassword = findViewById(R.id.updatePassword);
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),UpdatePassword.class);
                startActivity(intent);
            }
        });
    }
}