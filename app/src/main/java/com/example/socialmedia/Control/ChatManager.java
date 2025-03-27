package com.example.socialmedia.Control;

import android.util.Log;

import com.example.socialmedia.Data.Firebase.RealtimeDatabase.ChatRepository;
import com.example.socialmedia.Model.Chat;

public class ChatManager {
    private final static String TAG = "ChatManager";
    private ChatRepository chatRepository;

    public ChatManager() {
        chatRepository = new ChatRepository();
    }

    public void CreateChat(Chat chat, ChatRepository.CreateChatCallBack callBack) {
        chatRepository.CreateChat(chat, new ChatRepository.CreateChatCallBack() {
            @Override
            public void onSuccess(String idChat) {
                Log.d(TAG, "Create chat success " + idChat);
                callBack.onSuccess(idChat);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Create chat failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }

    public void GetChat(String idChat, ChatRepository.GetChatCallBack callBack) {
        chatRepository.GetChat(idChat, new ChatRepository.GetChatCallBack() {
            @Override
            public void onSuccess(Chat chat) {
                Log.d(TAG, "Get chat success " + chat.getIdChat());
                callBack.onSuccess(chat);


            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Get chat failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }




}



