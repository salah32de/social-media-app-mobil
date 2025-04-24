package com.example.socialmedia.Control;

import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ChatRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.MessageRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.Chat;
import com.example.socialmedia.Model.User;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatManager {
    private final static String TAG = "TAG: ChatManager";
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


    public void GetChatsUser(String idUser, ChatRepository.GetChatsUserCallBack callBack) {
        chatRepository.GetChatsUser(idUser, new ChatRepository.GetChatsUserCallBack() {

            @Override
            public void onSuccess(List<Chat> chat) {
                AtomicInteger count = new AtomicInteger(chat.size());
                UserManager userManager = new UserManager();
                Log.d(TAG, "Get chats user success " + chat.size());

                Iterator<Chat> iterator = chat.iterator();
                while (iterator.hasNext()) {
                    Chat c = iterator.next();
                    String idUserReceiver = c.getIdUser1().equals(idUser) ? c.getIdUser2() : c.getIdUser1();

                    userManager.getUserById(idUserReceiver, new UserRepository.UserCallBack<User>() {
                        @Override
                        public void onSuccess(User value) {
                            if (value != null) {
                                c.setUserReceiver(value);
                                Log.d(TAG, "Get user by id success " + value.getName() + " 00 " + value.getId());
                            } else {
                                Log.e(TAG, "User is null!");
                            }
                            setChat();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d(TAG, "Get user by id failure " + e.getMessage());
                            synchronized (chat) {
                                iterator.remove();
                                count.decrementAndGet();
                            }
                            setChat();
                        }

                        private void setChat() {
                            if (count.decrementAndGet() == 0) {
                                callBack.onSuccess(chat);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Get chats user failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });


    }

    public void UpdateChat(Chat chat, ChatRepository.UpdateChatCallBack callBack) {
        chatRepository.UpdateChat(chat, new ChatRepository.UpdateChatCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Update chat success " + chat.getIdChat() + " " + chat.getLastMessage());
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Update chat failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }

    public void DeleteChatById(String idChat, ChatRepository.DeleteChatCallBack callBack) {
        chatRepository.DeleteChatById(idChat, new ChatRepository.DeleteChatCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Delete chat success " + idChat);
                MessageManager messageManager = new MessageManager();
                messageManager.DeleteMessagesChat(idChat, new MessageRepository.DeleteMessageCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callBack.onFailure(e);
                    }
                });

            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Delete chat failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }


}



