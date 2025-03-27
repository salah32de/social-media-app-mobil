package com.example.socialmedia.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Control.PostManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.ProfileAdapter;

import java.util.List;

public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        RecyclerView postProfileRecyclerView = findViewById(R.id.postProfileRecyclerView);
        postProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        User user = (User) getIntent().getSerializableExtra(SharedPreferencesManager.USER_KEY);

        PostManager postManager = new PostManager();
        postManager.getPostsUser(user, new PostRepository.GetPostsUserCallback() {
            @Override
            public void getPostsUserSuccess(List<Post> post) {
                postProfileRecyclerView.setAdapter(new ProfileAdapter(user,Profile.this, post));
            }

            @Override
            public void getPostsUserFailure(Exception e) {
                Toast.makeText(getBaseContext(), "error in get Post the user", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
