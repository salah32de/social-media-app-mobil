package com.example.socialmedia.Control;

import android.util.Log;

import com.example.socialmedia.Data.Firebase.RealtimeDatabase.ChatRepository;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.MessageRepository;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Model.Chat;
import com.example.socialmedia.Model.Message;
import com.example.socialmedia.Model.Notification;

import java.util.List;

public class MessageManager {
    private final String TAG = "TAG: MessageManager";
    private MessageRepository messageRepository;

    public MessageManager() {
        messageRepository = new MessageRepository();
    }

    public void SendMessage(Chat chat, Message message,MessageRepository.SendMessageCallBack callBack) {

        ChatManager chatManager = new ChatManager();
        chatManager.UpdateChat(chat, new ChatRepository.UpdateChatCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Update chat success");
                messageRepository.SendMessage(message, new MessageRepository.SendMessageCallBack() {
                    @Override
                    public void onSuccess(String idMessage) {
                        Log.d(TAG, "Send message success " + idMessage + " message " + message.getContent());
                        callBack.onSuccess(idMessage);

                        Notification notification=new Notification(message.getReceiverId(),chat.getIdChat(),Notification.SENT_MESSAGE);
                        NotificationManager notificationManager=new NotificationManager();
                        notificationManager.addNotification(notification, message.getSenderId(), new NotificationRepository.AddNotificationCallback() {
                            @Override
                            public void addNotificationSuccess() {
                                Log.d(TAG,"add notification success");
                            }

                            @Override
                            public void addNotificationFailure(Exception e) {
                                Log.d(TAG,"add notification failure "+e.getMessage());
                            }
                        });

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "Send message failure " + e.getMessage());
                        callBack.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Update chat failure " + e.getMessage());
            }
        });


    }

    public void GetMessagesChat(String chatId, MessageRepository.GetMessagesChatCallBack callBack) {
        messageRepository.GetMessagesChat(chatId, new MessageRepository.GetMessagesChatCallBack() {

            @Override
            public void onSuccess(List<Message> messageList) {
                Log.d(TAG, "Get messages chat success " + messageList.size());
                callBack.onSuccess(messageList);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Get messages chat failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }


}
