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
import com.example.socialmedia.Database.RemoteDatabase.Entity.Chat;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Chat.MessagesActivity;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> listChat;
    private Context context;
    private Activity activity;

    public ChatAdapter(List<Chat> listChat, Context context, Activity activity) {
        this.listChat = listChat;
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        holder.bind(activity, context, listChat.get(position));
    }

    @Override
    public int getItemCount() {
        if (listChat != null)
            return listChat.size();
        else return 0;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUser, isOnline;
        TextView username, lastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUser = itemView.findViewById(R.id.imageUser);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            isOnline = itemView.findViewById(R.id.isActive);
        }


        public void bind(Activity activity, Context context, Chat chat) {

            username.setText(chat.getUserReceiver().getName());
            if (chat.getLastMessage().contains("jpg"))
                lastMessage.setText("image");
            else
                lastMessage.setText(chat.getLastMessage());

            if (chat.getUserReceiver() != null) {

                if (chat.getUserReceiver().isOnline()) {
                    isOnline.setVisibility(View.VISIBLE);
                } else {
                    isOnline.setVisibility(View.GONE);
                }

                if (!chat.getUserReceiver().getPhotoProfile().isEmpty()) {
                    Glide.with(context).load(chat.getUserReceiver().getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(imageUser);
                } else {
                    Glide.with(context).load(R.drawable.user_cicrle_duotone).circleCrop().into(imageUser);
                }

                if (chat.getUserReceiver().isOnline())
                    isOnline.setVisibility(View.VISIBLE);
                else isOnline.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MessagesActivity.class);
                        intent.putExtra(Chat.CHAT_KEY, chat);
                        intent.putExtra(SharedPreferencesHelper.USER_KEY, chat.getUserReceiver());
                        (activity).startActivity(intent);
                    }
                });
            }
        }

    }

}
