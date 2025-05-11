package com.example.socialmedia.Controller;

import android.app.Activity;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.Authentication.Authentication;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;

public class AuthenticationManager {

    private static final String TAG = "AuthenticationManager";

    private Authentication authentication;

    public AuthenticationManager(Activity activity) {
        this.authentication = new Authentication();
    }

    // إضافة مستخدم جديد
    public void addUser(Activity activity, User user, String password, authCallBack callBack) {
        authentication.addUser(activity, user, password, callBack);
    }

    // التحقق من وجود البريد الإلكتروني
    public void checkEmail(String email, authCallBack callBack) {
        authentication.checkEmail(email, callBack);
    }

    // تسجيل الدخول
    public void signIn(String email, String password, checkSingInCallBack callback) {
        authentication.SignInByEmailAndPassword(email, password, new checkSingInCallBack() {
            @Override
            public void onSuccessful(String id) {
                if (id.equals("")) {
                    Log.d("TAG: AuthenticationManager", "Sign in failed");
                    callback.onFailure(null);
                } else {
                    Log.d("TAG: AuthenticationManager", "Sign in successful");
                    callback.onSuccessful(id);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("TAG: AuthenticationManager", "Sign in failed " + e.getMessage());
                callback.onFailure(null);
            }
        });
    }

    public void SignOut() {
        authentication.SignOut();
    }

    public void DeleteUser() {
        authentication.DeleteUser(new Authentication.DeleteAccountUserAutCallback() {
            @Override
            public void deleteAccountUserAutSuccess() {
                Log.d(TAG, "deleteAccountUserAutSuccess: ");
            }

            @Override
            public void deleteAccountUserAutFailure(Exception e) {
                Log.d(TAG, "deleteAccountUserAutFailure: " + e.getMessage());
            }
        });
    }

    public void UpdatePassword(String oldPassword, String newPassword, authCallBack callBack) {
        authentication.UpdatePassword(oldPassword, newPassword, new authCallBack() {
            @Override
            public void onSuccessful(boolean success) {
                Log.d(TAG, "onSuccessful: " + success);
                callBack.onSuccessful(success);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }

    public interface authCallBack {
        void onSuccessful(boolean success);

        void onFailure(Exception e);
    }

    public interface checkSingInCallBack {
        void onSuccessful(String id);

        void onFailure(Exception e);
    }

}
