package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import com.example.socialmedia.Control.UserManager;
import com.example.socialmedia.Model.Notification;
import com.example.socialmedia.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationRepository {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference DRef;

    public NotificationRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DRef = firebaseDatabase.getReference("notifications");
    }
    //notifications{
    //              idUser1{
    //                      idNotification1
    //                      idNotification2
    //              }
    //              idUser2{
    //                      idNotification1
    //                      idNotification2
    //                      idNotification3
    //                      idNotification4
    //              }
    //              idUser3{
    //                      idNotification1
    //              }
    //}

    //NotificationActivity{
    // idNotification
    // idUserNotify
    // typeNotification
    //
    // }


    public void addNotification(Notification notification, String idUser, AddNotificationCallback addNotificationCallback) {
        DatabaseReference ref = DRef.child(idUser).getRef();
        notification.setIdNotification(ref.push().getKey());
        ref.child(notification.getIdNotification()).setValue(notification).addOnSuccessListener(aVoid -> {
            addNotificationCallback.addNotificationSuccess();
        }).addOnFailureListener(e -> {
            addNotificationCallback.addNotificationFailure(e);
        });
    }


    public void getNotification(String idUser, GetNotificationCallback getNotificationCallback) {
        DatabaseReference ref = DRef.child(idUser);
        List<Notification> list = new ArrayList<>();
        ref.get().addOnSuccessListener(dataSnapshot -> {
            AtomicInteger counter = new AtomicInteger(0);//counter for asynchronous operations
            UserManager userManager = new UserManager();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Notification notification = snapshot.getValue(Notification.class);
                list.add(0, notification);

                counter.incrementAndGet(); // increment the counter when asynchronous operation is start

                //get user create comment
                userManager.getUserById(notification.getIdUserNotify(), new UserRepository.UserCallBack<User>() {
                    @Override
                    public void onSuccess(User value) {
                        notification.setUser(value);//set the user create comment
                        if (counter.decrementAndGet() == 0) {//if all asynchronous operations are finished
                            getNotificationCallback.getNotificationSuccess(list);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        list.remove(notification); // delete the comment if get user is failed
                        if (counter.decrementAndGet() == 0) {//if all asynchronous operations are finished
                            getNotificationCallback.getNotificationSuccess(list);
                        }
                    }
                });
            }


        });
    }

    public void DeleteNotification(String idUser, DeleteNotificationCallback deleteNotificationCallback) {
        DRef.child(idUser).removeValue().addOnSuccessListener(aVoid -> {
            deleteNotificationCallback.deleteNotificationSuccess();
        }).addOnFailureListener(e -> {
            deleteNotificationCallback.deleteNotificationFailure(e);
        });
    }


    public interface AddNotificationCallback {
        void addNotificationSuccess();

        void addNotificationFailure(Exception e);
    }

    public interface GetNotificationCallback {
        void getNotificationSuccess(List<Notification> notifications);

        void getNotificationFailure(Exception e);
    }

    public interface DeleteNotificationCallback {
        void deleteNotificationSuccess();

        void deleteNotificationFailure(Exception e);
    }
}
