package com.example.socialmedia.Control;

import android.util.Log;

import com.example.socialmedia.Data.Firebase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.User;

import java.util.List;

public class UserManager {
    private final String TAG = "TAG: UserManager";
    private UserRepository userRepository;

    public UserManager() {
        userRepository = new UserRepository();
    }

    public void getUserById(String idUser, UserRepository.UserCallBack<User> callback) {
        userRepository.getUserById(idUser, new UserRepository.UserCallBack<User>() {
            @Override
            public void onSuccess(User value) {
                Log.d(TAG, "onSuccess: " + value.getName());
                callback.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    public void SearchUserByName(String nameUser, UserRepository.UserCallBack<List<User>> callback) {
        userRepository.SearchUserByName(nameUser, new UserRepository.UserCallBack<List<User>>() {
            @Override
            public void onSuccess(List<User> value) {
                Log.d(TAG, "onSuccess: ");
                for (User user : value) {
                    Log.d(TAG, "value: " + user.getName());
                }

                callback.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure in search user: " + e.getMessage());
            }
        });
    }

    public void DeleteUser(String idUser, UserRepository.UserCallBack deleteUserCallback) {
        userRepository.DeleteUser(idUser, new UserRepository.UserCallBack<Void>() {
            @Override
            public void onSuccess(Void value) {
                Log.d(TAG, "DeleteUser: success");
                PostManager postManager = new PostManager();
                postManager.DeletePostsUser(idUser);
                FriendManager friendManager = new FriendManager();
                friendManager.DeleteFriendUser(idUser);
                NotificationManager notificationManager = new NotificationManager();
                notificationManager.DeleteNotificationUser(idUser);
                CommentManager commentManager = new CommentManager();
                commentManager.DeleteCommentsPostsUser(idUser);
                deleteUserCallback.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "DeleteUser: failure");
                deleteUserCallback.onFailure(e);
            }

        });
    }

    public void UpdateUser(User user, UserRepository.UserCallBack<Void> callback) {
        userRepository.UpdateUser(user, new UserRepository.UserCallBack<Void>() {
            @Override
            public void onSuccess(Void value) {
                Log.d(TAG, "UpdateUser: success");
                callback.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "UpdateUser: failure");
                callback.onFailure(e);
            }
        });
    }


}

