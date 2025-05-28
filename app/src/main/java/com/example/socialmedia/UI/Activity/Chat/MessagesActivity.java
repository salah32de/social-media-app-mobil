package com.example.socialmedia.UI.Activity.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Controller.ChatsManager;
import com.example.socialmedia.Controller.StorageManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Chat;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Message;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ChatsRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.MessagesRepository;
import com.example.socialmedia.Database.RemoteDatabase.StorageDatabase.StorageFirebase;
import com.example.socialmedia.R;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.UI.RecyclerView.MessageAdapter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Chat chat;
    private RecyclerView messagesRecyclerView;
    private User receivedUser;
    private User senderUser;
    private List<Message> messageList;
    private ImageView chatUserImage;
    private TextView chatUserName;
    private ImageView attachImageButton;
    private EditText messageEditText;
    private ImageView sendMessageButton;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        ImageView settingButton = findViewById(R.id.setting);
        CardView deleteChatLayout = findViewById(R.id.deleteChatLayout);
        TextView deleteChat = findViewById(R.id.deleteChat);


        settingButton.setOnClickListener(v -> {
            if (deleteChatLayout.getVisibility() == View.GONE) {
                deleteChatLayout.setVisibility(View.VISIBLE);
            } else {
                deleteChatLayout.setVisibility(View.GONE);
            }
        });


        messageList = new ArrayList<>();

        receivedUser = (User) getIntent().getSerializableExtra(SharedPreferencesHelper.USER_KEY);
        senderUser = SharedPreferencesHelper.getUser(getBaseContext());


        chat = (Chat) getIntent().getSerializableExtra(Chat.CHAT_KEY);
        if (chat == null) {
            ChatsManager chatManager = new ChatsManager();
            chatManager.GetChat(receivedUser.getId() + "_" + senderUser.getId(), new ChatsRepository.GetChatCallBack() {
                @Override
                public void onSuccess(Chat chat) {
                    MessagesActivity.this.chat = chat;
                    getMessages();
                }

                @Override
                public void onFailure(Exception e) {
                    // إذا فشل الحصول على المحادثة بالـ ID الأول، جرب الـ ID المعكوس
                    chatManager.GetChat(senderUser.getId() + "_" + receivedUser.getId(), new ChatsRepository.GetChatCallBack() {
                        @Override
                        public void onSuccess(Chat chat) {
                            MessagesActivity.this.chat = chat;
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


        chatUserName.setText(receivedUser.getName());

        if (!receivedUser.getPhotoProfile().isEmpty())
            Glide.with(getBaseContext()).load(receivedUser.getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(chatUserImage);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                ChatsManager chatManager = new ChatsManager();
                String chatId = "";
                if (chat == null) {

                    chat = new Chat(senderUser.getId(), receivedUser.getId(), message);

                    chatManager.CreateChat(chat, new ChatsRepository.CreateChatCallBack() {
                        @Override
                        public void onSuccess(String idChat) {
                            Toast.makeText(MessagesActivity.this, "send message success", Toast.LENGTH_SHORT).show();
                            Message m = new Message(idChat, receivedUser.getId(), senderUser.getId(), message, getCurrentTime(), Message.TEXT_TYPE);
                            sendMessage(m);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(MessagesActivity.this, "send message failed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    Message m = new Message(chat.getIdChat(), receivedUser.getId(), senderUser.getId(), message, getCurrentTime(), Message.TEXT_TYPE);
                    sendMessage(m);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(MessagesActivity.this, receivedUser, messageList);
        messagesRecyclerView.setAdapter(messageAdapter);


        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        deleteChat.setOnClickListener(v -> {
            if (chat != null) {
                messageList.clear();
                messageAdapter.notifyDataSetChanged();
                ChatsManager chatManager = new ChatsManager();
                chatManager.DeleteChatById(chat.getIdChat(), new ChatsRepository.DeleteChatCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MessagesActivity.this, "delete chat success", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MessagesActivity.this, "delete chat failure .Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    private void sendMessage(Message message) {
        chat.setLastMessage(message.getContent());
        User u = chat.getUserReceiver();
        chat.setUserReceiver(null);
        ChatsManager chatManager = new ChatsManager();
        chatManager.SendMessage(getBaseContext(), chat, message, new MessagesRepository.SendMessageCallBack() {

            @Override
            public void onSuccess(String idMessage) {
                messagesRecyclerView.getAdapter().notifyItemChanged(messageList.size());
                messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                messageEditText.setText("");
                messageEditText.requestFocus();
                chat.setUserReceiver(u);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getBaseContext(), "send message failed", Toast.LENGTH_SHORT).show();
                chat.setUserReceiver(u);
            }
        });
    }

    public static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.now().format(formatter);
    }

    private void getMessages() {
        ChatsManager chatManager = new ChatsManager();
        chatManager.GetMessagesChat(chat.getIdChat(), new MessagesRepository.GetMessagesChatCallBack() {
            @Override
            public void onSuccess(List<Message> messageLi) {
                messageList.clear();
                messageList.addAll(messageLi);
                messagesRecyclerView.getAdapter().notifyItemChanged(messageList.size());
                messagesRecyclerView.scrollToPosition(messageList.size() - 1);

            }

            @Override
            public void onFailure(Exception e) {
            }
        });

    }


    //user choose image from his phone
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //get the result of user choose
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();//get the uri of image or video user choose

            if (requestCode == PICK_IMAGE_REQUEST) {//if user choose image

                StorageManager storageManager = new StorageManager();
                storageManager.uploadImage(uri, new StorageFirebase.UploadCallback() {
                    @Override
                    public void onSuccess(String downloadUrl) {
                        Message m = new Message(chat.getIdChat(), receivedUser.getId(), senderUser.getId(), downloadUrl, getCurrentTime(), Message.IMAGE_TYPE);
                        sendMessage(m);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MessagesActivity.this, "error in upload image .try again another time", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View deleteChatLayout = findViewById(R.id.deleteChatLayout);
             if (deleteChatLayout.getVisibility() == View.VISIBLE &&
                    !isTouchInsideView(ev, deleteChatLayout)) {
                deleteChatLayout.setVisibility(View.GONE);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

     private boolean isTouchInsideView(MotionEvent ev, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float x = ev.getRawX();
        float y = ev.getRawY();

        return x >= location[0] &&
                x <= (location[0] + view.getWidth()) &&
                y >= location[1] &&
                y <= (location[1] + view.getHeight());
    }

}