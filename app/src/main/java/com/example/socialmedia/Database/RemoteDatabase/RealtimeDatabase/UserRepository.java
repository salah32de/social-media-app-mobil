package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
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

        userRef.child(idUser);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot dataSnapshot = snapshot.child(idUser);
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.d("asodwqodmqwodmqwodmqwod", user.getName() + " user is " + user.isActive());
                        if (user.isActive())
                            callback.onSuccess(user);
                        else callback.onFailure(new Exception("user is banned"));

                    } else {
                        callback.onFailure(new Exception("user not found"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                        if (user != null && user.isActive() && user.getListNameIndex() != null && user.getListNameIndex().contains(nameUser.toLowerCase())) {
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


    public void UpdateStateUser(String idUser, boolean state, UserCallBack<Void> callback) {
        userRef.child(idUser).child("online").setValue(state).addOnSuccessListener(aVoid -> {
            callback.onSuccess(aVoid);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    public void UpdateLastSeenTimeUser(User user, long time, UserCallBack<Void> callback) {
        user.setLastSeenTime(time);
        userRef.child(user.getId()).child("lastSeenTime").setValue(time).addOnSuccessListener(aVoid -> {
            callback.onSuccess(aVoid);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    public void UpdateTotalActiveTimeUser(User user, long time, UserCallBack<Void> callback) {
        user.setTotalActiveTime(time - user.getLastSeenTime() + user.getTotalActiveTime());
        userRef.child(user.getId()).child("totalActiveTime").setValue(time - user.getLastSeenTime() + user.getTotalActiveTime()).addOnSuccessListener(aVoid -> {
            callback.onSuccess(aVoid);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }

    public void UpdateInteractionCount(String idUser){
        userRef.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                userRef.child(idUser).child("interactionCount").setValue(user.getInteractionCount()+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void GetUsers(GetUsersCallback callback) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                callback.onSuccess(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void GetNumUser(UserCallBack callBack) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callBack.onSuccess(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onFailure(error.toException());
            }
        });
    }

    public void GetCountUserActive(UserCallBack callBack) {
        Query query = userRef.orderByChild("online").startAt(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callBack.onSuccess(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onFailure(error.toException());
            }
        });
    }

    public void BlockUserById(String idUser, UserCallBack<Void> callback) {
        userRef.child(idUser).child("active").setValue(false).addOnSuccessListener(aVoid -> {
            callback.onSuccess(aVoid);
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
        });
    }



    public interface UserCallBack<T> {
        void onSuccess(T value);

        void onFailure(Exception e);
    }

    public interface GetUsersCallback {
        void onSuccess(List<User> userList);

        void onFailure(Exception e);
    }

    private List<String> generateNameIndex(String name) {
        List<String> nameParts = new ArrayList<>();
        for (int i = 0; i < name.length(); i++) {
            for (int j = i + 1; j <= name.length(); j++) {
                nameParts.add(name.substring(i, j));
            }
        }
        return nameParts;
    }

}
