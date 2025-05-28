package com.example.socialmedia.UI.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.Controller.ReportManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ReportRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;

public class Dashboard extends AppCompatActivity {

    ImageView photoProfile;
    View reports, users, posts;
    User admin;
    TextView numOfReport, numOfPosts, numOfUsers, userActive, todayPosts, newReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        photoProfile = findViewById(R.id.profile_photo);
        reports = findViewById(R.id.card_reports);
        users = findViewById(R.id.card_users);
        posts = findViewById(R.id.card_posts);
        numOfReport = findViewById(R.id.numOfReport);
        numOfPosts = findViewById(R.id.numOfPosts);
        numOfUsers = findViewById(R.id.numOfUser);
        userActive = findViewById(R.id.active_users);
        todayPosts = findViewById(R.id.today_posts);
        newReports = findViewById(R.id.reports_count);


        admin = SharedPreferencesHelper.getUser(getBaseContext());

        if (admin.getPhotoProfile().isEmpty()) {
            Glide.with(this).load(R.drawable.user_cicrle_duotone).circleCrop().into(photoProfile);
        } else {
            Glide.with(this).load(admin.getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(photoProfile);
        }

        photoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminProfile.class);
                startActivity(intent);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReviewReports.class);
                startActivity(intent);
            }
        });


        ReportManager reportManage = new ReportManager();
        reportManage.GetNumOfReport(new ReportRepository.GetNumOfReportCallback() {
            @Override
            public void getNumOfReportSuccess(long num) {
                numOfReport.setText("\uD83D\uDEA8  reports \n" + num);
            }

            @Override
            public void getNumOfReportFailure(Exception e) {
                numOfReport.setText("\uD83D\uDEA8  reports \\n ???");
            }
        });
        UserManager userManager = new UserManager();
        userManager.GetNumUser(new UserRepository.UserCallBack<Long>() {
            @Override
            public void onSuccess(Long value) {
                numOfUsers.setText("\uD83D\uDC65  users \n" + value);
            }

            @Override
            public void onFailure(Exception e) {
                numOfUsers.setText("\uD83D\uDC65  users \n???");

            }
        });
        PostManager postManager = new PostManager();
        postManager.GetCountPosts(new PostRepository.GetCountPosts() {
            @Override
            public void onSuccess(long count) {
                numOfPosts.setText("\uD83D\uDCDD  posts \n" + count);
            }

            @Override
            public void onFailure(Exception e) {
                numOfPosts.setText("\uD83D\uDCDD  posts \n???");
            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserManagement.class);
                startActivity(intent);
            }
        });
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PostsManagement.class);
                startActivity(intent);
            }
        });

        userManager.GetCountUserActive(new UserRepository.UserCallBack() {
            @Override
            public void onSuccess(Object value) {
                userActive.setText("\uD83D\uDC65 Active users: " + value);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        reportManage.NumReportOfToday(new ReportRepository.NumReportOfToday() {
            @Override
            public void onSuccess(long num) {
                newReports.setText("\uD83D\uDEA8 New reports: " + num);

            }

            @Override
            public void onFailure(Exception e) {
                newReports.setText("\uD83D\uDEA8 New reports: ???");
            }
        });

    }
}
