package com.example.socialmedia.Controller;

import android.content.Context;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ChatsRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.MessagesRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Chat;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Message;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatsManager {
    private final static String TAG = "TAG: ChatsManager";
    private ChatsRepository chatRepository;
    private MessagesRepository messageRepository;


    public ChatsManager() {
        chatRepository = new ChatsRepository();
        messageRepository=new MessagesRepository();
    }

    public void CreateChat(Chat chat, ChatsRepository.CreateChatCallBack callBack) {
        chatRepository.CreateChat(chat, new ChatsRepository.CreateChatCallBack() {
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

    public void GetChat(String idChat, ChatsRepository.GetChatCallBack callBack) {
        chatRepository.GetChat(idChat, new ChatsRepository.GetChatCallBack() {
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


    public void GetChatsUser(String idUser, ChatsRepository.GetChatsUserCallBack callBack) {
        chatRepository.GetChatsUser(idUser, new ChatsRepository.GetChatsUserCallBack() {

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

    public void UpdateChat(Chat chat, ChatsRepository.UpdateChatCallBack callBack) {
        chatRepository.UpdateChat(chat, new ChatsRepository.UpdateChatCallBack() {
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

    public void DeleteChatById(String idChat, ChatsRepository.DeleteChatCallBack callBack) {
        chatRepository.DeleteChatById(idChat, new ChatsRepository.DeleteChatCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Delete chat success " + idChat);
                DeleteMessagesChat(idChat, new MessagesRepository.DeleteMessageCallBack() {
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



    public void SendMessage(Context context, Chat chat, Message message, MessagesRepository.SendMessageCallBack callBack) {

        UpdateChat(chat, new ChatsRepository.UpdateChatCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Update chat success");
                messageRepository.SendMessage(message, new MessagesRepository.SendMessageCallBack() {
                    @Override
                    public void onSuccess(String idMessage) {
                        Log.d(TAG, "Send message success " + idMessage + " message " + message.getContent());
                        callBack.onSuccess(idMessage);

                        Notification notification=new Notification(message.getReceiverId(),chat.getIdChat(),Notification.SENT_MESSAGE);
                        NotificationManager notificationManager=new NotificationManager();
                        notificationManager.addNotification(context,notification, message.getSenderId(), new NotificationRepository.AddNotificationCallback() {
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

    public void GetMessagesChat(String chatId, MessagesRepository.GetMessagesChatCallBack callBack) {
        messageRepository.GetMessagesChat(chatId, new MessagesRepository.GetMessagesChatCallBack() {

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

    public void DeleteMessagesChat(String chatId, MessagesRepository.DeleteMessageCallBack callBack) {
        messageRepository.DeleteMessagesChat(chatId, new MessagesRepository.DeleteMessageCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Delete messages chat success " + chatId);
                callBack.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Delete messages chat failure " + e.getMessage());
                callBack.onFailure(e);
            }
        });
    }
}



