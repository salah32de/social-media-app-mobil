package com.example.socialmedia.UI.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Profile;
import com.example.socialmedia.UI.Fragment.CreatePostFragment;

import java.util.List;

public class MainViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private ProfileAdapter.deletePostCallBack deletePostCallBack;
    private final int POST_TYPE = 1;
    private final int CREATE_POST_TYPE = 0;

    public MainViewAdapter(Context context, List<Post> posts, ProfileAdapter.deletePostCallBack deletePostCallBack) {
        this.context = context;
        this.posts = posts;
        this.deletePostCallBack=deletePostCallBack;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.post_item, parent, false);
            return new ProfileAdapter.PostViewHolder(view);

        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.create_post_item, parent, false);
            return new CreatePostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfileAdapter.PostViewHolder) {
            ((ProfileAdapter.PostViewHolder) holder).bind(posts.get(position-1), context,posts,position,deletePostCallBack);
        } else {
            ((CreatePostViewHolder) holder).bind(context);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size() + 1;
    }

    public int getItemViewType(int position) {
        if (position == 0) return CREATE_POST_TYPE;
        else return POST_TYPE;
    }
    public static class CreatePostViewHolder extends RecyclerView.ViewHolder {
        ImageView seeProfile, chooseImage;
        TextView createPost;

        public CreatePostViewHolder(@NonNull View itemView) {
            super(itemView);
            seeProfile = itemView.findViewById(R.id.seeProfile);
            chooseImage = itemView.findViewById(R.id.chooseImage);
            createPost = itemView.findViewById(R.id.createPost);

        }

        public void bind(Context context) {
            User user = SharedPreferencesHelper.getUser(context);

            if (!user.getPhotoProfile().isEmpty()) {
                Glide.with(context).load(user.getPhotoProfile()).circleCrop().into(seeProfile);
            }

            seeProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Profile.class);
                    User user = SharedPreferencesHelper.getUser(context);
                    intent.putExtra(SharedPreferencesHelper.USER_KEY, user);
                    (context).startActivity(intent);
                }
            });


//            chooseImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    CreatePostFragment createPostFragment = new CreatePostFragment();
//                    createPostFragment.pickImage();
//
//                }
//            });

            createPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View fragment = ((AppCompatActivity) context).findViewById(R.id.createPostFragment);
                    fragment.setVisibility(View.VISIBLE);
                    CreatePostFragment createPostFragment = new CreatePostFragment();
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.createPostFragment, createPostFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

    }


}
