package com.example.socialmedia.UI.Activity;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.ProfileAdapter;

import java.util.List;

public class Profile extends AppCompatActivity {
    private RecyclerView postProfileRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        postProfileRecyclerView = findViewById(R.id.postProfileRecyclerView);
        postProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        User user = (User) getIntent().getSerializableExtra(SharedPreferencesHelper.USER_KEY);

        PostManager postManager = new PostManager();
        postManager.getPostsUser(user, new PostRepository.GetPostsUserCallback() {
            @Override
            public void getPostsUserSuccess(List<Post> post) {
                postProfileRecyclerView.setAdapter(new ProfileAdapter(user, Profile.this, post, new ProfileAdapter.deletePostCallBack() {
                    @Override
                    public void onRemove(int position) {
                        postProfileRecyclerView.getAdapter().notifyItemRemoved(position);
                    }
                }));
                postProfileRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void getPostsUserFailure(Exception e) {
                Toast.makeText(getBaseContext(), "error in get Post the user", Toast.LENGTH_SHORT).show();
            }
        });

        postProfileRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideVisibleReportLayouts();
                return false;
            }
        });

    }

    private void hideVisibleReportLayouts() {
        int childCount = postProfileRecyclerView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = postProfileRecyclerView.getChildAt(i);
            View reportLayout = child.findViewById(R.id.reportLayout);
            View deletePostLayout = null;
            try {
                deletePostLayout=child.findViewById(R.id.deletePostLayout);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            if (reportLayout != null) {
                reportLayout.setVisibility(GONE);
            }
            if(deletePostLayout!=null)deletePostLayout.setVisibility(GONE);
        }

    }



}
