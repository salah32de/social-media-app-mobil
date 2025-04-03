package com.example.socialmedia.UI.Activity.Chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Control.ChatManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.ChatRepository;
import com.example.socialmedia.Model.Chat;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private User user;
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private RecyclerView showChatRecyclerView;
    private List<Chat> listChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listChat=new ArrayList<>();

        chatRecyclerView=findViewById(R.id.chatRecyclerView);

        user= SharedPreferencesManager.getUser(getBaseContext());

        chatRecyclerView.setLayoutManager( new LinearLayoutManager(getBaseContext()));
        chatAdapter=new ChatAdapter(listChat, getBaseContext(),ChatActivity.this);
        chatRecyclerView.setAdapter(chatAdapter);

        ChatManager chatManager=new ChatManager();
        chatManager.GetChatsUser(user.getId(), new ChatRepository.GetChatsUserCallBack() {
            @Override
            public void onSuccess(List<Chat> chat) {
                listChat.clear();
                listChat.addAll(chat);
                chatRecyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }
}