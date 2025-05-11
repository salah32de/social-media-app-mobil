package com.example.socialmedia.UI.Activity.Setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmedia.Controller.AuthenticationManager;
import com.example.socialmedia.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UpdatePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        TextInputEditText oldPassword = findViewById(R.id.oldPassword);
        TextInputEditText newPassword = findViewById(R.id.newPassword);
        TextInputEditText confirmPassword = findViewById(R.id.confirmPassword);

        TextInputLayout oldPasswordLayout = findViewById(R.id.oldPasswordLayout);
        TextInputLayout newPasswordLayout = findViewById(R.id.newPasswordLayout);
        TextInputLayout confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);


        addErrorClearingListener(oldPassword, oldPasswordLayout);
        addErrorClearingListener(newPassword, newPasswordLayout);
        addErrorClearingListener(confirmPassword, confirmPasswordLayout);

        Button savePasswordButton = findViewById(R.id.savePasswordButton);

        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                newPasswordLayout.setError(null);
                confirmPasswordLayout.setError(null);
                oldPasswordLayout.setError(null);


                if (!newPass.equals(confirmPass)) {
                    confirmPasswordLayout.setError("Confirm password is not match");
                }
                if (newPass.length() < 8) {
                    newPasswordLayout.setError("New password must be at least 8 characters");
                }
                if (oldPass.length() < 8) {
                    oldPasswordLayout.setError("Old password must be at least 8 characters");
                }
                if (confirmPass.length() < 8) {
                    confirmPasswordLayout.setError("Confirm password must be at least 8 characters");
                }

                if (!oldPass.isEmpty() && !newPass.isEmpty() && !confirmPass.isEmpty() && newPass.equals(confirmPass)) {
                    oldPasswordLayout.setError(null);
                    newPasswordLayout.setError(null);
                    confirmPasswordLayout.setError(null);
                    AuthenticationManager authenticationManager = new AuthenticationManager(UpdatePassword.this);
                    authenticationManager.UpdatePassword(oldPass, newPass, new AuthenticationManager.authCallBack() {
                        @Override
                        public void onSuccessful(boolean success) {
                            Toast.makeText(UpdatePassword.this, "update password success", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (e.getMessage().contains("INVALID_LOGIN_CREDENTIALS")) {
                                oldPasswordLayout.setError("Old password is wrong");
                            } else {
                                Toast.makeText(UpdatePassword.this, "update password failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void addErrorClearingListener(EditText editText, TextInputLayout textInputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

}