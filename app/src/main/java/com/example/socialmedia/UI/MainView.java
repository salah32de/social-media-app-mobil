package com.example.socialmedia.UI;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Chat.ChatActivity;
import com.example.socialmedia.UI.Activity.NotificationActivity;
import com.example.socialmedia.UI.Activity.Profile;
import com.example.socialmedia.UI.Activity.Setting.SettingActivity;
import com.example.socialmedia.UI.Fragment.CreatePostFragment;
import com.example.socialmedia.UI.Fragment.SearchFragment;

public class MainView extends AppCompatActivity {
    TextView createPost;
    ImageView chooseImage;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_view);

        user=SharedPreferencesManager.getUser(getBaseContext());

        createPost=findViewById(R.id.createPost);
        chooseImage=findViewById(R.id.chooseImage);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePostFragment createPostFragment=new CreatePostFragment();
                createPostFragment.pickImage();

            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CreatePostFragment createPostFragment=new CreatePostFragment();
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.createPostFragment,createPostFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
            }
        });

        ImageView seeProfile=findViewById(R.id.seeProfile);

        if(!user.getPhotoProfile().isEmpty()){
            Glide.with(getBaseContext()).load(user.getPhotoProfile()).circleCrop().into(seeProfile);
        }

        seeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Profile.class);
                User user= SharedPreferencesManager.getUser(getApplicationContext());
                intent.putExtra(SharedPreferencesManager.USER_KEY,user);

                startActivity(intent);
            }
        });


        ImageView notificationIcon=findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        ImageView search=findViewById(R.id.searchMainActivity);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment=new SearchFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.searchFragment,searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        ImageView setting=findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });


        ImageView chat=findViewById(R.id.chat);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });


    }


}