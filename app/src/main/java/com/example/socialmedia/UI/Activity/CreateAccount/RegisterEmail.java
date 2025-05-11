package com.example.socialmedia.UI.Activity.CreateAccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.socialmedia.Controller.AuthenticationManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.Controller.EmailVerificationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class RegisterEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_email);


        Button buttonNext = findViewById(R.id.buttonNext);
        Button verificationButton = findViewById(R.id.verificationButton);
        LinearLayout pinLayout = findViewById(R.id.PinLayout);
        TextView pinText = findViewById(R.id.PinText);
        TextView registerGmailText = findViewById(R.id.registerGmailText);
        TextInputLayout enterEmailLayout = findViewById(R.id.enterEmailLayout);
        TextInputEditText enterEmail = findViewById(R.id.enterEmail);

        TextInputEditText pin1 = findViewById(R.id.pin1);
        TextInputEditText pin2 = findViewById(R.id.pin2);
        TextInputEditText pin3 = findViewById(R.id.pin3);
        TextInputEditText pin4 = findViewById(R.id.pin4);
        TextInputEditText pin5 = findViewById(R.id.pin5);
        TextInputEditText pin6 = findViewById(R.id.pin6);

        ProgressBar progressBar=findViewById(R.id.progressBar);


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


        //code verification
        //we can't use simple string because we use the value in backend thread
        String[] codeVerification = new String[1];





        //if the user click on the button next
        buttonNext.setOnClickListener(v -> {

            enterEmail.setClickable(false);
            //take the email from the user
            String userEmail = enterEmail.getText().toString();

            //validate email structure
            if (userEmail.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {

                //delete message error if the user enter the email correctly
                enterEmailLayout.setError(null);

                AuthenticationManager auth = new AuthenticationManager(this);

                //verify the email is use before in firebase in another account
                auth.checkEmail(userEmail, new AuthenticationManager.authCallBack() {
                    @Override
                    public void onSuccessful(boolean success) {
                        if (success) {
                            //the email use in another account
                            runOnUiThread(() -> enterEmailLayout.setError("this email use in another account"));
                        } else {
                            //if email not use in another account do this


                            EmailVerificationHelper emailHelper = new EmailVerificationHelper();

                            progressBar.setVisibility(View.VISIBLE);

                            //sent the code verification to the user and save it in the codeVerification array
                            codeVerification[0] = emailHelper.initiateEmailVerification(userEmail, new EmailVerificationHelper.EmailCallback() {

                                //onSuccess and onFailure: method in EmailCallback interface

                                //if success sent the code verification to the user
                                @Override
                                public void onSuccess() {
                                    //hide the button and the text view and show the pin layout for use to enter the code pin
                                    //we should use runOnUiThread(()->) because we are in backend thread

                                    runOnUiThread(() -> {
                                                verificationButton.setVisibility(Button.VISIBLE);
                                                pinText.setVisibility(View.VISIBLE);
                                                pinLayout.setVisibility(View.VISIBLE);

                                                buttonNext.setVisibility(View.GONE);
                                                enterEmail.setVisibility(View.GONE);
                                                enterEmailLayout.setVisibility(View.GONE);
                                                registerGmailText.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                    );
                                }


                                ////If it fails to send the verification code to the user
                                @Override
                                public void onFailure(Exception e) {
                                    //send message error
                                    runOnUiThread(() -> {
                                        enterEmailLayout.setError("the email is wrong");
                                        progressBar.setVisibility(View.GONE);
                                    });

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }

                });
            } else {
                enterEmailLayout.setError("the email is wrong");
            }


        });




        //requestFocus(): methode to make the next pin active
        pin1.requestFocus();

        pin1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pin2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pin2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pin3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pin3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pin4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pin4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pin5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        pin5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pin6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pin6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    verificationButton.setFocusable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take the code from the user
                String code = "" + pin1.getText().toString() + pin2.getText().toString() + pin3.getText().toString() + pin4.getText().toString() + pin5.getText().toString() + pin6.getText().toString();

                //if the code is correct go to the Information page


                if (codeVerification[0].equals(code)) {


                    Intent intent = new Intent(RegisterEmail.this, RegisterPassword.class);
                    User user = new User();
                    user.setEmail(enterEmail.getText().toString());

                    //for send the user information to the next page
                    intent.putExtra("user", user);
                    startActivity(intent);

                }

            }
        });
    }
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}