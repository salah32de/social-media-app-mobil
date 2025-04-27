package com.example.socialmedia.UI.Activity.Chat;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Control.ChatsManager;
import com.example.socialmedia.Control.RoomDatabaseManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.LocalDatabase.Entity.ChatEntity;
import com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ChatsRepository;
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
    private RoomDatabaseManager roomDatabaseManager;
    private List<Chat> listChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listChat=new ArrayList<>();

        roomDatabaseManager =new RoomDatabaseManager(getBaseContext());
        chatRecyclerView=findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager( new LinearLayoutManager(getBaseContext()));

        user= SharedPreferencesHelper.getUser(getBaseContext());



        if(!isNetworkAvailable()){
            new Thread(() -> {
                List<ChatEntity> chatEntities = roomDatabaseManager.getAllChatsByIdUser(user.getId());
                List<Chat> chats = new ArrayList<>();

                for (ChatEntity chatEntity : chatEntities) {
                    Chat chat = new Chat(chatEntity.getIdUser1(), chatEntity.getIdUser2(), chatEntity.getLastMessage());
                    chat.setIdChat(chatEntity.getId());
                    UserEntity userEntity=roomDatabaseManager.getUser(chatEntity.getIdUser1().equals(SharedPreferencesHelper.getUser(ChatActivity.this).getId())?chatEntity.getIdUser2():chatEntity.getIdUser1());

                    User user=new User();
                    user.setId(userEntity.getId());
                    user.setName(userEntity.getName());
                    chat.setUserReceiver(user);
                    chats.add(chat);
                }

                runOnUiThread(() -> {
                    chatAdapter = new ChatAdapter(chats, getBaseContext(), ChatActivity.this);
                    chatRecyclerView.setAdapter(chatAdapter);

                    Toast.makeText(ChatActivity.this, "Loaded from local DB", Toast.LENGTH_SHORT).show();
                });

            }).start();

        }


    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;

    }

    @Override
    protected void onResume(){
        super.onResume();
        ChatsManager chatManager=new ChatsManager();

        if(isNetworkAvailable()){
            chatManager.GetChatsUser(user.getId(), new ChatsRepository.GetChatsUserCallBack() {
                @Override
                public void onSuccess(List<Chat> chat) {
                    Toast.makeText(ChatActivity.this, "asasdas", Toast.LENGTH_SHORT).show();
                    chatAdapter=new ChatAdapter(chat, getBaseContext(),ChatActivity.this);
                    chatRecyclerView.setAdapter(chatAdapter);
                    chatRecyclerView.getAdapter().notifyDataSetChanged();

                    roomDatabaseManager=new RoomDatabaseManager(getApplicationContext());
                    roomDatabaseManager.UpdateChatInRoomDB(chat);
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }
}