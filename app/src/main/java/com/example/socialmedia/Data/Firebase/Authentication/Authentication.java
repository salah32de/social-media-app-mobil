package com.example.socialmedia.Data.Firebase.Authentication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmedia.Control.AuthenticationManager;
import com.example.socialmedia.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Authentication {


    private FirebaseAuth auth;
    private DatabaseReference reference;

    public Authentication() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("user");
    }

    public void addUser(Activity activity, User user, String password, AuthenticationManager.authCallBack callBack) {
        //create new account with email and password
        //id user in authentication generated by firebase
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(activity, task -> {
                    //if account create successfully in firebase authentication
                    if (task.isSuccessful()) {
                        //then add the user in realtime database
                        user.setListNameIndex(generateNameIndex(user.getName()));
                        //for tack the id of the user in firebase authentication
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            //save id that in firebase authentication in the user Realtime database
                            String id = firebaseUser.getUid();
                            user.setId(id);
                            reference.child(id).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            //if add successfully in add user in realtime database
                                            Log.d("Authentication", "123User added successfully");
                                            callBack.onSuccessful(true);
                                        } else {
                                            //if add failed in add user in realtime database
                                            Log.d("Authentication", "Error:in add User in realtime database");
                                            callBack.onSuccessful(false);
                                            //delete the account that create in firebase authentication
                                            firebaseUser.delete();
                                        }
                                    });
                        }

                    } else {
                        //if account create failed in firebase authentication
                        Log.e("Authentication", "Error: " + task.getException().getMessage());
                        callBack.onSuccessful(false);
                        callBack.onFailure(task.getException());
                    }

                });
    }



    public void checkEmail(String email, AuthenticationManager.authCallBack callBack) {
        //verification the email is exist or not in firebase authentication by using createUserWithEmailAndPassword() methode
        //if user exist in firebase authentication return true else return false
        //we create new account with email and password to check the email is exist or not and that delete the account after check
        auth.createUserWithEmailAndPassword(email, "password").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                user.delete();
                callBack.onSuccessful(false);
            } else {
                callBack.onSuccessful(true);
            }
        });


    }

    public void SignInByEmailAndPassword(String email, String password, AuthenticationManager.checkSingInCallBack callBack) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String id = auth.getCurrentUser().getUid();
                callBack.onSuccessful(id);
            } else {
                callBack.onFailure(task.getException());
            }
        });
    }

    public void SignOut() {
        auth.signOut();
    }

    public void DeleteUser(DeleteAccountUserAutCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.deleteAccountUserAutSuccess();
                } else {
                    callback.deleteAccountUserAutFailure(task.getException());
                }
            }
        });
    }

    public void UpdatePassword(String oldPassword, String newPassword, AuthenticationManager.authCallBack callBack) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            callBack.onFailure(new Exception("User is not logged in"));
            return;
        }

        String email = user.getEmail();
        if (email == null) {
            callBack.onFailure(new Exception("User email is null"));
            return;
        }


        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccessful(true);
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
            } else {
                callBack.onFailure(authTask.getException());
            }
        });
    }



// Fonction pour générer les index de nom
private List<String> generateNameIndex(String name) {
    List<String> nameParts = new ArrayList<>();
    for (int i = 0; i < name.length(); i++) {
        for (int j = i + 1; j <= name.length(); j++) {
            nameParts.add(name.substring(i, j));
        }
    }
    return nameParts;
}


public interface DeleteAccountUserAutCallback {
    void deleteAccountUserAutSuccess();

    void deleteAccountUserAutFailure(Exception e);
}
}
