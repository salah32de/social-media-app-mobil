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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.socialmedia.Controller.AuthenticationManager;
import com.example.socialmedia.Controller.RoomDatabaseManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.CreateAccount.RegisterEmail;
import com.example.socialmedia.UI.Dashboard.Dashboard;
import com.example.socialmedia.UI.MainView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;

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
                                if (!LogIn.this.isFinishing()) {
                                    Toast.makeText(LogIn.this, "" + user.isActive(), Toast.LENGTH_SHORT).show();
                                    if (user.isActive()) {
                                        SharedPreferencesHelper.LogIn(getBaseContext(), user.getId(), user.getEmail(), user);
                                        Toast.makeText(LogIn.this, SharedPreferencesHelper.getUser(getBaseContext()).getName()+" "+ SharedPreferencesHelper.isLogIn(getBaseContext()), Toast.LENGTH_SHORT).show();
                                        if(!user.isOnline()){
                                            UserRepository userRepository=new UserRepository();
                                            userRepository.UpdateStateUser(user.getId(), true, new UserRepository.UserCallBack<Void>() {
                                                @Override
                                                public void onSuccess(Void value) {

                                                }

                                                @Override
                                                public void onFailure(Exception e) {

                                                }
                                            });
                                        }
                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            RoomDatabaseManager roomDatabaseManager = new RoomDatabaseManager(getBaseContext());
                                            roomDatabaseManager.insertUser(new UserEntity(user.getId(), user.getName()));
                                        });

                                        Log.d(TAG, "store user in sharedPreferences successfully " + emailUser);
                                        if (user.isAdmin()) {
                                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), MainView.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(LogIn.this, "the account is blocked ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }


                            @Override
                            public void onFailure(Exception e) {
                                Log.d(TAG, "not store user in Shared Preferences, Error:" + e.getMessage());
                                if (e.getMessage().equals("user is banned")) {
                                    Toast.makeText(LogIn.this, "the account is blocked ", Toast.LENGTH_SHORT).show();
                                }
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

        DeleteMessageError();
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

    public void DeleteMessageError() {
        TextInputLayout enterEmailLayout = findViewById(R.id.enterEmailLayout);
        TextInputEditText enterEmail = findViewById(R.id.enterEmail);
        TextInputLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
        TextInputEditText enterPassword = findViewById(R.id.enterPassword);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == enterEmail.getId()) {
                    enterEmailLayout.setError(null);
                    enterEmail.setError(null);
                } else {
                    enterPassword.setError(null);
                    enterPasswordLayout.setError(null);
                }
                return false;
            }
        };
        enterEmail.setOnTouchListener(touchListener);
        enterEmailLayout.setOnTouchListener(touchListener);
        enterPassword.setOnTouchListener(touchListener);
        enterPasswordLayout.setOnTouchListener(touchListener);
    }
}

