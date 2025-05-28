package com.example.socialmedia.UI.RecyclerView;

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
import com.example.socialmedia.Database.RemoteDatabase.Entity.Message;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.UI.Activity.Profile;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int SEND_MESSAGE = 1;
    private final int RECEIVE_MESSAGE = 2;
    private Context context;
    private User user;
    private List<Message> messageList;

    public MessageAdapter(Context context, User user, List<Message> messages) {
        this.context = context;
        this.user = user;
        this.messageList = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SEND_MESSAGE) {
            View view = inflater.inflate(R.layout.send_message_item, parent, false);
            return new SendMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.receive_message_item, parent, false);
            return new ReceiveMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == SEND_MESSAGE) {
            ((SendMessageViewHolder) holder).bind(context, messageList.get(position), position, messageList);
        } else {
            ((ReceiveMessageViewHolder) holder).bind(context, user, messageList.get(position), position);
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(user.getId())) {
            return SEND_MESSAGE;
        } else {
            return RECEIVE_MESSAGE;
        }
    }

    public static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView sendMessageText;
        TextView sendMessageTime;
        ImageView sendMessageImage;

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessageText = itemView.findViewById(R.id.sentMessageText);
            sendMessageTime = itemView.findViewById(R.id.sentMessageTime);
            sendMessageImage = itemView.findViewById(R.id.sentMessageImage);
        }

        public void bind(Context context, Message message, Integer position, List<Message> list) {
            if (message.getType().equals(Message.TEXT_TYPE)) {
                sendMessageText.setVisibility(View.VISIBLE);
                sendMessageText.setText(message.getContent());
                sendMessageImage.setVisibility(View.GONE);
            } else {
                sendMessageImage.setVisibility(View.VISIBLE);
                sendMessageText.setVisibility(View.GONE);
                Glide.with(context).load(message.getContent()).placeholder(R.drawable.edittext_background).into(sendMessageImage);
            }
            if (position > 0 && position < list.size() && !list.get(position - 1).getTimestamp().equals(message.getTimestamp())) {
                sendMessageTime.setText(message.getTimestamp());
                sendMessageTime.setVisibility(View.VISIBLE);
            } else{ sendMessageTime.setVisibility(View.GONE);}
        }


    }

    public class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessageText;
        TextView receivedMessageTime;
        ImageView receivedMessageImage;
        ImageView receiverProfileImage;

        public ReceiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMessageText = itemView.findViewById(R.id.receivedMessageText);
            receivedMessageTime = itemView.findViewById(R.id.receivedMessageTime);
            receivedMessageImage = itemView.findViewById(R.id.receivedMessageImage);
            receiverProfileImage = itemView.findViewById(R.id.receiverProfileImage);
        }

        public void bind(Context context, User user, Message message, int position) {
            if (message.getType().equals(Message.TEXT_TYPE)) {
                receivedMessageText.setVisibility(View.VISIBLE);
                receivedMessageText.setText(message.getContent());
                receivedMessageImage.setVisibility(View.GONE);
            } else {
                receivedMessageText.setVisibility(View.GONE);
                receivedMessageImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getContent()).placeholder(R.drawable.edittext_background).into(receivedMessageImage);
            }

            if(position > 0 && position < getItemCount() && !messageList.get(position - 1).getTimestamp().equals(message.getTimestamp())){
                receivedMessageTime.setVisibility(View.VISIBLE);
                receivedMessageTime.setText(message.getTimestamp());
            }else{
                receivedMessageTime.setVisibility(View.GONE);
            }

            if (position > 0 && position < getItemCount() && messageList.get(position - 1).getSenderId().equals(message.getSenderId())) {
                receiverProfileImage.setVisibility(View.GONE);
            } else {
                receiverProfileImage.setVisibility(View.VISIBLE);
                if (!user.getPhotoProfile().isEmpty())
                    Glide.with(context).load(user.getPhotoProfile()).circleCrop().placeholder(R.drawable.user_cicrle_duotone).into(receiverProfileImage);
            }
            receiverProfileImage.setOnClickListener(click -> {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra(SharedPreferencesHelper.USER_KEY, user);
                context.startActivity(intent);

            });
        }
    }


}
