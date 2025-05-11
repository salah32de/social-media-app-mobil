package com.example.socialmedia;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;


public class Myapp extends Application implements DefaultLifecycleObserver {

    private final String TAG = "TAG: Myapp";
    private UserManager userManager;
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();

        userManager = new UserManager();

        user = SharedPreferencesHelper.getUser(getApplicationContext());

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
        UserRepository userRepository = new UserRepository();
        if (user != null && user.isActive()) {
            user.setOnline(status);

            long time = System.currentTimeMillis();

            if (!status) {
                userRepository.UpdateTotalActiveTimeUser(user, time, new UserRepository.UserCallBack<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        SharedPreferencesHelper.saveUser(user, getApplicationContext());
                        userRepository.UpdateLastSeenTimeUser(user, time, new UserRepository.UserCallBack<Void>() {
                            @Override
                            public void onSuccess(Void value) {
                                SharedPreferencesHelper.saveUser(user, getApplicationContext());
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }else {
                userRepository.UpdateLastSeenTimeUser(user, time, new UserRepository.UserCallBack<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        SharedPreferencesHelper.saveUser(user, getApplicationContext());
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }

            userRepository.UpdateStateUser(user.getId(), status, new UserRepository.UserCallBack<Void>() {

                @Override
                public void onSuccess(Void value) {
                    SharedPreferencesHelper.saveUser(user, getApplicationContext());
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    public void GetUsers(String idUser) {
        userManager.getUserById(idUser, new UserRepository.UserCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.isActive())
                    SharedPreferencesHelper.saveUser(user, getApplicationContext());
                else SharedPreferencesHelper.LogOut(getApplicationContext());
                Log.d(TAG, "store user in sharedPreferences successfully " + user.isActive());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "not store user in Shared Preferences, Error:" + e.getMessage());
            }
        });
    }
}

