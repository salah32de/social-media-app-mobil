package com.example.socialmedia.UI.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Control.NotificationManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Model.Notification;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        User user= SharedPreferencesManager.getUser(getApplicationContext());
        RecyclerView recyclerView=findViewById(R.id.recyclerViewNotification);
        recyclerView.setPaddingRelative(9,9,9,9);
        List<Notification> list=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));



        NotificationManager notificationManager=new NotificationManager();
        notificationManager.getNotification(user.getId(), new NotificationRepository.GetNotificationCallback() {
            @Override
            public void getNotificationSuccess(List<Notification> notifications) {
                list.addAll(notifications);
                recyclerView.setAdapter(new NotificationAdapter(NotificationActivity.this,list));

            }

            @Override
            public void getNotificationFailure(Exception e) {

            }
        });


    }
}
