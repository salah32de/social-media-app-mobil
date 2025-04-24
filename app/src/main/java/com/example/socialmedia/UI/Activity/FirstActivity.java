package com.example.socialmedia.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Control.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.LogIn.LogIn;
import com.example.socialmedia.UI.Dashboard.Dashboard;
import com.example.socialmedia.UI.MainView;

public class FirstActivity extends AppCompatActivity {
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);

        if (SharedPreferencesHelper.getTypeMode(getBaseContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        userManager = new UserManager();

        if (SharedPreferencesHelper.isLogIn(getApplicationContext())) {
            User user = SharedPreferencesHelper.getUser(getApplicationContext());
            getUserFromFirebase(user.getId());
        } else {
            goToLogin();
        }
    }

    private void getUserFromFirebase(String userId) {
        userManager.getUserById(userId, new UserRepository.UserCallBack<User>() {
            @Override
            public void onSuccess(User user) {

                if (!FirstActivity.this.isFinishing()) {
                    if (user.isActive()) {
                        SharedPreferencesHelper.saveUser(user, getApplicationContext());
                        if (user.isAdmin()) {
                            goToDashboard();
                        } else {
                            goToMain();
                        }

                    } else {
                        SharedPreferencesHelper.LogOut(getApplicationContext());
                        goToLogin();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (e.getMessage().equals("user is banned")) {
                    SharedPreferencesHelper.LogOut(getApplicationContext());
                    Toast.makeText(FirstActivity.this, "the account is banned", Toast.LENGTH_SHORT).show();
                }
                goToLogin();
            }
        });
    }

    private void goToMain() {
        startActivity(new Intent(this, MainView.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LogIn.class));
        finish();
    }

    public void goToDashboard() {
        startActivity(new Intent(this, Dashboard.class));
        finish();

    }
}