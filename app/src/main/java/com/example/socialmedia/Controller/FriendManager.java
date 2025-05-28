package com.example.socialmedia.Controller;

import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.FriendRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Friend;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;

import java.util.List;

public class FriendManager {

    private final String TAG = "TAG: FriendManager";

    private FriendRepository friendRepository;

    public FriendManager() {
        this.friendRepository = new FriendRepository();
    }

    public void AddFriend(Friend friend, FriendRepository.AddFriendCallback addFriendCallback) {
        friendRepository.AddFriend(friend, new FriendRepository.AddFriendCallback() {
            @Override
            public void addFriendSuccess() {
                Log.d(TAG, "AddFriend: success");
                addFriendCallback.addFriendSuccess();
            }

            @Override
            public void addFriendFailure(Exception e) {
                Log.d(TAG,"AddFriend: failure");
            }
        });
    }
    public void GetFriendsByIdUser(String idUser, FriendRepository.GetFriendsByIdUserCallback getFriendsByIdUserCallback){
        Log.d("aaaaaaaaa", "getFriendsByIdUserSuccess: ");
        friendRepository.GetFriendsByIdUser(idUser, new FriendRepository.GetFriendsByIdUserCallback() {
            @Override
            public void getFriendsByIdUserSuccess(List<User> friends) {
                Log.d(TAG, "GetFriendsByIdUser: success num friends = " + friends.size());
                getFriendsByIdUserCallback.getFriendsByIdUserSuccess(friends);
            }

            @Override
            public void getFriendsByIdUserFailure(Exception e) {
                Log.d(TAG, "GetFriendsByIdUser: failure :error "+e.getMessage() );
            }
        });
    }

    public void DeleteFriend(String idFriend, FriendRepository.DeleteFriendCallback deleteFriendCallback){
        friendRepository.DeleteFriend(idFriend, new FriendRepository.DeleteFriendCallback() {
            @Override
            public void deleteFriendSuccess() {
                Log.d(TAG,"delete friend success id:"+idFriend);
                deleteFriendCallback.deleteFriendSuccess();
            }

            @Override
            public void deleteFriendFailure(Exception e) {
                Log.d(TAG,"delete friend failure error: "+e.getMessage());
            }
        });
    }

    public void GetFriend(String idUser1, String idUser2, FriendRepository.IsFriendCallback isFriendCallback){
        friendRepository.GetFriend(idUser1, idUser2, new FriendRepository.IsFriendCallback() {
            @Override
            public void isFriendSuccess(Friend friend) {
                Log.d(TAG, "IsFriend: success");
                isFriendCallback.isFriendSuccess(friend);
            }
            @Override
            public void isFriendFailure(Exception e) {
                Log.d(TAG, "IsFriend: failure error: "+e.getMessage());
            }

        });
    }
    public void DeleteFriendUser(String idUser){
        friendRepository.DeleteFriendUser(idUser);
    }

}
