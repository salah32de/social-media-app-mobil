package com.example.socialmedia.Data.Firebase.RealtimeDatabase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmedia.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private DatabaseReference userRef;

    public UserRepository() {
        userRef = FirebaseDatabase.getInstance().getReference("user");
    }


    public void getUserById(String idUser, UserCallBack<User> callback) {
        userRef.child(idUser).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                callback.onSuccess(user);
            } else {
                callback.onFailure(new Exception("User not found"));
            }
        });
    }



    //search user by name
    public void SearchUserByName(String nameUser, UserCallBack<List<User>> callback) {

        Query query = userRef.orderByChild("listNameIndex"); //get all listNameIndex from user node

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();

                if (dataSnapshot.exists()) {//if the user exists
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {// for each user in the list
                        User user = snapshot.getValue(User.class);//get user

                        // verify if the user name contains the nameUser
                        if (user != null && user.getListNameIndex() != null && user.getListNameIndex().contains(nameUser.toLowerCase())) {
                            userList.add(user);//add user to the list
                        }
                    }

                    if (!userList.isEmpty()) {//if the list is not empty
                        callback.onSuccess(userList);
                    } else {//if the list is empty
                        callback.onFailure(new Exception("User not found"));
                    }
                } else {//if the user does not exist
                    callback.onFailure(new Exception("User not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());//if the query is cancelled
            }
        });

    }
    public void DeleteUser(String idUser, UserCallBack<Void> callback) {
        userRef.child(idUser).removeValue().addOnSuccessListener(aVoid -> {
            callback.onSuccess(aVoid);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }
    public void UpdateUser(User user, UserCallBack<Void> callback) {
        userRef.child(user.getId()).setValue(user).addOnSuccessListener(aVoid -> {
            callback.onSuccess(aVoid);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }


    public interface UserCallBack<T> {
        void onSuccess(T value);

        void onFailure(Exception e);
    }


}
