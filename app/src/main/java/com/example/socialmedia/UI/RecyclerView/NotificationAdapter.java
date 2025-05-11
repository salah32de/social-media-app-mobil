package com.example.socialmedia.UI.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Chat.MessagesActivity;
import com.example.socialmedia.UI.Activity.Profile;
import com.example.socialmedia.UI.Fragment.CommentFragment;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TEXT_VIEW_NOTIFICATION = 0;
    private final int ITEM_VIEW_TYPE = 1;
    private Activity context;
    private List<Notification> notificationList;

    public NotificationAdapter(Activity context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TEXT_VIEW_NOTIFICATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
            return new NotificationTextView(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new ItemNotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemNotificationViewHolder) {
            ((ItemNotificationViewHolder) holder).bind(context, notificationList.get(position - 1));
        } else {
            ((NotificationTextView) holder).bind();
        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TEXT_VIEW_NOTIFICATION;
        } else {
            return ITEM_VIEW_TYPE;
        }
    }

    private static class NotificationTextView extends RecyclerView.ViewHolder {
        TextView textView;

        public NotificationTextView(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }


        public void bind() {
            textView.setText("Notification");

        }
    }

    private static class ItemNotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUser;
        TextView textNotification;
        View viewNotification;

        public ItemNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUser = itemView.findViewById(R.id.imageUser);
            textNotification = itemView.findViewById(R.id.textNotification);
            viewNotification = itemView.findViewById(R.id.itemNotification);
        }


        public void bind(Context context, Notification notification) {


            if (!notification.getUser().getPhotoProfile().isEmpty())
                Glide.with(context).load(notification.getUser().getPhotoProfile()).placeholder(R.drawable.wait_download).circleCrop().into(imageUser);

            textNotification.setText(notification.getUser().getName() + " " + notification.getTextTypeNotification());

            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra(SharedPreferencesHelper.USER_KEY, notification.getUser());
                    context.startActivity(intent);
                }
            });


            viewNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //write the code in another time to open the notification

                    if (notification.getTypeNotification() == Notification.LIKE_POST || notification.getTypeNotification() == Notification.COMMENT_POST) {
                        PostManager postManager = new PostManager();
                        postManager.GetPostById(notification.getIdNotify(), new PostRepository.GetPostByIdCallback() {

                            @Override
                            public void getPostByIdSuccess(Post post) {
                                View fragment = ((AppCompatActivity) context).findViewById(R.id.showPostNotificationActivity);
                                if (fragment != null && fragment.getVisibility()==View.GONE) {
                                    fragment.setVisibility(View.VISIBLE);
                                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    CommentFragment commentFragment = new CommentFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("post", post);
                                    commentFragment.setArguments(bundle);
                                    transaction.replace(R.id.showPostNotificationActivity, commentFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            }

                            @Override
                            public void getPostByIdFailure(Exception e) {
                                Toast.makeText(context, "error in get post " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (notification.getTypeNotification() == Notification.FRIEND_REQUEST || notification.getTypeNotification() == Notification.ACCEPT_FRIEND_REQUEST) {
                        Intent intent = new Intent(context, Profile.class);
                        intent.putExtra(SharedPreferencesHelper.USER_KEY, notification.getUser());
                        context.startActivity(intent);

                    } else if (notification.getTypeNotification() == Notification.SENT_MESSAGE) {
                        Intent intent = new Intent(context, MessagesActivity.class);
                        intent.putExtra(SharedPreferencesHelper.USER_KEY, notification.getUser());
                        context.startActivity(intent);
                    }
                }
            });
        }

    }


}
