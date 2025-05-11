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

import com.example.socialmedia.Controller.AuthenticationManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.LogIn.LogIn;

public class SettingActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        user= SharedPreferencesHelper.getUser(getBaseContext());


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

                UserManager userManager = new UserManager();
                user.setOnline(false);
                userManager.UpdateUser(user, new UserRepository.UserCallBack<Void>() {
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
                        Toast.makeText(SettingActivity.this, "error .try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        TextView deleteAccount = findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = SharedPreferencesHelper.getUser(getBaseContext());

                UserManager userManager = new UserManager();
                userManager.DeleteUser(user.getId(), new UserRepository.UserCallBack() {
                    @Override
                    public void onSuccess(Object value) {
                        Toast.makeText(getBaseContext(), "delete account success", Toast.LENGTH_SHORT).show();

                        AuthenticationManager authenticationManager = new AuthenticationManager(SettingActivity.this);
                        authenticationManager.DeleteUser();

                        SharedPreferencesHelper.LogOut(getBaseContext());
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

        TextView savedPosts=findViewById(R.id.savedPosts);
        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,SavedPosts.class);
                startActivity(intent);
            }
        });
    }
}