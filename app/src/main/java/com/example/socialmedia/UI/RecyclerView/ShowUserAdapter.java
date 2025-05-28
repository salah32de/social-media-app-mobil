package com.example.socialmedia.UI.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.socialmedia.UI.Activity.Chat.MessagesActivity;
import com.example.socialmedia.UI.Activity.Profile;

import java.util.List;

public class ShowUserAdapter extends RecyclerView.Adapter<ShowUserAdapter.UserViewHolder> {

    private Context context;//context the activity
    private Activity activity;
    private List<User> list;//list the user

    //constructor
    public ShowUserAdapter(@NonNull Activity activity, @NonNull Context context, @NonNull List<User> list) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//create the view that will be displayed in the recycler view
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.show_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {//bind the data to the view
        holder.bind(activity, context, list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageUser;
        TextView nameUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageUser = itemView.findViewById(R.id.imageUser);
            nameUser = itemView.findViewById(R.id.nameUser);
        }

        public void bind(Activity activity, Context context, User user) {
            // download the photo profile from firebase storage
            if (!user.getPhotoProfile().isEmpty())//when the user has a photo profile else use the default photo profile
                Glide.with(context).load(user.getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(imageUser);

            nameUser.setText(user.getName());//set the name of the user


            imageUser.setOnClickListener(new View.OnClickListener() {  //when click on the user go to the profile of the user

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra(SharedPreferencesHelper.USER_KEY, user);
                    activity.startActivity(intent);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessagesActivity.class);
                    intent.putExtra(SharedPreferencesHelper.USER_KEY, user);
                    ((Activity) context).startActivity(intent);
                }
            });

        }
    }

}
