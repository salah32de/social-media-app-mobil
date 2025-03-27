package com.example.socialmedia.UI.Activity.Chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Control.ChatManager;
import com.example.socialmedia.Control.MessageManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.ChatRepository;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.MessageRepository;
import com.example.socialmedia.Model.Chat;
import com.example.socialmedia.Model.Message;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.MessageAdapter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private Chat chat;
    private RecyclerView messagesRecyclerView;
    private User senderUser;
    private User receivedUser;
    private List<Message> messageList;
    private ImageView chatUserImage;
    private TextView chatUserName;
    private ImageView attachImageButton;
    private EditText messageEditText;
    private ImageView sendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageList = new ArrayList<>();

        senderUser = (User) getIntent().getSerializableExtra(SharedPreferencesManager.USER_KEY);
        receivedUser = SharedPreferencesManager.getUser(getBaseContext());


        chat = (Chat) getIntent().getSerializableExtra(Chat.CHAT_KEY);
        if (chat == null) {
            ChatManager chatManager = new ChatManager();
            chatManager.GetChat(senderUser.getId() + "_" + receivedUser.getId(), new ChatRepository.GetChatCallBack() {
                @Override
                public void onSuccess(Chat chat) {
                    MessageActivity.this.chat = chat;
                    getMessages();
                }

                @Override
                public void onFailure(Exception e) {
                    // إذا فشل الحصول على المحادثة بالـ ID الأول، جرب الـ ID المعكوس
                    chatManager.GetChat(receivedUser.getId() + "_" + senderUser.getId(), new ChatRepository.GetChatCallBack() {
                        @Override
                        public void onSuccess(Chat chat) {
                            MessageActivity.this.chat = chat;
                            getMessages();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // يمكنك وضع رسالة خطأ هنا مثلاً
                        }
                    });
                }
            });


        } else {
            getMessages();
        }


        chatUserImage = findViewById(R.id.chatUserImage);
        chatUserName = findViewById(R.id.chatUserName);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        attachImageButton = findViewById(R.id.attachImageButton);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);


        chatUserName.setText(senderUser.getName());

        if (!senderUser.getPhotoProfile().isEmpty())
            Glide.with(getBaseContext()).load(senderUser.getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(chatUserImage);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                ChatManager chatManager = new ChatManager();

                if (chat == null) {
                    chat = new Chat(receivedUser.getId(), senderUser.getId());

                    chatManager.CreateChat(chat, new ChatRepository.CreateChatCallBack() {
                        @Override
                        public void onSuccess(String idChat) {
                            Toast.makeText(MessageActivity.this, "send message success", Toast.LENGTH_SHORT).show();
                            Message m = new Message(idChat, senderUser.getId(), receivedUser.getId(), message, getCurrentTime(), Message.TEXT_TYPE);
                            sendMessage(m);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(MessageActivity.this, "send message failed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    Message m = new Message(chat.getIdChat(), senderUser.getId(), receivedUser.getId(), message, getCurrentTime(), Message.TEXT_TYPE);
                    sendMessage(m);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(getBaseContext(), senderUser, messageList);
        messagesRecyclerView.setAdapter(messageAdapter);

    }

    private void sendMessage(Message message) {
        MessageManager messageManager = new MessageManager();
        messageManager.SendMessage(message, new MessageRepository.SendMessageCallBack() {

            @Override
            public void onSuccess(String idMessage) {
                Toast.makeText(MessageActivity.this, "send message success", Toast.LENGTH_SHORT).show();
                messageList.add(message);
                messagesRecyclerView.getAdapter().notifyItemChanged(messageList.size());
                messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                messageEditText.setText("");
                messageEditText.requestFocus();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getBaseContext(), "send message failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.now().format(formatter);
    }

    private void getMessages() {
        MessageManager messageManager = new MessageManager();
        messageManager.GetMessagesChat(chat.getIdChat(), new MessageRepository.GetMessagesChatCallBack() {
            @Override
            public void onSuccess(List<Message> messageLi) {
                messageList.clear();
                messageList.addAll(messageLi);
                messagesRecyclerView.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onFailure(Exception e) {
            }
        });

    }
}