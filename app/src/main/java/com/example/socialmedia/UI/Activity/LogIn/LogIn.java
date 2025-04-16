package com.example.socialmedia.UI.Activity.LogIn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.socialmedia.Control.AuthenticationManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Control.UserManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.CreateAccount.RegisterEmail;
import com.example.socialmedia.UI.Dashboard.Dashboard;
import com.example.socialmedia.UI.MainView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LogIn extends AppCompatActivity {
    private final String TAG = "TAG: LogIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        TextInputLayout enterEmailLayout = findViewById(R.id.enterEmailLayout);
        TextInputEditText enterEmail = findViewById(R.id.enterEmail);
        TextInputLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
        TextInputEditText enterPassword = findViewById(R.id.enterPassword);

        Button logInButton = findViewById(R.id.logInButton);
        TextView createAccount = findViewById(R.id.createAccount);


        if (SharedPreferencesManager.getTypeMode(getBaseContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (SharedPreferencesManager.isLogIn(getBaseContext())) {
            User user=SharedPreferencesManager.getUser(getBaseContext());
            if(user.isAdmin()){
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);

            }else{
                Intent intent = new Intent(getApplicationContext(), MainView.class);
                startActivity(intent);

            }
            finish();
        }

        Switch s = findViewById(R.id.sw);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });


        logInButton.setOnClickListener(v -> {
            String emailUser = enterEmail.getText().toString();
            String password = enterPassword.getText().toString();
            if (emailUser.isEmpty() || !emailUser.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                enterPasswordLayout.setError(null);
                enterEmailLayout.setError("enter Email");
            } else if (password.isEmpty() || password.length() < 8) {
                enterEmailLayout.setError(null);
                enterPasswordLayout.setError("enter Password");
            } else {
                enterEmailLayout.setError(null);
                enterPasswordLayout.setError(null);


                AuthenticationManager authManager = new AuthenticationManager(this);
                authManager.signIn(emailUser, password, new AuthenticationManager.checkSingInCallBack() {
                    @Override
                    public void onSuccessful(String id) {
                        UserManager userManager = new UserManager();
                        userManager.getUserById(id, new UserRepository.UserCallBack<User>() {
                            @Override
                            public void onSuccess(User user) {

                                SharedPreferencesManager.LogIn(getBaseContext(), user.getId(), user.getEmail(), user);
                                Log.d("asadwqkodq", SharedPreferencesManager.isLogIn(getBaseContext()) + " isLogIn, " + SharedPreferencesManager.getTypeMode(getBaseContext()));

                                Log.d(TAG, "store user in sharedPreferences successfully " + emailUser);
                                if(user.isAdmin()){
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(getApplicationContext(), MainView.class);
                                    startActivity(intent);
                                }
                                finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d(TAG, "not store user in Shared Preferences, Error:" + e.getMessage());
                            }
                        });


                    }

                    @Override
                    public void onFailure(Exception e) {
                        enterPasswordLayout.setError("the password is wrong");

                    }


                });


            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterEmail.class);
                startActivity(intent);
            }
        });


    }

    //close the keyboard when click on the screen
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

