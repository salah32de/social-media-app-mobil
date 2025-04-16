package com.example.socialmedia.Control;

import android.app.Application;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.socialmedia.Data.Firebase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.User;


public class Myapp extends Application implements DefaultLifecycleObserver {

    private UserManager userManager;
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();

        userManager = new UserManager();
        user = SharedPreferencesManager.getUser(getBaseContext());

        // تسجيل التطبيق كمراقب لحالة التشغيل
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onStart(LifecycleOwner owner) {
        setUserStatus(true);
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        setUserStatus(false);
    }

    private void setUserStatus(boolean status) {
        if (user != null) {  // تجنب حدوث NullPointerException
            user.setOnline(status);
            userManager.UpdateUser(user, new UserRepository.UserCallBack<Void>() {
                @Override
                public void onSuccess(Void value) {

                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }
}
