package com.example.socialmedia.UI.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Profile;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public FriendAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item_view, parent, false);
        return new FriendAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        holder.bind(context, userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageUser;
        TextView nameUser;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUser = itemView.findViewById(R.id.imageUserFriend);
            nameUser = itemView.findViewById(R.id.nameUserFriend);
        }

        public void bind(Context context, User user) {
            if (user.getPhotoProfile().isEmpty()) {
                Glide.with(context).load(R.drawable.user_cicrle_duotone).into(imageUser);
            } else {
                Glide.with(context).load(user.getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(imageUser);
            }
            Log.d("aaaaaaaaaa", user.getName());
            nameUser.setText(user.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra(SharedPreferencesHelper.USER_KEY, user);
                    ((Activity) context).startActivity(intent);
                }
            });

        }

    }


}
