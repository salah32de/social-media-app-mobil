package com.example.socialmedia.Data.Firebase.RealtimeDatabase;

import androidx.annotation.NonNull;

import com.example.socialmedia.Model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatRepository {
    DatabaseReference DRef;
    FirebaseDatabase firebaseDatabase;

    public ChatRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DRef = firebaseDatabase.getReference("chat");
    }

    public void CreateChat(Chat chat, CreateChatCallBack callBack) {
        chat.setIdChat(chat.getIdUser1() + "_" + chat.getIdUser2());
        DRef.child(chat.getIdChat()).setValue(chat).addOnSuccessListener(aVoid -> {
            callBack.onSuccess(chat.getIdChat());
        }).addOnFailureListener(e -> {
            callBack.onFailure(e);
        });
    }

    public void GetChat(String idChat, GetChatCallBack callBack) {
        DRef.child(idChat).get().addOnSuccessListener(dataSnapshot -> {
            Chat chat = dataSnapshot.getValue(Chat.class);
            if (chat != null) {
                callBack.onSuccess(chat);
            } else {
                callBack.onFailure(new Exception("chat not found"));
            }
        }).addOnFailureListener(e -> {
            callBack.onFailure(e);
        });

    }

    public void UpdateChat(Chat chat, UpdateChatCallBack callBack) {
        DRef.child(chat.getIdChat()).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callBack.onSuccess();
                } else {
                    callBack.onFailure(task.getException());
                }
            }
        });
    }

    public void GetChatsUser(String idUser, GetChatsUserCallBack callBack) {

        Query query1 = DRef.orderByChild("idUser1").startAt(idUser);
        Query query2 = DRef.orderByChild("idUser2").equalTo(idUser);

        AtomicInteger count = new AtomicInteger(2);
        AtomicBoolean hasFailure = new AtomicBoolean(false);
        Set<Chat> chats = new HashSet<>();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null) {
                        chats.add(chat);
                    }
                }
                setChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                synchronized (hasFailure) {
                    hasFailure.set(true);
                }
                setChat();
            }

            private void setChat() {
                if (count.decrementAndGet() == 0) {
                    if (hasFailure.get() && chats.isEmpty()) {
                        callBack.onFailure(new Exception("Both queries failed."));
                    } else {
                        List<Chat> chatList = new ArrayList<>(chats);
                        callBack.onSuccess(chatList);
                    }
                }
            }
        };

        query1.addValueEventListener(eventListener);
        query2.addValueEventListener(eventListener);
    }


    public void DeleteChatById(String idChat, DeleteChatCallBack callBack) {
        DRef.child(idChat).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    callBack.onSuccess();
                }else{
                    callBack.onFailure(task.getException());
                }
            }
        });
    }


    public interface CreateChatCallBack {
        void onSuccess(String idChat);

        void onFailure(Exception e);
    }

    public interface GetChatCallBack {
        void onSuccess(Chat chat);

        void onFailure(Exception e);
    }

    public interface UpdateChatCallBack {
        void onSuccess();

        void onFailure(Exception e);
    }

    public interface GetChatsUserCallBack {
        void onSuccess(List<Chat> chat);

        void onFailure(Exception e);
    }
    public interface  DeleteChatCallBack{
        void onSuccess();
        void onFailure(Exception e);
    }
}
