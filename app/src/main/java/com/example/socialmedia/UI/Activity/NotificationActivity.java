package com.example.socialmedia.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.NotificationManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.R;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.UI.Activity.Chat.ChatActivity;
import com.example.socialmedia.UI.MainView;
import com.example.socialmedia.UI.RecyclerView.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        User user = SharedPreferencesHelper.getUser(getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotification);
        recyclerView.setPaddingRelative(9, 9, 9, 9);
        List<Notification> list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));


        NotificationManager notificationManager = new NotificationManager();
        notificationManager.getNotification(user.getId(), new NotificationRepository.GetNotificationCallback() {
            @Override
            public void getNotificationSuccess(List<Notification> notifications) {
                if (!NotificationActivity.this.isFinishing()) {
                    list.addAll(notifications);
                    recyclerView.setAdapter(new NotificationAdapter(NotificationActivity.this, list));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void getNotificationFailure(Exception e) {

            }
        });


        ImageView back = findViewById(R.id.backNotification);
        ImageView home = findViewById(R.id.homeNotificationActivity);
        ImageView chat = findViewById(R.id.chatIconNotificationActivity);

        View.OnClickListener activityIconListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.homeNotificationActivity) {
                    Intent intent = new Intent(getBaseContext(), MainView.class);
                    startActivity(intent);
                    finish();
                } else if (v.getId() == R.id.chatIconNotificationActivity) {
                    Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                    startActivity(intent);
                    finish();
                } else if (v.getId() == R.id.videoIconNotificationActivity) {

                } else if (v.getId() == R.id.backNotification) {
                    finish();
                }
            }
        };
        back.setOnClickListener(activityIconListener);
        home.setOnClickListener(activityIconListener);
        chat.setOnClickListener(activityIconListener);

    }
}
