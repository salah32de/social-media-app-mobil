package com.example.socialmedia.UI;


import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.UI.Activity.Chat.ChatActivity;
import com.example.socialmedia.UI.Activity.NotificationActivity;
import com.example.socialmedia.UI.Activity.Setting.SettingActivity;
import com.example.socialmedia.UI.Fragment.SearchFragment;
import com.example.socialmedia.UI.RecyclerView.MainViewAdapter;
import com.example.socialmedia.UI.RecyclerView.ProfileAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainView extends AppCompatActivity {

    private User user;
    private RecyclerView postRecyclerView;
    private List<Post> listPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_view);

        user = SharedPreferencesHelper.getUser(getBaseContext());


        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        ImageView search = findViewById(R.id.searchMainActivity);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.searchFragment).setVisibility(View.VISIBLE);
                SearchFragment searchFragment = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.searchFragment, searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        ImageView setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });


        ImageView chat = findViewById(R.id.chat);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        PostManager postManager = new PostManager();
        List<Post> listPost = new ArrayList<>();
        postRecyclerView = findViewById(R.id.postProfileRecyclerView);
        postRecyclerView.setAdapter(new MainViewAdapter(MainView.this, listPost, new ProfileAdapter.deletePostCallBack() {
            @Override
            public void onRemove(int position) {
                postRecyclerView.getAdapter().notifyItemRemoved(position);
            }
        }));
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        postManager.getFeedPosts(user, new PostManager.FeedPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                listPost.clear();
                listPost.addAll(posts);
                postRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getBaseContext(), "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        postRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideVisibleReportLayouts();
                return false;
            }
        });

    }

    private void hideVisibleReportLayouts() {
        int childCount = postRecyclerView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = postRecyclerView.getChildAt(i);
            View reportLayout = child.findViewById(R.id.reportLayout);
            View deletePostLayout = null;
            try {
                deletePostLayout = child.findViewById(R.id.deletePostLayout);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (reportLayout != null) {
                reportLayout.setVisibility(GONE);
            }
            if (deletePostLayout != null) deletePostLayout.setVisibility(GONE);
        }
    }

}