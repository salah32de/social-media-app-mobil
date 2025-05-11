package com.example.socialmedia.Controller;

import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;

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
                callback.onFailure(e);
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

    public void GetUsers(UserRepository.GetUsersCallback callback) {
        userRepository.GetUsers(new UserRepository.GetUsersCallback() {
            @Override
            public void onSuccess(List<User> userList) {
                Log.d(TAG, "GetUsers: success .listSize=" + userList.size());
                callback.onSuccess(userList);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "GetUsers: failure");
                callback.onFailure(e);
            }

        });
    }

    public void GetNumUser(UserRepository.UserCallBack callBack) {
        userRepository.GetNumUser(new UserRepository.UserCallBack<Long>() {


            @Override
            public void onSuccess(Long value) {
                Log.d(TAG, "GetNumUser: success .nmb user =" + value);
                callBack.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "GetNumUser: failure .error is: " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }

    public void GetCountUserActive(UserRepository.UserCallBack callBack) {
        userRepository.GetCountUserActive(new UserRepository.UserCallBack<Long>() {
            @Override
            public void onSuccess(Long value) {
                Log.d(TAG, "GetCountUserActive: success .nmb user =" + value);
                callBack.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "GetCountUserActive: failure .error is: " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }

    public void BlockUserById(String idUser, UserRepository.UserCallBack<Void> callback) {
        userRepository.BlockUserById(idUser, new UserRepository.UserCallBack<Void>() {
            @Override
            public void onSuccess(Void value) {
                Log.d(TAG, "BlockUserById: success");
                callback.onSuccess(value);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "BlockUserById: failure");
                callback.onFailure(e);
            }
        });
    }

    public void UpdateInteractionCount(String idUser) {
        userRepository.UpdateInteractionCount(idUser);
    }


}

