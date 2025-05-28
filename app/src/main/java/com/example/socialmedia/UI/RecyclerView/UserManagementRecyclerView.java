package com.example.socialmedia.UI.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Profile;

import java.util.List;

public class UserManagementRecyclerView extends RecyclerView.Adapter<UserManagementRecyclerView.UserManagementViewHolder> {

    private Context context;
    private List<User> userList;

    public UserManagementRecyclerView(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserManagementViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_dashboard, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserManagementViewHolder holder, int position) {
        holder.bind(context, userList.get(position));
    }

    @Override
    public int getItemCount() {
        if (userList != null)
            return userList.size();
        return 0;
    }

    public class UserManagementViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName, userEmail;
        Button btnBan;

        public UserManagementViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            btnBan = itemView.findViewById(R.id.btn_ban);
        }


        public void bind(Context context, User user) {
            if (user.getPhotoProfile().isEmpty()) {
                Glide.with(context).load(R.drawable.user_cicrle_duotone).into(userImage);
            } else {
                Glide.with(context).load(user.getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(userImage);
            }
            userName.setText(user.getName());
            userEmail.setText("@gmail.com");

            if (user.isActive()) {
                btnBan.setText("ban");
            } else {
                btnBan.setText("active");
            }

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra(SharedPreferencesHelper.USER_KEY, user);
                    context.startActivity(intent);
                }
            });

            btnBan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setActive(!user.isActive());
                    UserManager userManager = new UserManager();
                    userManager.UpdateUser(user, new UserRepository.UserCallBack<Void>() {


                        @Override
                        public void onSuccess(Void value) {
                            Toast.makeText(context,""+(user.isActive()?"active":"banned")+" user success",Toast.LENGTH_SHORT).show();
                            btnBan.setText(""+(user.isActive()?"active":"ban"));
                            user.setActive(!user.isActive());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, ""+(user.isActive()?"active":"ban")+" user is failure .try again another time", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }
}
