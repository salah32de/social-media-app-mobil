package com.example.socialmedia.Control;

import android.util.Log;

import com.example.socialmedia.Data.Firebase.RealtimeDatabase.MessageRepository;
import com.example.socialmedia.Model.Message;

import java.util.List;

public class MessageManager {
    private final String TAG="MessageManager";
    private MessageRepository messageRepository;
    public MessageManager(){
        messageRepository=new MessageRepository();
    }

    public void SendMessage(Message message, MessageRepository.SendMessageCallBack callBack){
        messageRepository.SendMessage(message, new MessageRepository.SendMessageCallBack() {
            @Override
            public void onSuccess(String idMessage) {
                Log.d(TAG,"Send message success "+idMessage+ " message "+message.getContent());
                callBack.onSuccess(idMessage);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"Send message failure "+e.getMessage());
                callBack.onFailure(e);
            }
        });
    }
    public void GetMessagesChat(String chatId, MessageRepository.GetMessagesChatCallBack callBack){
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
