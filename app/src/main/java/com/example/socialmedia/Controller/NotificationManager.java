package com.example.socialmedia.Controller;

import android.content.Context;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.SharedPreferencesHelper;

import java.util.List;

public class NotificationManager {
    private final String TAG = "TAG: NotificationManager";
    private NotificationRepository notificationRepository;

    public NotificationManager() {
        notificationRepository = new NotificationRepository();
    }

    public void addNotification(Context context, Notification notification, String idUser, NotificationRepository.AddNotificationCallback addNotificationCallback) {
        notificationRepository.addNotification( notification,idUser, new NotificationRepository.AddNotificationCallback() {
            @Override
            public void addNotificationSuccess() {
                Log.d(TAG,"addNotificationSuccess");
                addNotificationCallback.addNotificationSuccess();

                UserManager userManager=new UserManager();
                userManager.UpdateInteractionCount(SharedPreferencesHelper.getUser(context).getId());


            }

            @Override
            public void addNotificationFailure(Exception e) {
                Log.d(TAG,"addNotificationFailure "+e.getMessage());
                addNotificationCallback.addNotificationFailure(e);
            }
        });
    }
    public void getNotification(String idUser, NotificationRepository.GetNotificationCallback getNotificationCallback) {
        notificationRepository.getNotification(idUser, new NotificationRepository.GetNotificationCallback() {
            @Override
            public void getNotificationSuccess(List<Notification> notifications) {
                Log.d(TAG,"getNotificationSuccess num of notifications: "+notifications.size());
                getNotificationCallback.getNotificationSuccess(notifications);
            }

            @Override
            public void getNotificationFailure(Exception e) {
                Log.d(TAG,"getNotificationFailure "+e.getMessage());
                getNotificationCallback.getNotificationFailure(e);
            }
        });
    }

    public void DeleteNotificationUser(String idUser) {
        notificationRepository.DeleteNotification(idUser, new NotificationRepository.DeleteNotificationCallback() {
            @Override
            public void deleteNotificationSuccess() {
                Log.d(TAG,"deleteNotificationSuccess");
            }

            @Override
            public void deleteNotificationFailure(Exception e) {
                Log.d(TAG,"deleteNotificationFailure "+e.getMessage());
            }
        });
    }

}
