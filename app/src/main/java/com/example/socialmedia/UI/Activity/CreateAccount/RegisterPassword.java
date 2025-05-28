package com.example.socialmedia.UI.Activity.CreateAccount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmedia.Controller.AuthenticationManager;
import com.example.socialmedia.Controller.RoomDatabaseManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.MainView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_password);
        TextInputEditText enterPassword = findViewById(R.id.enterPassword);
        TextInputLayout enterPasswordLayout=findViewById(R.id.enterPasswordLayout);
        TextInputEditText confirmPassword=findViewById(R.id.confirmPassword);
        TextInputLayout confirmPasswordLayout=findViewById(R.id.confirmPasswordLayout);
        TextInputLayout registerNameLayout=findViewById(R.id.enterNameLayout);
        TextInputEditText registerName=findViewById(R.id.enterName);
        Button createAccountButton=findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterName=registerName.getText().toString();
                String enterPasswordText=enterPassword.getText().toString();
                String confirmPasswordText=confirmPassword.getText().toString();
                if(enterName.isEmpty()){
                    registerNameLayout.setError("enter name");
                }else if(enterPasswordText.isEmpty()){
                    enterPasswordLayout.setError("enter Password");
                    confirmPasswordLayout.setError(null);
                }else if(!confirmPasswordText.equals(enterPasswordText)){
                        confirmPasswordLayout .setError("enter Password");
                        enterPasswordLayout.setError(null);
                }else if(enterPasswordText.length()<8){
                    confirmPasswordLayout.setError(null);
                    enterPasswordLayout.setError("the password must be at least 8 characters");
                }else {

                    enterPasswordLayout.setError(null);
                    enterPasswordLayout.setError(null);
                    confirmPasswordLayout.setError(null);

                    User user = (User) getIntent().getSerializableExtra("user");
                    user.setName(enterName);
                    user.setActive(true);
                    user.setOnline(true);
                    AuthenticationManager authManager=new AuthenticationManager(getActivity());

                    authManager.addUser(getActivity(),user,enterPasswordText,new AuthenticationManager.authCallBack() {
                        @Override
                        public void onSuccessful(boolean success) {

                            if(success){
                                SharedPreferencesHelper.LogIn(getBaseContext(),user.getId(),user.getEmail(),user);

                                Log.d("TAG: RegisterPassword","store user in sharedPreferences successfully "+user.getEmail());
                                Intent intent=new Intent(RegisterPassword.this, MainView.class);
                                startActivity(intent);
                                new Thread(()->{
                                    RoomDatabaseManager roomDatabaseManager=new RoomDatabaseManager(getBaseContext());
                                    roomDatabaseManager.insertUser(new UserEntity(user.getId(),user.getName()));
                                });
                                finish();
                            }else{
                                Toast.makeText(RegisterPassword.this, "Error in create account. try again", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterPassword.this, RegisterEmail.class);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });


                }
            }
        });

        DeleteMessageError();
    }
    public Activity getActivity() {
        return  this;
    }
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    public void DeleteMessageError(){
        TextInputLayout enterPasswordLayout=findViewById(R.id.enterPasswordLayout);
        TextInputLayout confirmPasswordLayout=findViewById(R.id.confirmPasswordLayout);
        TextInputLayout registerNameLayout=findViewById(R.id.enterNameLayout);


        View.OnTouchListener touchListener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getId()==registerNameLayout.getId()){
                    registerNameLayout.setError(null);
                }else if(v.getId()==confirmPasswordLayout.getId()){
                    confirmPasswordLayout.setError(null);
                }else if(v.getId()==enterPasswordLayout.getId()){
                    enterPasswordLayout.setError(null);
                }
                return false;
            }
        };
        enterPasswordLayout.setOnTouchListener(touchListener);
        confirmPasswordLayout.setOnTouchListener(touchListener);
        registerNameLayout.setOnTouchListener(touchListener);

    }
}